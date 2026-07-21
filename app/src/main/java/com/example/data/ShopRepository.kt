package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class ShopRepository(
    private val context: android.content.Context,
    private val categoryDao: CategoryDao,
    private val productDao: ProductDao,
    private val customerDao: CustomerDao,
    private val transactionDao: TransactionDao,
    private val stockHistoryDao: StockHistoryDao
) {
    // Flows
    val allCategories: Flow<List<Category>> = categoryDao.getAllCategories()
    val allProducts: Flow<List<Product>> = productDao.getAllProducts()
    val allCustomers: Flow<List<Customer>> = customerDao.getAllCustomers()
    val allTransactions: Flow<List<Transaction>> = transactionDao.getAllTransactions()
    val allStockHistory: Flow<List<StockHistory>> = stockHistoryDao.getAllStockHistory()

    fun getTransactionsByCustomer(customerId: Int): Flow<List<Transaction>> =
        transactionDao.getTransactionsByCustomer(customerId)

    fun getCustomerById(id: Int): Flow<Customer?> =
        customerDao.getCustomerById(id)

    // Category Operations
    suspend fun insertCategory(category: Category): Long =
        categoryDao.insertCategory(category)

    suspend fun updateCategory(category: Category) =
        categoryDao.updateCategory(category)

    suspend fun deleteCategory(category: Category) =
        categoryDao.deleteCategory(category)

    // Product Operations
    suspend fun insertProduct(product: Product): Long =
        productDao.insertProduct(product)

    suspend fun updateProduct(product: Product) =
        productDao.updateProduct(product)

    suspend fun deleteProduct(product: Product) =
        productDao.deleteProduct(product)

    // Customer Operations
    suspend fun insertCustomer(customer: Customer): Long =
        customerDao.insertCustomer(customer)

    suspend fun updateCustomer(customer: Customer) =
        customerDao.updateCustomer(customer)

    suspend fun deleteCustomer(customer: Customer) =
        customerDao.deleteCustomer(customer)

    // Atomically Record a Sale
    suspend fun recordSale(
        productId: Int,
        quantity: Double,
        isBaki: Boolean,
        customerId: Int?
    ): Boolean {
        val product = productDao.getProductById(productId) ?: return false
        if (product.stock < quantity) {
            // Even if stock is low, we let the sale pass but we cap or allow negative as typical,
            // but let's deduct stock properly. Let's keep it safe.
        }

        val saleAmount = product.salePrice * quantity
        val purchaseCost = product.purchasePrice * quantity
        val profit = saleAmount - purchaseCost

        // 1. Deduct Stock
        val newStock = (product.stock - quantity).coerceAtLeast(0.0)
        productDao.updateStock(productId, newStock)

        // Smart stock notification triggers
        if (newStock <= 0.0) {
            NotificationHelper.showNotification(
                context,
                "স্টক শেষ হয়ে গেছে!",
                "${product.name}-এর সম্পূর্ণ স্টক শেষ হয়ে গেছে। দ্রুত নতুন স্টক সংগ্রহ করুন।"
            )
        } else if (newStock <= 3.0 && product.stock > 3.0) {
            val formattedStock = if (newStock % 1.0 == 0.0) newStock.toInt().toString() else newStock.toString()
            NotificationHelper.showNotification(
                context,
                "স্বল্প স্টক সতর্কতা",
                "${product.name}-এর স্টক কমে গেছে। বর্তমান স্টক মাত্র $formattedStock ${product.unit}।"
            )
        }

        // 2. Insert Transaction
        val type = if (isBaki && customerId != null) "DUE_SALE" else "SALE"
        val formattedQuantity = if (quantity % 1.0 == 0.0) quantity.toInt().toString() else quantity.toString()
        val suffix = if (isBaki && customerId != null) " (বাকি)" else " (নগদ)"
        val details = "${product.name} — $formattedQuantity ${product.unit}$suffix"

        transactionDao.insertTransaction(
            Transaction(
                customerId = customerId,
                type = type,
                amount = saleAmount,
                profit = profit,
                details = details
            )
        )

        // 3. Update Customer's Due balance
        if (isBaki && customerId != null) {
            customerDao.updateCustomerDue(customerId, saleAmount)
            // Smart due alert
            val customer = customerDao.getCustomerByIdSuspend(customerId)
            if (customer != null) {
                val newDue = customer.currentDue + saleAmount
                if (newDue >= 1000.0) {
                    NotificationHelper.showNotification(
                        context,
                        "বাকি আদায়ের সর্তকতা",
                        "${customer.name}-এর বকেয়া দাঁড়িয়েছে ${newDue.toInt()} টাকা।"
                    )
                }
            }
        }

        return true
    }

    // Atomically Record a Stock Addition (Purchase)
    suspend fun recordStockAddition(
        productId: Int,
        quantityAdded: Double,
        purchasePrice: Double,
        salePrice: Double,
        supplier: String?
    ): Boolean {
        val product = productDao.getProductById(productId) ?: return false

        // 1. Update product properties (new stock, updated prices)
        val updatedProduct = product.copy(
            stock = product.stock + quantityAdded,
            purchasePrice = purchasePrice,
            salePrice = salePrice
        )
        productDao.updateProduct(updatedProduct)

        // 2. Add Stock Log
        stockHistoryDao.insertStockHistory(
            StockHistory(
                productId = productId,
                quantityAdded = quantityAdded,
                purchasePrice = purchasePrice,
                salePrice = salePrice,
                supplier = supplier
            )
        )

        // 3. Log Stock purchase expense (Transaction)
        val formattedQuantity = if (quantityAdded % 1.0 == 0.0) quantityAdded.toInt().toString() else quantityAdded.toString()
        transactionDao.insertTransaction(
            Transaction(
                type = "STOCK_BUY",
                amount = purchasePrice * quantityAdded,
                profit = 0.0, // Stock purchases do not immediately produce sales profit
                details = "স্টক ক্রয়: ${product.name} — $formattedQuantity ${product.unit}"
            )
        )

        return true
    }

    // Atomically Record customer payment of baki
    suspend fun recordPayment(
        customerId: Int,
        amount: Double,
        note: String
    ): Boolean {
        val customer = customerDao.getCustomerByIdSuspend(customerId) ?: return false

        // 1. Reduce Customer's Due (due reduces, so delta is negative)
        customerDao.updateCustomerDue(customerId, -amount)

        // 2. Insert Payment Transaction
        val noteSuffix = if (note.isNotBlank()) " ($note)" else ""
        transactionDao.insertTransaction(
            Transaction(
                customerId = customerId,
                type = "PAYMENT",
                amount = amount,
                profit = 0.0,
                details = "${customer.name} — টাকা পরিশোধ$noteSuffix"
            )
        )

        return true
    }

    // Record other shop expense
    suspend fun recordOtherExpense(
        title: String,
        amount: Double
    ) {
        transactionDao.insertTransaction(
            Transaction(
                type = "EXPENSE",
                amount = amount,
                profit = 0.0,
                details = "অন্যান্য ব্যয়: $title"
            )
        )
    }

    // Check overdue accounts dynamically for notifications
    suspend fun checkDuePaymentReminders() {
        val customersList = customerDao.getAllCustomers().firstOrNull() ?: emptyList()
        val allTxs = transactionDao.getAllTransactions().firstOrNull() ?: emptyList()
        val now = System.currentTimeMillis()

        customersList.forEach { customer ->
            if (customer.currentDue > 0.0) {
                // Find oldest DUE_SALE transaction for this customer
                val customerTxs = allTxs.filter { it.customerId == customer.id && it.type == "DUE_SALE" }
                val oldestTx = customerTxs.minByOrNull { it.date }
                if (oldestTx != null) {
                    val daysElapsed = ((now - oldestTx.date) / (1000 * 60 * 60 * 24)).toInt()
                    if (daysElapsed >= 30) {
                        NotificationHelper.showNotification(
                            context,
                            "বাকি পরিশোধের ৩০ দিন অতিবাহিত",
                            "${customer.name}-এর বাকি ${customer.currentDue.toInt()} টাকা পরিশোধের ৩০ দিন পূর্ণ হয়েছে।"
                        )
                    } else if (daysElapsed >= 15) {
                        NotificationHelper.showNotification(
                            context,
                            "বাকি আদায়ের ১৫ দিন অতিবাহিত",
                            "${customer.name}-এর বাকি ${customer.currentDue.toInt()} টাকা পরিশোধের ১৫ দিন অতিবাহিত হয়েছে।"
                        )
                    }
                }
            }
        }
    }
}
