package com.android.softfingerprint.managers

import android.content.Context
import android.support.v4.app.FragmentManager
import com.android.softfingerprint.callbacks.FingerPrintAuthenticationCallback
import com.android.softfingerprint.callbacks.FingerPrintHandlerCallback
import com.android.softfingerprint.dialogs.BiometricCompatBottomSheet
import com.android.softfingerprint.entities.DialogOptions
import com.android.softfingerprint.states.BiometricState
import com.android.softfingerprint.states.BiometricType
import com.android.softfingerprint.utils.BiometricUtils

/**
 * This class will be used as a centralized manager to use fingerprint sensor for any version.
 */
class BiometricManager(private val mContext: Context) {

    private var mFragmentManager: FragmentManager? = null

    private var mBiometricType: BiometricType = BiometricType.Native

    private var mDialogOptions: DialogOptions = DialogOptions()

    private val mFingerPrintManager by lazy { FingerPrintManager(this.mContext) }

    private val mBiometricPromptManager by lazy { BiometricPromptManager(this.mContext) }

    private val mBiometricValidations by lazy { BiometricValidations(this.mContext) }

    private val mBiometricUtils by lazy { BiometricUtils(this.mContext) }

    private val mBiometricBottomSheetDialog by lazy { BiometricCompatBottomSheet() }

    fun validateFingerPrint() {
        this.mBiometricValidations.validateFingerPrint()
    }

    fun startAuthProcess() {
        when (this.mBiometricType) {
            BiometricType.Native -> this.handleNativeAuthProcess()
            BiometricType.Dialog -> this.handleDialogAuthProcess()
            BiometricType.BottomSheet -> this.handleBottomSheetAuthProcess()
            BiometricType.Custom -> this.operateWithManager()
        }
    }

    fun registerBasicListener(listener: FingerPrintHandlerCallback): BiometricManager {
        this.mBiometricValidations.registerBasicListener(listener)
        return this
    }

    fun registerAuthListener(listener: FingerPrintAuthenticationCallback): BiometricManager {
        when (this.mBiometricUtils.getDeviceCapabilities()) {
            BiometricState.BiometricPromptSupported ->
                this.mBiometricPromptManager.registerAuthListener(listener)
            BiometricState.FingerprintSupported ->
                this.mFingerPrintManager.registerAuthListener(listener)
            BiometricState.DeviceUnsupported -> raiseUnsupportedOperationException()
        }
        return this
    }

    // Handle native process for device-specific API available
    private fun handleNativeAuthProcess() {
        when (this.mBiometricUtils.getDeviceCapabilities()) {
            BiometricState.BiometricPromptSupported -> operateWithBiometrics()
            BiometricState.FingerprintSupported -> handleDialogAuthProcess()
            BiometricState.DeviceUnsupported -> raiseUnsupportedOperationException()
        }
    }

    private fun handleDialogAuthProcess() {
        if (this.mBiometricUtils.getDeviceCapabilities() == BiometricState.DeviceUnsupported) {
            raiseUnsupportedOperationException()
        }
        TODO("Handle dialog auth")
    }

    private fun handleBottomSheetAuthProcess() {
        if (this.mBiometricUtils.getDeviceCapabilities() == BiometricState.DeviceUnsupported) {
            raiseUnsupportedOperationException()
        }
        this.mBiometricBottomSheetDialog.setData(this.mDialogOptions)
        this.mBiometricBottomSheetDialog
            .show(this.mFragmentManager, BiometricManager::class.java.name)
    }

    private fun operateWithBiometrics() {
        this.mBiometricPromptManager.displayBiometricPrompt(this.mDialogOptions) {

        }
    }

    private fun operateWithManager() {
        this.mFingerPrintManager.startAuthProcess()
    }

    private fun raiseUnsupportedOperationException() {
        throw UnsupportedOperationException("This operation is unsupported by your device.")

    }

    object Builder {

        private var biometricType: BiometricType = BiometricType.Native

        private var title: String = "Title"

        private var subtitle: String = "Subtitle"

        private var description: String = "Description"

        private var cancel: String = "Cancel"

        fun setBiometricType(type: BiometricType): Builder =
            Builder.apply { this.biometricType = type }

        fun setTitle(title: String): Builder =
            Builder.apply {
                this.title = title
            }

        fun setSubtitle(subtitle: String): Builder =
            Builder.apply { this.subtitle = subtitle }

        fun setDescription(description: String): Builder =
            Builder.apply { this.description = description }

        fun setCancel(cancel: String): Builder =
            Builder.apply { this.cancel = cancel }

        fun build(context: Context, fragmentManager: FragmentManager): BiometricManager =
            BiometricManager(context).apply {
                this.mFragmentManager = fragmentManager
                this.mBiometricType = biometricType
                this.mDialogOptions = buildDialogOptions()
            }

        private fun buildDialogOptions(): DialogOptions =
            DialogOptions(this.title, this.subtitle, this.description, this.cancel)

    }

}

//Reference: https://proandroiddev.com/5-steps-to-implement-biometric-authentication-in-android-dbeb825aeee8