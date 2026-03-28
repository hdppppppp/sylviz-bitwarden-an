package com.bitwarden.network.service

import com.bitwarden.network.api.SyncApi
import com.bitwarden.network.model.SyncResponseJson
import com.bitwarden.network.util.toResult
import java.time.Instant
import java.time.format.DateTimeParseException

internal class SyncServiceImpl(
    private val syncApi: SyncApi,
) : SyncService {
    override suspend fun sync(): Result<SyncResponseJson> = syncApi
        .sync()
        .toResult()

    override suspend fun getAccountRevisionDateMillis(): Result<Long> =
        syncApi
            .getAccountRevisionDateMillis()
            .toResult()
            .mapCatching { raw ->
                // Official Bitwarden returns a Long (epoch ms).
                // Vaultwarden returns an ISO-8601 DateTime string.
                raw.toLongOrNull()
                    ?: try {
                        Instant.parse(raw).toEpochMilli()
                    } catch (_: DateTimeParseException) {
                        // If we can't parse it at all, treat as "needs sync"
                        Long.MAX_VALUE
                    }
            }
}
