package com.android.fingerprintlibrary

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import com.markoid.biometrics.callbacks.BiometricAuthenticationCallback
import com.markoid.biometrics.callbacks.BiometricValidationCallback
import com.markoid.biometrics.managers.Jade
import com.markoid.biometrics.states.AuthReason
import com.markoid.biometrics.states.BiometricState
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BiometricAuthenticationCallback,
    BiometricValidationCallback {

    private val jade: Jade by lazy {
        Jade.Builder
            .setTitle("Login")
            .setSubtitle("Login now")
            .setDescription("Describe this")
            .build(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.jade.registerAuthListener(this)
        this.jade.registerValidationListener(this)

        this.main_authenticate.setOnClickListener {
            this.jade.validate()
        }

    }

    override fun onAuthenticationFailed(reason: AuthReason) {
    }

    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
    }

    override fun onBiometricResult(state: BiometricState) {
        when (state) {
            BiometricState.BIOMETRIC_SUCCESS -> this.jade.authenticate(this)
            BiometricState.BIOMETRIC_ERROR_NO_HARDWARE -> TODO()
            BiometricState.BIOMETRIC_ERROR_HW_UNAVAILABLE -> TODO()
            BiometricState.BIOMETRIC_ERROR_NONE_ENROLLED -> TODO()
        }
    }

}
