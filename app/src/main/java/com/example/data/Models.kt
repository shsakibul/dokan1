package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val unit: String
)

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val categoryId: Int,
    val purchasePrice: Double, // ক্রয়মূল্য
    val salePrice: Double, // বিক্রয়মূল্য
    val stock: Double, // বর্তমান স্টক
    val unit: String, // e.g. কেজি, পিস, লিটার
    val imagePath: String? = null
)

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phone: String,
    val address: String,
    val nid: String? = null,
    val currentDue: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val customerId: Int? = null, // null for general sales, customerId if specific
    val type: String, // "SALE" (নগদ বিক্রয়), "DUE_SALE" (বাকি বিক্রয়), "PAYMENT" (টাকা পরিশোধ), "STOCK_BUY" (নতুন স্টক ক্রয়), "EXPENSE" (অন্যান্য ব্যয়)
    val amount: Double,
    val profit: Double = 0.0,
    val date: Long = System.currentTimeMillis(),
    val details: String, // e.g. "চাল — ৫ কেজি"
    val itemsJson: String? = null // optional JSON or description
)

@Entity(tableName = "stock_history")
data class StockHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: Int,
    val quantityAdded: Double,
    val purchasePrice: Double,
    val salePrice: Double,
    val supplier: String?,
    val date: Long = System.currentTimeMillis()
)
