package com.android.softfingerprint.callbacks

import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import com.android.softfingerprint.states.AuthenticationState

interface FingerPrintAuthenticationCallback {
    fun onAuthSucceeded(result: FingerprintManagerCompat.AuthenticationResult?)
    fun onAuthFailed(reason: AuthenticationState)
}