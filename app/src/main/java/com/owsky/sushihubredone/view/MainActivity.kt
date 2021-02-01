package com.owsky.sushihubredone.view

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
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.navigation.NavigationView
import com.owsky.sushihubredone.R

class MainActivity : NavigationView.OnNavigationItemSelectedListener, AppCompatActivity() {
	private lateinit var navController: NavController
	private lateinit var appBarConfiguration: AppBarConfiguration
	private lateinit var drawer: DrawerLayout

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		// Navigation Component setup
		val navHostFragment =
			supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
		navController = navHostFragment.navController
		drawer = findViewById(R.id.drawer_layout)
		appBarConfiguration =
			AppBarConfiguration(hashSetOf(R.id.homePageNav, R.id.listsNav, R.id.historyNav), drawer)
		setupActionBarWithNavController(navController, appBarConfiguration)
		findViewById<NavigationView>(R.id.navigation_view).setNavigationItemSelectedListener(this)

		checkPermissions()
		checkBluetooth()
		checkWifi()
	}

	override fun onSupportNavigateUp(): Boolean {
//		when(navController.currentDestination?.id) {
//			R.id.userInputNav -> {
//				CancelDialog().show(supportFragmentManager, null)
//				return true
//			}
//		}
		return NavigationUI.navigateUp(
			navController,
			appBarConfiguration
		) || super.onSupportNavigateUp()
	}

	override fun onBackPressed() {
		if (drawer.isDrawerOpen(GravityCompat.START))
			drawer.closeDrawer(GravityCompat.START)
		else
			super.onBackPressed()
	}

	override fun onNavigationItemSelected(item: MenuItem): Boolean {
		val prefs = getPreferences(Context.MODE_PRIVATE)
		when (item.itemId) {
			R.id.listsNav -> {
				if (prefs.contains("table_code")) {
					navController.navigate(R.id.listsNav)
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

	private fun checkPermissions() {
		val requestPermissionsLauncher =
			registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}
		val requiredPermissions = listOf(
			Manifest.permission.ACCESS_FINE_LOCATION,
			Manifest.permission.ACCESS_COARSE_LOCATION
		)
		val permissionsToRequest = mutableListOf<String>()
		requiredPermissions.forEach {
			if (ActivityCompat.checkSelfPermission(
					applicationContext,
					it
				) != PackageManager.PERMISSION_GRANTED
			) {
				permissionsToRequest.add(it)
			}
		}
		if (permissionsToRequest.isNotEmpty()) {
			requestPermissionsLauncher.launch(requiredPermissions.toTypedArray())
		}
	}

	private fun checkBluetooth() {
		val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
		val requestBluetoothLauncher =
			registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
				if (!bluetoothAdapter?.isEnabled!!) {
					val dialogBuilder = AlertDialog.Builder(this)
					dialogBuilder
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
		val requestWifiLauncher =
			registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
		val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
		if (!wifiManager.isWifiEnabled)
			requestWifiLauncher.launch(Intent(Settings.Panel.ACTION_WIFI))
	}
}