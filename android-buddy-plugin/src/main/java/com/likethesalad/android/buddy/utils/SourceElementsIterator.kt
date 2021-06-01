package com.likethesalad.android.buddy.utils

import net.bytebuddy.build.Plugin

class SourceElementsIterator(
    iterators: MutableList<out Iterator<Plugin.Engine.Source.Element>>,
    private val excludePrefixes: Set<String>
) : ConcatIterator<Plugin.Engine.Source.Element>(iterators) {
    private var nextElement: Plugin.Engine.Source.Element? = null

    override fun hasNext(): Boolean {
        if (nextElement == null) {
            while (super.hasNext()) {
                val element = super.next()
                if (excludePrefixes.isEmpty() || excludePrefixes.none { element.name.startsWith(it) }) {
                    nextElement = element
                    return true
                }
            }

            return false
        } else {
            return true
        }
    }

    override fun next(): Plugin.Engine.Source.Element {
        hasNext()

        val tmp = nextElement
        nextElement = null
        return tmp ?: throw IllegalStateException("no next source element available")
    }

    override fun remove() {
        throw UnsupportedOperationException()
    }
}