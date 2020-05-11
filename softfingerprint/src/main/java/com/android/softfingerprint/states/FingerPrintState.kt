package com.android.softfingerprint.states

sealed class FingerPrintState {
    object LockScreenSecurityDisabled: FingerPrintState()
    object NoFingerPrintRegistered: FingerPrintState()
    object NoFingerPrintSensor: FingerPrintState()
}