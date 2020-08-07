package com.likethesalad.android.buddylib.modules.createmetadata

import com.android.build.gradle.api.BaseVariant
import com.likethesalad.android.buddylib.extension.AndroidBuddyLibExtension
import com.likethesalad.android.buddylib.modules.createmetadata.data.CreateMetadataTaskArgs
import com.likethesalad.android.buddylib.modules.createmetadata.utils.CreateMetadataTaskNameGenerator
import com.likethesalad.android.buddylib.providers.AndroidBuddyLibExtensionProvider
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
import org.gradle.api.Action
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.AbstractCopyTask
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
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
    lateinit var createAndroidBuddyLibraryMetadata: CreateAndroidBuddyLibraryMetadata

    @MockK
    lateinit var createAndroidBuddyLibraryMetadataProvider: TaskProvider<CreateAndroidBuddyLibraryMetadata>

    @MockK
    lateinit var variantJavaResourcesTask: AbstractCopyTask

    @MockK
    lateinit var variantJavaResourcesTaskProvider: TaskProvider<AbstractCopyTask>

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
        every {
            variantJavaResourcesTaskProvider.hint(AbstractCopyTask::class).get()
        }.returns(variantJavaResourcesTask)
        every { variant.processJavaResourcesProvider }.returns(variantJavaResourcesTaskProvider)
        every { taskContainerProvider.getTaskContainer() }.returns(taskContainer)
        every { extensionProvider.getExtension() }.returns(androidBuddyLibExtension)
        every {
            taskContainer.register(
                taskName,
                CreateAndroidBuddyLibraryMetadata::class.java,
                createMetadataTaskArgs
            )
        }.returns(createAndroidBuddyLibraryMetadataProvider)
        every {
            createAndroidBuddyLibraryMetadataProvider.hint(CreateAndroidBuddyLibraryMetadata::class).get()
        }.returns(createAndroidBuddyLibraryMetadata)
        every { androidExtensionDataProvider.allVariants(capture(allVariantsCaptor)) } just Runs
        createMetadataTaskGenerator = CreateMetadataTaskGenerator(
            androidExtensionDataProvider,
            createMetadataTaskNameGenerator,
            createMetadataTaskArgs,
            taskContainerProvider,
            extensionProvider,
            incrementalDirProvider
        )
        createMetadataTaskGenerator.createTaskPerVariant()
    }

    @Test
    fun `Create jar description properties generator task`() {
        val configureActionCaptor = slot<Action<CreateAndroidBuddyLibraryMetadata>>()
        val pluginNames = mockk<SetProperty<String>>()
        val outputDir = mockk<DirectoryProperty>()
        val expectedOutputDir = mockk<File>()
        every { incrementalDirProvider.createIncrementalDir(taskName) }.returns(expectedOutputDir)
        every { createMetadataTaskNameGenerator.generateTaskName(variantName) }.returns(taskName)
        every { createAndroidBuddyLibraryMetadata.outputDir }.returns(outputDir)
        every { androidBuddyLibExtension.pluginNames }.returns(pluginNames)
        allVariantsCaptor.captured.invoke(variant)


        verify {
            taskContainer.register(
                taskName,
                CreateAndroidBuddyLibraryMetadata::class.java,
                createMetadataTaskArgs
            )
            createAndroidBuddyLibraryMetadataProvider.configure(capture(configureActionCaptor))
        }

        configureActionCaptor.captured.execute(createAndroidBuddyLibraryMetadata)
        verify {
            createAndroidBuddyLibraryMetadata.inputClassNames = pluginNames
            outputDir.set(expectedOutputDir)
        }
    }
}