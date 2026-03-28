package com.bitwarden.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 组织成员列表响应
 */
@Serializable
data class OrganizationUsersResponseJson(
    @SerialName("data")
    val data: List<OrganizationUserDetailsJson>,

    @SerialName("object")
    val type: String? = null,

    @SerialName("continuationToken")
    val continuationToken: String? = null,
)

/**
 * 组织成员详情（Vaultwarden/Bitwarden 兼容字段子集）
 */
@Serializable
data class OrganizationUserDetailsJson(
    @SerialName("id")
    val id: String,

    @SerialName("userId")
    val userId: String? = null,

    @SerialName("email")
    val email: String? = null,

    @SerialName("name")
    val name: String? = null,

    /** 成员类型（owner/admin/manager/user/custom 等在服务端以数字或字符串表示，保持原样） */
    @SerialName("type")
    val memberType: Int? = null,

    /** 成员状态（invited/accepted/confirmed 等，保持原样） */
    @SerialName("status")
    val status: Int? = null,

    @SerialName("accessAll")
    val accessAll: Boolean? = null,
)
