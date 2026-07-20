package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category): Long

    @Update
    suspend fun updateCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Int): Product?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long

    @Update
    suspend fun updateProduct(product: Product)

    @Query("UPDATE products SET stock = :newStock WHERE id = :productId")
    suspend fun updateStock(productId: Int, newStock: Double)

    @Delete
    suspend fun deleteProduct(product: Product)
}

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers ORDER BY name ASC")
    fun getAllCustomers(): Flow<List<Customer>>

    @Query("SELECT * FROM customers WHERE id = :id")
    fun getCustomerById(id: Int): Flow<Customer?>

    @Query("SELECT * FROM customers WHERE id = :id")
    suspend fun getCustomerByIdSuspend(id: Int): Customer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer): Long

    @Update
    suspend fun updateCustomer(customer: Customer)

    @Query("UPDATE customers SET currentDue = currentDue + :deltaDue WHERE id = :customerId")
    suspend fun updateCustomerDue(customerId: Int, deltaDue: Double)

    @Delete
    suspend fun deleteCustomer(customer: Customer)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE customerId = :customerId ORDER BY date DESC")
    fun getTransactionsByCustomer(customerId: Int): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction): Long

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)
}

@Dao
interface StockHistoryDao {
    @Query("SELECT * FROM stock_history ORDER BY date DESC")
    fun getAllStockHistory(): Flow<List<StockHistory>>

    @Query("SELECT * FROM stock_history WHERE productId = :productId ORDER BY date DESC")
    fun getStockHistoryByProduct(productId: Int): Flow<List<StockHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockHistory(history: StockHistory): Long
}
