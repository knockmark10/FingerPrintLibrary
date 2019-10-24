package com.android.softfingerprint.callbacks

import android.hardware.fingerprint.FingerprintManager

interface FingerPrintHelperCallback {
    fun onAuthenticationError(errorCode: Int, errString: CharSequence?)
    fun onAuthenticationFailed()
    fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?)
    fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult?)
    fun onTooManyAttempts(errorCode: Int, errString: CharSequence?)
}