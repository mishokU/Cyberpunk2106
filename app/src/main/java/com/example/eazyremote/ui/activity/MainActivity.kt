package com.example.eazyremote.ui.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.example.eazyremote.R
import com.example.eazyremote.databinding.ActivityMainBinding
import com.example.eazyremote.di.utils.injectViewModel
import com.example.eazyremote.domain.utils.Constants.ENTER_KEY
import com.example.eazyremote.domain.utils.Constants.SERVER_IP
import com.example.eazyremote.domain.utils.Constants.SERVER_PORT
import com.example.eazyremote.domain.utils.Constants.openConnection
import com.example.eazyremote.domain.viewmodels.DataViewModel
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject


class MainActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFac : ViewModelProvider.Factory
    lateinit var viewModelReceiver : DataViewModel

    private lateinit var binding : ActivityMainBinding
    private lateinit var navigation : NavController

    var networkStatus : String ?= null
    var menu : Menu ?= null

    override fun supportFragmentInjector() = dispatchingAndroidInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        viewModelReceiver = injectViewModel(viewModelFac)

        setUpToolbar()
        setUpBottomNavigationBar()
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.mainTitle)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(openConnection){
            menuInflater.inflate(R.menu.disconnect_menu, menu)
        } else {
            menuInflater.inflate(R.menu.reconnect_menu, menu)
        }
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun setUpBottomNavigationBar() {

        navigation = Navigation.findNavController(this,R.id.myNavHostFragment)

        binding.bottomNavigationView.selectedItemId = R.id.home_fragment
        binding.bottomNavigationView.setupWithNavController(navigation)

        binding.bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.switcher_fragment -> {
                    navigation.navigate(R.id.carCharacteristicsFragment)
                    true
                }
                R.id.settings_fragment -> {
                    navigation.navigate(R.id.carSettingsFragment)
                    true
                }
                R.id.home_fragment -> {
                    navigation.navigate(R.id.mainScreenFragment, bundleOf("showUI" to false))
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val intent = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkStateBroadcast, intent)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(networkStateBroadcast)
    }

    @Suppress("DEPRECATION")
    private val networkStateBroadcast = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            val connMgr = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connMgr.activeNetworkInfo

            if (networkInfo != null) {
                if (networkInfo.type == ConnectivityManager.TYPE_WIFI) {
                    networkStatus = "Network: Wifi"
                    supportActionBar?.title = networkStatus
                } else if (networkInfo.type == ConnectivityManager.TYPE_MOBILE) {
                    networkStatus = "Network: Mobile"
                    supportActionBar?.title = networkStatus
                }
                checkServerConnection()
            } else {
                networkStatus = "Network: Error"
                supportActionBar?.title = networkStatus
                openConnection = true
                invalidateOptionsMenu()
            }
        }
    }

    private fun checkServerConnection() {
        if(openConnection){
            viewModelReceiver.handleConnection(SERVER_IP, SERVER_PORT)
            val handler = Handler()
            handler.postDelayed({
                openConnection = if(viewModelReceiver.isConnected()){
                    viewModelReceiver.openReceiver()
                    viewModelReceiver.sendMessage(ENTER_KEY)
                    viewModelReceiver.readData()
                    false
                } else {
                    true
                }
            }, 1000)
        }
    }
}
