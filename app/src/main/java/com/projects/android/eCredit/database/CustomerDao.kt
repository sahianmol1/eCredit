package com.projects.android.eCredit.database

import androidx.room.*

@Dao
interface CustomerDao {

    @Insert
    fun addCustomer(customer: Customer)

    @Query("SELECT * FROM 'customer' ORDER BY name ASC")
    fun getCustomers(): List<Customer>

    @Query("SELECT * FROM customer WHERE id = :id")
    fun getSingleCustomer(id: Int): Customer

    @Update
    fun updateCustomer(customer: Customer)

    @Query("DELETE FROM customer WHERE id = :id")
    fun deleteCustomer(id: Int)
}