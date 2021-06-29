package com.likethesalad.android.buddy.utils

import com.google.common.truth.Truth
import com.likethesalad.android.buddy.modules.transform.utils.bytebuddy.SourceElementTransformationSkipPolicy
import com.likethesalad.android.testutils.BaseMockable
import com.likethesalad.android.testutils.MockUtils.createSourceElementMock
import io.mockk.every
import io.mockk.impl.annotations.MockK
import net.bytebuddy.build.Plugin
import org.junit.Before
import org.junit.Test

class SourceElementsIteratorTest : BaseMockable() {

    @MockK
    lateinit var skipPolicy: SourceElementTransformationSkipPolicy

    @Before
    fun setUp() {
        every { skipPolicy.shouldSkipItem(any()) }.returns(false)
    }

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

    @Test
    fun `Skip items that require to be skipped`() {
        val skipItem1_2 = createSourceElementMock("item1_2")
        val skipItem3_1 = createSourceElementMock("item3_1")
        val items1 = createIteratorOfSourceElements(createSourceElementMock("item1_1"), skipItem1_2)
        val items2 = createIteratorOfSourceElementsWithNames()
        val items3 = createIteratorOfSourceElements(skipItem3_1, createSourceElementMock("item3_2"))
        val items4 = createIteratorOfSourceElementsWithNames("item4_1")
        val list = mutableListOf(items1, items2, items3, items4)
        every { skipPolicy.shouldSkipItem(skipItem1_2) }.returns(true)
        every { skipPolicy.shouldSkipItem(skipItem3_1) }.returns(true)

        val iterator = createSourceElementsIterator(list)

        Truth.assertThat(getItemNamesFromIterator(iterator))
            .containsExactly("item1_1", "item3_2", "item4_1")
    }

    private fun getItemNamesFromIterator(iterator: SourceElementsIterator): List<String> {
        val names = mutableListOf<String>()
        while (iterator.hasNext()) {
            names.add(iterator.next().name)
        }

        return names
    }

    private fun createSourceElementsIterator(
        iterators: MutableList<out Iterator<Plugin.Engine.Source.Element>>
    ): SourceElementsIterator {
        return SourceElementsIterator(iterators, skipPolicy)
    }

    private fun createIteratorOfSourceElementsWithNames(vararg names: String): Iterator<Plugin.Engine.Source.Element> {
        val list = names.map { createSourceElementMock(it) }
        return createIteratorOfSourceElements(*list.toTypedArray())
    }

    private fun createIteratorOfSourceElements(vararg items: Plugin.Engine.Source.Element)
            : Iterator<Plugin.Engine.Source.Element> {
        return items.iterator()
    }
}