package com.likethesalad.android.buddy.modules.libraries.exceptions

import com.likethesalad.android.common.base.AndroidBuddyException
import com.likethesalad.android.common.models.libinfo.AndroidBuddyLibraryInfo

class DuplicateByteBuddyPluginException(
    val pluginNames: Set<String>,
    val librariesContainingThePlugin: List<AndroidBuddyLibraryInfo>
) : AndroidBuddyException(
    "The plugin name(s) '$pluginNames' was(were) found in multiple Android Buddy libraries: $librariesContainingThePlugin",
    null
)