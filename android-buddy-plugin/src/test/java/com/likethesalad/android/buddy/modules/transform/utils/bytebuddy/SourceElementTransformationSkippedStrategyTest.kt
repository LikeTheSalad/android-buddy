package com.likethesalad.android.buddy.modules.transform.utils.bytebuddy

import com.google.common.truth.Truth
import com.likethesalad.android.buddy.modules.transform.utils.FilePathScanner
import com.likethesalad.android.common.utils.Logger
import com.likethesalad.android.testutils.BaseMockable
import com.likethesalad.android.testutils.DummyResourcesFinder
import com.likethesalad.android.testutils.MockUtils.createSourceElementMock
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.io.InputStream

class SourceElementTransformationSkippedStrategyTest : BaseMockable() {

    @MockK
    lateinit var logger: Logger

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private val dummyResourcesFinder = DummyResourcesFinder()

    private val filePathScanner = FilePathScanner()
    private val outputFileName = "main"
    private lateinit var outputDir: File

    private lateinit var sourceElementTransformationSkippedStrategy: SourceElementTransformationSkippedStrategy

    @Before
    fun setUp() {
        outputDir = temporaryFolder.newFolder(outputFileName)
        every { logger.debug(any(), any()) } just Runs

        sourceElementTransformationSkippedStrategy =
            SourceElementTransformationSkippedStrategy(outputDir, filePathScanner, logger)
    }

    @Test
    fun `Save file item into output dir`() {
        val relativeOutputPath = "some/path/to/file.txt"
        val item = createSourceElementMock(relativeOutputPath)
        val localFile = getLocalFile()
        val localInputStream = localFile.inputStream()
        every { item.inputStream }.returns(localInputStream)

        localInputStream.use {
            sourceElementTransformationSkippedStrategy.onTransformationSkipped(item)
        }

        val expectedFile = File(outputDir, relativeOutputPath)
        Truth.assertThat(expectedFile.exists()).isTrue()
        Truth.assertThat(expectedFile.readText()).isEqualTo(localFile.readText())
    }

    @Test
    fun `Ignore folder item into output dir`() {
        val relativeOutputPath = "some/path/to/dir"
        val outputFile = File(outputDir, relativeOutputPath)
        val item = createSourceElementMock(relativeOutputPath)
        val localInputStream = mockk<InputStream>()
        every { localInputStream.available() }.returns(0)
        every { item.inputStream }.returns(localInputStream)

        sourceElementTransformationSkippedStrategy.onTransformationSkipped(item)

        Truth.assertThat(outputFile.exists()).isFalse()
    }

    private fun getLocalFile(): File {
        return dummyResourcesFinder
            .getResourceFile("classdirs/withplugins/com/likethesalad/android/buddy/AndroidBuddyPlugin.class")
    }
}