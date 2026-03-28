package com.bitwarden.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmailTokenRequestJson(
    @SerialName("masterPasswordHash")
    val masterPasswordHash: String,
    @SerialName("newEmail")
    val newEmail: String,
)

@Serializable
data class ChangeEmailRequestJson(
    @SerialName("masterPasswordHash")
    val masterPasswordHash: String,
    @SerialName("newEmail")
    val newEmail: String,
    @SerialName("token")
    val token: String,
)

@Serializable
data class UpdateProfileRequestJson(
    @SerialName("name")
    val name: String?,
    @SerialName("masterPasswordHint")
    val masterPasswordHint: String? = null,
)

@Serializable
data class UpdateProfileResponseJson(
    @SerialName("name")
    val name: String?,
    @SerialName("email")
    val email: String,
)
