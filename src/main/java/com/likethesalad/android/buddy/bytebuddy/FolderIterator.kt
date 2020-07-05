package com.likethesalad.android.buddy.bytebuddy

import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import com.likethesalad.android.buddy.bytebuddy.utils.ByteBuddyClassesInstantiator
import com.likethesalad.android.buddy.utils.FileTreeIteratorProvider
import net.bytebuddy.build.Plugin
import java.io.File

@AutoFactory
class FolderIterator(
    @Provided fileTreeIteratorProvider: FileTreeIteratorProvider,
    @Provided private val byteBuddyClassesInstantiator: ByteBuddyClassesInstantiator,
    private val folder: File
) : Iterator<Plugin.Engine.Source.Element> {

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