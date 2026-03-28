package com.bitwarden.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 邀请组织成员请求
 * 说明：Vaultwarden 的 invite 接口支持更复杂的 permissions/collections 配置，这里先实现常用字段，
 * 通过可选字段保证兼容。
 */
@Serializable
data class OrganizationInviteRequestJson(
    @SerialName("emails")
    val emails: List<String>,

    /** 角色类型：Vaultwarden 侧通常用字符串数字（如 "0"/"1"/"2"/"3"/"4"） */
    @SerialName("type")
    val type: String,

    /** 是否可访问所有集合（部分 Vaultwarden 逻辑会从自定义角色权限推导，该字段保留兼容） */
    @SerialName("accessAll")
    val accessAll: Boolean? = null,

    /** 给被邀请者预先分配的集合权限（可选） */
    @SerialName("collections")
    val collections: List<InviteCollectionAccessJson> = emptyList(),

    /** 自定义角色权限（可选） */
    @SerialName("permissions")
    val permissions: Map<String, Boolean> = emptyMap(),
)

@Serializable
data class InviteCollectionAccessJson(
    @SerialName("id")
    val id: String,

    @SerialName("readOnly")
    val readOnly: Boolean = false,

    @SerialName("hidePasswords")
    val hidePasswords: Boolean = false,

    @SerialName("manage")
    val manage: Boolean = false,
)
