package com.likethesalad.android.common.utils

import com.google.common.truth.Truth
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class DirectoryCleanerTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private val mainDirName = "someDir"
    private lateinit var mainDir: File

    private lateinit var directoryCleaner: DirectoryCleaner

    @Before
    fun setUp() {
        mainDir = temporaryFolder.newFolder(mainDirName)
        directoryCleaner = DirectoryCleaner()
    }

    @Test
    fun `Clean all files`() {
        val file1 = createFileInMainDirectory("file1.txt")
        val file2 = createFileInMainDirectory("file2.txt")
        verifyMainDirContainsFiles(file1, file2)

        directoryCleaner.cleanDirectory(mainDir)

        verifyMainDirCleaned()
    }

    @Test
    fun `Clean all files and empty folders`() {
        val file1 = createFileInMainDirectory("file1.txt")
        val file2 = createFileInMainDirectory("file2.txt")
        val folder1 = createFolderInMainDirectory("folder1")
        verifyMainDirContainsFiles(file1, file2, folder1)

        directoryCleaner.cleanDirectory(mainDir)

        verifyMainDirCleaned()
    }

    @Test
    fun `Clean all files and folders with other files`() {
        val subFolderName = "subFolder"
        val subFolder = createFolderInMainDirectory(subFolderName)
        val mainFile1 = createFileInMainDirectory("file1")
        val mainFile2 = createFileInMainDirectory("file2")
        val subFolderFile = createFileInMainDirectory("$subFolderName/subFile1")
        verifyMainDirContainsFiles(mainFile1, mainFile2, subFolder)
        verifyDirContainsFiles(subFolder, subFolderFile)

        directoryCleaner.cleanDirectory(mainDir)

        verifyMainDirCleaned()
    }

    private fun verifyMainDirCleaned() {
        Truth.assertThat(mainDir.exists()).isTrue()
        Truth.assertThat(mainDir.listFiles()).asList().isEmpty()
    }

    private fun verifyMainDirContainsFiles(vararg files: File) {
        verifyDirContainsFiles(mainDir, *files)
    }

    private fun verifyDirContainsFiles(dir: File, vararg files: File) {
        Truth.assertThat(dir.listFiles()).asList().containsExactlyElementsIn(files)
    }

    private fun createFileInMainDirectory(fileName: String): File {
        return temporaryFolder.newFile("$mainDirName/$fileName")
    }

    private fun createFolderInMainDirectory(folderRelativePath: String): File {
        val folderPaths = mutableListOf(mainDirName)
        folderPaths.addAll(folderRelativePath.split("/"))
        return temporaryFolder.newFolder(*folderPaths.toTypedArray())
    }
}