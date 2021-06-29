package com.likethesalad.android.buddy.utils

import com.google.common.truth.Truth
import com.likethesalad.android.testutils.BaseMockable
import com.likethesalad.android.testutils.MockUtils.createSourceElementMock
import net.bytebuddy.build.Plugin
import org.junit.Test

class SourceElementsIteratorTest : BaseMockable() {

    @Test
    fun `Iterate through all items`() {
        val items1 = createIteratorOfSourceElementsWithNames("item1_1", "item1_2")
        val items2 = createIteratorOfSourceElementsWithNames("item2_1", "item2_2")
        val list = mutableListOf(items1, items2)

        val iterator = createSourceElementsIterator(list)

        Truth.assertThat(getItemNamesFromIterator(iterator))
            .containsExactly("item1_1", "item1_2", "item2_1", "item2_2")
    }

    @Test
    fun `Ignore empty iterators and keep going further on the list`() {
        val items1 = createIteratorOfSourceElementsWithNames("item1_1", "item1_2")
        val items2 = createIteratorOfSourceElementsWithNames()
        val items3 = createIteratorOfSourceElementsWithNames("item3_1", "item3_2")
        val list = mutableListOf(items1, items2, items3)

        val iterator = createSourceElementsIterator(list)

        Truth.assertThat(getItemNamesFromIterator(iterator))
            .containsExactly("item1_1", "item1_2", "item3_1", "item3_2")
    }

    private fun getItemNamesFromIterator(iterator: SourceElementsIterator): List<String> {
        val names = mutableListOf<String>()
        while (iterator.hasNext()) {
            names.add(iterator.next().name)
        }

        return names
    }

    private fun createSourceElementsIterator(
        iterators: MutableList<out Iterator<Plugin.Engine.Source.Element>>,
        excludePrefixes: Set<String> = emptySet()
    ): SourceElementsIterator {
        return SourceElementsIterator(iterators, excludePrefixes)
    }

    private fun createIteratorOfSourceElementsWithNames(vararg names: String): Iterator<Plugin.Engine.Source.Element> {
        val list = names.map { createSourceElementMock(it) }
        return list.iterator()
    }
}