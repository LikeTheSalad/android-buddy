package com.likethesalad.android.buddy.bytebuddy

import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import com.likethesalad.android.buddy.utils.FileTreeCreator
import net.bytebuddy.build.Plugin
import java.io.File

@AutoFactory
class FolderIterator(
    @Provided fileTreeCreator: FileTreeCreator,
    private val folder: File
) : Iterator<Plugin.Engine.Source.Element> {

    private val filesIterator: Iterator<File>

    init {
        val fileTree = fileTreeCreator.createFileTree(folder).filter { it.isFile }

        filesIterator = fileTree.iterator()
    }

    override fun hasNext(): Boolean {
        return filesIterator.hasNext()
    }

    override fun next(): Plugin.Engine.Source.Element {
        return Plugin.Engine.Source.Element.ForFile(folder, filesIterator.next())
    }
}