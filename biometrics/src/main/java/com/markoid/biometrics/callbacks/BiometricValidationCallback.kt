package com.markoid.biometrics.callbacks

import com.markoid.biometrics.states.BiometricState

interface BiometricValidationCallback {
    fun onBiometricResult(state: BiometricState)
}