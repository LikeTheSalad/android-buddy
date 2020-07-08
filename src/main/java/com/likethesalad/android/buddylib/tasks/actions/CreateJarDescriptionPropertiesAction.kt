package com.likethesalad.android.buddylib.tasks.actions

import java.io.File
import java.util.Properties

class CreateJarDescriptionPropertiesAction(
    private val pluginNames: Set<String>,
    private val outputDir: File
) {
    companion object {
        private const val PLUGINS_PROPERTIES_FILE_NAME = "plugins.properties"
        private const val PLUGINS_PROPERTIES_CLASSES_KEY = "plugin-classes"
    }

    fun execute() {
        val propertiesFile = File(outputDir, PLUGINS_PROPERTIES_FILE_NAME)
        val properties = Properties()
        properties.setProperty(
            PLUGINS_PROPERTIES_CLASSES_KEY,
            pluginNames.joinToString(",")
        )
        propertiesFile.writer().use {
            properties.store(it, null)
        }
    }
}