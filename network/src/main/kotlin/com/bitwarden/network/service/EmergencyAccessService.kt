package com.bitwarden.network.service

import com.bitwarden.network.model.EmergencyAccessResponseJson
import com.bitwarden.network.model.EmergencyAccessTakeoverResponseJson
import com.bitwarden.network.model.EmergencyAccessType
import com.bitwarden.network.model.EmergencyAccessViewVaultResponseJson

/**
 * 紧急访问服务接口
 * 提供与 Vaultwarden/Bitwarden 紧急访问功能相关的 API
 */
interface EmergencyAccessService {

    /**
     * 获取紧急访问列表
     * 返回我信任的人和我被谁信任
     */
    suspend fun getEmergencyAccessList(): Result<EmergencyAccessResponseJson>

    /**
     * 邀请可信联系人
     *
     * @param email 被邀请人邮箱
     * @param type 访问类型（查看或接管）
     * @param waitTimeDays 等待时间（天）
     */
    suspend fun inviteEmergencyContact(
        email: String,
        type: EmergencyAccessType,
        waitTimeDays: Int,
    ): Result<EmergencyAccessResponseJson.EmergencyAccessContact>

    /**
     * 接受紧急访问邀请
     *
     * @param id 紧急访问关系ID
     * @param token 验证令牌（可选）
     */
    suspend fun acceptEmergencyAccess(
        id: String,
        token: String? = null,
    ): Result<Unit>

    /**
     * 确认紧急访问关系
     *
     * @param id 紧急访问关系ID
     * @param token 验证令牌
     */
    suspend fun confirmEmergencyAccess(
        id: String,
        token: String,
    ): Result<Unit>

    /**
     * 更新紧急访问设置
     *
     * @param id 紧急访问关系ID
     * @param type 访问类型
     * @param waitTimeDays 等待时间（天）
     */
    suspend fun updateEmergencyAccess(
        id: String,
        type: EmergencyAccessType,
        waitTimeDays: Int,
    ): Result<Unit>

    /**
     * 删除/移除紧急访问联系人
     *
     * @param id 紧急访问关系ID
     */
    suspend fun deleteEmergencyAccess(id: String): Result<Unit>

    /**
     * 获取单个紧急访问详情
     *
     * @param id 紧急访问关系ID
     */
    suspend fun getEmergencyAccessDetails(
        id: String,
    ): Result<EmergencyAccessResponseJson.EmergencyAccessContact>

    /**
     * 发起紧急访问请求
     *
     * @param id 紧急访问关系ID
     */
    suspend fun initiateEmergencyAccess(id: String): Result<Unit>

    /**
     * 批准紧急访问请求
     *
     * @param id 紧急访问关系ID
     */
    suspend fun approveEmergencyAccess(id: String): Result<Unit>

    /**
     * 拒绝紧急访问请求
     *
     * @param id 紧急访问关系ID
     */
    suspend fun rejectEmergencyAccess(id: String): Result<Unit>

    /**
     * 查看被信任人的密码库（VIEW 类型）
     *
     * @param id 紧急访问关系ID
     */
    suspend fun viewEmergencyAccessVault(
        id: String,
    ): Result<EmergencyAccessViewVaultResponseJson>

    /**
     * 接管被信任人的账户（TAKEOVER 类型）
     *
     * @param id 紧急访问关系ID
     */
    suspend fun takeoverEmergencyAccess(
        id: String,
    ): Result<EmergencyAccessTakeoverResponseJson>

    /**
     * 重置被接管账户的密码
     *
     * @param id 紧急访问关系ID
     * @param newMasterPasswordHash 新主密码哈希
     * @param key 被加密的密钥
     */
    suspend fun resetEmergencyAccessPassword(
        id: String,
        newMasterPasswordHash: String,
        key: String,
    ): Result<Unit>
}
