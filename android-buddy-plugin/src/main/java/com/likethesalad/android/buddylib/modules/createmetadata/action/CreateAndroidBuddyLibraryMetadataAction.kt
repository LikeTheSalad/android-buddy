package com.likethesalad.android.buddylib.modules.createmetadata.action

import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import com.likethesalad.android.common.actions.BaseAction
import com.likethesalad.android.common.models.AndroidBuddyLibraryInfo
import com.likethesalad.android.common.utils.Constants.LIBRARY_METADATA_DIR
import com.likethesalad.android.common.utils.Constants.PLUGINS_METADATA_CLASS_NAME
import com.likethesalad.android.common.utils.Constants.PLUGINS_METADATA_FILE_NAME
import com.likethesalad.android.common.utils.DirectoryCleaner
import com.likethesalad.android.common.utils.Logger
import net.bytebuddy.ByteBuddy
import net.bytebuddy.implementation.FixedValue
import net.bytebuddy.matcher.ElementMatchers
import java.io.File

@AutoFactory
class CreateAndroidBuddyLibraryMetadataAction(
    @Provided private val directoryCleaner: DirectoryCleaner,
    @Provided private val logger: Logger,
    private val info: AndroidBuddyLibraryInfo,
    private val outputDir: File
) : BaseAction {

    override fun execute() {
        cleanUpDir()
        val metadataFile = File(
            getPropertiesDir(),
            PLUGINS_METADATA_FILE_NAME
        )
        val bytes = ByteBuddy().subclass(Any::class.java)
            .name(PLUGINS_METADATA_CLASS_NAME)
            .method(ElementMatchers.isToString())
            .intercept(FixedValue.value(pluginNames.joinToString(",")))
            .make()
            .bytes

        metadataFile.writeBytes(bytes)

        logger.debug("Transformations found: {}", pluginNames)
    }

    private fun cleanUpDir() {
        directoryCleaner.cleanDirectory(outputDir)
    }

    private fun getPropertiesDir(): File {
        val dir = File(
            outputDir,
            LIBRARY_METADATA_DIR
        )
        dir.mkdirs()
        return dir
    }
}