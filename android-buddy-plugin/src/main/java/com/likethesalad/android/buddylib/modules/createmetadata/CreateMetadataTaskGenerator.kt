package com.likethesalad.android.buddylib.modules.createmetadata

import com.android.build.gradle.api.BaseVariant
import com.likethesalad.android.buddylib.di.LibraryScope
import com.likethesalad.android.buddylib.extension.AndroidBuddyLibExtension
import com.likethesalad.android.buddylib.modules.createmetadata.data.CreateMetadataTaskArgs
import com.likethesalad.android.buddylib.modules.createmetadata.utils.CreateMetadataTaskNameGenerator
import com.likethesalad.android.buddylib.providers.AndroidBuddyLibExtensionProvider
import com.likethesalad.android.buddylib.providers.FileCollectionProvider
import com.likethesalad.android.buddylib.providers.IncrementalDirProvider
import com.likethesalad.android.buddylib.providers.TaskContainerProvider
import com.likethesalad.android.common.utils.android.AndroidExtensionDataProvider
import org.gradle.api.tasks.TaskContainer
import java.io.File
import javax.inject.Inject

@Suppress("UnstableApiUsage")
@LibraryScope
class CreateMetadataTaskGenerator @Inject constructor(
    private val androidExtensionDataProvider: AndroidExtensionDataProvider,
    private val createMetadataTaskNameGenerator: CreateMetadataTaskNameGenerator,
    private val createMetadataTaskArgs: CreateMetadataTaskArgs,
    private val taskContainerProvider: TaskContainerProvider,
    private val extensionProvider: AndroidBuddyLibExtensionProvider,
    private val incrementalDirProvider: IncrementalDirProvider,
    private val fileCollectionProvider: FileCollectionProvider
) {

    private val taskContainer: TaskContainer by lazy {
        taskContainerProvider.getTaskContainer()
    }

    private val extension: AndroidBuddyLibExtension by lazy {
        extensionProvider.getExtension()
    }

    fun createTaskPerVariant() {
        androidExtensionDataProvider.allVariants {
            val taskName = createMetadataTaskNameGenerator.generateTaskName(it.name)
            val destinationDir = incrementalDirProvider.createIncrementalDir(taskName)
            val task = createTaskForVariant(taskName, destinationDir)
            attachTaskToVariantJavaResources(it, task, destinationDir)
        }
    }

    private fun createTaskForVariant(taskName: String, destinationDir: File)
            : CreateAndroidBuddyLibraryMetadata {
        val task = taskContainer.create(
            taskName, CreateAndroidBuddyLibraryMetadata::class.java, createMetadataTaskArgs, extension
        )
        task.outputDir.set(destinationDir)

        return task
    }

    private fun attachTaskToVariantJavaResources(
        variant: BaseVariant,
        task: CreateAndroidBuddyLibraryMetadata,
        destinationDir: File
    ) {
        val collection = fileCollectionProvider.createCollectionForFiles(destinationDir).builtBy(task)
        variant.registerPreJavacGeneratedBytecode(collection)
    }
}