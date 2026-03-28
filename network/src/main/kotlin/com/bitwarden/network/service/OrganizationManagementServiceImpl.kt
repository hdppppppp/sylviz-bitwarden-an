package com.bitwarden.network.service

import com.bitwarden.network.api.AuthenticatedOrganizationApi
import com.bitwarden.network.model.OrganizationCollectionJson
import com.bitwarden.network.model.OrganizationCollectionUpsertRequestJson
import com.bitwarden.network.model.OrganizationInviteRequestJson
import com.bitwarden.network.model.OrganizationUserDetailsJson
import com.bitwarden.network.util.toResult

/**
 * 组织管理（关键功能）服务实现
 */
internal class OrganizationManagementServiceImpl(
    private val authenticatedOrganizationApi: AuthenticatedOrganizationApi,
) : OrganizationManagementService {

    override suspend fun getOrganizationMembers(
        organizationId: String,
        includeCollections: Boolean,
        includeGroups: Boolean,
    ): Result<List<OrganizationUserDetailsJson>> =
        authenticatedOrganizationApi
            .getOrganizationMembers(
                organizationId = organizationId,
                includeCollections = includeCollections,
                includeGroups = includeGroups,
            )
            .toResult()
            .map { it.data }

    override suspend fun inviteOrganizationMembers(
        organizationId: String,
        request: OrganizationInviteRequestJson,
    ): Result<Unit> =
        authenticatedOrganizationApi
            .inviteOrganizationMembers(
                organizationId = organizationId,
                body = request,
            )
            .toResult()

    override suspend fun removeOrganizationMember(
        organizationId: String,
        memberId: String,
    ): Result<Unit> =
        authenticatedOrganizationApi
            .removeOrganizationMember(
                organizationId = organizationId,
                memberId = memberId,
            )
            .toResult()

    override suspend fun getOrganizationCollections(
        organizationId: String,
    ): Result<List<OrganizationCollectionJson>> =
        authenticatedOrganizationApi
            .getOrganizationCollections(organizationId = organizationId)
            .toResult()
            .map { it.data }

    override suspend fun createOrganizationCollection(
        organizationId: String,
        name: String,
        externalId: String?,
    ): Result<OrganizationCollectionJson> =
        authenticatedOrganizationApi
            .createOrganizationCollection(
                organizationId = organizationId,
                body = OrganizationCollectionUpsertRequestJson(
                    name = name,
                    externalId = externalId,
                    groups = emptyList(),
                    users = emptyList(),
                ),
            )
            .toResult()

    override suspend fun updateOrganizationCollection(
        organizationId: String,
        collectionId: String,
        name: String,
        externalId: String?,
    ): Result<OrganizationCollectionJson> =
        authenticatedOrganizationApi
            .updateOrganizationCollection(
                organizationId = organizationId,
                collectionId = collectionId,
                body = OrganizationCollectionUpsertRequestJson(
                    name = name,
                    externalId = externalId,
                    groups = emptyList(),
                    users = emptyList(),
                ),
            )
            .toResult()

    override suspend fun deleteOrganizationCollection(
        organizationId: String,
        collectionId: String,
    ): Result<Unit> =
        authenticatedOrganizationApi
            .deleteOrganizationCollection(
                organizationId = organizationId,
                collectionId = collectionId,
            )
            .toResult()
}
