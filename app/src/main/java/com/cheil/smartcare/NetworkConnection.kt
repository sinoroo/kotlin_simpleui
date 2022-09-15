package com.cheil.smartcare

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest

class NetworkConnection(private val context: Context) : ConnectivityManager.NetworkCallback() {

    private val connectivityManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val networkRequest: NetworkRequest = NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .build()

    private val dialog: AlertDialog by lazy {
        AlertDialog.Builder(context)
            .setTitle("네트워크 연결 안 됨")
            .setMessage("와이파이 또는 모바일 데이터를 확인해 주세요")
            .create()
    }

    fun register(){
        connectivityManager.registerNetworkCallback(networkRequest, this)
    }

    fun unregister(){
        connectivityManager.unregisterNetworkCallback(this)
    }

    fun getConnectivityStatus(): Network?{
        return connectivityManager.activeNetwork
    }

    override fun onAvailable(network: Network) {
        super.onAvailable(network)

        if( getConnectivityStatus() == null) {
            dialog.show()
        } else {
            dialog.dismiss()
        }
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        dialog.show()
    }
}