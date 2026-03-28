package com.bitwarden.data.datasource.disk.model

import com.bitwarden.network.model.ConfigResponseJson
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A higher-level wrapper around [ConfigResponseJson] that provides a timestamp
 * to check if a sync is necessary
 *
 * @property lastSync The [Long] of the last sync.
 * @property serverData The raw [ConfigResponseJson] that contains specific data of the
 * server configuration
 */
@Serializable
data class ServerConfig(
    @SerialName("lastSync")
    val lastSync: Long,

    @SerialName("serverData")
    val serverData: ConfigResponseJson,
) {
    /**
     * Whether the server is an official Bitwarden server or not.
     */
    val isOfficialBitwardenServer: Boolean
        get() = serverData.server == null

    /**
     * Whether the server is a Vaultwarden server.
     */
    val isVaultwardenServer: Boolean
        get() = serverData.vaultwarden != null ||
            serverData.server?.name?.contains("Vaultwarden", ignoreCase = true) == true

    /**
     * Whether emergency access is enabled on this server.
     * For Vaultwarden, checks the specific setting; for others, assumes true.
     */
    val isEmergencyAccessEnabled: Boolean
        get() = serverData.vaultwarden?.emergencyAccessAllowed != false

    /**
     * Maximum emergency access wait time in days.
     * Only applicable for Vaultwarden servers.
     */
    val maxEmergencyAccessWaitTimeDays: Int?
        get() = serverData.vaultwarden?.maxEmergencyAccessWaitTimeDays
}
