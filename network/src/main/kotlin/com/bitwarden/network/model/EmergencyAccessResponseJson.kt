package com.bitwarden.network.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

/**
 * 紧急访问响应模型
 * 包含可信联系人和被信任人列表
 *
 * @property trustedContacts 我信任的人（可信联系人）列表
 * @property trustees 信任我的人（被信任人）列表
 */
@Serializable
data class EmergencyAccessResponseJson(
    @SerialName("trusted")
    val trustedContacts: List<EmergencyAccessContact>,

    @SerialName("grantees")
    val trustees: List<EmergencyAccessContact>,
) {
    /**
     * 紧急访问联系人
     *
     * @property id 紧急访问关系ID
     * @property grantorId 授权人ID（信任我的人）
     * @property granteeId 被授权人ID（我信任的人）
     * @property type 访问类型（0=查看，1=接管）
     * @property status 状态（0=已邀请，1=已接受，2=已确认，3=紧急访问已发起，4=已批准，5=已拒绝）
     * @property waitTimeDays 等待时间（天）
     * @property creationDate 创建日期
     * @property modificationDate 修改日期
     * @property email 联系人邮箱
     * @property name 联系人姓名
     * @property objectType 对象类型
     */
    @Serializable
    data class EmergencyAccessContact(
        @SerialName("id")
        val id: String,

        @SerialName("grantorId")
        val grantorId: String? = null,

        @SerialName("granteeId")
        val granteeId: String? = null,

        @SerialName("type")
        val type: EmergencyAccessType,

        @SerialName("status")
        val status: EmergencyAccessStatus,

        @SerialName("waitTimeDays")
        val waitTimeDays: Int? = null,

        @SerialName("creationDate")
        @Contextual
        val creationDate: Instant? = null,

        @SerialName("modificationDate")
        @Contextual
        val modificationDate: Instant? = null,

        @SerialName("email")
        val email: String? = null,

        @SerialName("name")
        val name: String? = null,

        @SerialName("object")
        val objectType: String? = null,
    )
}

/**
 * 紧急访问类型
 */
@Serializable
enum class EmergencyAccessType {
    @SerialName("0")
    VIEW,      // 查看密码库

    @SerialName("1")
    TAKEOVER,  // 接管账户
}

/**
 * 紧急访问状态
 */
@Serializable
enum class EmergencyAccessStatus {
    @SerialName("0")
    INVITED,      // 已邀请

    @SerialName("1")
    ACCEPTED,     // 已接受

    @SerialName("2")
    CONFIRMED,    // 已确认

    @SerialName("3")
    RECOVERY_INITIATED,  // 紧急访问已发起

    @SerialName("4")
    RECOVERY_APPROVED,   // 已批准

    @SerialName("5")
    RECOVERY_REJECTED,   // 已拒绝
}
