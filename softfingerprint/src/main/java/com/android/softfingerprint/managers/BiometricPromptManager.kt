package com.android.softfingerprint.managers

import android.annotation.TargetApi
import android.content.Context
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import com.android.softfingerprint.callbacks.FingerPrintAuthenticationCallback
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

    fun registerAuthListener(listener: FingerPrintAuthenticationCallback) {
        this.mAuthListeners.add(listener)
    }

    fun displayBiometricPrompt(
        title: String = "Login",
        subtitle: String = "Required by app",
        description: String = "Place your fingerprint",
        negativeMessage: String = "Cancel",
        executor: Executor? = null,
        negativeAction: ((dialog: Dialog) -> Unit)? = null
    ) {
        val checkedExecutor = executor ?: this.mContext.mainExecutor
        BiometricPrompt.Builder(this.mContext)
            .setTitle(title)
            .setSubtitle(subtitle)
            .setDescription(description)
            .setNegativeButton(negativeMessage, checkedExecutor,
                Dialog.OnClickListener { dialog, _ ->
                    negativeAction?.let { it(dialog) }
                    dialog.dismiss()
                })
            .build()
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