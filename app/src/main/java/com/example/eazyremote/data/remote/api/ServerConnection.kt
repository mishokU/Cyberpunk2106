package com.example.eazyremote.data.remote.api

interface ServerConnection {
    suspend fun connection(ip : String, port : Int)
}