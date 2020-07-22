package com.likethesalad.android.common.utils

import com.likethesalad.android.generated.BuildConfig

object Constants {
    const val LIBRARY_PROPERTIES_DIR = "META-INF/android-buddy-plugins"
    const val PLUGINS_PROPERTIES_FILE_NAME = "plugins.properties"
    const val PLUGINS_PROPERTIES_CLASSES_KEY = "plugin-classes"
    const val BYTE_BUDDY_DEPENDENCY_VERSION = BuildConfig.BYTE_BUDDY_VERSION
}