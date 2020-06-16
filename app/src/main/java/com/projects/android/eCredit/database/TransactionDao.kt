package com.projects.android.eCredit.database

import androidx.room.*

@Dao
interface TransactionDao {

    @Insert
    fun addTransaction(transaction: Transaction)

    @Update
    fun updateTransaction(transaction: Transaction)

    @Query("SELECT * FROM `transaction` where id = :id")
    fun getAllTransactions(id: Int): List<Transaction>

    @Query("SELECT * FROM `transaction` ORDER BY id ASC")
    fun getAllTransactionsForAllCustomers(): List<Transaction>

    @Query("DELETE FROM `transaction` WHERE transactionId = :id")
    fun deleteTransactions(id: Int)
}