package com.likethesalad.android.buddy.utils

class ConcatIterator<T>(val iterators: MutableList<out Iterator<T>>) : MutableIterator<T> {

    override fun hasNext(): Boolean {
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