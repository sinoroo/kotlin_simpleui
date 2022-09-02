package com.cheil.smartcare

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.admin.DevicePolicyManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cheil.smartcare.adapters.LoginAttemptsDbHelper
import com.cheil.smartcare.adapters.LoginAtteptAdapter
import com.cheil.smartcare.receivers.KioskDeviceAdminReceiver
import com.google.android.material.floatingactionbutton.FloatingActionButton


class BleDeviceScanActivity : AppCompatActivity() {

    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val BluetoothAdapter.isDisabled: Boolean
        get() = !isEnabled

    private var scanning: Boolean = false
    private var devicesArr = ArrayList<BluetoothDevice>()

    private val REQUEST_ENABLE_BT=1
    private val REQUEST_ALL_PERMISSION= 2
    private val SCAN_PERIOD = 10000L
    private val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private val SERVICE_STRING = "6E400001-B5A3-F393-E0A9-E50E24DCCA9E"
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var recyclerViewAdapter : RecyclerViewAdapter

    private val handler = Handler()
    private val bLeScanCallback = @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    object : ScanCallback() {
        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.d("scanCallback", "BLE Scan Failed : " + errorCode)
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
            results?.let {
                // results is not null
                for (result in it) {
                    if (!devicesArr.contains(result.device) && result.device.name != null) {
                        devicesArr.add(result.device)
                        Log.d("scanCallback", "BLE Scan Result : " + result.device.name)
                    }
                }

            }
        }

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            result?.let {
                //// result is not null
                if (!devicesArr.contains(it.device) && it.device.name!=null)
                    devicesArr.add(it.device)
                recyclerViewAdapter.notifyDataSetChanged()
                Log.d("scanCallback", "BLE Scan Result : " + result.device.name)
            }
        }

    }

    private fun hasPermissions(context: Context?, permissions: Array<String>): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun scanDevice(state:Boolean) = if(state){
        handler.postDelayed({
            scanning = false
            bluetoothAdapter?.bluetoothLeScanner?.stopScan(bLeScanCallback)
        }, SCAN_PERIOD)
        scanning = true
        devicesArr.clear()
        /*
        //scan filter
        val filters: MutableList<ScanFilter> = ArrayList()
        val scanFilter: ScanFilter = ScanFilter.Builder()
            .setServiceUuid(ParcelUuid(UUID.fromString(SERVICE_STRING)))
            .build()

        filters.add(scanFilter)

        // scan settings
        // set low power scan mode
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
            .build()
        bluetoothAdapter?.bluetoothLeScanner?.startScan(filters, settings, bLeScanCallback)
        */

        bluetoothAdapter?.bluetoothLeScanner?.startScan(bLeScanCallback)


    }else{
        scanning = false
        bluetoothAdapter?.bluetoothLeScanner?.stopScan(bLeScanCallback)
    }


    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ble_device_scan)

        val bleOnOffBtn:ToggleButton = findViewById(R.id.toggleButton)
        val btScanBtn: Button = findViewById(R.id.bt_scan_btn)
        viewManager = LinearLayoutManager(this)
        recyclerViewAdapter =  RecyclerViewAdapter(devicesArr)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = viewManager
            adapter = recyclerViewAdapter
        }


        if(bluetoothAdapter!=null){
            if(bluetoothAdapter?.isEnabled==false){
                bleOnOffBtn.isChecked = true
                btScanBtn.isVisible = false
            } else{
                bleOnOffBtn.isChecked = false
                btScanBtn.isVisible = true
            }
        }

        bleOnOffBtn.setOnCheckedChangeListener { _, isChecked ->
            bluetoothOnOff()
            btScanBtn.visibility = if (btScanBtn.visibility == View.VISIBLE){ View.INVISIBLE } else{ View.VISIBLE }
        }

        btScanBtn.setOnClickListener {  v: View? -> // Scan Button Onclick
            if (!hasPermissions(this, PERMISSIONS)) {
                requestPermissions(PERMISSIONS, REQUEST_ALL_PERMISSION)
            }
            scanDevice(true)
        }


        if( !packageManager.hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            //finish()
            Toast.makeText(this, "This device Not support BLE", Toast.LENGTH_SHORT).show()
        }
        /*
        bluetoothAdapter?.takeIf{ it.isDisabled}?.apply{
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
         */
        supportActionBar?.title = "데이터 허브"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // Permission check
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_ALL_PERMISSION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permissions granted!", Toast.LENGTH_SHORT).show()
                } else {
                    requestPermissions(permissions, REQUEST_ALL_PERMISSION)
                    Toast.makeText(this, "Permissions must be granted", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    fun bluetoothOnOff(){
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Log.d("bluetoothAdapter","Device doesn't support Bluetooth")
        }else{
            if (bluetoothAdapter?.isEnabled == false) { // 블루투스 꺼져 있으면 블루투스 활성화
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            } else{ // 블루투스 켜져있으면 블루투스 비활성화
                bluetoothAdapter?.disable()
            }
        }
    }

    class RecyclerViewAdapter(private val myDataset: ArrayList<BluetoothDevice>):
        RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder > () {
        class MyViewHolder(val linearView: LinearLayout):RecyclerView.ViewHolder(linearView)
        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int):RecyclerViewAdapter.MyViewHolder {
            // create a new view
            val linearView = LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerview_item, parent, false) as LinearLayout
            return MyViewHolder(linearView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val itemName: TextView = holder.linearView.findViewById(R.id.item_name)
            val itemAddress: TextView = holder.linearView.findViewById(R.id.item_address)
            itemName.text = myDataset[position].name
            itemAddress.text = myDataset[position].address
        }
        override fun getItemCount() = myDataset.size
    }
}
