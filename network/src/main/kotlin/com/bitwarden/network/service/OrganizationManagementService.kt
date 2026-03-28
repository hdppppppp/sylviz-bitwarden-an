package com.bitwarden.network.service

import com.bitwarden.network.model.OrganizationCollectionJson
import com.bitwarden.network.model.OrganizationInviteRequestJson
import com.bitwarden.network.model.OrganizationUserDetailsJson

/**
 * 组织管理（关键功能）
 * 目标：对接 Vaultwarden 的组织成员/集合管理接口
 */
interface OrganizationManagementService {

    /** 获取组织成员列表 */
    suspend fun getOrganizationMembers(
        organizationId: String,
        includeCollections: Boolean = false,
        includeGroups: Boolean = false,
    ): Result<List<OrganizationUserDetailsJson>>

    /** 邀请成员加入组织 */
    suspend fun inviteOrganizationMembers(
        organizationId: String,
        request: OrganizationInviteRequestJson,
    ): Result<Unit>

    /** 移除组织成员（memberId 为 membership id） */
    suspend fun removeOrganizationMember(
        organizationId: String,
        memberId: String,
    ): Result<Unit>

    /** 获取组织集合列表 */
    suspend fun getOrganizationCollections(
        organizationId: String,
    ): Result<List<OrganizationCollectionJson>>

    /** 新建组织集合 */
    suspend fun createOrganizationCollection(
        organizationId: String,
        name: String,
        externalId: String? = null,
    ): Result<OrganizationCollectionJson>

    /** 更新组织集合 */
    suspend fun updateOrganizationCollection(
        organizationId: String,
        collectionId: String,
        name: String,
        externalId: String? = null,
    ): Result<OrganizationCollectionJson>

    /** 删除组织集合 */
    suspend fun deleteOrganizationCollection(
        organizationId: String,
        collectionId: String,
    ): Result<Unit>
}
