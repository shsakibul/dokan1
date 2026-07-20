package com.example.ui

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

sealed interface Screen {
    object Dashboard : Screen
    object Products : Screen
    object AddProduct : Screen
    object DueKhata : Screen
    object AddCustomer : Screen
    data class CustomerDetail(val customerId: Int) : Screen
    data class CustomerAddDue(val customerId: Int) : Screen
    object History : Screen
    object Settings : Screen
    object CategoryManagement : Screen
}

class ShopViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val repository = ShopRepository(
        db.categoryDao(),
        db.productDao(),
        db.customerDao(),
        db.transactionDao(),
        db.stockHistoryDao()
    )

    // Navigation Back-stack
    val navigationStack = mutableStateListOf<Screen>(Screen.Dashboard)

    fun navigateTo(screen: Screen) {
        navigationStack.add(screen)
    }

    fun navigateBack(): Boolean {
        if (navigationStack.size > 1) {
            navigationStack.removeAt(navigationStack.size - 1)
            return true
        }
        return false
    }

    // Database Flows
    val categories = repository.allCategories.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val products = repository.allProducts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val customers = repository.allCustomers.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val transactions = repository.allTransactions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val stockHistory = repository.allStockHistory.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Filters UI State
    val selectedTimeFilter = MutableStateFlow("TODAY") // "TODAY", "YESTERDAY", "WEEK", "MONTH", "YEAR", "ALL"
    var productSearchQuery by mutableStateOf("")
    var selectedCategoryFilterId by mutableStateOf<Int?>(null)
    var selectedStockFilter by mutableStateOf("ALL") // "ALL", "LOW_STOCK", "OUT_OF_STOCK"
    var customerSearchQuery by mutableStateOf("")
    var historyTypeFilter by mutableStateOf<String?>(null) // null, "SALE", "DUE_SALE", "PAYMENT", "STOCK_BUY", "EXPENSE"

    // Dialogs / Sheet states
    var quickSellProduct by mutableStateOf<Product?>(null)
    var addStockProduct by mutableStateOf<Product?>(null)

    // Derived stats
    val dashboardStats = combine(
        transactions,
        products,
        customers,
        selectedTimeFilter
    ) { txs, prods, custs, filter ->
        calculateStats(txs, prods, custs, filter)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardStats()
    )

    // Smart Notifications & Warnings
    val smartNotifications = combine(
        products,
        customers,
        transactions
    ) { prods, custs, txs ->
        generateNotifications(prods, custs, txs)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Smart Business Analysis
    val businessAnalysis = combine(
        products,
        transactions
    ) { prods, txs ->
        generateAnalysis(prods, txs)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AnalysisReport()
    )

    // DB Operations
    fun addCategory(name: String, unit: String) {
        viewModelScope.launch {
            repository.insertCategory(Category(name = name, unit = unit))
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }

    fun addProduct(name: String, categoryId: Int, purchasePrice: Double, salePrice: Double, stock: Double, unit: String) {
        viewModelScope.launch {
            val product = Product(
                name = name,
                categoryId = categoryId,
                purchasePrice = purchasePrice,
                salePrice = salePrice,
                stock = stock,
                unit = unit
            )
            repository.insertProduct(product)
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    fun addStock(productId: Int, quantity: Double, purchasePrice: Double, salePrice: Double, supplier: String?) {
        viewModelScope.launch {
            repository.recordStockAddition(productId, quantity, purchasePrice, salePrice, supplier)
        }
    }

    fun sellProduct(productId: Int, quantity: Double, isBaki: Boolean, customerId: Int?): Flow<Boolean> = flow {
        val success = repository.recordSale(productId, quantity, isBaki, customerId)
        emit(success)
    }

    fun addCustomer(name: String, phone: String, address: String, nid: String?) {
        viewModelScope.launch {
            repository.insertCustomer(
                Customer(
                    name = name,
                    phone = phone,
                    address = address,
                    nid = nid
                )
            )
        }
    }

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch {
            repository.deleteCustomer(customer)
        }
    }

    fun recordCustomerPayment(customerId: Int, amount: Double, note: String) {
        viewModelScope.launch {
            repository.recordPayment(customerId, amount, note)
        }
    }

    fun recordExpense(title: String, amount: Double) {
        viewModelScope.launch {
            repository.recordOtherExpense(title, amount)
        }
    }

    fun getTransactionsForCustomer(customerId: Int): Flow<List<Transaction>> {
        return repository.getTransactionsByCustomer(customerId)
    }

    fun getCustomerById(customerId: Int): Flow<Customer?> {
        return repository.getCustomerById(customerId)
    }

    // Helper functions for stats
    private fun calculateStats(
        allTxs: List<Transaction>,
        allProds: List<Product>,
        allCusts: List<Customer>,
        filter: String
    ): DashboardStats {
        val times = getPeriodTimestamps(filter)
        val filteredTxs = allTxs.filter { it.date in times.first..times.second }

        var totalSales = 0.0
        var totalProfit = 0.0
        var totalExpenses = 0.0
        var transactionsCount = filteredTxs.size

        for (tx in filteredTxs) {
            when (tx.type) {
                "SALE", "DUE_SALE" -> {
                    totalSales += tx.amount
                    totalProfit += tx.profit
                }
                "STOCK_BUY", "EXPENSE" -> {
                    totalExpenses += tx.amount
                }
            }
        }

        // Dues is a total market sum (global)
        val totalDues = allCusts.sumOf { it.currentDue }
        // Estimated stock value (global)
        val totalStockValue = allProds.sumOf { it.stock * it.purchasePrice }

        return DashboardStats(
            totalSales = totalSales,
            totalProfit = totalProfit,
            totalExpenses = totalExpenses,
            transactionsCount = transactionsCount,
            totalDues = totalDues,
            totalStockValue = totalStockValue
        )
    }

    private fun getPeriodTimestamps(filter: String): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfToday = calendar.timeInMillis

        return when (filter) {
            "TODAY" -> Pair(startOfToday, now)
            "YESTERDAY" -> {
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                val startOfYesterday = calendar.timeInMillis
                Pair(startOfYesterday, startOfToday - 1)
            }
            "WEEK" -> {
                calendar.add(Calendar.DAY_OF_YEAR, -6)
                Pair(calendar.timeInMillis, now)
            }
            "MONTH" -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                Pair(calendar.timeInMillis, now)
            }
            "YEAR" -> {
                calendar.set(Calendar.DAY_OF_YEAR, 1)
                Pair(calendar.timeInMillis, now)
            }
            else -> Pair(0L, now)
        }
    }

    // Generate low stock warning, customer dues warnings & sales summaries
    private fun generateNotifications(
        prods: List<Product>,
        custs: List<Customer>,
        txs: List<Transaction>
    ): List<ShopNotification> {
        val notifications = mutableListOf<ShopNotification>()

        // 1. Stock warnings
        for (p in prods) {
            if (p.stock <= 0.0) {
                notifications.add(
                    ShopNotification(
                        title = "স্টক শেষ!",
                        message = "${p.name}-এর স্টক শেষ হয়ে গেছে। অনুগ্রহ করে নতুন স্টক যুক্ত করুন।",
                        type = "ERROR"
                    )
                )
            } else if (p.stock <= 3.0) {
                notifications.add(
                    ShopNotification(
                        title = "স্বল্প স্টক সতর্কতা",
                        message = "${p.name} মাত্র ${p.stock.toInt()} ${p.unit} বাকি আছে।",
                        type = "WARNING"
                    )
                )
            }
        }

        // 2. High dues alert
        for (c in custs) {
            if (c.currentDue >= 1000.0) {
                notifications.add(
                    ShopNotification(
                        title = "বাকি আদায় সতর্কতা",
                        message = "${c.name}-এর কাছে বকেয়া ${c.currentDue.toInt()} টাকা। কথা বলে আদায়ের ব্যবস্থা করুন।",
                        type = "INFO"
                    )
                )
            }
        }

        // 3. Today's sales summary
        val todayStart = getPeriodTimestamps("TODAY").first
        val todaySales = txs.filter { it.date >= todayStart && (it.type == "SALE" || it.type == "DUE_SALE") }.sumOf { it.amount }
        if (todaySales > 0.0) {
            notifications.add(
                ShopNotification(
                    title = "আজকের বিক্রেয় তথ্য",
                    message = "আজকে এখন পর্যন্ত মোট ${todaySales.toInt()} টাকার পণ্য বিক্রি হয়েছে।",
                    type = "SUCCESS"
                )
            )
        }

        return notifications
    }

    // Analyze sales history to predict future demand and trends
    private fun generateAnalysis(
        prods: List<Product>,
        txs: List<Transaction>
    ): AnalysisReport {
        val sevenDaysAgo = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000)
        
        // Filter sales transactions in the last 7 days
        val recentSales = txs.filter { it.date >= sevenDaysAgo && (it.type == "SALE" || it.type == "DUE_SALE") }

        // Find how much of each product was sold.
        // Since Transaction.details looks like "চাল — ৫ কেজি" or "ডাল — ১ কেজি", we can match names.
        val itemSalesCount = mutableMapOf<Int, Double>()
        val itemDailyAverage = mutableMapOf<Int, Double>()

        for (prod in prods) {
            var totalSold = 0.0
            for (tx in recentSales) {
                if (tx.details.startsWith(prod.name)) {
                    // Extract quantity
                    try {
                        val parts = tx.details.split("—")
                        if (parts.size >= 2) {
                            val qtyStr = parts[1].trim().split(" ")[0]
                            val qty = qtyStr.toDoubleOrNull() ?: 0.0
                            totalSold += qty
                        }
                    } catch (e: Exception) {
                        // fallback or ignore parsing errors
                    }
                }
            }
            if (totalSold > 0.0) {
                itemSalesCount[prod.id] = totalSold
                itemDailyAverage[prod.id] = totalSold / 7.0
            }
        }

        // 1. Most sold product
        val bestSellingProductId = itemSalesCount.maxByOrNull { it.value }?.key
        val bestSellingProduct = prods.find { it.id == bestSellingProductId }
        val bestSellingQty = itemSalesCount[bestSellingProductId] ?: 0.0

        // 2. Least sold product
        val lowSellingProductId = prods.map { it.id }.filter { itemSalesCount.containsKey(it) }.minByOrNull { itemSalesCount[it] ?: 0.0 }
            ?: prods.firstOrNull()?.id
        val lowSellingProduct = prods.find { it.id == lowSellingProductId }
        val lowSellingQty = itemSalesCount[lowSellingProductId] ?: 0.0

        // 3. Stock Predictions & Demand Estimation
        val predictions = mutableListOf<DemandPrediction>()
        for (prod in prods) {
            val dailyAvg = itemDailyAverage[prod.id] ?: 0.0
            if (dailyAvg > 0.0) {
                val daysRemaining = prod.stock / dailyAvg
                if (daysRemaining <= 3.0 && prod.stock > 0.0) {
                    predictions.add(
                        DemandPrediction(
                            productName = prod.name,
                            stock = prod.stock,
                            unit = prod.unit,
                            dailyAvg = dailyAvg,
                            daysRemaining = daysRemaining,
                            advice = "${prod.name}-এর বর্তমান স্টক মাত্র ${prod.stock.toInt()} ${prod.unit}। গত ৭ দিনে গড়ে প্রতিদিন ${String.format("%.1f", dailyAvg)} বিক্রি হয়েছে। আগামী ${String.format("%.1f", daysRemaining)} দিনের মধ্যে স্টক ফুরিয়ে যাবে। জলদি নতুন স্টক সংগ্রহ করুন!"
                        )
                    )
                }
            }
        }

        return AnalysisReport(
            bestSellingProduct = bestSellingProduct?.name ?: "কোনো ডাটা নেই",
            bestSellingQty = bestSellingQty,
            bestSellingUnit = bestSellingProduct?.unit ?: "",
            lowSellingProduct = lowSellingProduct?.name ?: "কোনো ডাটা নেই",
            lowSellingQty = lowSellingQty,
            lowSellingUnit = lowSellingProduct?.unit ?: "",
            predictions = predictions
        )
    }
}

// Data structures
data class DashboardStats(
    val totalSales: Double = 0.0,
    val totalProfit: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val transactionsCount: Int = 0,
    val totalDues: Double = 0.0,
    val totalStockValue: Double = 0.0
)

data class ShopNotification(
    val title: String,
    val message: String,
    val type: String // "INFO", "WARNING", "ERROR", "SUCCESS"
)

data class DemandPrediction(
    val productName: String,
    val stock: Double,
    val unit: String,
    val dailyAvg: Double,
    val daysRemaining: Double,
    val advice: String
)

data class AnalysisReport(
    val bestSellingProduct: String = "কোনো ডাটা নেই",
    val bestSellingQty: Double = 0.0,
    val bestSellingUnit: String = "",
    val lowSellingProduct: String = "কোনো ডাটা নেই",
    val lowSellingQty: Double = 0.0,
    val lowSellingUnit: String = "",
    val predictions: List<DemandPrediction> = emptyList()
)
