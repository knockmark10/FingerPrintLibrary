package com.markoid.biometrics.managers

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.markoid.biometrics.entities.PromptOptions
import com.markoid.biometrics.states.AuthReason
import com.markoid.biometrics.states.BiometricState.*
import com.markoid.biometrics.states.SecurityLevel
import java.util.concurrent.Executor
import javax.crypto.Cipher
import com.markoid.biometrics.callbacks.BiometricAuthenticationCallback as AuthCallback
import com.markoid.biometrics.callbacks.BiometricValidationCallback as ValidationCallback

/**
 * Manager created to handle biometric authentication over all android
 */
class Jade private constructor(
    private val mContext: Context
) : BiometricPrompt.AuthenticationCallback() {

    private var mOptions: PromptOptions? = null

    private var mAuthCallback: AuthCallback? = null

    private var mBiometricPrompt: BiometricPrompt? = null

    private var mValidationCallback: ValidationCallback? = null

    private val mBiometricManager by lazy { BiometricManager.from(this.mContext) }

    private val mExecutor: Executor by lazy { ContextCompat.getMainExecutor(this.mContext) }

    private val mPromptInfo: BiometricPrompt.PromptInfo
        get() = BiometricPrompt.PromptInfo.Builder()
            .setTitle(this.mOptions?.title ?: "Title")
            .setSubtitle(this.mOptions?.subtitle ?: "Subtitle")
            .setConfirmationRequired(this.mOptions?.confirmationRequired ?: true)
            .setDescription(this.mOptions?.description ?: "Description")
            .setNegativeButtonText(this.mOptions?.negativeText ?: "Cancel")
            .build()

    fun registerValidationListener(listener: ValidationCallback) {
        this.mValidationCallback = listener
    }

    fun registerAuthListener(listener: AuthCallback) {
        this.mAuthCallback = listener
    }

    fun validate() {
        when (this.mBiometricManager.canAuthenticate()) {
            //App can authenticate using biometrics
            BiometricManager.BIOMETRIC_SUCCESS ->
                this.mValidationCallback?.onBiometricResult(BIOMETRIC_SUCCESS)
            //No biometric features available on this device
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                this.mValidationCallback?.onBiometricResult(BIOMETRIC_ERROR_NO_HARDWARE)
            //Biometric features are currently unavailable
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                this.mValidationCallback?.onBiometricResult(BIOMETRIC_ERROR_HW_UNAVAILABLE)
            //The user hasn't associated any biometric credentials with their account
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                this.mValidationCallback?.onBiometricResult(BIOMETRIC_ERROR_NONE_ENROLLED)
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun authenticate(
        fragmentActivity: FragmentActivity? = null,
        fragment: Fragment? = null
    ) {
        if (this.mBiometricPrompt == null) {
            fragmentActivity?.let {
                this.mBiometricPrompt = BiometricPrompt(it, this.mExecutor, this)
            } ?: fragment?.let {
                this.mBiometricPrompt = BiometricPrompt(it, this.mExecutor, this)
            } ?: throw IllegalArgumentException("You need to pass a valid activity or fragment.")
        }
        val cryptoObject: BiometricPrompt.CryptoObject? = when (mOptions?.securityLevel) {
            SecurityLevel.STRONG -> CipherSuite.getCryptoObject()
            SecurityLevel.WEAK -> null
            else -> null
        }
        cryptoObject?.let {
            this.mBiometricPrompt?.authenticate(this.mPromptInfo, it)
        } ?: run {
            this.mBiometricPrompt?.authenticate(this.mPromptInfo)
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun encryptData(data: String, cipher: Cipher): ByteArray =
        CipherSuite.encryptData(data, cipher)

    @TargetApi(Build.VERSION_CODES.M)
    fun decryptData(cipherText: ByteArray, cipher: Cipher): String =
        CipherSuite.decryptData(cipherText, cipher)

    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
        super.onAuthenticationError(errorCode, errString)
        if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
            this.mAuthCallback?.onAuthenticationFailed(AuthReason.DialogClosed)
        } else {
            val reason = AuthReason.AuthenticationError(errorCode, errString)
            this.mAuthCallback?.onAuthenticationFailed(reason)
        }
    }

    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
        super.onAuthenticationSucceeded(result)
        this.mAuthCallback?.onAuthenticationSucceeded(result)
    }

    override fun onAuthenticationFailed() {
        super.onAuthenticationFailed()
        this.mAuthCallback?.onAuthenticationFailed(AuthReason.AuthenticationFailed)
    }

    object Builder {

        private var title: String = "Title"

        private var subtitle: String = "Subtitle"

        private var description: String = "Description"

        private var negativeText: String = "Cancel"

        private var securityLevel: SecurityLevel = SecurityLevel.STRONG

        private var confirmationRequired: Boolean = true

        fun setSecurityLevel(level: SecurityLevel): Builder =
            Builder.apply { this.securityLevel = level }

        fun setConfirmationRequired(required: Boolean): Builder =
            Builder.apply { this.confirmationRequired = required }

        fun setTitle(title: String): Builder =
            Builder.apply { this.title = title }

        fun setSubtitle(subtitle: String): Builder =
            Builder.apply { this.subtitle = subtitle }

        fun setDescription(description: String): Builder =
            Builder.apply { this.description = description }

        fun setNegativeText(negativeText: String): Builder =
            Builder.apply { this.negativeText = negativeText }

        fun build(context: Context): Jade = Jade(context).apply {
            this.mOptions = createPromptOptions()
        }

        private fun createPromptOptions(): PromptOptions =
            PromptOptions(title, subtitle, description, negativeText, securityLevel)

    }

}