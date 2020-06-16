package com.projects.android.eCredit.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.projects.android.birthdayappfinal.database.converters.DateConverter


@Database(
    entities = [Customer::class, Transaction::class],
    version = 5,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class CustomerDatabase: RoomDatabase() {
    abstract val getCustomerDao: CustomerDao
    abstract val getTransactionDao: TransactionDao


    companion object {

        @Volatile
        private var INSTANCE: CustomerDatabase? = null

        fun getInstance(context: Context): CustomerDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        CustomerDatabase::class.java,
                        "birthday_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}