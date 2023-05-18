package com.example.cybsapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.provider.CallLog
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.cybsapp.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private var binding : ActivityMainBinding ?= null
    private var swipeLayout : SwipeRefreshLayout ?= null
    private var callLogAdapter : CallLogAdapter ?= null
    private lateinit var list : ArrayList<CallLogmodel>
    private val appPermissions = arrayOf(
        Manifest.permission.READ_CALL_LOG
    )
    private val permissionCode = 999

    private var flag = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        supportActionBar?.title = "Call Logs"
        swipeLayout = binding?.swipeFreshLayout
        list = ArrayList()
        if (checkAndRequestPermission()) {
            fetchCallLogs()
        }
        swipeLayout?.setOnRefreshListener {
            // Check for permission
            if (checkAndRequestPermission()) {
                fetchCallLogs()
            }
            swipeLayout?.isRefreshing = false
        }
        setUpView()
    }
    private fun setUpView(){
        binding?.mainRv?.layoutManager = LinearLayoutManager(this)
        callLogAdapter = CallLogAdapter(this,this, list)
        binding?.mainRv?.adapter = callLogAdapter
    }
    private fun fetchCallLogs() {
        val sortOrder = CallLog.Calls.DATE + " DESC"

        val cursor: Cursor? = this.contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            null,
            null,
            null,
            sortOrder
        )
        if(list.isNotEmpty()) {
            list.clear()
        }
        cursor?.use { c ->
            while (c.moveToNext()) {
                val strNumber: String = c.getString(c.getColumnIndexOrThrow(CallLog.Calls.NUMBER))
                var strContactName: String? = c.getString(c.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME))
                strContactName = if (strContactName.isNullOrEmpty()) "Unknown" else strContactName
                var strCallType: String = c.getString(c.getColumnIndexOrThrow(CallLog.Calls.TYPE))
                val strCallFullDate: String = c.getString(c.getColumnIndexOrThrow(CallLog.Calls.DATE))
                val strCallDuration: String = c.getString(c.getColumnIndexOrThrow(CallLog.Calls.DURATION))

                val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.US)
                val strCallDate = dateFormatter.format(Date(strCallFullDate.toLong()))

                val timeFormatter = SimpleDateFormat("HH:mm:ss", Locale.US)
                val strCallTime = timeFormatter.format(Date(strCallFullDate.toLong()))
                val formattedDuration = durationFormat(strCallDuration)

                strCallType = when (strCallType.toInt()) {
                    CallLog.Calls.INCOMING_TYPE -> "Incoming"
                    CallLog.Calls.OUTGOING_TYPE -> "Outgoing"
                    CallLog.Calls.MISSED_TYPE -> "Missed"
                    CallLog.Calls.VOICEMAIL_TYPE -> "Voicemail"
                    CallLog.Calls.REJECTED_TYPE -> "Rejected"
                    CallLog.Calls.BLOCKED_TYPE -> "Blocked"
                    CallLog.Calls.ANSWERED_EXTERNALLY_TYPE -> "Externally Answered"
                    else -> "NA"
                }

                val callLogItem = CallLogmodel(
                    strNumber,
                    strContactName,
                    strCallType,
                    strCallDate,
                    strCallTime,
                    formattedDuration
                )

                list.add(callLogItem)
                if(list.size>25){
                    break
                }
            }
        }
        callLogAdapter?.notifyDataSetChanged()


    }
    private fun checkAndRequestPermission(): Boolean {
        // Checking which permissions are granted
        val listPermissionNeeded = mutableListOf<String>()
        for (item in appPermissions) {
            if (ContextCompat.checkSelfPermission(this, item) != PackageManager.PERMISSION_GRANTED) {
                listPermissionNeeded.add(item)
            }
        }

        // Ask for non-granted permissions
        if (listPermissionNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionNeeded.toTypedArray(),
                permissionCode
            )
            return false
        }

        // App has all permissions. Proceed ahead
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionCode) {
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    flag = 1
                    break
                }
            }
            if (flag == 0) {
                fetchCallLogs()
            }
        }
    }

    private fun durationFormat(duration: String): String {
        val durationFormatted: String = if (duration.toInt() < 60) {
            "$duration sec"
        } else {
            val min = duration.toInt() / 60
            val sec = duration.toInt() % 60

            if (sec == 0)
                "$min min"
            else
                "$min min $sec sec"
        }
        return durationFormatted
    }




    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}