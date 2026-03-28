package com.bitwarden.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 新建/更新组织集合请求
 * Vaultwarden 需要 users/groups 数组（即使为空）
 */
@Serializable
data class OrganizationCollectionUpsertRequestJson(
    @SerialName("name")
    val name: String,

    @SerialName("externalId")
    val externalId: String? = null,

    @SerialName("groups")
    val groups: List<CollectionGroupAccessJson> = emptyList(),

    @SerialName("users")
    val users: List<CollectionUserAccessJson> = emptyList(),
)

@Serializable
data class CollectionGroupAccessJson(
    @SerialName("id")
    val id: String,

    @SerialName("readOnly")
    val readOnly: Boolean = false,

    @SerialName("hidePasswords")
    val hidePasswords: Boolean = false,

    @SerialName("manage")
    val manage: Boolean = false,
)

@Serializable
data class CollectionUserAccessJson(
    @SerialName("id")
    val id: String,

    @SerialName("readOnly")
    val readOnly: Boolean = false,

    @SerialName("hidePasswords")
    val hidePasswords: Boolean = false,

    @SerialName("manage")
    val manage: Boolean = false,
)
