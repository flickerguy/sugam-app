package com.example.sugam.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patients")
data class PatientEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,
    val phone: String,
    val age: Int? = null,
    val gender: String? = null,
    val address: String? = null,
    val notes: String? = null
)