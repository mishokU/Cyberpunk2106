package com.example.eazyremote.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CarModel(
    @PrimaryKey
    val id : Int,
    val temperature : Double
)