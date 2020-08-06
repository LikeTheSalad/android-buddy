package com.likethesalad.android.buddylib.modules.createmetadata.action

import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import com.likethesalad.android.common.actions.BaseAction
import com.likethesalad.android.common.utils.DirectoryCleaner
import com.likethesalad.android.common.utils.Logger
import java.io.File
import java.util.Properties

@AutoFactory
class CreateAndroidBuddyLibraryMetadataAction(
    @Provided private val directoryCleaner: DirectoryCleaner,
    @Provided private val logger: Logger,
    private val pluginNames: Set<String>,
    private val outputDir: File
) : BaseAction {

    companion object {
        private const val LIBRARY_PROPERTIES_DIR = "META-INF/android-buddy-plugins"
        private const val PLUGINS_PROPERTIES_FILE_NAME = "plugins.properties"
        private const val PLUGINS_PROPERTIES_CLASSES_KEY = "plugin-classes"
    }

    override fun execute() {
        cleanUpDir()
        val propertiesFile = File(getPropertiesDir(),
            PLUGINS_PROPERTIES_FILE_NAME
        )
        val properties = Properties()
        properties.setProperty(
            PLUGINS_PROPERTIES_CLASSES_KEY,
            pluginNames.joinToString(",")
        )
        propertiesFile.writer().use {
            properties.store(it, null)
        }
        logger.debug("Plugins found: {}", pluginNames)
    }

    private fun cleanUpDir() {
        directoryCleaner.cleanDirectory(outputDir)
    }

    private fun getPropertiesDir(): File {
        val dir = File(outputDir,
            LIBRARY_PROPERTIES_DIR
        )
        dir.mkdirs()
        return dir
    }
}