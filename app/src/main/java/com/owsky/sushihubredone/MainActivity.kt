package com.owsky.sushihubredone

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.navigation.NavigationView

class MainActivity : NavigationView.OnNavigationItemSelectedListener, AppCompatActivity() {
	private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
	private val requestBluetoothLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
		if (!bluetoothAdapter?.isEnabled!!) {
			val dialogBuilder = AlertDialog.Builder(this)
			dialogBuilder
				.setMessage("SushiHub requires Bluetooth to work. Do you want to enable it?")
				.setPositiveButton("OK") { _, _ -> bluetoothAdapter.enable() }
				.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
				.create().show()
		}
	}

	private val requestWifiLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
	}

	private lateinit var navController: NavController
	private lateinit var appBarConfiguration: AppBarConfiguration
	private lateinit var drawer: DrawerLayout

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		// Navigation Component setup
		navController = findNavController(R.id.nav_host_fragment)
		drawer = findViewById(R.id.drawer_layout)
		val navigationView = findViewById<NavigationView>(R.id.navigation_view)
		val topLevelDestinations = hashSetOf(R.id.homepageNav, R.id.listsNav, R.id.historyNav)
		appBarConfiguration = AppBarConfiguration(topLevelDestinations, drawer)
		setupActionBarWithNavController(navController, appBarConfiguration)
		navigationView.setNavigationItemSelectedListener(this)

		requestPermissions()
		if (!bluetoothAdapter?.isEnabled!!)
			requestBluetoothLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
		val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
		if (!wifiManager.isWifiEnabled)
			requestWifiLauncher.launch(Intent(Settings.Panel.ACTION_WIFI))
	}

	override fun onSupportNavigateUp(): Boolean {
		when(navController.currentDestination?.id) {
			R.id.userInputNav -> {
				CancelDialog().show(supportFragmentManager, null)
				return true
			}
		}
		return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
	}

	override fun onBackPressed() {
		if (drawer.isDrawerOpen(GravityCompat.START))
			drawer.closeDrawer(GravityCompat.START)
		else
			super.onBackPressed()
	}

	override fun onNavigationItemSelected(item: MenuItem): Boolean {
		val prefs = getPreferences(Context.MODE_PRIVATE)
		when(item.itemId) {
			R.id.listsNav -> {
				if (prefs.contains("table_code")) {
					navController.navigate(R.id.listsNav)
				} else {
					navController.navigate(R.id.homepageNav)
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

	private fun requestPermissions() {
		val requiredPermissions = listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
		val permissionsToRequest = mutableListOf<String>()
		requiredPermissions.forEach {
			if (ActivityCompat.checkSelfPermission(applicationContext, it) != PackageManager.PERMISSION_GRANTED) {
				permissionsToRequest.add(it)
			}
		}

		if (permissionsToRequest.isNotEmpty()) {
			ActivityCompat.requestPermissions(this, requiredPermissions.toTypedArray(), 0)
		}
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		if (requestCode == 0) {
			grantResults.forEach {
				if (it == PackageManager.PERMISSION_DENIED)
					Toast.makeText(this, "Permission $it required", Toast.LENGTH_LONG).show()
			}
		}
	}
}