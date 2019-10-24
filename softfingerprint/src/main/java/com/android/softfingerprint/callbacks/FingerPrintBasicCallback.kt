package com.android.softfingerprint.callbacks

interface FingerPrintBasicCallback {
    fun onFingerPrintReady()
    fun onFingerPrintNotAvailable()
    fun onNoFingerPrintSensor() {}
    fun onFingerPrintPermissionDenied() {}
    fun onNoFingerPrintRegistered() {}
    fun onLockScreenSecurityDisabled() {}
}