package com.likethesalad.android.buddylib.modules.createmetadata

import com.android.build.gradle.api.BaseVariant
import com.likethesalad.android.buddylib.di.LibraryScope
import com.likethesalad.android.buddylib.extension.AndroidBuddyLibExtension
import com.likethesalad.android.buddylib.modules.createmetadata.data.CreateMetadataTaskArgs
import com.likethesalad.android.buddylib.modules.createmetadata.utils.CreateMetadataTaskNameGenerator
import com.likethesalad.android.buddylib.providers.AndroidBuddyLibExtensionProvider
import com.likethesalad.android.buddylib.providers.IncrementalDirProvider
import com.likethesalad.android.buddylib.providers.TaskContainerProvider
import com.likethesalad.android.common.utils.android.AndroidExtensionDataProvider
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import javax.inject.Inject

@Suppress("UnstableApiUsage")
@LibraryScope
class CreateMetadataTaskGenerator @Inject constructor(
    private val androidExtensionDataProvider: AndroidExtensionDataProvider,
    private val createMetadataTaskNameGenerator: CreateMetadataTaskNameGenerator,
    private val createMetadataTaskArgs: CreateMetadataTaskArgs,
    private val taskContainerProvider: TaskContainerProvider,
    private val extensionProvider: AndroidBuddyLibExtensionProvider,
    private val incrementalDirProvider: IncrementalDirProvider
) {

    private val taskContainer: TaskContainer by lazy {
        taskContainerProvider.getTaskContainer()
    }

    private val extension: AndroidBuddyLibExtension by lazy {
        extensionProvider.getExtension()
    }

    fun createTaskPerVariant() {
        androidExtensionDataProvider.allVariants {
            attachTaskToVariantJavaResources(it, createTaskForVariant(it))
        }
    }

    private fun createTaskForVariant(variant: BaseVariant): TaskProvider<CreateAndroidBuddyLibraryMetadata> {
        val taskName = createMetadataTaskNameGenerator.generateTaskName(variant.name)
        val task = taskContainer.register(
            taskName, CreateAndroidBuddyLibraryMetadata::class.java, createMetadataTaskArgs
        )
        task.configure {
            it.inputClassNames = extension.pluginNames
            it.outputDir.set(incrementalDirProvider.createIncrementalDir(taskName))
        }

        return task
    }

    private fun attachTaskToVariantJavaResources(
        variant: BaseVariant,
        task: TaskProvider<CreateAndroidBuddyLibraryMetadata>
    ) {
        variant.processJavaResourcesProvider.configure {
            it.from(task)
        }
    }
}