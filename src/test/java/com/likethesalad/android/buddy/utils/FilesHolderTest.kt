package com.likethesalad.android.buddy.utils

import com.google.common.truth.Truth
import com.likethesalad.android.testutils.BaseMockable
import io.mockk.every
import org.junit.Test
import java.io.File

class FilesHolderTest : BaseMockable() {

    @Test
    fun `Get dirs from files`() {
        val file1 = createFileMock()
        val file2 = createFileMock()
        val dir1 = createDirMock()
        val dir2 = createDirMock()
        val dir3 = createDirMock()

        val filesHolder = FilesHolder(setOf(file1, file2, dir1, dir2, dir3))

        Truth.assertThat(filesHolder.dirs).containsExactly(dir1, dir2, dir3)
    }

    private fun createFileMock(): File {
        val file = mockk<File>()
        every {
            file.isDirectory
        }.returns(false)
        return file
    }

    private fun createDirMock(): File {
        val dir = mockk<File>()
        every {
            dir.isDirectory
        }.returns(true)
        return dir
    }
}