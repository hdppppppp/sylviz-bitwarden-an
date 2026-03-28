package com.x8bit.bitwarden.data.organization.repository

import com.bitwarden.network.model.OrganizationCollectionJson
import com.bitwarden.network.model.OrganizationGroupJson
import com.bitwarden.network.model.OrganizationPolicyJson
import com.bitwarden.network.model.OrganizationUserDetailsJson
import kotlinx.serialization.json.JsonObject

/**
 * 组织管理仓库接口
 * 封装组织成员、集合、组、策略的管理操作
 */
interface OrganizationRepository {

    /**
     * 获取组织成员列表
     */
    suspend fun getOrganizationMembers(
        organizationId: String,
        includeCollections: Boolean = false,
        includeGroups: Boolean = false,
    ): Result<List<OrganizationUserDetailsJson>>

    /**
     * 移除组织成员
     */
    suspend fun removeOrganizationMember(
        organizationId: String,
        memberId: String,
    ): Result<Unit>

    /**
     * 获取组织集合列表
     */
    suspend fun getOrganizationCollections(
        organizationId: String,
    ): Result<List<OrganizationCollectionJson>>

    /**
     * 创建组织集合
     */
    suspend fun createOrganizationCollection(
        organizationId: String,
        name: String,
        externalId: String? = null,
    ): Result<OrganizationCollectionJson>

    /**
     * 删除组织集合
     */
    suspend fun deleteOrganizationCollection(
        organizationId: String,
        collectionId: String,
    ): Result<Unit>

    /**
     * 获取组织组列表
     */
    suspend fun getOrganizationGroups(
        organizationId: String,
    ): Result<List<OrganizationGroupJson>>

    /**
     * 创建组织组
     */
    suspend fun createOrganizationGroup(
        organizationId: String,
        name: String,
        accessAll: Boolean = false,
        externalId: String? = null,
    ): Result<OrganizationGroupJson>

    /**
     * 删除组织组
     */
    suspend fun deleteOrganizationGroup(
        organizationId: String,
        groupId: String,
    ): Result<Unit>

    /**
     * 获取组织策略列表
     */
    suspend fun getOrganizationPolicies(
        organizationId: String,
    ): Result<List<OrganizationPolicyJson>>

    /**
     * 更新组织策略
     */
    suspend fun updateOrganizationPolicy(
        organizationId: String,
        policyType: Int,
        enabled: Boolean,
        data: JsonObject? = null,
    ): Result<OrganizationPolicyJson>
}
