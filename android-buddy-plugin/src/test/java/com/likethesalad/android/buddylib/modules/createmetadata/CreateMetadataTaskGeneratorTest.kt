package com.likethesalad.android.buddylib.modules.createmetadata

import com.android.build.gradle.api.BaseVariant
import com.likethesalad.android.buddylib.extension.AndroidBuddyLibExtension
import com.likethesalad.android.buddylib.modules.createmetadata.data.CreateMetadataTaskArgs
import com.likethesalad.android.buddylib.modules.createmetadata.utils.CreateMetadataTaskNameGenerator
import com.likethesalad.android.buddylib.providers.AndroidBuddyLibExtensionProvider
import com.likethesalad.android.buddylib.providers.FileCollectionProvider
import com.likethesalad.android.buddylib.providers.IncrementalDirProvider
import com.likethesalad.android.buddylib.providers.TaskContainerProvider
import com.likethesalad.android.common.utils.android.AndroidExtensionDataProvider
import com.likethesalad.android.testutils.BaseMockable
import io.mockk.CapturingSlot
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.slot
import io.mockk.verify
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.TaskContainer
import org.junit.Before
import org.junit.Test
import java.io.File

@Suppress("UnstableApiUsage")
class CreateMetadataTaskGeneratorTest : BaseMockable() {

    @MockK
    lateinit var androidExtensionDataProvider: AndroidExtensionDataProvider

    @MockK
    lateinit var createMetadataTaskNameGenerator: CreateMetadataTaskNameGenerator

    @MockK
    lateinit var createMetadataTaskArgs: CreateMetadataTaskArgs

    @MockK
    lateinit var taskContainerProvider: TaskContainerProvider

    @MockK
    lateinit var taskContainer: TaskContainer

    @MockK
    lateinit var extensionProvider: AndroidBuddyLibExtensionProvider

    @MockK
    lateinit var androidBuddyLibExtension: AndroidBuddyLibExtension

    @MockK
    lateinit var incrementalDirProvider: IncrementalDirProvider

    @MockK
    lateinit var fileCollectionProvider: FileCollectionProvider

    @MockK
    lateinit var createAndroidBuddyLibraryMetadata: CreateAndroidBuddyLibraryMetadata

    @MockK
    lateinit var variant: BaseVariant


    private val variantName = "someVariant"
    private val taskName = "someTaskName"
    private lateinit var allVariantsCaptor: CapturingSlot<(BaseVariant) -> Unit>
    private lateinit var createMetadataTaskGenerator: CreateMetadataTaskGenerator

    @Before
    fun setUp() {
        allVariantsCaptor = slot()
        every { variant.name }.returns(variantName)
        every { taskContainerProvider.getTaskContainer() }.returns(taskContainer)
        every { extensionProvider.getExtension() }.returns(androidBuddyLibExtension)
        every {
            taskContainer.create(
                taskName,
                CreateAndroidBuddyLibraryMetadata::class.java,
                createMetadataTaskArgs
            )
        }.returns(createAndroidBuddyLibraryMetadata)
        every { androidExtensionDataProvider.allVariants(capture(allVariantsCaptor)) } just Runs
        createMetadataTaskGenerator = CreateMetadataTaskGenerator(
            androidExtensionDataProvider,
            createMetadataTaskNameGenerator,
            createMetadataTaskArgs,
            taskContainerProvider,
            extensionProvider,
            incrementalDirProvider,
            fileCollectionProvider
        )
        createMetadataTaskGenerator.createTaskPerVariant()
    }

    @Test
    fun `Create jar description properties generator task`() {
        val pluginNames = mockk<SetProperty<String>>()
        val outputDir = mockk<DirectoryProperty>()
        val pluginId = mockk<Property<String>>()
        val expectedOutputDir = mockk<File>()
        val collection = mockk<ConfigurableFileCollection>()
        every { incrementalDirProvider.createIncrementalDir(taskName) }.returns(expectedOutputDir)
        every { createMetadataTaskNameGenerator.generateTaskName(variantName) }.returns(taskName)
        every { createAndroidBuddyLibraryMetadata.outputDir }.returns(outputDir)
        every { androidBuddyLibExtension.exposedTransformationNames }.returns(pluginNames)
        every { androidBuddyLibExtension.id }.returns(pluginId)
        every { fileCollectionProvider.createCollectionForFiles(expectedOutputDir) }.returns(collection)
        every { collection.builtBy(createAndroidBuddyLibraryMetadata) }.returns(collection)
        every { variant.registerPreJavacGeneratedBytecode(collection) }.returns(variant)
        allVariantsCaptor.captured.invoke(variant)


        verify {
            taskContainer.create(
                taskName,
                CreateAndroidBuddyLibraryMetadata::class.java,
                createMetadataTaskArgs
            )
            createAndroidBuddyLibraryMetadata.inputClassNames = pluginNames
            createAndroidBuddyLibraryMetadata.id = pluginId
            outputDir.set(expectedOutputDir)
            collection.builtBy(createAndroidBuddyLibraryMetadata)
            variant.registerPreJavacGeneratedBytecode(collection)
        }
    }
}