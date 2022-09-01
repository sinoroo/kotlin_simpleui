package com.cheil.smartcare.models


class LoginAttemptModel(
    var id : Long,
    var success : Int,
    var attemptTime : String) {

    companion object{
        const val SUCCESS : Int = 1
        const val FAILED : Int = 0
    }
}