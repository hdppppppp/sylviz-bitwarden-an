package com.bitwarden.network.api

import com.bitwarden.network.model.NetworkResult
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
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

/**
 * Defines raw calls under the /two-factor API with authentication applied.
 */
internal interface TwoFactorApi {

    // region TOTP Authenticator

    @POST("two-factor/get-authenticator")
    suspend fun getAuthenticatorKey(
        @Body body: TwoFactorSetupRequestJson,
    ): NetworkResult<TwoFactorAuthenticatorResponseJson>

    @PUT("two-factor/authenticator")
    suspend fun enableAuthenticator(
        @Body body: TwoFactorSetupRequestJson,
    ): NetworkResult<TwoFactorAuthenticatorResponseJson>

    // endregion

    // region Email

    @POST("two-factor/send-email")
    suspend fun sendEmailSetupCode(
        @Body body: TwoFactorSetupRequestJson,
    ): NetworkResult<Unit>

    @PUT("two-factor/email")
    suspend fun enableEmail(
        @Body body: TwoFactorEmailRequestJson,
    ): NetworkResult<TwoFactorEmailResponseJson>

    // endregion

    // region Duo

    @POST("two-factor/get-duo")
    suspend fun getDuoConfig(
        @Body body: TwoFactorSetupRequestJson,
    ): NetworkResult<TwoFactorDuoResponseJson>

    @PUT("two-factor/duo")
    suspend fun enableDuo(
        @Body body: TwoFactorDuoRequestJson,
    ): NetworkResult<TwoFactorDuoResponseJson>

    // endregion

    // region YubiKey

    @POST("two-factor/get-yubikey")
    suspend fun getYubiKeyConfig(
        @Body body: TwoFactorSetupRequestJson,
    ): NetworkResult<TwoFactorYubiKeyResponseJson>

    @PUT("two-factor/yubikey")
    suspend fun enableYubiKey(
        @Body body: TwoFactorYubiKeyRequestJson,
    ): NetworkResult<TwoFactorYubiKeyResponseJson>

    // endregion

    // region WebAuthn

    @POST("two-factor/get-webauthn")
    suspend fun getWebAuthnConfig(
        @Body body: TwoFactorSetupRequestJson,
    ): NetworkResult<TwoFactorWebAuthnResponseJson>

    @DELETE("two-factor/webauthn")
    suspend fun deleteWebAuthnCredential(
        @Query("id") id: Int,
        @Body body: TwoFactorSetupRequestJson,
    ): NetworkResult<TwoFactorWebAuthnResponseJson>

    // endregion

    // region Recovery Code

    @POST("two-factor/get-recover")
    suspend fun getRecoveryCode(
        @Body body: TwoFactorSetupRequestJson,
    ): NetworkResult<TwoFactorRecoveryResponseJson>

    // endregion
}
