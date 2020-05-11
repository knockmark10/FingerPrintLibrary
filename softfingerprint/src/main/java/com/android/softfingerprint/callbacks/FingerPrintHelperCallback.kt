package com.android.softfingerprint.callbacks

import android.support.v4.hardware.fingerprint.FingerprintManagerCompat

interface FingerPrintHelperCallback {
    fun onAuthenticationError(errorCode: Int, errString: CharSequence?)
    fun onAuthenticationFailed()
    fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?)
    fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult?)
    fun onTooManyAttempts(errorCode: Int, errString: CharSequence?)
}