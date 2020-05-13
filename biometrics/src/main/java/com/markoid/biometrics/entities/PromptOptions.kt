package com.markoid.biometrics.entities

import com.markoid.biometrics.states.SecurityLevel

data class PromptOptions(
    val title: String = "Title",
    val subtitle: String = "Subtitle",
    val description: String = "Description",
    val negativeText: String = "Cancel",
    val securityLevel: SecurityLevel = SecurityLevel.STRONG,
    val confirmationRequired: Boolean = true
)