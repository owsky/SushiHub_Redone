package com.owsky.sushihubredone

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SushiHubApp : Application()

const val prefsIdentifier = "sushiHub_redone"
const val prefsUsername = "username"
const val prefsIsMaster = "is_master"
const val prefsTableCode = "table_code"