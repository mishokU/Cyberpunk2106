package com.example.eazyremote.ui.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.eazyremote.R
import com.example.eazyremote.data.local.sharedPref.SharedPrefData
import com.example.eazyremote.databinding.FragmentCarSettingsBinding
import com.example.eazyremote.di.utils.Injectable
import com.example.eazyremote.di.utils.injectViewModel
import com.example.eazyremote.domain.utils.Constants.SETTINGS_CALL
import com.example.eazyremote.domain.viewmodels.DataViewModel
import javax.inject.Inject


class CarSettingsFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var viewModel: DataViewModel

    @Inject
    lateinit var sharedPrefData: SharedPrefData

    private lateinit var binding : FragmentCarSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCarSettingsBinding.inflate(inflater)

        viewModel = injectViewModel(viewModelFactory)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        getPrevSettings()

        createObservers()
        updateDataButton()
        handleJokeButton()

        return binding.root
    }

    private fun handleJokeButton() {
        binding.rideTypeBtn.setOnClickListener {
            onCreateDialog()
        }
    }

    @SuppressLint("InflateParams")
    private fun onCreateDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.joke_layout, null)
        builder.setView(dialogView)
        builder.create()
        val alert = builder.show()
        val okeyButton = dialogView.findViewById<View>(R.id.okey_btn)
        okeyButton.setOnClickListener {
            alert.dismiss()
        }
    }

    private fun getPrevSettings() {
        val tok = sharedPrefData.getLiquidTemperature()
        if(!tok.isNullOrEmpty()){
            setLiquidTemperature(tok.drop(3))
        }
        val tnv = sharedPrefData.getCarTemperature()
        if(!tnv.isNullOrEmpty()){
            setCarTemperature(tnv.drop(3))
        }
        val vol = sharedPrefData.getVoltage()
        if(!vol.isNullOrEmpty()){
            setCarVoltage(vol.drop(3))
        }
        val doors = sharedPrefData.getLock()
        if(!doors.isNullOrEmpty()){
            setLockState(doors.drop(1) == "1")
        }
        val engine = sharedPrefData.getEngine()
        if(!engine.isNullOrEmpty()){
            setEngineState(engine.drop(1) == "1")
        }
        val handle = sharedPrefData.getSecurity()
        if(!handle.isNullOrEmpty()){
            setSecurityState(handle.drop(1) == "1")
        }
    }

    private fun updateDataButton() {
        binding.updateDataBtn.setOnClickListener {
            viewModel.sendMessage(SETTINGS_CALL)
        }
    }

    private fun createObservers() {
        viewModel.property.observe(viewLifecycleOwner, Observer {
            it?.let {
                when {
                    it.startsWith("tok") -> {
                        sharedPrefData.saveLiquidTemperature(it)
                        setLiquidTemperature(it.drop(3))
                    }
                    it.startsWith("vol") -> {
                        sharedPrefData.saveVoltage(it)
                        setCarVoltage(it.drop(3))
                    }
                    it.startsWith("tvn") -> {
                        sharedPrefData.saveCarTemperature(it)
                        setCarTemperature(it.drop(3))
                    }
                    it.startsWith("z") -> {
                        sharedPrefData.saveEngine(it)
                        setEngineState(it.drop(1) == "1")
                    }
                    it.startsWith("d") -> {
                        sharedPrefData.saveLock(it)
                        setLockState(it.drop(1) == "1")
                    }
                    it.startsWith("r") -> {
                        sharedPrefData.saveSecurity(it)
                        setSecurityState(it.drop(1) == "1")
                    }
                }
              }
        })
    }

    private fun setCarTemperature(temperature: String) {
        binding.tempSalonTv.text = temperature
    }

    private fun setLiquidTemperature(temperature: String) {
        binding.tempLiquidTv.text = temperature
    }

    private fun setCarVoltage(voltage: String) {
        binding.voltageTv.text = (voltage.toDouble() / 10.0).toString()
    }

    private fun setSecurityState(isSecure: Boolean) {
        if(isSecure){
            binding.defenceTv.text = "Включен"
        } else {
            binding.defenceTv.text = "Выключен"
        }
    }

    private fun setLockState(isLocked: Boolean) {
        if(isLocked) {
            binding.doorsTv.text = "Открыты"
        }  else {
            binding.doorsTv.text = "Закрыты"
        }
    }

    private fun setEngineState(isRunning: Boolean) {
        if(isRunning){
            binding.engineTv.text = "Заведен"
        } else {
            binding.engineTv.text = "Не заведен"
        }
    }

}
