package com.bitwarden.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a device from the server.
 */
@Serializable
data class DeviceResponseJson(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("type")
    val type: Int,
    @SerialName("identifier")
    val identifier: String,
    @SerialName("creationDate")
    val creationDate: String,
    @SerialName("revisionDate")
    val revisionDate: String?,
    @SerialName("isTrusted")
    val isTrusted: Boolean,
)
