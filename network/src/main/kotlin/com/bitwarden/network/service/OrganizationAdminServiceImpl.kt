package com.bitwarden.network.service

import com.bitwarden.network.api.AuthenticatedOrganizationApi
import com.bitwarden.network.model.BulkIdsRequestJson
import com.bitwarden.network.model.OrganizationGroupJson
import com.bitwarden.network.model.OrganizationGroupUpsertRequestJson
import com.bitwarden.network.model.OrganizationPolicyJson
import com.bitwarden.network.model.OrganizationPolicyUpsertRequestJson
import com.bitwarden.network.util.toResult

/**
 * 组织管理（全量接口对齐 Vaultwarden）服务实现
 */
internal class OrganizationAdminServiceImpl(
    private val authenticatedOrganizationApi: AuthenticatedOrganizationApi,
) : OrganizationAdminService {

    override suspend fun getOrganizationGroups(organizationId: String): Result<List<OrganizationGroupJson>> =
        authenticatedOrganizationApi
            .getOrganizationGroups(organizationId = organizationId)
            .toResult()
            .map { it.data }

    override suspend fun createOrganizationGroup(
        organizationId: String,
        request: OrganizationGroupUpsertRequestJson,
    ): Result<OrganizationGroupJson> =
        authenticatedOrganizationApi
            .createOrganizationGroup(
                organizationId = organizationId,
                body = request,
            )
            .toResult()

    override suspend fun updateOrganizationGroup(
        organizationId: String,
        groupId: String,
        request: OrganizationGroupUpsertRequestJson,
    ): Result<OrganizationGroupJson> =
        authenticatedOrganizationApi
            .updateOrganizationGroup(
                organizationId = organizationId,
                groupId = groupId,
                body = request,
            )
            .toResult()

    override suspend fun deleteOrganizationGroup(
        organizationId: String,
        groupId: String,
    ): Result<Unit> =
        authenticatedOrganizationApi
            .deleteOrganizationGroup(
                organizationId = organizationId,
                groupId = groupId,
            )
            .toResult()

    override suspend fun getOrganizationGroupUsers(
        organizationId: String,
        groupId: String,
    ): Result<List<com.bitwarden.network.model.OrganizationUserDetailsJson>> =
        authenticatedOrganizationApi
            .getOrganizationGroupUsers(
                organizationId = organizationId,
                groupId = groupId,
            )
            .toResult()
            .map { it.data }

    override suspend fun updateOrganizationGroupUsers(
        organizationId: String,
        groupId: String,
        memberIds: List<String>,
    ): Result<Unit> =
        authenticatedOrganizationApi
            .updateOrganizationGroupUsers(
                organizationId = organizationId,
                groupId = groupId,
                body = BulkIdsRequestJson(ids = memberIds),
            )
            .toResult()

    override suspend fun getOrganizationPolicies(organizationId: String): Result<List<OrganizationPolicyJson>> =
        authenticatedOrganizationApi
            .getOrganizationPolicies(organizationId = organizationId)
            .toResult()
            .map { it.data }

    override suspend fun getOrganizationPolicy(
        organizationId: String,
        policyType: Int,
    ): Result<OrganizationPolicyJson> =
        authenticatedOrganizationApi
            .getOrganizationPolicy(
                organizationId = organizationId,
                policyType = policyType,
            )
            .toResult()

    override suspend fun updateOrganizationPolicy(
        organizationId: String,
        policyType: Int,
        request: OrganizationPolicyUpsertRequestJson,
    ): Result<Unit> =
        authenticatedOrganizationApi
            .updateOrganizationPolicy(
                organizationId = organizationId,
                policyType = policyType,
                body = request,
            )
            .toResult()

    override suspend fun bulkRemoveOrganizationMembers(
        organizationId: String,
        request: BulkIdsRequestJson,
    ): Result<String> =
        authenticatedOrganizationApi
            .bulkRemoveOrganizationMembers(
                organizationId = organizationId,
                body = request,
            )
            .toResult()
            .map { "ok" }
}
