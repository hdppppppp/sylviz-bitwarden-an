package com.x8bit.bitwarden.data.organization.repository

import com.bitwarden.network.BitwardenServiceClient
import com.bitwarden.network.model.OrganizationCollectionJson
import com.bitwarden.network.model.OrganizationGroupJson
import com.bitwarden.network.model.OrganizationGroupUpsertRequestJson
import com.bitwarden.network.model.OrganizationPolicyJson
import com.bitwarden.network.model.OrganizationPolicyUpsertRequestJson
import com.bitwarden.network.model.OrganizationUserDetailsJson
import com.bitwarden.network.service.OrganizationAdminService
import com.bitwarden.network.service.OrganizationManagementService
import kotlinx.serialization.json.JsonObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 组织管理仓库实现
 * 通过 BitwardenServiceClient 调用网络服务
 */
@Singleton
class OrganizationRepositoryImpl @Inject constructor(
    private val bitwardenServiceClient: BitwardenServiceClient,
) : OrganizationRepository {

    private val managementService: OrganizationManagementService
        get() = bitwardenServiceClient.organizationManagementService

    private val adminService: OrganizationAdminService
        get() = bitwardenServiceClient.organizationAdminService

    override suspend fun getOrganizationMembers(
        organizationId: String,
        includeCollections: Boolean,
        includeGroups: Boolean,
    ): Result<List<OrganizationUserDetailsJson>> {
        return managementService.getOrganizationMembers(
            organizationId = organizationId,
            includeCollections = includeCollections,
            includeGroups = includeGroups,
        )
    }

    override suspend fun removeOrganizationMember(
        organizationId: String,
        memberId: String,
    ): Result<Unit> {
        return managementService.removeOrganizationMember(
            organizationId = organizationId,
            memberId = memberId,
        )
    }

    override suspend fun getOrganizationCollections(
        organizationId: String,
    ): Result<List<OrganizationCollectionJson>> {
        return managementService.getOrganizationCollections(
            organizationId = organizationId,
        )
    }

    override suspend fun createOrganizationCollection(
        organizationId: String,
        name: String,
        externalId: String?,
    ): Result<OrganizationCollectionJson> {
        return managementService.createOrganizationCollection(
            organizationId = organizationId,
            name = name,
            externalId = externalId,
        )
    }

    override suspend fun deleteOrganizationCollection(
        organizationId: String,
        collectionId: String,
    ): Result<Unit> {
        return managementService.deleteOrganizationCollection(
            organizationId = organizationId,
            collectionId = collectionId,
        )
    }

    override suspend fun getOrganizationGroups(
        organizationId: String,
    ): Result<List<OrganizationGroupJson>> {
        return adminService.getOrganizationGroups(
            organizationId = organizationId,
        )
    }

    override suspend fun createOrganizationGroup(
        organizationId: String,
        name: String,
        accessAll: Boolean,
        externalId: String?,
    ): Result<OrganizationGroupJson> {
        return adminService.createOrganizationGroup(
            organizationId = organizationId,
            request = OrganizationGroupUpsertRequestJson(
                name = name,
                accessAll = accessAll,
                externalId = externalId,
            ),
        )
    }

    override suspend fun deleteOrganizationGroup(
        organizationId: String,
        groupId: String,
    ): Result<Unit> {
        return adminService.deleteOrganizationGroup(
            organizationId = organizationId,
            groupId = groupId,
        )
    }

    override suspend fun getOrganizationPolicies(
        organizationId: String,
    ): Result<List<OrganizationPolicyJson>> {
        return adminService.getOrganizationPolicies(
            organizationId = organizationId,
        )
    }

    override suspend fun updateOrganizationPolicy(
        organizationId: String,
        policyType: Int,
        enabled: Boolean,
        data: JsonObject?,
    ): Result<OrganizationPolicyJson> {
        // 先获取当前策略以获取完整信息
        val currentPolicyResult = adminService.getOrganizationPolicy(
            organizationId = organizationId,
            policyType = policyType,
        )
        
        return currentPolicyResult.mapCatching { currentPolicy ->
            adminService.updateOrganizationPolicy(
                organizationId = organizationId,
                policyType = policyType,
                request = OrganizationPolicyUpsertRequestJson(
                    enabled = enabled,
                    data = data,
                ),
            ).getOrThrow()
            currentPolicy.copy(enabled = enabled, data = data)
        }
    }
}
