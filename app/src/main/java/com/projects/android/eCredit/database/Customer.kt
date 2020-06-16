package com.projects.android.eCredit.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Customer(
    val name: String,
    val phoneNumber: String? = "",
    val image: String? = ""

) : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}