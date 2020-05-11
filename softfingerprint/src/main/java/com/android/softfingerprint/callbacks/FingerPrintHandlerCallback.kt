package com.android.softfingerprint.callbacks

import com.android.softfingerprint.states.FingerPrintState

interface FingerPrintHandlerCallback {
    fun onFingerPrintSucceeded()
    fun onFingerPrintFailed(reason: FingerPrintState)
    fun onRequestPermission(permission: String)
}