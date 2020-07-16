package com.likethesalad.android.buddy.bytebuddy

import com.google.common.truth.Truth
import com.likethesalad.android.buddy.bytebuddy.utils.ByteBuddyClassesInstantiator
import com.likethesalad.android.buddy.providers.AndroidPluginDataProvider
import com.likethesalad.android.common.providers.ProjectLoggerProvider
import com.likethesalad.android.testutils.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import net.bytebuddy.ClassFileVersion
import net.bytebuddy.build.EntryPoint
import net.bytebuddy.build.Plugin
import net.bytebuddy.dynamic.scaffold.inline.MethodNameTransformer
import org.gradle.api.logging.Logger
import org.junit.Before
import org.junit.Test

class PluginEngineProviderTest : BaseMockable() {

    @MockK
    lateinit var instantiator: ByteBuddyClassesInstantiator

    @MockK
    lateinit var androidPluginDataProvider: AndroidPluginDataProvider

    @MockK
    lateinit var projectLoggerProvider: ProjectLoggerProvider

    @MockK
    lateinit var transformationLoggerFactory: TransformationLoggerFactory

    private lateinit var pluginEngineProvider: PluginEngineProvider

    @Before
    fun setUp() {
        pluginEngineProvider = PluginEngineProvider(
            instantiator, androidPluginDataProvider,
            projectLoggerProvider, transformationLoggerFactory
        )
    }

    @Test
    fun `Make engine with logger and android java class target version`() {
        val entryPoint = mockk<EntryPoint.Default>()
        val classFileVersion = mockk<ClassFileVersion>()
        val methodNameTransformer = mockk<MethodNameTransformer>()
        val projectLogger = mockk<Logger>()
        val transformationLogger = mockk<TransformationLogger>()
        val javaTargetCompatibility = 7
        val variantName = "someName"
        val expectedPluginEngine = mockk<Plugin.Engine>()
        every { androidPluginDataProvider.getJavaTargetCompatibilityVersion(variantName) }.returns(javaTargetCompatibility)
        every { instantiator.makeDefaultEntryPoint() }.returns(entryPoint)
        every { instantiator.makeClassFileVersionOfJavaVersion(javaTargetCompatibility) }.returns(classFileVersion)
        every { instantiator.makeDefaultMethodNameTransformer() }.returns(methodNameTransformer)
        every { projectLoggerProvider.getLogger() }.returns(projectLogger)
        every { transformationLoggerFactory.create(projectLogger) }.returns(transformationLogger)
        every {
            instantiator.makePluginEngineOf(entryPoint, classFileVersion, methodNameTransformer)
        }.returns(expectedPluginEngine)
        every { expectedPluginEngine.with(any<Plugin.Engine.Listener>()) }.returns(expectedPluginEngine)

        val engine = pluginEngineProvider.makeEngine(variantName)

        Truth.assertThat(engine).isEqualTo(expectedPluginEngine)
        verify {
            instantiator.makePluginEngineOf(entryPoint, classFileVersion, methodNameTransformer)
            expectedPluginEngine.with(transformationLogger)
        }
    }
}