package com.likethesalad.android.buddy.bytebuddy

import com.google.common.truth.Truth
import com.likethesalad.android.common.utils.bytebuddy.ByteBuddyClassesInstantiator
import com.likethesalad.android.testutils.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import net.bytebuddy.ByteBuddy
import net.bytebuddy.ClassFileVersion
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.dynamic.ClassFileLocator
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.dynamic.scaffold.TypeValidation
import net.bytebuddy.dynamic.scaffold.inline.MethodNameTransformer
import org.junit.Before
import org.junit.Test

class AndroidBuddyEntryPointTest : BaseMockable() {

    @MockK
    lateinit var byteBuddyClassesInstantiator: ByteBuddyClassesInstantiator

    @MockK
    lateinit var byteBuddy: ByteBuddy

    private lateinit var androidBuddyEntryPoint: AndroidBuddyEntryPoint

    @Before
    fun setUp() {
        androidBuddyEntryPoint = AndroidBuddyEntryPoint(byteBuddyClassesInstantiator)
    }

    @Test
    fun `Test byte buddy instance config`() {
        val classFileVersion = mockk<ClassFileVersion>()
        every {
            byteBuddyClassesInstantiator.makeByteBuddy(classFileVersion)
        }.returns(byteBuddy)
        every {
            byteBuddy.with(any<TypeValidation>())
        }.returns(byteBuddy)

        val result = androidBuddyEntryPoint.byteBuddy(classFileVersion)

        Truth.assertThat(result).isEqualTo(byteBuddy)
        verify {
            byteBuddy.with(TypeValidation.DISABLED)
        }
    }

    @Test
    fun `Test byte buddy transform type`() {
        val typeDescription = mockk<TypeDescription>()
        val classFileLocator = mockk<ClassFileLocator>()
        val methodNameTransformer = mockk<MethodNameTransformer>()
        val builder = mockk<DynamicType.Builder<Any>>()
        every {
            byteBuddy.rebase<Any>(typeDescription, classFileLocator, methodNameTransformer)
        }.returns(builder)

        val result = androidBuddyEntryPoint
            .transform(typeDescription, byteBuddy, classFileLocator, methodNameTransformer)

        Truth.assertThat(result).isEqualTo(builder)
        verify {
            byteBuddy.rebase<Any>(typeDescription, classFileLocator, methodNameTransformer)
        }
    }
}