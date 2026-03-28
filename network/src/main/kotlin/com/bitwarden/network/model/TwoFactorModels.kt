package com.bitwarden.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request body for two-factor setup operations that require master password.
 */
@Serializable
data class TwoFactorSetupRequestJson(
    @SerialName("masterPasswordHash")
    val masterPasswordHash: String,
    @SerialName("token")
    val token: String? = null,
)

/**
 * Response from getting/enabling TOTP authenticator.
 */
@Serializable
data class TwoFactorAuthenticatorResponseJson(
    @SerialName("key")
    val key: String,
    @SerialName("enabled")
    val enabled: Boolean,
)

/**
 * Request body for enabling email two-factor.
 */
@Serializable
data class TwoFactorEmailRequestJson(
    @SerialName("masterPasswordHash")
    val masterPasswordHash: String,
    @SerialName("token")
    val token: String,
)

/**
 * Response from enabling email two-factor.
 */
@Serializable
data class TwoFactorEmailResponseJson(
    @SerialName("email")
    val email: String,
    @SerialName("enabled")
    val enabled: Boolean,
)

/**
 * Response from getting Duo configuration.
 */
@Serializable
data class TwoFactorDuoResponseJson(
    @SerialName("host")
    val host: String?,
    @SerialName("secretKey")
    val secretKey: String?,
    @SerialName("integrationKey")
    val integrationKey: String?,
    @SerialName("enabled")
    val enabled: Boolean,
)

/**
 * Request body for enabling Duo two-factor.
 */
@Serializable
data class TwoFactorDuoRequestJson(
    @SerialName("masterPasswordHash")
    val masterPasswordHash: String,
    @SerialName("host")
    val host: String,
    @SerialName("secretKey")
    val secretKey: String,
    @SerialName("integrationKey")
    val integrationKey: String,
)

/**
 * Response from getting YubiKey configuration.
 */
@Serializable
data class TwoFactorYubiKeyResponseJson(
    @SerialName("key1")
    val key1: String?,
    @SerialName("key2")
    val key2: String?,
    @SerialName("key3")
    val key3: String?,
    @SerialName("key4")
    val key4: String?,
    @SerialName("key5")
    val key5: String?,
    @SerialName("nfc")
    val nfc: Boolean,
    @SerialName("enabled")
    val enabled: Boolean,
)

/**
 * Request body for enabling YubiKey two-factor.
 */
@Serializable
data class TwoFactorYubiKeyRequestJson(
    @SerialName("masterPasswordHash")
    val masterPasswordHash: String,
    @SerialName("key1")
    val key1: String?,
    @SerialName("key2")
    val key2: String?,
    @SerialName("key3")
    val key3: String?,
    @SerialName("key4")
    val key4: String?,
    @SerialName("key5")
    val key5: String?,
    @SerialName("nfc")
    val nfc: Boolean,
)

/**
 * A single WebAuthn credential entry.
 */
@Serializable
data class TwoFactorWebAuthnCredential(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
)

/**
 * Response from getting WebAuthn configuration.
 */
@Serializable
data class TwoFactorWebAuthnResponseJson(
    @SerialName("keys")
    val keys: List<TwoFactorWebAuthnCredential>?,
    @SerialName("enabled")
    val enabled: Boolean,
)

/**
 * Response from getting the recovery code.
 */
@Serializable
data class TwoFactorRecoveryResponseJson(
    @SerialName("code")
    val code: String,
)
