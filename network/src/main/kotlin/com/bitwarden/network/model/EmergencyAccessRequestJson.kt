package com.bitwarden.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 邀请可信联系人请求
 *
 * @property email 被邀请人邮箱
 * @property type 访问类型（0=查看，1=接管）
 * @property waitTimeDays 等待时间（天）
 */
@Serializable
data class EmergencyAccessInviteRequestJson(
    @SerialName("email")
    val email: String,

    @SerialName("type")
    val type: EmergencyAccessType,

    @SerialName("waitTimeDays")
    val waitTimeDays: Int,
)

/**
 * 接受紧急访问邀请请求
 *
 * @property token 验证令牌（用于确认关系）
 */
@Serializable
data class EmergencyAccessAcceptRequestJson(
    @SerialName("token")
    val token: String? = null,
)

/**
 * 确认紧急访问关系请求
 *
 * @property token 验证令牌
 */
@Serializable
data class EmergencyAccessConfirmRequestJson(
    @SerialName("token")
    val token: String,
)

/**
 * 更新紧急访问设置请求
 *
 * @property type 访问类型
 * @property waitTimeDays 等待时间（天）
 */
@Serializable
data class EmergencyAccessUpdateRequestJson(
    @SerialName("type")
    val type: EmergencyAccessType,

    @SerialName("waitTimeDays")
    val waitTimeDays: Int,
)

/**
 * 发起紧急访问请求
 * （无需额外参数，通过路径ID标识）
 */
@Serializable
data class EmergencyAccessInitiateRequestJson(
    @SerialName("email")
    val email: String? = null,
)

/**
 * 批准紧急访问请求
 * （无需额外参数，通过路径ID标识）
 */
@Serializable
object EmergencyAccessApproveRequestJson

/**
 * 拒绝紧急访问请求
 * （无需额外参数，通过路径ID标识）
 */
@Serializable
object EmergencyAccessRejectRequestJson
