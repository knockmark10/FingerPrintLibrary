package com.android.softfingerprint.managers

import android.content.Context
import com.android.softfingerprint.callbacks.FingerPrintAuthenticationCallback
import com.android.softfingerprint.callbacks.FingerPrintHandlerCallback
import com.android.softfingerprint.states.BiometricState
import com.android.softfingerprint.states.FingerPrintState.*
import com.android.softfingerprint.utils.BiometricUtils

/**
 * This class will be used as a centralized manager to use fingerprint sensor for any version.
 */
class BiometricManager(private val mContext: Context) {

    private val mFingerPrintManager by lazy { FingerPrintManager(this.mContext) }

    private val mBiometricPromptManager by lazy { BiometricPromptManager(this.mContext) }

    private val mBiometricUtils by lazy {
        BiometricUtils(
            this.mContext
        )
    }

    private var mBasicListeners = mutableListOf<FingerPrintHandlerCallback>()

    fun registerBasicListener(listener: FingerPrintHandlerCallback) {
        this.mBasicListeners.add(listener)
    }

    fun registerAuthListener(listener: FingerPrintAuthenticationCallback) {
        when (this.mBiometricUtils.getDeviceCapabilities()) {
            BiometricState.BiometricPromptSupported ->
                this.mBiometricPromptManager.registerAuthListener(listener)
            BiometricState.FingerprintSupported ->
                this.mFingerPrintManager.registerAuthListener(listener)
            BiometricState.DeviceUnsupported ->
                throw UnsupportedOperationException("This operation is unsupported by your device.")
        }
    }

    fun clearListeners() {
        this.mBasicListeners.clear()
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
                        notifyBasicListener { it.onFingerPrintFailed(LockScreenSecurityDisabled) }
                    }
                } else {
                    //Fingerprint unavailable
                    notifyBasicListener { it.onFingerPrintFailed(NoFingerPrintRegistered) }
                }
            } else {
                //Permission not granted
                notifyBasicListener { it.onRequestPermission(this.getRequiredPermission()) }
            }
        } else {
            //Hardware not supported
            notifyBasicListener { it.onFingerPrintFailed(NoFingerPrintSensor) }
        }
    }

    fun startAuthProcess() {
        when (this.mBiometricUtils.getDeviceCapabilities()) {
            BiometricState.BiometricPromptSupported -> operateWithBiometrics()
            BiometricState.FingerprintSupported -> operateWithManager()
            BiometricState.DeviceUnsupported ->
                throw UnsupportedOperationException("This operation is unsupported by your device.")
        }
    }

    private fun operateWithBiometrics() {
        this.mBiometricPromptManager.displayBiometricPrompt()
    }

    private fun operateWithManager() {
        this.mFingerPrintManager.startAuthProcess()
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

//Reference: https://proandroiddev.com/5-steps-to-implement-biometric-authentication-in-android-dbeb825aeee8