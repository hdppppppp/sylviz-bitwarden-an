package com.x8bit.bitwarden.ui.platform.util

import com.bitwarden.data.repository.model.Environment
import com.bitwarden.ui.platform.resource.BitwardenString
import com.bitwarden.ui.util.Text
import com.bitwarden.ui.util.asText

/**
 * Returns a human-readable display label for the given [Environment.Type].
 */
val Environment.Type.displayLabel: Text
    get() = when (this) {
        Environment.Type.US -> "官方".asText()
        Environment.Type.EU -> "官方".asText()
        Environment.Type.SELF_HOSTED -> BitwardenString.self_hosted.asText()
    }
