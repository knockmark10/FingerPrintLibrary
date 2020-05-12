package com.android.fingerprintlibrary

import android.os.Bundle
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.android.softfingerprint.callbacks.FingerPrintAuthenticationCallback
import com.android.softfingerprint.callbacks.FingerPrintHandlerCallback
import com.android.softfingerprint.managers.BiometricManager
import com.android.softfingerprint.states.AuthenticationState
import com.android.softfingerprint.states.BiometricType
import com.android.softfingerprint.states.FingerPrintState
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), FingerPrintAuthenticationCallback,
    FingerPrintHandlerCallback {

    private val biometricsManager by lazy {
        BiometricManager.Builder
            .setBiometricType(BiometricType.Native)
            .setTitle("Authentication")
            .setSubtitle("Authentication is required")
            .setDescription("User authentication is required to proceed. Place your finger in the fingerprint sensor.")
            .setCancel("Cancel operation")
            .build(this, supportFragmentManager)
            .registerBasicListener(this)
            .registerAuthListener(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.main_authenticate.setOnClickListener {
            this.biometricsManager.validateFingerPrint()
        }
    }

    override fun onAuthSucceeded(result: FingerprintManagerCompat.AuthenticationResult?) {
        Log.d("onAuthSucceeded", result.toString())
    }

    override fun onAuthFailed(reason: AuthenticationState) {
        Log.d("onAuthFailed", reason.toString())
    }

    override fun onFingerPrintSucceeded() {
        this.biometricsManager.startAuthProcess()
        Log.d("onFingerPrintSucceeded", "Succeeded")
    }

    override fun onFingerPrintFailed(reason: FingerPrintState) {
        Log.d("onFingerPrintFailed", reason.toString())
    }

    override fun onRequestPermission(permission: String) {
        Log.d("onRequestPermission", permission)
    }
}
