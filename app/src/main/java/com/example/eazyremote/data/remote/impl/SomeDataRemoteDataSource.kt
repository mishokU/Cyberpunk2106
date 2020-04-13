package com.example.eazyremote.data.remote.impl

import androidx.lifecycle.LiveData
import com.example.eazyremote.data.remote.utils.NetworkStatus
import javax.inject.Inject

class SomeDataRemoteDataSource @Inject constructor(
    private val service: TcpClientImpl) {

    suspend fun setConnection(ip : String, port : Int){
        service.connection(ip,port)
    }

    fun fetchData() : LiveData<String> {
        return service.serverOutput
    }

    fun fetchStatus() : LiveData<NetworkStatus> {
        return service.networkStatus
    }

    fun isConnected() : Boolean {
        return service.isConnected()
    }

    suspend fun sendMessage(message: String) {
        service.writeData(message)
    }

    suspend fun readData() {
        service.readData()
    }

    fun openReceiver() {
        service.openReceiver()
    }

    fun closeReceiver(){
        service.closeReceiver()
    }

    fun clearData() {
        service.clearData()
    }
}
