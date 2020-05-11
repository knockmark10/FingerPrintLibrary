package com.android.softfingerprint.utils

import android.Manifest
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import com.android.softfingerprint.states.BiometricState

class BiometricUtils(private val context: Context) {

    fun isHardWareSupported(): Boolean {
        val fingerprintManager = FingerprintManagerCompat.from(context)
        return this.isSdkVersionSupported() && fingerprintManager.isHardwareDetected
    }

    fun isKeyGuardSecure(): Boolean {
        val keyGuardManager =
            this.context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        return keyGuardManager.isKeyguardSecure
    }

    fun isFingerprintAvailable(): Boolean {
        val fingerprintManager = FingerprintManagerCompat.from(context)
        return fingerprintManager.hasEnrolledFingerprints()
    }

    fun isPermissionGranted(): Boolean =
        when {
            this.isBiometricPromptAvailable() ->
                checkSelfPermission(Manifest.permission.USE_BIOMETRIC)
            this.isSdkVersionSupported() ->
                checkSelfPermission(Manifest.permission.USE_FINGERPRINT)
            else -> false
        }

    fun getRequiredPermission(): String =
        when (getDeviceCapabilities()) {
            BiometricState.BiometricPromptSupported -> Manifest.permission.USE_BIOMETRIC
            BiometricState.FingerprintSupported -> Manifest.permission.USE_FINGERPRINT
            BiometricState.DeviceUnsupported -> "None"
        }

    fun getDeviceCapabilities(): BiometricState = when {
        this.isBiometricPromptAvailable() -> BiometricState.BiometricPromptSupported
        this.isSdkVersionSupported() -> BiometricState.FingerprintSupported
        else -> BiometricState.DeviceUnsupported
    }

    private fun isBiometricPromptAvailable(): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

    private fun isSdkVersionSupported(): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    private fun checkSelfPermission(permission: String): Boolean =
        ActivityCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_GRANTED

}