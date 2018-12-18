package com.everis.wifirtttest

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.rtt.RangingRequest
import android.net.wifi.rtt.RangingResult
import android.net.wifi.rtt.RangingResultCallback
import android.net.wifi.rtt.WifiRttManager
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.Toast
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val isCompatible = packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_RTT)

        if (isCompatible) {
            txtCompatibility.setText("Compatível")
            val wifiRttManager: WifiRttManager = getSystemService(Context.WIFI_RTT_RANGING_SERVICE) as WifiRttManager
            val myReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    if (wifiRttManager.isAvailable) {
                        txtWifiStatus.setText("Ativo")
                    } else {
                        txtWifiStatus.setText("Inativo")
                    }
                }
            }
            registerReceiver(myReceiver, IntentFilter(WifiRttManager.ACTION_WIFI_RTT_STATE_CHANGED))
        } else {
            txtCompatibility.setText("Incompativel")
        }

        wifiList.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_2)
    }

    override fun onResume() {
        super.onResume()
        checkPermissions()
    }

    fun checkPermissions() {
        if (!TedPermission.isGranted(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            val permissionlistener: PermissionListener = object : PermissionListener {
                override fun onPermissionGranted() {

                }

                override fun onPermissionDenied(deniedPermissions: List<String>) {
                    Toast.makeText(this@MainActivity, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show()
                }
            }
            TedPermission.with(this)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                    .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                    .check()
        }
    }

    @SuppressLint("MissingPermission")
    fun startRangingRequest(pointList: ArrayList<ScanResult>) {

        val request: RangingRequest = RangingRequest.Builder().run {
            for (item: ScanResult in pointList) {
                addAccessPoint(item)
            }
            build()
        }

        val mgr = getSystemService(Context.WIFI_RTT_RANGING_SERVICE) as WifiRttManager
        mgr.startRanging(request, AsyncTask.THREAD_POOL_EXECUTOR, object : RangingResultCallback() {
            override fun onRangingResults(results: List<RangingResult>) {
            }

            override fun onRangingFailure(code: Int) {
            }
        })
    }
}
