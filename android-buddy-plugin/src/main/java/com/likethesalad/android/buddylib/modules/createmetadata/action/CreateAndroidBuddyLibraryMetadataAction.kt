package com.likethesalad.android.buddylib.modules.createmetadata.action

import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import com.likethesalad.android.common.actions.BaseAction
import com.likethesalad.android.common.models.libinfo.AndroidBuddyLibraryInfo
import com.likethesalad.android.common.models.libinfo.LibraryInfoMapper
import com.likethesalad.android.common.utils.Constants
import com.likethesalad.android.common.utils.Constants.LIBRARY_METADATA_DIR
import com.likethesalad.android.common.utils.DirectoryCleaner
import com.likethesalad.android.common.utils.Logger
import java.io.File

@AutoFactory
class CreateAndroidBuddyLibraryMetadataAction(
    @Provided private val directoryCleaner: DirectoryCleaner,
    @Provided private val logger: Logger,
    @Provided private val libraryInfoMapper: LibraryInfoMapper,
    private val info: AndroidBuddyLibraryInfo,
    private val outputDir: File
) : BaseAction {

    override fun execute() {
        cleanUpDir()
        val classInfo = libraryInfoMapper.convertToClassByteArray(info)

        val metadataFile = File(
            getPropertiesDir(),
            "${classInfo.name}.${Constants.PLUGINS_METADATA_FILE_EXT}"
        )

        metadataFile.writeBytes(classInfo.byteArray)

        logger.debug("Transformations found: {}", info.pluginNames)
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