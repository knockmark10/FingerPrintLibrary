package com.android.softfingerprint.managers

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import android.support.v4.os.CancellationSignal
import com.android.softfingerprint.callbacks.FingerPrintAuthenticationCallback
import com.android.softfingerprint.callbacks.FingerPrintHelperCallback
import com.android.softfingerprint.states.AuthenticationState.*
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator

@TargetApi(Build.VERSION_CODES.M)
internal class FingerPrintManager(private val mContext: Context) : FingerPrintHelperCallback {

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val FingerKey = "FingerKey"
    }

    private val fingerPrintManager by lazy { FingerprintManagerCompat.from(this.mContext) }

    private lateinit var cryptoObject: FingerprintManagerCompat.CryptoObject

    private lateinit var cipher: Cipher

    private lateinit var keyStore: KeyStore

    private val mHelper by lazy { Helper(this.fingerPrintManager, this) }

    private lateinit var keyGenerator: KeyGenerator

    private var mAuthListeners = mutableListOf<FingerPrintAuthenticationCallback>()

    fun registerAuthListener(listener: FingerPrintAuthenticationCallback) {
        this.mAuthListeners.add(listener)
    }

    /**
     * Call this method when you want to start scanning
     * your fingertip
     */
    @SuppressLint("NewApi")
    fun startAuthProcess() {
        checkAuthListener()
        generateKey()
        if (initCipher()) {
            this.cryptoObject = FingerprintManagerCompat.CryptoObject(this.cipher)
            this.mHelper.startAuth(cryptoObject)
        }
    }

    private fun generateKey() {
        try {
            this.keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
            this.keyGenerator =
                KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES,
                    ANDROID_KEYSTORE
                )
            this.keyStore.load(null)

            val keyProperties = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            val builder = KeyGenParameterSpec.Builder(FingerKey, keyProperties)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setUserAuthenticationRequired(true)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)

            this.keyGenerator.run {
                init(builder.build())
                generateKey()
            }
        } catch (e: Throwable) {
            notifyListener { it.onAuthFailed(ProcessFailed(e)) }
        }
    }

    private fun initCipher(): Boolean =
        try {
            this.cipher = Cipher.getInstance(
                KeyProperties.KEY_ALGORITHM_AES + "/"
                        + KeyProperties.BLOCK_MODE_CBC + "/"
                        + KeyProperties.ENCRYPTION_PADDING_PKCS7
            )
            this.keyStore.load(null)
            val key = keyStore.getKey(FingerKey, null)
            this.cipher.init(Cipher.ENCRYPT_MODE, key)
            true
        } catch (exception: Throwable) {
            notifyListener { it.onAuthFailed(ProcessFailed(exception)) }
            false
        }

    private class Helper(
        private val mManager: FingerprintManagerCompat,
        private val mListener: FingerPrintHelperCallback
    ) : FingerprintManagerCompat.AuthenticationCallback() {

        private var cancellationSignal: CancellationSignal? = null

        private var selfCancelled = false

        fun startAuth(cryptoObject: FingerprintManagerCompat.CryptoObject) {
            startListening(cryptoObject)
        }

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
            if (!selfCancelled) {
                stopListening()
                if (errorCode == FingerprintManager.FINGERPRINT_ERROR_LOCKOUT) {
                    mListener.onTooManyAttempts(errorCode, errString)
                } else {
                    mListener.onAuthenticationError(errorCode, errString)
                }
            }
        }

        private fun startListening(cryptoObject: FingerprintManagerCompat.CryptoObject) {
            this.cancellationSignal = CancellationSignal()
            this.selfCancelled = false
            this.mManager.authenticate(
                cryptoObject,
                0,
                this.cancellationSignal,
                this,
                null
            )
        }

        fun stopListening() {
            this.cancellationSignal?.also {
                this.selfCancelled = true
                it.cancel()
            }
            this.cancellationSignal = null
        }

        override fun onAuthenticationFailed() {
            if (!this.selfCancelled) {
                stopListening()
            }
            this.mListener.onAuthenticationFailed()
        }

        override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
            this.mListener.onAuthenticationHelp(helpCode, helpString)
        }

        override fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult?) {
            this.mListener.onAuthenticationSucceeded(result)
        }

    }

    private fun notifyListener(output: (callback: FingerPrintAuthenticationCallback) -> Unit) {
        this.mAuthListeners.forEach { output(it) }
    }

    private fun checkAuthListener() {
        if (this.mAuthListeners.isEmpty()) {
            throw NullPointerException("FingerPrintBasicCallback interface required. You need to register it.")
        }
    }

    override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
        notifyListener { it.onAuthFailed(AuthenticationError(errorCode, errString)) }
    }

    override fun onAuthenticationFailed() {
        notifyListener { it.onAuthFailed(AuthenticationFailed) }
    }

    override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
        notifyListener { it.onAuthFailed(AuthenticationHelp(helpCode, helpString)) }
    }

    override fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult?) {
        notifyListener { it.onAuthSucceeded(result) }
    }

    override fun onTooManyAttempts(errorCode: Int, errString: CharSequence?) {
        notifyListener { it.onAuthFailed(TooManyAttempts(errorCode, errString)) }
    }

}