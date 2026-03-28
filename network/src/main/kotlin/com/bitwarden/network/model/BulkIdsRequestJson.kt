package com.bitwarden.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 通用批量 ID 请求
 */
@Serializable
data class BulkIdsRequestJson(
    @SerialName("ids")
    val ids: List<String>,
)
