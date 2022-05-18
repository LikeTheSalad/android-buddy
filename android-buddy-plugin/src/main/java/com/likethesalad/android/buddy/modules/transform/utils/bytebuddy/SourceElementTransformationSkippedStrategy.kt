package com.likethesalad.android.buddy.modules.transform.utils.bytebuddy

import com.likethesalad.android.buddy.modules.transform.base.TransformationSkippedStrategy
import com.likethesalad.android.buddy.modules.transform.utils.FilePathScanner
import com.likethesalad.android.common.utils.Logger
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import net.bytebuddy.build.Plugin
import java.io.File

class SourceElementTransformationSkippedStrategy @AssistedInject constructor(
    @Assisted private val outputDir: File,
    private val filePathScanner: FilePathScanner,
    private val logger: Logger
) : TransformationSkippedStrategy<Plugin.Engine.Source.Element> {

    @AssistedFactory
    interface Factory {
        fun create(outputDir: File): SourceElementTransformationSkippedStrategy
    }

    override fun onTransformationSkipped(item: Plugin.Engine.Source.Element) {
        if (item.inputStream.available() == 0) {
            logger.debug("Skipping to save item: {}, it's probably a folder", item.name)
            return
        }
        logger.debug("Attempting to save item: {}", item.name)

        val fileInfo = filePathScanner.scanFilePath(item.name)
        val outputFileDir = File(outputDir, fileInfo.dirPath)

        if (!outputFileDir.exists()) {
            outputFileDir.mkdirs()
        }

        File(outputFileDir, fileInfo.name).outputStream().use { outputStream ->
            item.inputStream.copyTo(outputStream)
        }
    }
}