package com.example.packagename

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        // Other setup stuff like View and layout goes here ...

        lifecycleScope.launch(Dispatchers.IO) {     // Launches a coroutine in the IO dispatcher and gets GA_ID
            retrieveGAID()?.let { gaid ->
                launch(Dispatchers.Main) {
                    launchTWA(gaid)
                }
            }

        }

    }

    private fun retrieveGAID(): String? {   // To retrieve GA_ID using AdvertisingIdClient dependency
        return try {
            val adInfo = AdvertisingIdClient.getAdvertisingIdInfo(this)
            adInfo.id
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    private fun launchTWA(gaid: String) {
        val url = "https://domain.example?gps_adid=$gaid"  // Replace with your actual URL

        // Launch TWA with URL here ...
    }
}
