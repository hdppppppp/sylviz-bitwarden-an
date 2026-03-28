package com.bitwarden.network.api

import com.bitwarden.network.model.EmergencyAccessAcceptRequestJson
import com.bitwarden.network.model.EmergencyAccessConfirmRequestJson
import com.bitwarden.network.model.EmergencyAccessInitiateRequestJson
import com.bitwarden.network.model.EmergencyAccessInviteRequestJson
import com.bitwarden.network.model.EmergencyAccessPasswordRequestJson
import com.bitwarden.network.model.EmergencyAccessRejectRequestJson
import com.bitwarden.network.model.EmergencyAccessResponseJson
import com.bitwarden.network.model.EmergencyAccessTakeoverResponseJson
import com.bitwarden.network.model.EmergencyAccessUpdateRequestJson
import com.bitwarden.network.model.EmergencyAccessViewVaultResponseJson
import com.bitwarden.network.model.NetworkResult
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * 紧急访问 API 接口
 * 定义与 Vaultwarden/Bitwarden 紧急访问功能相关的所有 API 调用
 */
internal interface EmergencyAccessApi {

    /**
     * 获取紧急访问列表（我信任的人 + 信任我的人）
     */
    @GET("emergency-access")
    suspend fun getEmergencyAccessList(): NetworkResult<EmergencyAccessResponseJson>

    /**
     * 邀请可信联系人
     */
    @POST("emergency-access")
    suspend fun inviteEmergencyContact(
        @Body body: EmergencyAccessInviteRequestJson,
    ): NetworkResult<EmergencyAccessResponseJson.EmergencyAccessContact>

    /**
     * 接受紧急访问邀请
     */
    @POST("emergency-access/{id}/accept")
    suspend fun acceptEmergencyAccess(
        @Path("id") id: String,
        @Body body: EmergencyAccessAcceptRequestJson? = null,
    ): NetworkResult<Unit>

    /**
     * 确认紧急访问关系
     */
    @POST("emergency-access/{id}/confirm")
    suspend fun confirmEmergencyAccess(
        @Path("id") id: String,
        @Body body: EmergencyAccessConfirmRequestJson,
    ): NetworkResult<Unit>

    /**
     * 更新紧急访问设置
     */
    @PUT("emergency-access/{id}")
    suspend fun updateEmergencyAccess(
        @Path("id") id: String,
        @Body body: EmergencyAccessUpdateRequestJson,
    ): NetworkResult<Unit>

    /**
     * 删除/移除紧急访问联系人
     */
    @DELETE("emergency-access/{id}")
    suspend fun deleteEmergencyAccess(
        @Path("id") id: String,
    ): NetworkResult<Unit>

    /**
     * 获取单个紧急访问详情
     */
    @GET("emergency-access/{id}")
    suspend fun getEmergencyAccessDetails(
        @Path("id") id: String,
    ): NetworkResult<EmergencyAccessResponseJson.EmergencyAccessContact>

    /**
     * 发起紧急访问请求
     */
    @POST("emergency-access/{id}/initiate")
    suspend fun initiateEmergencyAccess(
        @Path("id") id: String,
    ): NetworkResult<Unit>

    /**
     * 批准紧急访问请求
     */
    @POST("emergency-access/{id}/approve")
    suspend fun approveEmergencyAccess(
        @Path("id") id: String,
    ): NetworkResult<Unit>

    /**
     * 拒绝紧急访问请求
     */
    @POST("emergency-access/{id}/reject")
    suspend fun rejectEmergencyAccess(
        @Path("id") id: String,
    ): NetworkResult<Unit>

    /**
     * 查看被信任人的密码库（VIEW 类型）
     */
    @GET("emergency-access/view/{id}")
    suspend fun viewEmergencyAccessVault(
        @Path("id") id: String,
    ): NetworkResult<EmergencyAccessViewVaultResponseJson>

    /**
     * 接管被信任人的账户（TAKEOVER 类型）
     * 返回被加密的主密钥和私钥
     */
    @POST("emergency-access/{id}/takeover")
    suspend fun takeoverEmergencyAccess(
        @Path("id") id: String,
    ): NetworkResult<EmergencyAccessTakeoverResponseJson>

    /**
     * 重置被接管账户的密码
     */
    @POST("emergency-access/{id}/password")
    suspend fun resetEmergencyAccessPassword(
        @Path("id") id: String,
        @Body body: EmergencyAccessPasswordRequestJson,
    ): NetworkResult<Unit>
}
