package com.likethesalad.android.buddy.bytebuddy

import com.likethesalad.android.buddy.providers.FileTreeIteratorProvider
import com.likethesalad.android.common.utils.bytebuddy.ByteBuddyClassesInstantiator
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import net.bytebuddy.build.Plugin
import java.io.File

class FolderIterator @AssistedInject constructor(
    fileTreeIteratorProvider: FileTreeIteratorProvider,
    private val byteBuddyClassesInstantiator: ByteBuddyClassesInstantiator,
    @Assisted private val folder: File
) : Iterator<Plugin.Engine.Source.Element> {

    @AssistedFactory
    interface Factory {
        fun create(folder: File): FolderIterator
    }

    private val filesIterator: Iterator<File> by lazy {
        fileTreeIteratorProvider.createFileTreeIterator(folder)
    }

    override fun hasNext(): Boolean {
        return filesIterator.hasNext()
    }

    override fun next(): Plugin.Engine.Source.Element {
        return byteBuddyClassesInstantiator.makeSourceElementForFile(folder, filesIterator.next())
    }
}