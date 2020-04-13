package com.example.eazyremote.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.eazyremote.R
import com.example.eazyremote.data.local.sharedPref.SharedPrefData
import com.example.eazyremote.databinding.FragmentMainScreenBinding
import com.example.eazyremote.di.utils.Injectable
import com.example.eazyremote.di.utils.injectViewModel
import com.example.eazyremote.domain.utils.Constants
import com.example.eazyremote.domain.utils.Constants.BAD_CAR_DELAY
import com.example.eazyremote.domain.utils.Constants.CLOSE_DOORS
import com.example.eazyremote.domain.utils.Constants.CONNECTION_DELAY
import com.example.eazyremote.domain.utils.Constants.FAILED_TO_START_ACC_OFF
import com.example.eazyremote.domain.utils.Constants.FAILED_TO_START_AFTER_TIMES
import com.example.eazyremote.domain.utils.Constants.FAILED_TO_START_HAND_DOWN
import com.example.eazyremote.domain.utils.Constants.HERE_WE_GO
import com.example.eazyremote.domain.utils.Constants.HIDE_MESSAGE_DELAY
import com.example.eazyremote.domain.utils.Constants.KEY_START
import com.example.eazyremote.domain.utils.Constants.OPEN_DOORS
import com.example.eazyremote.domain.utils.Constants.PUMP
import com.example.eazyremote.domain.utils.Constants.START_CAR_DELAY
import com.example.eazyremote.domain.utils.Constants.START_THE_CAR
import com.example.eazyremote.domain.utils.Constants.WARMED
import com.example.eazyremote.domain.utils.Constants.WARMING_UP_DELAY
import com.example.eazyremote.domain.utils.Constants.canPressCarRun
import com.example.eazyremote.domain.utils.Constants.openConnection
import com.example.eazyremote.domain.viewmodels.DataViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import javax.inject.Inject


class MainScreenFragment : Fragment(), Injectable {

    @Inject
    lateinit var sharedPrefData: SharedPrefData

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var viewModel: DataViewModel

    private lateinit var binding : FragmentMainScreenBinding

    private var defend : Boolean = false
    private var startCar : Boolean = true

    lateinit var connectionHandler : Handler
    lateinit var badStartCarHandler : Handler
    lateinit var startTheCarHandler : Handler
    lateinit var warmingUpHandler : Handler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMainScreenBinding.inflate(inflater)

        initUI()
        initViewModel()
        initHandlers()
        initPrevValues()

        createObservers()
        handleButtons()

        return binding.root
    }

    private fun initHandlers() {
        connectionHandler = Handler()
        badStartCarHandler = Handler()
        startTheCarHandler = Handler()
        warmingUpHandler = Handler()
    }

    private fun handleSignal() {
        binding.bipBtn.setOnClickListener {
            viewModel.sendMessage(Constants.SIGNAL_BIP)
        }
    }

    private fun initPrevValues() {
        val values = sharedPrefData.getValues()
        if(!values.isNullOrEmpty()){
            setEngineState(values[1])
            setSecurity(values[2])
        }
    }

    private fun initUI() {
        if(arguments?.getBoolean("showUI")!!){
           showElements()
        }
    }

    private fun showElements() {
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility = View.VISIBLE
        activity?.findViewById<Toolbar>(R.id.main_title)?.visibility = View.VISIBLE
    }

    private fun handleButtons() {
        lockButton()
        defendedButton()
        unlockButton()
        undeffendButton()
        startCar()
        handleSignal()
    }

    private fun undeffendButton() {
        binding.undefendedBtn.setOnClickListener {
            setUnSecurityState(defend)
            saveValues(null,false,1)
            viewModel.sendMessage(Constants.SECURITY_CAR_OFF)
        }
    }

    private fun defendedButton() {
        binding.defendedBtn.setOnClickListener {
            setSecurityState(defend)
            saveValues(null,true,1)
            viewModel.sendMessage(Constants.SECURITY_CAR_ON)
        }
    }

    private fun unlockButton() {
        binding.unlockBtn.setOnClickListener {
            viewModel.sendMessage(OPEN_DOORS)
        }
    }

    private fun lockButton() {
        binding.lockBtn.setOnClickListener {
            viewModel.sendMessage(CLOSE_DOORS)
        }
    }

    private fun startCar() {
        binding.startTheCarBtn.setOnClickListener {
            if(startCar){
                if(canPressCarRun){
                    viewModel.sendMessage(START_THE_CAR)
                    binding.receiveMsg.visibility = View.VISIBLE
                    binding.receiveMsg.text = resources.getString(R.string.connect)

                    connectionHandler.postDelayed({
                        binding.receiveMsg.text = "Не удалось соединиться"
                        changeStartCarButton(R.color.whiteColor, R.color.backgroundColor, R.color.whiteColor)

                        connectionHandler.postDelayed({
                            binding.receiveMsg.visibility = View.INVISIBLE
                        }, HIDE_MESSAGE_DELAY)

                    }, CONNECTION_DELAY)

                } else {
                    viewModel.sendMessage(KEY_START)
                }
            }
        }
    }

    private fun initViewModel() {
        viewModel = injectViewModel(viewModelFactory)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun createObservers() {
        viewModel.property.observe(viewLifecycleOwner, Observer {
            it?.let {
                when {
                    it.startsWith("v") && it.length == 7 -> {
                        setEngineState(it[1])
                        setSecurity(it[2])
                        sharedPrefData.saveValues(it)
                        if (!openConnection) {
                            activity?.invalidateOptionsMenu()
                        }
                    }
                    it == PUMP -> {
                        pumpStarts()
                    }
                    it == WARMED -> {
                        hereWeGo()
                    }
                    it.startsWith("4") -> {
                        handleEngineError(it)
                    }
                    it.startsWith("n") -> {
                        engineWorks(it.drop(1))
                    }
                    it == HERE_WE_GO -> {
                        hereWeGo()
                    }
                }
                viewModel.clearProperty()
            }
        })
    }

    private fun setSecurity(security: Char) {
        if (security.toString() == "1") {
            setSecurityState(true)
        } else {
            setUnSecurityState(false)
        }
    }

    private fun pumpStarts() {
        //stop connection handler
        connectionHandler.removeCallbacksAndMessages(null)

        changeStartCarButton(R.color.whiteColor, R.color.backgroundColor, R.color.yellowColor)

        binding.receiveMsg.text = resources.getString(R.string.pump_ready)

        //We have to run the car after sucker
        startTheCarHandler.postDelayed( {
            binding.receiveMsg.text = resources.getString(R.string.run_the_car)
            saveValues('1',null,0)
        }, START_CAR_DELAY)

        //if we won't get n* times to run
        badStartCarHandler.postDelayed({
            binding.receiveMsg.text = "Не удалось завести"
            changeStartCarButton(R.color.whiteColor, R.color.backgroundColor, R.color.whiteColor)

            badStartCarHandler.postDelayed({
                binding.receiveMsg.visibility = View.INVISIBLE
            }, HIDE_MESSAGE_DELAY)

        }, BAD_CAR_DELAY)
    }

    private fun changeStartCarButton(iconColor : Int,backgroundColor: Int, strokeColor: Int){
        binding.startTheCarBtn.iconTint = ContextCompat.getColorStateList(requireContext(), iconColor)
        binding.startTheCarBtn.backgroundTintList = ContextCompat.getColorStateList(requireContext(), backgroundColor)
        binding.startTheCarBtn.strokeColor = ContextCompat.getColorStateList(requireContext(), strokeColor)
    }

    private fun hereWeGo() {
        warmingUpHandler.removeCallbacksAndMessages(null)

        binding.receiveMsg.text = resources.getString(R.string.here_we_go)
        changeStartCarButton(R.color.whiteColor, R.color.backgroundColor, R.color.whiteColor)
        binding.receiveMsg.visibility = View.INVISIBLE

        saveValues('0', null, 1)

        startCar = true
        canPressCarRun = true
    }

    @SuppressLint("SetTextI18n")
    private fun engineWorks(engine: String) {
        badStartCarHandler.removeCallbacksAndMessages(null)

        binding.receiveMsg.text = resources.getString(R.string.started_with_some_times) + " " + engine + " раза"

        changeStartCarButton(R.color.backgroundColor, R.color.whiteColor, R.color.yellowColor)

        val handler = Handler()
        handler.postDelayed({
            binding.receiveMsg.text = resources.getString(R.string.warming_up)
            saveValues('2',null,0)
        }, WARMING_UP_DELAY)

        startCar = true
        canPressCarRun = false
    }

    private fun handleEngineError(error: String) {
        when(error){
            FAILED_TO_START_ACC_OFF -> binding.receiveMsg.text = resources.getString(R.string.battery_is_low)
            FAILED_TO_START_HAND_DOWN -> binding.receiveMsg.text = resources.getString(R.string.handbrake_not_raised)
            FAILED_TO_START_AFTER_TIMES -> binding.receiveMsg.text = resources.getString(R.string.failed_to_run)
        }

        startTheCarHandler.removeCallbacksAndMessages(null)
        connectionHandler.removeCallbacksAndMessages(null)
        warmingUpHandler.removeCallbacksAndMessages(null)

        saveValues('0',null,0)

        val handlerError = Handler()
        handlerError.postDelayed({
            binding.receiveMsg.visibility = View.INVISIBLE
        }, HIDE_MESSAGE_DELAY)

        changeStartCarButton(R.color.whiteColor, R.color.backgroundColor, R.color.whiteColor)
        canPressCarRun = true
        startCar = true
    }

    private fun setSecurityState(isSecure: Boolean) {
        if(isSecure){
            binding.defendedBtn.iconTint = ContextCompat.getColorStateList(requireContext(), R.color.backgroundColor)
            binding.defendedBtn.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.whiteColor)
            binding.defendedBtn.strokeColor = ContextCompat.getColorStateList(requireContext(), R.color.yellowColor)

            binding.undefendedBtn.iconTint = ContextCompat.getColorStateList(requireContext(), R.color.whiteColor)
            binding.undefendedBtn.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.backgroundColor)
            binding.undefendedBtn.strokeColor = ContextCompat.getColorStateList(requireContext(), R.color.whiteColor)
            defend = false
        }
    }

    private fun setUnSecurityState(isSecure: Boolean){
        if(!isSecure){
            binding.defendedBtn.iconTint = ContextCompat.getColorStateList(requireContext(), R.color.whiteColor)
            binding.defendedBtn.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.backgroundColor)
            binding.defendedBtn.strokeColor = ContextCompat.getColorStateList(requireContext(), R.color.whiteColor)

            binding.undefendedBtn.iconTint = ContextCompat.getColorStateList(requireContext(), R.color.backgroundColor)
            binding.undefendedBtn.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.whiteColor)
            binding.undefendedBtn.strokeColor = ContextCompat.getColorStateList(requireContext(), R.color.yellowColor)
            defend = true
        }
    }

    private fun setEngineState(isRunning: Char) {
        when(isRunning.toString()){
            "1" -> {
                binding.receiveMsg.text = resources.getString(R.string.run_the_car)
                changeStartCarButton(R.color.whiteColor, R.color.backgroundColor, R.color.yellowColor)

                badStartCarHandler.postDelayed({
                    binding.receiveMsg.text = "Не удалось завести"
                    changeStartCarButton(R.color.whiteColor, R.color.backgroundColor, R.color.whiteColor)
                }, BAD_CAR_DELAY)

                canPressCarRun = true
            }
            "2" -> {
                binding.receiveMsg.text = resources.getString(R.string.warming_up)
                changeStartCarButton(R.color.backgroundColor, R.color.whiteColor, R.color.yellowColor)
                canPressCarRun = false
            }
        }
        binding.receiveMsg.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        viewModel.closeReceiver()
        super.onDestroy()
    }

    private fun saveValues(engine : Char?, checked: Boolean?, i: Int) {
        val values = sharedPrefData.getValues()
        if(!values.isNullOrEmpty()){
            val string = StringBuilder(values)
            if(checked != null){
                if(checked){
                    string.setCharAt(1 + i, '1')
                    sharedPrefData.saveValues(string.toString())
                } else {
                    string.setCharAt(1 + i, '0')
                    sharedPrefData.saveValues(string.toString())
                }
            } else {
                if(engine != null){
                    string.setCharAt(i, engine)
                    sharedPrefData.saveValues(string.toString())
                }
            }
        }
    }

}
