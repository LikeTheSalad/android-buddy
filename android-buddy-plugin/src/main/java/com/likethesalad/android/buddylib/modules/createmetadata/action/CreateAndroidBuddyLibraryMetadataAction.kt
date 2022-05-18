package com.likethesalad.android.buddylib.modules.createmetadata.action

import com.likethesalad.android.common.actions.BaseAction
import com.likethesalad.android.common.models.libinfo.AndroidBuddyLibraryInfo
import com.likethesalad.android.common.models.libinfo.LibraryInfoMapper
import com.likethesalad.android.common.utils.Constants
import com.likethesalad.android.common.utils.Constants.LIBRARY_METADATA_DIR
import com.likethesalad.android.common.utils.DirectoryCleaner
import com.likethesalad.android.common.utils.Logger
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.io.File

class CreateAndroidBuddyLibraryMetadataAction @AssistedInject constructor(
    private val directoryCleaner: DirectoryCleaner,
    private val logger: Logger,
    private val libraryInfoMapper: LibraryInfoMapper,
    @Assisted private val info: AndroidBuddyLibraryInfo,
    @Assisted private val outputDir: File
) : BaseAction {

    @AssistedFactory
    interface Factory {
        fun create(info: AndroidBuddyLibraryInfo, outputDir: File): CreateAndroidBuddyLibraryMetadataAction
    }

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