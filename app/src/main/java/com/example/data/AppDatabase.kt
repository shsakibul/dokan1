package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        Category::class,
        Product::class,
        Customer::class,
        Transaction::class,
        StockHistory::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao
    abstract fun customerDao(): CustomerDao
    abstract fun transactionDao(): TransactionDao
    abstract fun stockHistoryDao(): StockHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "grocery_shop_db"
                )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed standard Bengali categories with their typical units
                        db.execSQL("INSERT INTO categories (id, name, unit) VALUES (1, 'চাল', 'কেজি')")
                        db.execSQL("INSERT INTO categories (id, name, unit) VALUES (2, 'ডাল', 'কেজি')")
                        db.execSQL("INSERT INTO categories (id, name, unit) VALUES (3, 'তেল', 'লিটার')")
                        db.execSQL("INSERT INTO categories (id, name, unit) VALUES (4, 'চিনি', 'কেজি')")
                        db.execSQL("INSERT INTO categories (id, name, unit) VALUES (5, 'বিস্কুট', 'পিস')")
                        db.execSQL("INSERT INTO categories (id, name, unit) VALUES (6, 'চিপস', 'পিস')")
                        db.execSQL("INSERT INTO categories (id, name, unit) VALUES (7, 'পানীয়', 'বোতল')")
                        db.execSQL("INSERT INTO categories (id, name, unit) VALUES (8, 'ডিম', 'পিস')")
                        db.execSQL("INSERT INTO categories (id, name, unit) VALUES (9, 'সাবান', 'পিস')")
                        db.execSQL("INSERT INTO categories (id, name, unit) VALUES (10, 'শ্যাম্পু', 'প্যাকেট')")
                        db.execSQL("INSERT INTO categories (id, name, unit) VALUES (11, 'অন্যান্য', 'পিস')")
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
