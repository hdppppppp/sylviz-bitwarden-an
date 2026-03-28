package com.bitwarden.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * 组织策略列表响应
 */
@Serializable
data class OrganizationPoliciesResponseJson(
    @SerialName("data")
    val data: List<OrganizationPolicyJson>,

    @SerialName("object")
    val type: String? = null,

    @SerialName("continuationToken")
    val continuationToken: String? = null,
)

/**
 * 组织策略（为了兼容 Vaultwarden/Bitwarden，policyData 使用 JsonObject 保留原始结构）
 */
@Serializable
data class OrganizationPolicyJson(
    @SerialName("id")
    val id: String? = null,

    @SerialName("organizationId")
    val organizationId: String? = null,

    /** 对应 PolicyTypeJson 的数值 */
    @SerialName("type")
    val type: Int,

    @SerialName("enabled")
    val enabled: Boolean,

    @SerialName("data")
    val data: JsonObject? = null,

    @SerialName("object")
    val objectType: String? = null,
)

/**
 * 更新组织策略请求
 */
@Serializable
data class OrganizationPolicyUpsertRequestJson(
    @SerialName("enabled")
    val enabled: Boolean,

    @SerialName("data")
    val data: JsonObject? = null,
)
