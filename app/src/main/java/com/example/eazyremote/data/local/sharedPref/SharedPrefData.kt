package com.example.eazyremote.data.local.sharedPref

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.eazyremote.domain.utils.Constants.APP_PREFERENCES
import javax.inject.Inject
import androidx.lifecycle.LiveData as LiveData


class SharedPrefData @Inject constructor(private val context: Context) {

    val pref : SharedPreferences by lazy {
        context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
    }

    private val _errors = MutableLiveData<String>()
    val errors : LiveData<String>
        get() = _errors

    fun saveValues(values : String) {
        val editor = pref.edit()
        editor.putString("values", values)
        Log.d("values saved", values)
        editor.apply()
    }

    fun saveError(errors: String) {
        val editor = pref.edit()
        editor.putString("errors", errors)
        editor.apply()
    }

    fun saveVoltage(voltage: String) {
        val editor = pref.edit()
        editor.putString("voltage", voltage)
        editor.apply()
    }

    fun saveCarTemperature(temperature: String) {
        val editor = pref.edit()
        editor.putString("car_temperature", temperature)
        editor.apply()
    }

    fun saveEngine(engine: String) {
        val editor = pref.edit()
        editor.putString("engine", engine)
        editor.apply()
    }

    fun saveLock(lock: String) {
        val editor = pref.edit()
        editor.putString("lock", lock)
        editor.apply()
    }

    fun saveLiquidTemperature(temperature: String) {
        val editor = pref.edit()
        editor.putString("liquid_temperature", temperature)
        editor.apply()
    }

    fun saveSecurity(security: String) {
        val editor = pref.edit()
        editor.putString("security", security)
        editor.apply()
    }

    fun getValues() : String? {
        return pref.getString("values", "v000000")
    }

    fun getLiquidTemperature(): String? {
        return pref.getString("liquid_temperature", "tvn0")
    }

    fun getCarTemperature(): String? {
        return pref.getString("car_temperature", "tok0")
    }

    fun getVoltage(): String? {
        return pref.getString("voltage", "vol1")
    }

    fun getLock(): String? {
        return pref.getString("lock", "d0")
    }

    fun getEngine(): String? {
        return pref.getString("engine", "n0")
    }

    fun getSecurity(): String? {
        return pref.getString("security", "o0")
    }

}