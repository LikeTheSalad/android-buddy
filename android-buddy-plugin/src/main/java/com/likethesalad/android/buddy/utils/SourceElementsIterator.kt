package com.likethesalad.android.buddy.utils

import com.likethesalad.android.buddy.modules.transform.utils.bytebuddy.SourceElementTransformationSkipPolicy
import com.likethesalad.android.buddy.modules.transform.utils.bytebuddy.SourceElementTransformationSkippedStrategy
import net.bytebuddy.build.Plugin

class SourceElementsIterator(
    iterators: MutableList<out Iterator<Plugin.Engine.Source.Element>>,
    private val skipPolicy: SourceElementTransformationSkipPolicy,
    private val skippedStrategy: SourceElementTransformationSkippedStrategy
) : ConcatIterator<Plugin.Engine.Source.Element>(iterators) {
    private var nextElement: Plugin.Engine.Source.Element? = null

    override fun hasNext(): Boolean {
        if (nextElement == null) {
            while (super.hasNext()) {
                val element = super.next()
                if (!skipPolicy.shouldSkipItem(element)) {
                    nextElement = element
                    return true
                } else {
                    skippedStrategy.onTransformationSkipped(element)
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