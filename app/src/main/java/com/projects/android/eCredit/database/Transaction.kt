package com.projects.android.eCredit.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity(foreignKeys = arrayOf(ForeignKey(entity = Customer::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("id"),
    onDelete = ForeignKey.CASCADE
    )))
data class Transaction(
    @ForeignKey(entity = Customer::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("id"),
        onDelete = ForeignKey.CASCADE)
    val id: Int,
    val amount: String,
    val date: Date,
    val notes: String,
    val time: Long,
    val youGave: Boolean
): Serializable {
    @PrimaryKey(autoGenerate = true)
    var transactionId: Int = 0
}