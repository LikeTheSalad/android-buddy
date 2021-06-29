package com.likethesalad.android.buddy.bytebuddy

import com.google.common.truth.Truth
import com.likethesalad.android.buddy.modules.transform.utils.bytebuddy.SourceElementTransformationSkipPolicy
import com.likethesalad.android.buddy.utils.ConcatIterator
import com.likethesalad.android.common.utils.bytebuddy.ByteBuddyClassesInstantiator
import com.likethesalad.android.testutils.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import net.bytebuddy.build.Plugin
import net.bytebuddy.dynamic.ClassFileLocator
import org.junit.Before
import org.junit.Test

class CompoundSourceTest : BaseMockable() {

    @MockK
    lateinit var byteBuddyClassesInstantiator: ByteBuddyClassesInstantiator

    @MockK
    lateinit var origin1: Plugin.Engine.Source.Origin

    @MockK
    lateinit var origin2: Plugin.Engine.Source.Origin

    @MockK
    lateinit var sourceElementTransformationSkipPolicy: SourceElementTransformationSkipPolicy

    private lateinit var origins: Set<Plugin.Engine.Source.Origin>

    private lateinit var compoundSource: CompoundSource

    @Before
    fun setUp() {
        origins = setOf(origin1, origin2)
        compoundSource = CompoundSource(byteBuddyClassesInstantiator, origins, sourceElementTransformationSkipPolicy)
    }

    @Test
    fun `Return self on read`() {
        Truth.assertThat(compoundSource.read()).isEqualTo(compoundSource)
    }

    @Test
    fun `Return null manifest`() {
        Truth.assertThat(compoundSource.manifest).isNull()
    }

    @Test
    fun `Return compound class file locator from all origins`() {
        val locator1 = mockk<ClassFileLocator>()
        val locator2 = mockk<ClassFileLocator>()
        val expectedCompoundLocator = mockk<ClassFileLocator>()
        every { origin1.classFileLocator }.returns(locator1)
        every { origin2.classFileLocator }.returns(locator2)
        every {
            byteBuddyClassesInstantiator.makeCompoundClassFileLocator(listOf(locator1, locator2))
        }.returns(expectedCompoundLocator)

        Truth.assertThat(compoundSource.classFileLocator).isEqualTo(expectedCompoundLocator)
        verify {
            byteBuddyClassesInstantiator.makeCompoundClassFileLocator(listOf(locator1, locator2))
        }
    }

    @Test
    fun `Close all origins on close`() {
        compoundSource.close()

        verify {
            origin1.close()
            origin2.close()
        }
    }

    @Test
    fun `Concat all origin iterators for iterator`() {
        val iterator1 = mockk<MutableIterator<Plugin.Engine.Source.Element>>()
        val iterator2 = mockk<MutableIterator<Plugin.Engine.Source.Element>>()
        every { origin1.iterator() }.returns(iterator1)
        every { origin2.iterator() }.returns(iterator2)

        val result = compoundSource.iterator()

        Truth.assertThat(result).isInstanceOf(ConcatIterator::class.java)
        Truth.assertThat((result as ConcatIterator).iterators).containsExactly(iterator1, iterator2)
    }
}