package com.bitwarden.network.service

import com.bitwarden.network.api.TwoFactorApi
import com.bitwarden.network.model.TwoFactorAuthenticatorResponseJson
import com.bitwarden.network.model.TwoFactorDuoRequestJson
import com.bitwarden.network.model.TwoFactorDuoResponseJson
import com.bitwarden.network.model.TwoFactorEmailRequestJson
import com.bitwarden.network.model.TwoFactorEmailResponseJson
import com.bitwarden.network.model.TwoFactorSetupRequestJson
import com.bitwarden.network.model.TwoFactorWebAuthnResponseJson
import com.bitwarden.network.model.TwoFactorYubiKeyRequestJson
import com.bitwarden.network.model.TwoFactorYubiKeyResponseJson
import com.bitwarden.network.model.TwoFactorRecoveryResponseJson
import com.bitwarden.network.util.toResult

internal class TwoFactorServiceImpl(
    private val twoFactorApi: TwoFactorApi,
) : TwoFactorService {

    override suspend fun getAuthenticatorKey(masterPasswordHash: String): Result<TwoFactorAuthenticatorResponseJson> =
        twoFactorApi.getAuthenticatorKey(TwoFactorSetupRequestJson(masterPasswordHash)).toResult()

    override suspend fun enableAuthenticator(masterPasswordHash: String, token: String): Result<TwoFactorAuthenticatorResponseJson> =
        twoFactorApi.enableAuthenticator(TwoFactorSetupRequestJson(masterPasswordHash, token)).toResult()

    override suspend fun sendEmailSetupCode(masterPasswordHash: String): Result<Unit> =
        twoFactorApi.sendEmailSetupCode(TwoFactorSetupRequestJson(masterPasswordHash)).toResult()

    override suspend fun enableEmail(masterPasswordHash: String, token: String): Result<TwoFactorEmailResponseJson> =
        twoFactorApi.enableEmail(TwoFactorEmailRequestJson(masterPasswordHash, token)).toResult()

    override suspend fun getDuoConfig(masterPasswordHash: String): Result<TwoFactorDuoResponseJson> =
        twoFactorApi.getDuoConfig(TwoFactorSetupRequestJson(masterPasswordHash)).toResult()

    override suspend fun enableDuo(masterPasswordHash: String, host: String, secretKey: String, integrationKey: String): Result<TwoFactorDuoResponseJson> =
        twoFactorApi.enableDuo(TwoFactorDuoRequestJson(masterPasswordHash, host, secretKey, integrationKey)).toResult()

    override suspend fun getYubiKeyConfig(masterPasswordHash: String): Result<TwoFactorYubiKeyResponseJson> =
        twoFactorApi.getYubiKeyConfig(TwoFactorSetupRequestJson(masterPasswordHash)).toResult()

    override suspend fun enableYubiKey(masterPasswordHash: String, keys: List<String?>, nfc: Boolean): Result<TwoFactorYubiKeyResponseJson> =
        twoFactorApi.enableYubiKey(
            TwoFactorYubiKeyRequestJson(
                masterPasswordHash = masterPasswordHash,
                key1 = keys.getOrNull(0),
                key2 = keys.getOrNull(1),
                key3 = keys.getOrNull(2),
                key4 = keys.getOrNull(3),
                key5 = keys.getOrNull(4),
                nfc = nfc,
            ),
        ).toResult()

    override suspend fun getWebAuthnConfig(masterPasswordHash: String): Result<TwoFactorWebAuthnResponseJson> =
        twoFactorApi.getWebAuthnConfig(TwoFactorSetupRequestJson(masterPasswordHash)).toResult()

    override suspend fun deleteWebAuthnCredential(id: Int, masterPasswordHash: String): Result<TwoFactorWebAuthnResponseJson> =
        twoFactorApi.deleteWebAuthnCredential(id, TwoFactorSetupRequestJson(masterPasswordHash)).toResult()

    override suspend fun getRecoveryCode(masterPasswordHash: String): Result<TwoFactorRecoveryResponseJson> =
        twoFactorApi.getRecoveryCode(TwoFactorSetupRequestJson(masterPasswordHash)).toResult()
}
