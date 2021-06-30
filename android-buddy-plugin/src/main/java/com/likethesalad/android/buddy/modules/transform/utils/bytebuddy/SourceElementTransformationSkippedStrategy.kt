package com.likethesalad.android.buddy.modules.transform.utils.bytebuddy

import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import com.likethesalad.android.buddy.modules.transform.base.TransformationSkippedStrategy
import com.likethesalad.android.buddy.modules.transform.utils.FilePathScanner
import com.likethesalad.android.common.utils.Logger
import net.bytebuddy.build.Plugin
import java.io.File

@AutoFactory
class SourceElementTransformationSkippedStrategy(
    private val outputDir: File,
    @Provided private val filePathScanner: FilePathScanner,
    @Provided private val logger: Logger
) : TransformationSkippedStrategy<Plugin.Engine.Source.Element> {

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