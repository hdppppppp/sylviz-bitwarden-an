package com.bitwarden.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 组织集合列表响应
 */
@Serializable
data class OrganizationCollectionsResponseJson(
    @SerialName("data")
    val data: List<OrganizationCollectionJson>,

    @SerialName("object")
    val type: String? = null,

    @SerialName("continuationToken")
    val continuationToken: String? = null,
)

/**
 * 组织集合（用于组织管理接口返回）
 */
@Serializable
data class OrganizationCollectionJson(
    @SerialName("id")
    val id: String,

    @SerialName("organizationId")
    val organizationId: String? = null,

    @SerialName("name")
    val name: String,

    @SerialName("externalId")
    val externalId: String? = null,

    @SerialName("readOnly")
    val readOnly: Boolean? = null,

    @SerialName("hidePasswords")
    val hidePasswords: Boolean? = null,

    @SerialName("manage")
    val manage: Boolean? = null,

    @SerialName("object")
    val objectType: String? = null,
)
