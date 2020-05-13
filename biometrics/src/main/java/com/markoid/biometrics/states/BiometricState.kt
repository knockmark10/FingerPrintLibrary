package com.markoid.biometrics.states

enum class BiometricState {
    BIOMETRIC_SUCCESS,
    BIOMETRIC_ERROR_NO_HARDWARE,
    BIOMETRIC_ERROR_HW_UNAVAILABLE,
    BIOMETRIC_ERROR_NONE_ENROLLED
}