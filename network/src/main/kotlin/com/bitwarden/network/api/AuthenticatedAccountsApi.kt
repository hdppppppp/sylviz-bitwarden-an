package com.bitwarden.network.api

import com.bitwarden.network.model.CreateAccountKeysRequest
import com.bitwarden.network.model.CreateAccountKeysResponseJson
import com.bitwarden.network.model.DeleteAccountRequestJson
import com.bitwarden.network.model.EmailTokenRequestJson
import com.bitwarden.network.model.ChangeEmailRequestJson
import com.bitwarden.network.model.UpdateProfileRequestJson
import com.bitwarden.network.model.UpdateProfileResponseJson
import com.bitwarden.network.model.NetworkResult
import com.bitwarden.network.model.ResetPasswordRequestJson
import com.bitwarden.network.model.SetPasswordRequestJson
import com.bitwarden.network.model.UpdateKdfJsonRequest
import com.bitwarden.network.model.VerifyOtpRequestJson
import retrofit2.http.Body
import retrofit2.http.HTTP
import retrofit2.http.POST

/**
 * Defines raw calls under the /accounts API with authentication applied.
 */
internal interface AuthenticatedAccountsApi {

    @POST("/accounts/convert-to-key-connector")
    suspend fun convertToKeyConnector(): NetworkResult<Unit>

    @POST("/accounts/keys")
    suspend fun createAccountKeys(
        @Body body: CreateAccountKeysRequest,
    ): NetworkResult<CreateAccountKeysResponseJson>

    @HTTP(method = "DELETE", path = "/accounts", hasBody = true)
    suspend fun deleteAccount(@Body body: DeleteAccountRequestJson): NetworkResult<Unit>

    @POST("/accounts/request-otp")
    suspend fun requestOtp(): NetworkResult<Unit>

    @POST("/accounts/kdf")
    suspend fun updateKdf(@Body body: UpdateKdfJsonRequest): NetworkResult<Unit>

    @POST("/accounts/verify-otp")
    suspend fun verifyOtp(@Body body: VerifyOtpRequestJson): NetworkResult<Unit>

    @HTTP(method = "PUT", path = "/accounts/update-temp-password", hasBody = true)
    suspend fun resetTempPassword(@Body body: ResetPasswordRequestJson): NetworkResult<Unit>

    @HTTP(method = "POST", path = "/accounts/password", hasBody = true)
    suspend fun resetPassword(@Body body: ResetPasswordRequestJson): NetworkResult<Unit>

    @POST("/accounts/set-password")
    suspend fun setPassword(@Body body: SetPasswordRequestJson): NetworkResult<Unit>

    /** Step 1: send verification code to the new email address. */
    @POST("/accounts/email-token")
    suspend fun requestEmailToken(@Body body: EmailTokenRequestJson): NetworkResult<Unit>

    /** Step 2: confirm the email change with the verification code. */
    @POST("/accounts/email")
    suspend fun changeEmail(@Body body: ChangeEmailRequestJson): NetworkResult<Unit>

    /** Update the user's profile (name/username). */
    @PUT("/accounts/profile")
    suspend fun updateProfile(@Body body: UpdateProfileRequestJson): NetworkResult<UpdateProfileResponseJson>
}
