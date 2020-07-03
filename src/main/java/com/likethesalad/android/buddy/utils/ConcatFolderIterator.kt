package com.likethesalad.android.buddy.utils

import com.likethesalad.android.buddy.bytebuddy.FolderIterator
import net.bytebuddy.build.Plugin

class ConcatFolderIterator(private val iterators: MutableList<FolderIterator>) :
    MutableIterator<Plugin.Engine.Source.Element> {

    override fun hasNext(): Boolean {
        if (iterators.isEmpty()) {
            return false
        }

        return iterators.first().hasNext()
    }

    override fun next(): Plugin.Engine.Source.Element {
        val first = iterators.first()
        val next = first.next()
        if (!first.hasNext()) {
            iterators.remove(first)
        }
        return next
    }

    override fun remove() {
        throw UnsupportedOperationException()
    }
}