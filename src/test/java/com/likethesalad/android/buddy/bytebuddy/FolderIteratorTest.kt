package com.likethesalad.android.buddy.bytebuddy

import com.google.common.truth.Truth
import com.likethesalad.android.buddy.bytebuddy.utils.ByteBuddyClassesInstantiator
import com.likethesalad.android.buddy.testutils.BaseMockable
import com.likethesalad.android.buddy.utils.FileTreeIteratorProvider
import io.mockk.every
import io.mockk.impl.annotations.MockK
import net.bytebuddy.build.Plugin
import org.junit.Test
import java.io.File

class FolderIteratorTest : BaseMockable() {

    @MockK
    lateinit var fileTreeIteratorProvider: FileTreeIteratorProvider

    @MockK
    lateinit var byteBuddyClassesInstantiator: ByteBuddyClassesInstantiator

    private lateinit var folderIterator: FolderIterator

    @Test
    fun `Iterate through files inside a folder while turning them into Source Elements`() {
        val folder = mockk<File>()
        val file1 = createFileAndExpectedSource(folder)
        val file2 = createFileAndExpectedSource(folder)
        val file3 = createFileAndExpectedSource(folder)
        every { fileTreeIteratorProvider.createFileTreeIterator(folder) }.returns(
            listOf(file1.file, file2.file, file3.file).iterator()
        )

        folderIterator = FolderIterator(fileTreeIteratorProvider, byteBuddyClassesInstantiator, folder)

        val iteratedElements = mutableListOf<Plugin.Engine.Source.Element>()
        for (element in folderIterator) {
            iteratedElements.add(element)
        }

        Truth.assertThat(iteratedElements).containsExactly(
            file1.expectedElement, file2.expectedElement, file3.expectedElement
        )
    }

    private fun createFileAndExpectedSource(folder: File): FileAndExpectedSourceElement {
        val file = mockk<File>()
        val sourceElement = mockk<Plugin.Engine.Source.Element>()
        every { byteBuddyClassesInstantiator.makeSourceElementForFile(folder, file) }.returns(sourceElement)

        return FileAndExpectedSourceElement(file, sourceElement)
    }

    class FileAndExpectedSourceElement(val file: File, val expectedElement: Plugin.Engine.Source.Element)
}