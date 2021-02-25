package com.owsky.sushihubredone.ui.view

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.owsky.sushihubredone.R
import com.owsky.sushihubredone.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : NavigationView.OnNavigationItemSelectedListener, AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawer: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        // Navigation Component setup
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        drawer = binding.drawerLayout
        appBarConfiguration = AppBarConfiguration(setOf(R.id.homePageNav, R.id.tablePage, R.id.historyNav), drawer)
        toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.navigationView.setNavigationItemSelectedListener(this)

        checkPermission()
        checkBluetooth()
        checkWifi()
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val prefs = getSharedPreferences("SushiHub_Redone", Context.MODE_PRIVATE)
        when (item.itemId) {
            R.id.listsNav -> {
                if (prefs.contains("table_code")) {
                    navController.navigate(R.id.tablePage)
                } else {
                    navController.navigate(R.id.homePageNav)
                }
                drawer.closeDrawer(GravityCompat.START)
                return true
            }
            R.id.historyNav -> {
                navController.navigate(R.id.historyNav)
                drawer.closeDrawer(GravityCompat.START)
                return true
            }
        }
        return false
    }

    private fun checkPermission() {
        val requestLocationLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}
        if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            requestLocationLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun checkBluetooth() {
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        val requestBluetoothLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (!bluetoothAdapter?.isEnabled!!) {
                AlertDialog.Builder(this)
                    .setMessage("SushiHub requires Bluetooth to work. Do you want to enable it?")
                    .setPositiveButton("OK") { _, _ -> bluetoothAdapter.enable() }
                    .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                    .create().show()
            }
        }
        if (!bluetoothAdapter?.isEnabled!!)
            requestBluetoothLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
    }

    private fun checkWifi() {
        val requestWifiLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (!wifiManager.isWifiEnabled) requestWifiLauncher.launch(Intent(Settings.Panel.ACTION_WIFI))
    }
}