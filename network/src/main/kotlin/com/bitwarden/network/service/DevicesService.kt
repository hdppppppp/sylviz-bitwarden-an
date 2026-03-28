package com.bitwarden.network.service

import com.bitwarden.network.model.DeviceResponseJson
import com.bitwarden.network.model.TrustedDeviceKeysResponseJson

/**
 * Provides an API for interacting with the /devices endpoints.
 */
interface DevicesService {
    suspend fun getIsKnownDevice(emailAddress: String, deviceId: String): Result<Boolean>

    suspend fun trustDevice(
        appId: String,
        encryptedUserKey: String,
        encryptedDevicePublicKey: String,
        encryptedDevicePrivateKey: String,
    ): Result<TrustedDeviceKeysResponseJson>

    /** Returns all devices for the current user. */
    suspend fun getDevices(): Result<List<DeviceResponseJson>>

    /** Removes (deauthorizes) a device by its ID. */
    suspend fun deleteDevice(id: String): Result<Unit>
}
