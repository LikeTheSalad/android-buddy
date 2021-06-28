package com.likethesalad.android.buddy.utils

open class ConcatIterator<T>(val iterators: MutableList<out Iterator<T>>) : MutableIterator<T> {

    override fun hasNext(): Boolean {
        while (iterators.isNotEmpty() && !iterators.first().hasNext()) {
            val first = iterators.first()
            iterators.remove(first)
        }

        if (iterators.isEmpty()) {
            return false
        }

        return iterators.first().hasNext()
    }

    override fun next(): T {
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