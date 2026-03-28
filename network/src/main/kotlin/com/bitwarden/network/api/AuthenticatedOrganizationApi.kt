package com.bitwarden.network.api

import com.bitwarden.network.model.NetworkResult
import com.bitwarden.network.model.BulkIdsRequestJson
import com.bitwarden.network.model.OrganizationCollectionsResponseJson
import com.bitwarden.network.model.OrganizationAutoEnrollStatusResponseJson
import com.bitwarden.network.model.OrganizationCollectionJson
import com.bitwarden.network.model.OrganizationCollectionUpsertRequestJson
import com.bitwarden.network.model.OrganizationInviteRequestJson
import com.bitwarden.network.model.OrganizationGroupJson
import com.bitwarden.network.model.OrganizationGroupUpsertRequestJson
import com.bitwarden.network.model.OrganizationGroupUsersResponseJson
import com.bitwarden.network.model.OrganizationGroupsResponseJson
import com.bitwarden.network.model.OrganizationKeysResponseJson
import com.bitwarden.network.model.OrganizationPoliciesResponseJson
import com.bitwarden.network.model.OrganizationPolicyJson
import com.bitwarden.network.model.OrganizationPolicyUpsertRequestJson
import com.bitwarden.network.model.OrganizationResetPasswordEnrollRequestJson
import com.bitwarden.network.model.OrganizationUsersResponseJson
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Defines raw calls under the authenticated /organizations API.
 */
internal interface AuthenticatedOrganizationApi {
    /**
     * Enrolls this user in the organization's password reset.
     */
    @PUT("/organizations/{orgId}/users/{userId}/reset-password-enrollment")
    suspend fun organizationResetPasswordEnroll(
        @Path("orgId") organizationId: String,
        @Path("userId") userId: String,
        @Body body: OrganizationResetPasswordEnrollRequestJson,
    ): NetworkResult<Unit>

    /**
     * Checks whether this organization auto enrolls users in password reset.
     */
    @GET("/organizations/{identifier}/auto-enroll-status")
    suspend fun getOrganizationAutoEnrollResponse(
        @Path("identifier") organizationIdentifier: String,
    ): NetworkResult<OrganizationAutoEnrollStatusResponseJson>

    /**
     * Gets the public and private keys for this organization.
     */
    @GET("/organizations/{id}/keys")
    suspend fun getOrganizationKeys(
        @Path("id") organizationId: String,
    ): NetworkResult<OrganizationKeysResponseJson>

    /**
     * Leaves the organization
     */
    @POST("/organizations/{id}/leave")
    suspend fun leaveOrganization(
        @Path("id") organizationId: String,
    ): NetworkResult<Unit>

    /**
     * Revokes self from organization
     */
    @PUT("/organizations/{orgId}/users/revoke-self")
    suspend fun revokeFromOrganization(
        @Path("orgId") organizationId: String,
    ): NetworkResult<Unit>

    /**
     * 获取组织成员列表。
     */
    @GET("/organizations/{orgId}/users")
    suspend fun getOrganizationMembers(
        @Path("orgId") organizationId: String,
        @Query("includeCollections") includeCollections: Boolean = false,
        @Query("includeGroups") includeGroups: Boolean = false,
    ): NetworkResult<OrganizationUsersResponseJson>

    /**
     * 邀请成员加入组织。
     */
    @POST("/organizations/{orgId}/users/invite")
    suspend fun inviteOrganizationMembers(
        @Path("orgId") organizationId: String,
        @Body body: OrganizationInviteRequestJson,
    ): NetworkResult<Unit>

    /**
     * 移除组织成员（memberId 为 membership id）。
     */
    @DELETE("/organizations/{orgId}/users/{memberId}")
    suspend fun removeOrganizationMember(
        @Path("orgId") organizationId: String,
        @Path("memberId") memberId: String,
    ): NetworkResult<Unit>

    /**
     * 批量移除组织成员。
     */
    @HTTP(method = "DELETE", path = "/organizations/{orgId}/users", hasBody = true)
    suspend fun bulkRemoveOrganizationMembers(
        @Path("orgId") organizationId: String,
        @Body body: BulkIdsRequestJson,
    ): NetworkResult<Unit>

    /**
     * 获取组织集合列表。
     */
    @GET("/organizations/{orgId}/collections")
    suspend fun getOrganizationCollections(
        @Path("orgId") organizationId: String,
    ): NetworkResult<OrganizationCollectionsResponseJson>

    /**
     * 新建组织集合。
     */
    @POST("/organizations/{orgId}/collections")
    suspend fun createOrganizationCollection(
        @Path("orgId") organizationId: String,
        @Body body: OrganizationCollectionUpsertRequestJson,
    ): NetworkResult<OrganizationCollectionJson>

    /**
     * 更新组织集合。
     */
    @PUT("/organizations/{orgId}/collections/{collectionId}")
    suspend fun updateOrganizationCollection(
        @Path("orgId") organizationId: String,
        @Path("collectionId") collectionId: String,
        @Body body: OrganizationCollectionUpsertRequestJson,
    ): NetworkResult<OrganizationCollectionJson>

    /**
     * 删除组织集合。
     */
    @DELETE("/organizations/{orgId}/collections/{collectionId}")
    suspend fun deleteOrganizationCollection(
        @Path("orgId") organizationId: String,
        @Path("collectionId") collectionId: String,
    ): NetworkResult<Unit>

    /**
     * 获取组织组列表。
     */
    @GET("/organizations/{orgId}/groups")
    suspend fun getOrganizationGroups(
        @Path("orgId") organizationId: String,
    ): NetworkResult<OrganizationGroupsResponseJson>

    /**
     * 新建组织组。
     */
    @POST("/organizations/{orgId}/groups")
    suspend fun createOrganizationGroup(
        @Path("orgId") organizationId: String,
        @Body body: OrganizationGroupUpsertRequestJson,
    ): NetworkResult<OrganizationGroupJson>

    /**
     * 更新组织组。
     */
    @PUT("/organizations/{orgId}/groups/{groupId}")
    suspend fun updateOrganizationGroup(
        @Path("orgId") organizationId: String,
        @Path("groupId") groupId: String,
        @Body body: OrganizationGroupUpsertRequestJson,
    ): NetworkResult<OrganizationGroupJson>

    /**
     * 删除组织组。
     */
    @DELETE("/organizations/{orgId}/groups/{groupId}")
    suspend fun deleteOrganizationGroup(
        @Path("orgId") organizationId: String,
        @Path("groupId") groupId: String,
    ): NetworkResult<Unit>

    /**
     * 获取组织组用户列表。
     */
    @GET("/organizations/{orgId}/groups/{groupId}/users")
    suspend fun getOrganizationGroupUsers(
        @Path("orgId") organizationId: String,
        @Path("groupId") groupId: String,
    ): NetworkResult<OrganizationGroupUsersResponseJson>

    /**
     * 设置组织组用户（全量覆盖）。
     */
    @PUT("/organizations/{orgId}/groups/{groupId}/users")
    suspend fun updateOrganizationGroupUsers(
        @Path("orgId") organizationId: String,
        @Path("groupId") groupId: String,
        @Body body: BulkIdsRequestJson,
    ): NetworkResult<Unit>

    /**
     * 获取组织策略列表。
     */
    @GET("/organizations/{orgId}/policies")
    suspend fun getOrganizationPolicies(
        @Path("orgId") organizationId: String,
    ): NetworkResult<OrganizationPoliciesResponseJson>

    /**
     * 获取单个组织策略（polType 为数值类型）。
     */
    @GET("/organizations/{orgId}/policies/{polType}")
    suspend fun getOrganizationPolicy(
        @Path("orgId") organizationId: String,
        @Path("polType") policyType: Int,
    ): NetworkResult<OrganizationPolicyJson>

    /**
     * 更新组织策略。
     */
    @PUT("/organizations/{orgId}/policies/{polType}")
    suspend fun updateOrganizationPolicy(
        @Path("orgId") organizationId: String,
        @Path("polType") policyType: Int,
        @Body body: OrganizationPolicyUpsertRequestJson,
    ): NetworkResult<Unit>
}
