package com.example.eazyremote.data.remote.impl

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.eazyremote.data.remote.utils.NetworkStatus
import com.example.eazyremote.data.remote.api.ReadData
import com.example.eazyremote.data.remote.api.ServerConnection
import com.example.eazyremote.data.remote.api.WriteData
import com.example.eazyremote.domain.utils.Constants.HERE_WE_GO
import com.example.eazyremote.domain.utils.Constants.SECRET_KEY
import kotlinx.coroutines.*
import java.io.IOException
import java.io.PrintWriter
import java.lang.Exception
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress
import java.util.*

class TcpClientImpl : ReadData,WriteData, ServerConnection {

    private var address: SocketAddress ?= null
    private var socket: Socket? = null

    private lateinit var outMessage : PrintWriter
    private lateinit var inMessage : Scanner

    private var run : Boolean = true

    private val _serverOutput = MutableLiveData<String>()
    val serverOutput : LiveData<String>
        get() = _serverOutput

    private val _socketData = MutableLiveData<String>()
    val socketData : LiveData<String>
        get() = _socketData

    private val _socketConnection = MutableLiveData<Boolean>()
    val socketConnection : LiveData<Boolean>
        get() = _socketConnection

    private val _networkStatus = MutableLiveData<NetworkStatus>()
    val networkStatus : LiveData<NetworkStatus>
        get() = _networkStatus

    override suspend fun readData() {
        withContext(Dispatchers.IO) {
            try {
                while (run) {
                    if (inMessage.hasNext()) {
                        analiseMessage(inMessage.next())
                    }
                }
            } catch (e: IndexOutOfBoundsException) {
                handleStatus(NetworkStatus.Error)
            }
        }
    }

    private suspend fun analiseMessage(message: String) {
        withContext(Dispatchers.Main) {
            when {
                message.startsWith("v") && message.length > 5 -> {
                    _serverOutput.value = message
                }
                message.startsWith("tok") -> {
                    _serverOutput.value = message
                }
                message.startsWith("vol") -> {
                    _serverOutput.value = message
                }
                message.startsWith("tvn") -> {
                    _serverOutput.value = message
                }
                message.startsWith("4") -> {
                    _serverOutput.value = message
                }
                message.contains(HERE_WE_GO) -> {
                    _serverOutput.value = message
                }
                message.startsWith("n") && message.length == 2 -> {
                    _serverOutput.value = message
                }
                message.startsWith("z") && message.length == 2 -> {
                    _serverOutput.value = message
                }
                message.startsWith("d") && message.length == 2 -> {
                    _serverOutput.value = message
                }
                message.startsWith("r") && message.length == 2 -> {
                    _serverOutput.value
                }
                else -> {
                    Log.d("any message", message)
                    _socketData.value = message
                }
            }
            //_serverOutput.value = null
        }
    }

    override suspend fun writeData(data: String) {
        withContext(Dispatchers.IO){
            try{
                if(isConnected()){
                    outMessage.println(SECRET_KEY + data)
                }
            } catch (e: IOException){
                handleStatus(NetworkStatus.Error)
            }
        }
    }

    private suspend fun handleStatus(status: NetworkStatus) {
        withContext(Dispatchers.Main){
            _networkStatus.value = status
        }
    }

    override suspend fun connection(ip: String, port : Int) {
        withContext(Dispatchers.IO) {
            try {

                address = InetSocketAddress(ip, port)

                socket = Socket()
                socket!!.connect(address, 3000)

                outMessage = PrintWriter(socket!!.getOutputStream(), true)
                inMessage = Scanner(socket!!.getInputStream())

                handleStatus(NetworkStatus.Connected)
            } catch (e: Exception) {
                handleStatus(NetworkStatus.Error)
                closeConnection()
            }
        }
    }

    private suspend fun checkSocketConnection() {
        withContext(Dispatchers.Main){
            _socketConnection.value = socket!!.isConnected
        }
    }

    private fun closeConnection(){
        socket!!.close()
    }

    fun closeReceiver() {
        run = false
    }

    fun openReceiver() {
        run = true
    }

    fun isConnected() : Boolean {
        return if(socket == null){
            false
        } else {
            socket?.isConnected!!
        }
    }

    fun clearData() {
        _serverOutput.value = null
        _socketData.value = null
    }
}