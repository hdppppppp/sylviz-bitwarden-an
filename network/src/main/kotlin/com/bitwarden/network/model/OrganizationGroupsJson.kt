package com.bitwarden.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 组织组（Groups）列表响应
 */
@Serializable
data class OrganizationGroupsResponseJson(
    @SerialName("data")
    val data: List<OrganizationGroupJson>,

    @SerialName("object")
    val type: String? = null,

    @SerialName("continuationToken")
    val continuationToken: String? = null,
)

/**
 * 组织组基础信息
 */
@Serializable
data class OrganizationGroupJson(
    @SerialName("id")
    val id: String,

    @SerialName("organizationId")
    val organizationId: String? = null,

    @SerialName("name")
    val name: String,

    @SerialName("accessAll")
    val accessAll: Boolean? = null,

    @SerialName("externalId")
    val externalId: String? = null,

    @SerialName("object")
    val objectType: String? = null,
)

/**
 * 新建/更新组织组请求
 */
@Serializable
data class OrganizationGroupUpsertRequestJson(
    @SerialName("name")
    val name: String,

    @SerialName("accessAll")
    val accessAll: Boolean = false,

    @SerialName("externalId")
    val externalId: String? = null,
)

/**
 * 组用户列表响应（Vaultwarden 返回成员信息列表）
 */
@Serializable
data class OrganizationGroupUsersResponseJson(
    @SerialName("data")
    val data: List<OrganizationUserDetailsJson>,

    @SerialName("object")
    val type: String? = null,

    @SerialName("continuationToken")
    val continuationToken: String? = null,
)

/**
 * 更新组用户请求
 */
@Serializable
data class OrganizationGroupUsersUpdateRequestJson(
    @SerialName("ids")
    val ids: List<String>,
)
