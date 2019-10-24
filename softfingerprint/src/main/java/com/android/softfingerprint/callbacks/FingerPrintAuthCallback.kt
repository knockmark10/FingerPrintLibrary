package com.android.softfingerprint.callbacks

import android.hardware.fingerprint.FingerprintManager

interface FingerPrintAuthCallback {
    fun onAuthProcessFailed(error: Throwable)
    fun onAuthenticationError(errorCode: Int, errString: CharSequence?)
    fun onAuthenticationFailed()
    fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?)
    fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult?)
    fun onTooManyAttempts(errorCode: Int, errString: CharSequence?)
}