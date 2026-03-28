package com.bitwarden.network.service

import com.bitwarden.network.model.SyncResponseJson

/**
 * Provides an API for querying sync endpoints.
 */
interface SyncService {
    /**
     * Make sync request to get vault items.
     */
    suspend fun sync(): Result<SyncResponseJson>

    /**
     * Make a request to get the most recent revision date for the account that is returned as an
     * epoch time in milliseconds. Vaultwarden may return a DateTime string instead of a Long.
     */
    suspend fun getAccountRevisionDateMillis(): Result<Long>
}
