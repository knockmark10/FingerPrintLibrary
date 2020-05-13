package com.markoid.biometrics.callbacks

import androidx.biometric.BiometricPrompt
import com.markoid.biometrics.states.AuthReason

interface BiometricAuthenticationCallback {
    fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult)
    fun onAuthenticationFailed(reason: AuthReason)
}