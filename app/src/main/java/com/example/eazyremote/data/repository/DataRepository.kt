package com.example.eazyremote.data.repository

import androidx.lifecycle.LiveData
import com.example.eazyremote.data.local.dao.CarCharacteristicsDao
import com.example.eazyremote.data.local.sharedPref.SharedPrefData
import com.example.eazyremote.data.remote.impl.SomeDataRemoteDataSource
import com.example.eazyremote.data.remote.utils.NetworkStatus
import javax.inject.Inject
import javax.inject.Singleton

class DataRepository @Inject constructor(
    private val dao : CarCharacteristicsDao,
    private val sharedPref : SharedPrefData,
    private val someDataRemoteDataSource: SomeDataRemoteDataSource) {

    val values = sharedPref.getValues()

    fun getProperty(): LiveData<String> {
        return someDataRemoteDataSource.fetchData()
    }

    fun getStatus() : LiveData<NetworkStatus>{
        return someDataRemoteDataSource.fetchStatus()
    }

    suspend fun connection(ip: String, port: Int) {
        someDataRemoteDataSource.setConnection(ip,port)
    }

    suspend fun sendMessage(message: String) {
        someDataRemoteDataSource.sendMessage(message)
    }

    suspend fun readData(){
        someDataRemoteDataSource.readData()
    }

    fun openReceiver() {
        someDataRemoteDataSource.openReceiver()
    }

    fun closeReceiver() {
        someDataRemoteDataSource.closeReceiver()
    }

    fun isConnected(): Boolean {
        return someDataRemoteDataSource.isConnected()
    }

    fun clearProperty() {
        someDataRemoteDataSource.clearData()
    }

    companion object {

        const val PAGE_SIZE = 100

        // For Singleton instantiation
        @Volatile
        private var instance: DataRepository? = null

        fun getInstance(dao: CarCharacteristicsDao,
                        sharedPref: SharedPrefData,
                        legoSetRemoteDataSource: SomeDataRemoteDataSource) =
            instance ?: synchronized(this) {
                instance
                    ?: DataRepository(dao, sharedPref, legoSetRemoteDataSource).also { instance = it }
            }
    }

}