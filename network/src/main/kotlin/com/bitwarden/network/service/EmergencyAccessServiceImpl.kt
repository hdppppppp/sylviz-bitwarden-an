package com.bitwarden.network.service

import com.bitwarden.network.api.EmergencyAccessApi
import com.bitwarden.network.model.EmergencyAccessAcceptRequestJson
import com.bitwarden.network.model.EmergencyAccessConfirmRequestJson
import com.bitwarden.network.model.EmergencyAccessInviteRequestJson
import com.bitwarden.network.model.EmergencyAccessPasswordRequestJson
import com.bitwarden.network.model.EmergencyAccessResponseJson
import com.bitwarden.network.model.EmergencyAccessTakeoverResponseJson
import com.bitwarden.network.model.EmergencyAccessType
import com.bitwarden.network.model.EmergencyAccessUpdateRequestJson
import com.bitwarden.network.model.EmergencyAccessViewVaultResponseJson
import com.bitwarden.network.util.toResult

/**
 * 紧急访问服务实现
 * 用于对接 Vaultwarden/Bitwarden 紧急访问后端 API
 */
internal class EmergencyAccessServiceImpl(
    private val emergencyAccessApi: EmergencyAccessApi,
) : EmergencyAccessService {

    override suspend fun getEmergencyAccessList(): Result<EmergencyAccessResponseJson> {
        return emergencyAccessApi.getEmergencyAccessList().toResult()
    }

    override suspend fun inviteEmergencyContact(
        email: String,
        type: EmergencyAccessType,
        waitTimeDays: Int,
    ): Result<EmergencyAccessResponseJson.EmergencyAccessContact> {
        return emergencyAccessApi.inviteEmergencyContact(
            body = EmergencyAccessInviteRequestJson(
                email = email,
                type = type,
                waitTimeDays = waitTimeDays,
            ),
        ).toResult()
    }

    override suspend fun acceptEmergencyAccess(
        id: String,
        token: String?,
    ): Result<Unit> {
        return emergencyAccessApi.acceptEmergencyAccess(
            id = id,
            body = token?.let { EmergencyAccessAcceptRequestJson(token = it) },
        ).toResult()
    }

    override suspend fun confirmEmergencyAccess(
        id: String,
        token: String,
    ): Result<Unit> {
        return emergencyAccessApi.confirmEmergencyAccess(
            id = id,
            body = EmergencyAccessConfirmRequestJson(token = token),
        ).toResult()
    }

    override suspend fun updateEmergencyAccess(
        id: String,
        type: EmergencyAccessType,
        waitTimeDays: Int,
    ): Result<Unit> {
        return emergencyAccessApi.updateEmergencyAccess(
            id = id,
            body = EmergencyAccessUpdateRequestJson(
                type = type,
                waitTimeDays = waitTimeDays,
            ),
        ).toResult()
    }

    override suspend fun deleteEmergencyAccess(id: String): Result<Unit> {
        return emergencyAccessApi.deleteEmergencyAccess(id = id).toResult()
    }

    override suspend fun getEmergencyAccessDetails(
        id: String,
    ): Result<EmergencyAccessResponseJson.EmergencyAccessContact> {
        return emergencyAccessApi.getEmergencyAccessDetails(id = id).toResult()
    }

    override suspend fun initiateEmergencyAccess(id: String): Result<Unit> {
        return emergencyAccessApi.initiateEmergencyAccess(id = id).toResult()
    }

    override suspend fun approveEmergencyAccess(id: String): Result<Unit> {
        return emergencyAccessApi.approveEmergencyAccess(id = id).toResult()
    }

    override suspend fun rejectEmergencyAccess(id: String): Result<Unit> {
        return emergencyAccessApi.rejectEmergencyAccess(id = id).toResult()
    }

    override suspend fun viewEmergencyAccessVault(
        id: String,
    ): Result<EmergencyAccessViewVaultResponseJson> {
        return emergencyAccessApi.viewEmergencyAccessVault(id = id).toResult()
    }

    override suspend fun takeoverEmergencyAccess(
        id: String,
    ): Result<EmergencyAccessTakeoverResponseJson> {
        return emergencyAccessApi.takeoverEmergencyAccess(id = id).toResult()
    }

    override suspend fun resetEmergencyAccessPassword(
        id: String,
        newMasterPasswordHash: String,
        key: String,
    ): Result<Unit> {
        return emergencyAccessApi.resetEmergencyAccessPassword(
            id = id,
            body = EmergencyAccessPasswordRequestJson(
                newMasterPasswordHash = newMasterPasswordHash,
                key = key,
            ),
        ).toResult()
    }
}
