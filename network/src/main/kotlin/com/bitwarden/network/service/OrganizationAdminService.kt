package com.bitwarden.network.service

import com.bitwarden.network.model.BulkIdsRequestJson
import com.bitwarden.network.model.OrganizationGroupJson
import com.bitwarden.network.model.OrganizationGroupUpsertRequestJson
import com.bitwarden.network.model.OrganizationPolicyJson
import com.bitwarden.network.model.OrganizationPolicyUpsertRequestJson

/**
 * 组织管理（全量接口对齐 Vaultwarden）
 * 说明：此服务聚合组织级管理能力（组、策略等）。
 */
interface OrganizationAdminService {

    /** 获取组织组列表 */
    suspend fun getOrganizationGroups(organizationId: String): Result<List<OrganizationGroupJson>>

    /** 新建组织组 */
    suspend fun createOrganizationGroup(
        organizationId: String,
        request: OrganizationGroupUpsertRequestJson,
    ): Result<OrganizationGroupJson>

    /** 更新组织组 */
    suspend fun updateOrganizationGroup(
        organizationId: String,
        groupId: String,
        request: OrganizationGroupUpsertRequestJson,
    ): Result<OrganizationGroupJson>

    /** 删除组织组 */
    suspend fun deleteOrganizationGroup(
        organizationId: String,
        groupId: String,
    ): Result<Unit>

    /** 获取组用户列表 */
    suspend fun getOrganizationGroupUsers(
        organizationId: String,
        groupId: String,
    ): Result<List<com.bitwarden.network.model.OrganizationUserDetailsJson>>

    /** 设置组用户（全量覆盖） */
    suspend fun updateOrganizationGroupUsers(
        organizationId: String,
        groupId: String,
        memberIds: List<String>,
    ): Result<Unit>

    /** 获取组织策略列表 */
    suspend fun getOrganizationPolicies(organizationId: String): Result<List<OrganizationPolicyJson>>

    /** 获取单个组织策略 */
    suspend fun getOrganizationPolicy(
        organizationId: String,
        policyType: Int,
    ): Result<OrganizationPolicyJson>

    /** 更新组织策略 */
    suspend fun updateOrganizationPolicy(
        organizationId: String,
        policyType: Int,
        request: OrganizationPolicyUpsertRequestJson,
    ): Result<Unit>

    /** 批量删除成员（Vaultwarden 支持） */
    suspend fun bulkRemoveOrganizationMembers(
        organizationId: String,
        request: BulkIdsRequestJson,
    ): Result<String>
}
