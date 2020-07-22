package com.likethesalad.android.buddy.utils

import com.google.common.truth.Truth
import org.junit.Test

class ConcatIteratorTest {

    @Test
    fun `Iterate through multiple iterators`() {
        val list1 = listOf("one", "two", "three")
        val list2 = listOf("four", "five", "six")

        val concatIterator = ConcatIterator(mutableListOf(list1.iterator(), list2.iterator()))
        val elements = mutableListOf<String>()

        for (element in concatIterator) {
            elements.add(element)
        }

        Truth.assertThat(elements).containsExactly("one", "two", "three", "four", "five", "six")
    }
}