package com.likethesalad.android.common.utils

import com.likethesalad.android.buddy.plugin.generated.BuildConfig

object Constants {
    const val LIBRARY_PROPERTIES_DIR = "META-INF/android-buddy-plugins"
    const val PLUGINS_PROPERTIES_FILE_NAME = "plugins.properties"
    const val PLUGINS_PROPERTIES_CLASSES_KEY = "plugin-classes"
    const val ANDROID_BUDDY_TOOLS_DEPENDENCY_URI = BuildConfig.ANDROID_BUDDY_TOOLS_URI
}