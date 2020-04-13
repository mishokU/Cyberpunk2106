package com.example.eazyremote.data.remote.api

interface WriteData {
    suspend fun writeData(data : String)
}