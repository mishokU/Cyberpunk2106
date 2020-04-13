package com.example.eazyremote.domain.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.eazyremote.data.repository.DataRepository
import com.example.eazyremote.di.utils.CoroutineScropeIO
import kotlinx.coroutines.*
import javax.inject.Inject

class DataViewModel @Inject constructor(
    private val repository: DataRepository,
    @CoroutineScropeIO
    private val ioCoroutineScope: CoroutineScope)
    : ViewModel()  {

    val property = repository.getProperty()
    val values = repository.values

    fun sendMessage(message: String) {
        ioCoroutineScope.launch {
            repository.sendMessage(message)
        }
    }

    fun readData(){
        ioCoroutineScope.launch {
            repository.readData()
        }
    }

    fun openReceiver() {
        repository.openReceiver()
    }

    fun closeReceiver(){
        repository.closeReceiver()
    }

    fun isConnected() : Boolean {
        return repository.isConnected()
    }

    fun handleConnection(ip : String, port : Int){
        ioCoroutineScope.launch {
            repository.connection(ip,port)
        }
    }

    override fun onCleared() {
        ioCoroutineScope.cancel()
        super.onCleared()
    }

    fun clearProperty() {
        repository.clearProperty()
    }

}