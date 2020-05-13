package com.markoid.biometrics.managers

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.UserNotAuthenticatedException
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricPrompt
import java.nio.charset.Charset
import java.security.InvalidKeyException
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

@RequiresApi(Build.VERSION_CODES.M)
object CipherSuite {

    private const val ANDROID_KEYSTORE = "AndroidKeyStore"

    private const val JADE_KEY = "JadeRandomKey"

    private var mCipher: Cipher? = null

    fun getCryptoObject(): BiometricPrompt.CryptoObject? =
        if (initCipher()) this.mCipher?.let { BiometricPrompt.CryptoObject(it) } else null

    fun encryptData(data: String, cipher: Cipher): ByteArray = try {
        cipher.doFinal(data.toByteArray(Charset.defaultCharset())) ?: ByteArray(1)
    } catch (e: InvalidKeyException) {
        Log.e("MY_APP_TAG", "Key is invalid.")
        ByteArray(1)
    } catch (e: UserNotAuthenticatedException) {
        Log.d("MY_APP_TAG", "The key's validity timed out.")
        ByteArray(1)
    }

    fun decryptData(cipherText: ByteArray, cipher: Cipher): String =
        cipher.doFinal(cipherText)?.let { plainText ->
            String(plainText, Charset.forName("UTF-8"))
        } ?: ""

    private fun generateSecretKey(keyGenParameterSpec: KeyGenParameterSpec) {
        val keyGenerator =
            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }

    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        return keyStore.getKey(JADE_KEY, null) as SecretKey
    }

    private fun getCipher(): Cipher = Cipher.getInstance(
        KeyProperties.KEY_ALGORITHM_AES + "/"
                + KeyProperties.BLOCK_MODE_CBC + "/"
                + KeyProperties.ENCRYPTION_PADDING_PKCS7
    )

    private fun getKeyProperties(): Int =
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT

    private fun getKeyGenParameterSpec(): KeyGenParameterSpec =
        KeyGenParameterSpec.Builder(JADE_KEY, getKeyProperties())
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setUserAuthenticationRequired(true)
            /*.setUserAuthenticationValidityDurationSeconds(3)*/
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            .build()

    private fun initCipher(): Boolean = try {
        generateSecretKey(getKeyGenParameterSpec())
        this.mCipher = getCipher()
        this.mCipher?.init(Cipher.ENCRYPT_MODE, getSecretKey())
        true
    } catch (exception: Exception) {
        false
    }

}