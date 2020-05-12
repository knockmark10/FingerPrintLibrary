package com.android.softfingerprint.managers

import android.content.Context
import com.android.softfingerprint.callbacks.FingerPrintHandlerCallback
import com.android.softfingerprint.states.FingerPrintState
import com.android.softfingerprint.utils.BiometricUtils

class BiometricValidations(private val mContext: Context) {

    private val mBiometricUtils by lazy { BiometricUtils(this.mContext) }

    private val mBasicListeners = mutableListOf<FingerPrintHandlerCallback>()

    fun registerBasicListener(listener: FingerPrintHandlerCallback) {
        this.mBasicListeners.add(listener)
    }

    fun validateFingerPrint(): Unit = with(this.mBiometricUtils) {
        checkBasicListener()
        if (this.isHardWareSupported()) {
            if (this.isPermissionGranted()) {
                if (this.isFingerprintAvailable()) {
                    if (this.isKeyGuardSecure()) {
                        //Fingerprint validation succeeded
                        notifyBasicListener { it.onFingerPrintSucceeded() }
                    } else {
                        //Fingerprint not secured
                        notifyBasicListener { it.onFingerPrintFailed(FingerPrintState.LockScreenSecurityDisabled) }
                    }
                } else {
                    //Fingerprint unavailable
                    notifyBasicListener { it.onFingerPrintFailed(FingerPrintState.NoFingerPrintRegistered) }
                }
            } else {
                //Permission not granted
                notifyBasicListener { it.onRequestPermission(this.getRequiredPermission()) }
            }
        } else {
            //Hardware not supported
            notifyBasicListener { it.onFingerPrintFailed(FingerPrintState.NoFingerPrintSensor) }
        }
    }

    private fun checkBasicListener() {
        if (this.mBasicListeners.isEmpty()) {
            throw NullPointerException("FingerPrintBasicCallback interface required. You need to register it..")
        }
    }

    private fun notifyBasicListener(output: (callback: FingerPrintHandlerCallback) -> Unit) {
        this.mBasicListeners.forEach { output(it) }
    }

}