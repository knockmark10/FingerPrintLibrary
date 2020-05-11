package com.android.softfingerprint.states

sealed class AuthenticationState {
    data class AuthenticationError(val errorCode: Int, val errString: CharSequence?) :
        AuthenticationState()

    data class AuthenticationHelp(val helpCode: Int, val helpString: CharSequence?) :
        AuthenticationState()

    data class TooManyAttempts(val errorCode: Int, val errString: CharSequence?) :
        AuthenticationState()

    data class ProcessFailed(val error: Throwable): AuthenticationState()

    object AuthenticationFailed: AuthenticationState()

}