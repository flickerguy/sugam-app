package com.example.sugam.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "physios")
data class PhysioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,
    val phone: String? = null,
    val specialization: String? = null,
    val active: Boolean = true
)