package com.likethesalad.android.buddy.bytebuddy

import com.google.common.truth.Truth
import com.likethesalad.android.common.utils.bytebuddy.ByteBuddyClassesInstantiator
import com.likethesalad.android.common.providers.ProjectLoggerProvider
import com.likethesalad.android.testutils.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.spyk
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
    lateinit var projectLoggerProvider: ProjectLoggerProvider

    @MockK
    lateinit var transformationLogger: TransformationLogger

    @MockK
    lateinit var logger: com.likethesalad.android.common.utils.Logger

    private val processorsAvailable = 3
    private lateinit var pluginEngineProvider: PluginEngineProvider

    @Before
    fun setUp() {
        pluginEngineProvider = spyk(
            PluginEngineProvider(
                instantiator, transformationLogger, logger
            )
        )
        every { pluginEngineProvider.getJvmAvailableProcessors() }.returns(processorsAvailable)
    }

    @Test
    fun `Make engine with logger and android java class target version`() {
        val entryPoint = mockk<EntryPoint.Default>()
        val classFileVersion = mockk<ClassFileVersion>()
        val methodNameTransformer = mockk<MethodNameTransformer>()
        val projectLogger = mockk<Logger>()
        val javaTargetCompatibility = 7
        val expectedPluginEngine = mockk<Plugin.Engine>()
        every { instantiator.makeDefaultEntryPoint() }.returns(entryPoint)
        every { instantiator.makeClassFileVersionOfJavaVersion(javaTargetCompatibility) }.returns(classFileVersion)
        every { instantiator.makeDefaultMethodNameTransformer() }.returns(methodNameTransformer)
        every { projectLoggerProvider.getLogger() }.returns(projectLogger)
        every {
            instantiator.makePluginEngineOf(entryPoint, classFileVersion, methodNameTransformer)
        }.returns(expectedPluginEngine)
        every { expectedPluginEngine.with(any<Plugin.Engine.Listener>()) }.returns(expectedPluginEngine)
        every { expectedPluginEngine.withParallelTransformation(any()) }.returns(expectedPluginEngine)

        val engine = pluginEngineProvider.makeEngine(javaTargetCompatibility)

        Truth.assertThat(engine).isEqualTo(expectedPluginEngine)
        verify {
            instantiator.makePluginEngineOf(entryPoint, classFileVersion, methodNameTransformer)
            expectedPluginEngine.with(transformationLogger)
            expectedPluginEngine.withParallelTransformation(4)
            logger.debug("Using {} threads for transformation", 4)
        }
    }
}