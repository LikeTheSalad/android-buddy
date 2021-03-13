package com.likethesalad.android.buddy.modules.transform.utils

import com.google.common.truth.Truth
import com.likethesalad.android.buddy.modules.libraries.AndroidBuddyLibraryPluginsExtractor
import com.likethesalad.android.buddy.modules.libraries.models.LibraryPluginsExtracted
import com.likethesalad.android.buddy.providers.LibrariesJarsProvider
import com.likethesalad.android.common.providers.ProjectLoggerProvider
import com.likethesalad.android.common.utils.InstantiatorWrapper
import com.likethesalad.android.common.utils.Logger
import com.likethesalad.android.common.utils.bytebuddy.ByteBuddyClassesInstantiator
import com.likethesalad.android.testutils.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import net.bytebuddy.build.Plugin
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.dynamic.ClassFileLocator
import net.bytebuddy.dynamic.DynamicType
import org.junit.Before
import org.junit.Test
import java.io.File

class PluginFactoriesProviderTest : BaseMockable() {

    @MockK
    lateinit var instantiatorWrapper: InstantiatorWrapper

    @MockK
    lateinit var byteBuddyClassesInstantiator: ByteBuddyClassesInstantiator

    @MockK
    lateinit var logger: Logger

    @MockK
    lateinit var projectLoggerProvider: ProjectLoggerProvider

    @MockK
    lateinit var projectLogger: org.gradle.api.logging.Logger

    @MockK
    lateinit var androidBuddyLibraryPluginsExtractor: AndroidBuddyLibraryPluginsExtractor

    @MockK
    lateinit var loggerArgumentResolver: Plugin.Factory.UsingReflection.ArgumentResolver

    @MockK
    lateinit var localPluginsExtractor: LocalPluginsExtractor

    private lateinit var pluginFactoriesProvider: PluginFactoriesProvider

    @Before
    fun setUp() {
        every { projectLoggerProvider.getLogger() }.returns(projectLogger)
        every {
            byteBuddyClassesInstantiator.makeFactoryArgumentResolverFor(
                org.gradle.api.logging.Logger::class.java,
                projectLogger
            )
        }.returns(loggerArgumentResolver)
        pluginFactoriesProvider = PluginFactoriesProvider(
            instantiatorWrapper,
            byteBuddyClassesInstantiator,
            androidBuddyLibraryPluginsExtractor,
            localPluginsExtractor,
            logger,
            projectLoggerProvider
        )
    }

    @Test
    fun `Get factories from local and external classpaths`() {
        val androidBuddyJar = mockk<File>()
        val normalJar = mockk<File>()
        val classLoader = mockk<ClassLoader>()

        val localFactoryWrappers =
            createLibraryFactoryWrappers(classLoader, LocalPlugin1::class.java, LocalPlugin2::class.java)
        val libFactoryWrappers = createLibraryFactoryWrappers(classLoader, LibraryPlugin1::class.java)
        val localTransformationNames = localFactoryWrappers.map { it.name }.toSet()
        val libTransformationNames = libFactoryWrappers.map { it.name }.toSet()

        verifyFactoriesProvided(
            setOf(androidBuddyJar), setOf(normalJar), classLoader,
            localFactoryWrappers + libFactoryWrappers
        )

        verify {
            logger.debug("Local transformations found: {}", localTransformationNames)
            logger.debug("Dependencies transformations found: {}", libTransformationNames)
        }
    }

    private fun verifyFactoriesProvided(
        androidBuddyLibraries: Set<File>,
        normalLibraries: Set<File>,
        classLoader: ClassLoader,
        expectedFactoryWrappers: List<FactoryWrapper>
    ) {
        val librariesJars = normalLibraries + androidBuddyLibraries
        val libraryFactoryWrappers =
            expectedFactoryWrappers.filter { LibraryPlugin::class.java.isAssignableFrom(it.pluginClass) }
        val localFactoryWrappers =
            expectedFactoryWrappers.filter { LocalPlugin::class.java.isAssignableFrom(it.pluginClass) }
        val allFactories = expectedFactoryWrappers.map { it.expectedFactory }
        val librariesJarsProvider = mockk<LibrariesJarsProvider>()
        val localDirs = setOf<File>(mockk())
        val localPluginNames = localFactoryWrappers.map { it.name }.toSet()
        val libraryPluginNames = libraryFactoryWrappers.map { it.name }.toSet()

        val libraryPluginsExtracted = LibraryPluginsExtracted(
            libraryPluginNames,
            androidBuddyLibraries
        )

        every { librariesJarsProvider.getLibrariesJars() }.returns(librariesJars)
        every {
            androidBuddyLibraryPluginsExtractor.extractLibraryPlugins(librariesJars)
        }.returns(libraryPluginsExtracted)
        every {
            localPluginsExtractor.getLocalPluginNames(localDirs)
        }.returns(localPluginNames)

        val factories = pluginFactoriesProvider.getFactories(localDirs, librariesJarsProvider, classLoader)

        Truth.assertThat(factories).containsExactlyElementsIn(allFactories)
        verify {
            for (factory in allFactories) {
                factory.with(loggerArgumentResolver)
            }
        }
    }

    private fun createLibraryFactoryWrappers(
        classLoader: ClassLoader,
        vararg classes: Class<out BaseMockPlugin>
    ): List<FactoryWrapper> {
        val wrappers = mutableListOf<FactoryWrapper>()
        for (onePluginClass in classes) {
            wrappers.add(createFactoryWrapper(onePluginClass, classLoader))
        }
        return wrappers
    }

    private fun createFactoryWrapper(
        clazz: Class<out BaseMockPlugin>,
        classLoader: ClassLoader
    ): FactoryWrapper {
        val name = clazz.name
        val factory = mockk<Plugin.Factory.UsingReflection>()

        every {
            instantiatorWrapper.getClassForName<Plugin>(name, false, classLoader)
        }.returns(clazz)
        every {
            byteBuddyClassesInstantiator.makeFactoryUsingReflection(clazz)
        }.returns(factory)
        every {
            factory.with(any<Plugin.Factory.UsingReflection.ArgumentResolver>())
        }.returns(factory)

        return FactoryWrapper(name, clazz, factory)
    }

    class FactoryWrapper(
        val name: String,
        val pluginClass: Class<out BaseMockPlugin>,
        val expectedFactory: Plugin.Factory.UsingReflection
    )

    abstract class LocalPlugin : BaseMockPlugin()
    abstract class LibraryPlugin : BaseMockPlugin()

    class LocalPlugin1 : LocalPlugin()
    class LocalPlugin2 : LocalPlugin()
    class LibraryPlugin1 : LibraryPlugin()
    class LibraryPlugin2 : LibraryPlugin()

    open class BaseMockPlugin : Plugin {
        override fun apply(
            builder: DynamicType.Builder<*>?,
            typeDescription: TypeDescription?,
            classFileLocator: ClassFileLocator?
        ): DynamicType.Builder<*> {
            // Nothing to do
            throw UnsupportedOperationException()
        }

        override fun close() {
            // Nothing to do
        }

        override fun matches(target: TypeDescription?): Boolean {
            // Nothing to do
            throw UnsupportedOperationException()
        }
    }
}