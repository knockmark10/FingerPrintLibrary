package com.android.softfingerprint.managers

import android.annotation.TargetApi
import android.content.Context
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.CancellationSignal
import com.android.softfingerprint.callbacks.FingerPrintAuthenticationCallback
import com.android.softfingerprint.entities.DialogOptions
import com.android.softfingerprint.states.AuthenticationState
import com.android.softfingerprint.states.AuthenticationState.AuthenticationError
import com.android.softfingerprint.states.AuthenticationState.AuthenticationHelp
import java.util.concurrent.Executor
import android.content.DialogInterface as Dialog

@TargetApi(Build.VERSION_CODES.P)
internal class BiometricPromptManager(
    private val mContext: Context
) : BiometricPrompt.AuthenticationCallback() {

    private var mAuthListeners = mutableListOf<FingerPrintAuthenticationCallback>()

    private val mCancellationSignal by lazy { CancellationSignal() }

    private val mExecutor: Executor
        get() = this.mContext.mainExecutor

    fun registerAuthListener(listener: FingerPrintAuthenticationCallback) {
        this.mAuthListeners.add(listener)
    }

    fun displayBiometricPrompt(
        dialogOptions: DialogOptions = DialogOptions(),
        negativeAction: ((dialog: Dialog) -> Unit)? = null
    ) {
        BiometricPrompt.Builder(this.mContext)
            .setTitle(dialogOptions.title)
            .setSubtitle(dialogOptions.subtitle)
            .setDescription(dialogOptions.description)
            .setNegativeButton(dialogOptions.cancel, this.mExecutor,
                Dialog.OnClickListener { dialog, _ ->
                    negativeAction?.let { it(dialog) }
                    dialog.dismiss()
                })
            .build()
            .authenticate(this.mCancellationSignal, this.mExecutor, this)
    }

    private fun notifyListener(output: (callback: FingerPrintAuthenticationCallback) -> Unit) {
        this.mAuthListeners.forEach { output(it) }
    }

    override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
        super.onAuthenticationError(errorCode, errString)
        notifyListener { it.onAuthFailed(AuthenticationError(errorCode, errString)) }
    }

    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
        super.onAuthenticationSucceeded(result)
        notifyListener { it.onAuthSucceeded(null) }
    }

    override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
        super.onAuthenticationHelp(helpCode, helpString)
        notifyListener { it.onAuthFailed(AuthenticationHelp(helpCode, helpString)) }
    }

    override fun onAuthenticationFailed() {
        super.onAuthenticationFailed()
        notifyListener { it.onAuthFailed(AuthenticationState.AuthenticationFailed) }
    }
}