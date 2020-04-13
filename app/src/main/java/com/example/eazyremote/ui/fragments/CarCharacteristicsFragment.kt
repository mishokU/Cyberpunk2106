package com.example.eazyremote.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.eazyremote.data.local.sharedPref.SharedPrefData
import com.example.eazyremote.databinding.FragmentCarCharacteristicsBinding
import com.example.eazyremote.di.utils.Injectable
import com.example.eazyremote.di.utils.injectViewModel
import com.example.eazyremote.domain.utils.Constants
import com.example.eazyremote.domain.utils.Constants.DIMMER_OFF
import com.example.eazyremote.domain.utils.Constants.DIMMER_ON
import com.example.eazyremote.domain.utils.Constants.SIGNAL_BIP
import com.example.eazyremote.domain.utils.Constants.STOVE_OFF
import com.example.eazyremote.domain.utils.Constants.STOVE_ON
import com.example.eazyremote.domain.utils.Constants.highBeamHeadlightsOff
import com.example.eazyremote.domain.utils.Constants.highBeamHeadlightsOn
import com.example.eazyremote.domain.utils.Constants.turnSignalsOff
import com.example.eazyremote.domain.utils.Constants.turnSignalsOn
import com.example.eazyremote.domain.viewmodels.DataViewModel
import javax.inject.Inject

class CarCharacteristicsFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var viewModel: DataViewModel

    private lateinit var binding : FragmentCarCharacteristicsBinding

    @Inject
    lateinit var sharedPrefData: SharedPrefData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCarCharacteristicsBinding.inflate(inflater)

        viewModel = injectViewModel(viewModelFactory)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        initPrevValues()
        observeData()

        handleDippedBeam()
        handleHighBeam()
        handleTurnSignals()
        handleStove()
        handleSignal()

        return binding.root
    }

    private fun initPrevValues() {
        val element = sharedPrefData.getValues()
        val elements = element?.takeLast(4)
        if(!elements.isNullOrEmpty()){
            binding.dippedBeamSc.isChecked = elements[0].toString() == "1"
            binding.highBeamSc.isChecked = elements[1].toString() == "1"
            binding.stoveSc.isChecked = elements[2].toString() == "1"
            binding.turnSignalsSc.isChecked = elements[3].toString() == "1"
        }
    }

    private fun observeData() {
        viewModel.property.observe(viewLifecycleOwner, Observer {
            it?.let {
                if(it.startsWith("v") && it.length == 7){
                    val elements = it.takeLast(4)
                    binding.dippedBeamSc.isChecked = elements[0].toString() == "1"
                    binding.highBeamSc.isChecked = elements[1].toString() == "1"
                    binding.stoveSc.isChecked = elements[2].toString() == "1"
                    binding.turnSignalsSc.isChecked = elements[3].toString() == "1"
                    sharedPrefData.saveValues(it)

                    if (!Constants.openConnection) {
                        activity?.invalidateOptionsMenu()
                    }

                    viewModel.clearProperty()
                }
            }
        })
    }

    private fun handleSignal() {
        binding.bipBtn.setOnClickListener {
            viewModel.sendMessage(SIGNAL_BIP)
        }
    }

    private fun handleStove() {
        binding.stoveSc.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                viewModel.sendMessage(STOVE_ON)
                saveValues(isChecked, 2)
            } else {
                viewModel.sendMessage(STOVE_OFF)
                saveValues(isChecked, 2)
            }
        }
    }

    private fun saveValues(checked: Boolean, i: Int) {
        val values = sharedPrefData.getValues()
        if(!values.isNullOrEmpty()){
            val string = StringBuilder(values)
            if(checked){
                string.setCharAt(3 + i, '1')
                sharedPrefData.saveValues(string.toString())
                sharedPrefData.pref.edit().apply()
            } else {
                string.setCharAt(3 + i, '0')
                sharedPrefData.saveValues(string.toString())
            }
        }
    }

    private fun handleTurnSignals() {
        binding.turnSignalsSc.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                viewModel.sendMessage(turnSignalsOn)
                saveValues(isChecked, 3)
            } else {
                viewModel.sendMessage(turnSignalsOff)
                saveValues(isChecked, 3)
            }
        }
    }

    private fun handleHighBeam() {
        binding.highBeamSc.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                viewModel.sendMessage(highBeamHeadlightsOn)
                saveValues(isChecked,1)
            } else {
                viewModel.sendMessage(highBeamHeadlightsOff)
                saveValues(isChecked, 1)
            }
        }
    }

    private fun handleDippedBeam() {
        binding.dippedBeamSc.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                viewModel.sendMessage(DIMMER_ON)
                saveValues(isChecked,0)
            } else {
                viewModel.sendMessage(DIMMER_OFF)
                saveValues(isChecked,0)
            }
        }
    }

}