package com.markoid.biometrics.states

sealed class AuthReason {
    object DialogClosed: AuthReason()
    data class AuthenticationError(val errorCode: Int, val errString: CharSequence): AuthReason()
    object AuthenticationFailed : AuthReason()
}