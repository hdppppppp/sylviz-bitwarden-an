package com.bitwarden.network.service

import com.bitwarden.network.model.TwoFactorAuthenticatorResponseJson
import com.bitwarden.network.model.TwoFactorDuoResponseJson
import com.bitwarden.network.model.TwoFactorEmailResponseJson
import com.bitwarden.network.model.TwoFactorWebAuthnResponseJson
import com.bitwarden.network.model.TwoFactorYubiKeyResponseJson
import com.bitwarden.network.model.TwoFactorRecoveryResponseJson

interface TwoFactorService {
    // TOTP
    suspend fun getAuthenticatorKey(masterPasswordHash: String): Result<TwoFactorAuthenticatorResponseJson>
    suspend fun enableAuthenticator(masterPasswordHash: String, token: String): Result<TwoFactorAuthenticatorResponseJson>

    // Email
    suspend fun sendEmailSetupCode(masterPasswordHash: String): Result<Unit>
    suspend fun enableEmail(masterPasswordHash: String, token: String): Result<TwoFactorEmailResponseJson>

    // Duo
    suspend fun getDuoConfig(masterPasswordHash: String): Result<TwoFactorDuoResponseJson>
    suspend fun enableDuo(masterPasswordHash: String, host: String, secretKey: String, integrationKey: String): Result<TwoFactorDuoResponseJson>

    // YubiKey
    suspend fun getYubiKeyConfig(masterPasswordHash: String): Result<TwoFactorYubiKeyResponseJson>
    suspend fun enableYubiKey(masterPasswordHash: String, keys: List<String?>, nfc: Boolean): Result<TwoFactorYubiKeyResponseJson>

    // WebAuthn
    suspend fun getWebAuthnConfig(masterPasswordHash: String): Result<TwoFactorWebAuthnResponseJson>
    suspend fun deleteWebAuthnCredential(id: Int, masterPasswordHash: String): Result<TwoFactorWebAuthnResponseJson>

    // Recovery Code
    suspend fun getRecoveryCode(masterPasswordHash: String): Result<TwoFactorRecoveryResponseJson>
}
