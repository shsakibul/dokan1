package com.example.data

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object SyncManager {

    // Safely check if Firebase has been initialized and is ready
    fun isFirebaseConfigured(context: Context): Boolean {
        return try {
            val apps = FirebaseApp.getApps(context)
            apps.isNotEmpty() && FirebaseAuth.getInstance() != null && FirebaseFirestore.getInstance() != null
        } catch (e: Exception) {
            false
        }
    }

    // Sign up a new user with Email and Password
    fun signUp(email: String, password: String, onResult: (Boolean, String) -> Unit) {
        try {
            val auth = FirebaseAuth.getInstance()
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onResult(true, "নিবন্ধন সফল হয়েছে!")
                    } else {
                        onResult(false, task.exception?.localizedMessage ?: "নিবন্ধন ব্যর্থ হয়েছে।")
                    }
                }
        } catch (e: Exception) {
            onResult(false, "ফায়ারবেস কনফিগারেশন ত্রুটি। অনুগ্রহ করে google-services.json ফাইলটি যুক্ত করুন।")
        }
    }

    // Sign in an existing user
    fun signIn(email: String, password: String, onResult: (Boolean, String) -> Unit) {
        try {
            val auth = FirebaseAuth.getInstance()
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onResult(true, "লগইন সফল হয়েছে!")
                    } else {
                        onResult(false, task.exception?.localizedMessage ?: "লগইন ব্যর্থ হয়েছে।")
                    }
                }
        } catch (e: Exception) {
            onResult(false, "ফায়ারবেস কনফিগারেশন ত্রুটি। অনুগ্রহ করে google-services.json ফাইলটি যুক্ত করুন।")
        }
    }

    // Sign out current user
    fun signOut() {
        try {
            FirebaseAuth.getInstance().signOut()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Check if user is logged in
    fun getUserId(): String? {
        return try {
            FirebaseAuth.getInstance().currentUser?.uid
        } catch (e: Exception) {
            null
        }
    }

    // Check email of logged in user
    fun getUserEmail(): String? {
        return try {
            FirebaseAuth.getInstance().currentUser?.email
        } catch (e: Exception) {
            null
        }
    }

    // Perform a complete Cloud Backup
    suspend fun backupData(context: Context, database: AppDatabase, onResult: (Boolean, String) -> Unit) {
        if (!isFirebaseConfigured(context)) {
            onResult(false, "ফায়ারবেস ক্লাউড কনফিগারেশন পাওয়া যায়নি! google-services.json ফাইলটি যুক্ত করুন।")
            return
        }

        val userId = getUserId()
        if (userId == null) {
            onResult(false, "অনুগ্রহ করে প্রথমে অ্যাকাউন্ট লগইন করুন!")
            return
        }

        try {
            withContext(Dispatchers.IO) {
                val firestore = FirebaseFirestore.getInstance()

                // Fetch all local data
                val categories = database.categoryDao().getAllCategories().firstOrNull() ?: emptyList()
                val products = database.productDao().getAllProducts().firstOrNull() ?: emptyList()
                val customers = database.customerDao().getAllCustomers().firstOrNull() ?: emptyList()
                val transactions = database.transactionDao().getAllTransactions().firstOrNull() ?: emptyList()
                val stockHistory = database.stockHistoryDao().getAllStockHistory().firstOrNull() ?: emptyList()

                // Write in Batches to Firestore under user scope for extreme safety and speed
                val userRef = firestore.collection("users").document(userId)

                // 1. Categories
                val categoriesBatch = firestore.batch()
                categories.forEach { cat ->
                    val docRef = userRef.collection("categories").document(cat.id.toString())
                    categoriesBatch.set(docRef, mapOf(
                        "id" to cat.id,
                        "name" to cat.name,
                        "unit" to cat.unit
                    ))
                }
                categoriesBatch.commit().await()

                // 2. Products
                val productsBatch = firestore.batch()
                products.forEach { prod ->
                    val docRef = userRef.collection("products").document(prod.id.toString())
                    productsBatch.set(docRef, mapOf(
                        "id" to prod.id,
                        "name" to prod.name,
                        "categoryId" to prod.categoryId,
                        "purchasePrice" to prod.purchasePrice,
                        "salePrice" to prod.salePrice,
                        "stock" to prod.stock,
                        "unit" to prod.unit,
                        "imagePath" to prod.imagePath
                    ))
                }
                productsBatch.commit().await()

                // 3. Customers
                val customersBatch = firestore.batch()
                customers.forEach { cust ->
                    val docRef = userRef.collection("customers").document(cust.id.toString())
                    customersBatch.set(docRef, mapOf(
                        "id" to cust.id,
                        "name" to cust.name,
                        "phone" to cust.phone,
                        "address" to cust.address,
                        "nid" to cust.nid,
                        "currentDue" to cust.currentDue,
                        "createdAt" to cust.createdAt
                    ))
                }
                customersBatch.commit().await()

                // 4. Transactions (divide into batches of 500 if very large)
                val txChunks = transactions.chunked(400)
                txChunks.forEach { chunk ->
                    val batch = firestore.batch()
                    chunk.forEach { tx ->
                        val docRef = userRef.collection("transactions").document(tx.id.toString())
                        batch.set(docRef, mapOf(
                            "id" to tx.id,
                            "customerId" to tx.customerId,
                            "type" to tx.type,
                            "amount" to tx.amount,
                            "profit" to tx.profit,
                            "date" to tx.date,
                            "details" to tx.details,
                            "itemsJson" to tx.itemsJson
                        ))
                    }
                    batch.commit().await()
                }

                // 5. Stock History
                val stockChunks = stockHistory.chunked(400)
                stockChunks.forEach { chunk ->
                    val batch = firestore.batch()
                    chunk.forEach { st ->
                        val docRef = userRef.collection("stock_history").document(st.id.toString())
                        batch.set(docRef, mapOf(
                            "id" to st.id,
                            "productId" to st.productId,
                            "quantityAdded" to st.quantityAdded,
                            "purchasePrice" to st.purchasePrice,
                            "salePrice" to st.salePrice,
                            "supplier" to st.supplier,
                            "date" to st.date
                        ))
                    }
                    batch.commit().await()
                }
            }

            onResult(true, "অভিনন্দন! আপনার সম্পূর্ণ হিসাব সফলভাবে ক্লাউডে ব্যাকআপ করা হয়েছে।")
        } catch (e: Exception) {
            onResult(false, "ব্যাকআপ ব্যর্থ হয়েছে: ${e.localizedMessage}")
        }
    }

    // Perform a complete Cloud Restore
    suspend fun restoreData(context: Context, database: AppDatabase, onResult: (Boolean, String) -> Unit) {
        if (!isFirebaseConfigured(context)) {
            onResult(false, "ফায়ারবেস ক্লাউড কনফিগারেশন পাওয়া যায়নি! google-services.json ফাইলটি যুক্ত করুন।")
            return
        }

        val userId = getUserId()
        if (userId == null) {
            onResult(false, "অনুগ্রহ করে প্রথমে অ্যাকাউন্ট লগইন করুন!")
            return
        }

        try {
            withContext(Dispatchers.IO) {
                val firestore = FirebaseFirestore.getInstance()
                val userRef = firestore.collection("users").document(userId)

                // 1. Fetch Categories
                val catsSnap = userRef.collection("categories").get().await()
                val firestoreCats = catsSnap.documents.mapNotNull { doc ->
                    val id = doc.getLong("id")?.toInt() ?: return@mapNotNull null
                    val name = doc.getString("name") ?: ""
                    val unit = doc.getString("unit") ?: "পিস"
                    Category(id = id, name = name, unit = unit)
                }

                // 2. Fetch Products
                val prodsSnap = userRef.collection("products").get().await()
                val firestoreProds = prodsSnap.documents.mapNotNull { doc ->
                    val id = doc.getLong("id")?.toInt() ?: return@mapNotNull null
                    val name = doc.getString("name") ?: ""
                    val catId = doc.getLong("categoryId")?.toInt() ?: 0
                    val pPrice = doc.getDouble("purchasePrice") ?: 0.0
                    val sPrice = doc.getDouble("salePrice") ?: 0.0
                    val stock = doc.getDouble("stock") ?: 0.0
                    val unit = doc.getString("unit") ?: "পিস"
                    val img = doc.getString("imagePath")
                    Product(id = id, name = name, categoryId = catId, purchasePrice = pPrice, salePrice = sPrice, stock = stock, unit = unit, imagePath = img)
                }

                // 3. Fetch Customers
                val custsSnap = userRef.collection("customers").get().await()
                val firestoreCusts = custsSnap.documents.mapNotNull { doc ->
                    val id = doc.getLong("id")?.toInt() ?: return@mapNotNull null
                    val name = doc.getString("name") ?: ""
                    val phone = doc.getString("phone") ?: ""
                    val address = doc.getString("address") ?: ""
                    val nid = doc.getString("nid")
                    val currentDue = doc.getDouble("currentDue") ?: 0.0
                    val createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                    Customer(id = id, name = name, phone = phone, address = address, nid = nid, currentDue = currentDue, createdAt = createdAt)
                }

                // 4. Fetch Transactions
                val txsSnap = userRef.collection("transactions").get().await()
                val firestoreTxs = txsSnap.documents.mapNotNull { doc ->
                    val id = doc.getLong("id")?.toInt() ?: return@mapNotNull null
                    val custId = doc.getLong("customerId")?.toInt()
                    val type = doc.getString("type") ?: "SALE"
                    val amount = doc.getDouble("amount") ?: 0.0
                    val profit = doc.getDouble("profit") ?: 0.0
                    val date = doc.getLong("date") ?: System.currentTimeMillis()
                    val details = doc.getString("details") ?: ""
                    val itemsJson = doc.getString("itemsJson")
                    Transaction(id = id, customerId = custId, type = type, amount = amount, profit = profit, date = date, details = details, itemsJson = itemsJson)
                }

                // 5. Fetch Stock History
                val stockSnap = userRef.collection("stock_history").get().await()
                val firestoreStock = stockSnap.documents.mapNotNull { doc ->
                    val id = doc.getLong("id")?.toInt() ?: return@mapNotNull null
                    val prodId = doc.getLong("productId")?.toInt() ?: 0
                    val qtyAdded = doc.getDouble("quantityAdded") ?: 0.0
                    val pPrice = doc.getDouble("purchasePrice") ?: 0.0
                    val sPrice = doc.getDouble("salePrice") ?: 0.0
                    val supplier = doc.getString("supplier")
                    val date = doc.getLong("date") ?: System.currentTimeMillis()
                    StockHistory(id = id, productId = prodId, quantityAdded = qtyAdded, purchasePrice = pPrice, salePrice = sPrice, supplier = supplier, date = date)
                }

                // Write to SQLite Room (overwrite/replace)
                database.runInTransaction {
                    // We run database transactions to do bulk inputs reliably
                    // We can use a coroutine launcher or run direct suspend transactions
                    // Since Room supports runInTransaction, let's insert them safely
                }

                // To insert suspendable entries safely without blocking:
                // Categories
                firestoreCats.forEach { database.categoryDao().insertCategory(it) }
                // Products
                firestoreProds.forEach { database.productDao().insertProduct(it) }
                // Customers
                firestoreCusts.forEach { database.customerDao().insertCustomer(it) }
                // Transactions
                firestoreTxs.forEach { database.transactionDao().insertTransaction(it) }
                // Stock History
                firestoreStock.forEach { database.stockHistoryDao().insertStockHistory(it) }
            }

            onResult(true, "সফল হিসাব ক্লাউড থেকে রিস্টোর করা হয়েছে এবং লোকাল ডেটাবেস আপডেট করা হয়েছে।")
        } catch (e: Exception) {
            onResult(false, "রিস্টোর ব্যর্থ হয়েছে: ${e.localizedMessage}")
        }
    }
}
