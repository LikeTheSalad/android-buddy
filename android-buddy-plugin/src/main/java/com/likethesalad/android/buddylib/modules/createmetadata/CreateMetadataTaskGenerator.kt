package com.likethesalad.android.buddylib.modules.createmetadata

import com.android.build.gradle.api.BaseVariant
import com.likethesalad.android.buddylib.di.LibraryScope
import com.likethesalad.android.buddylib.extension.AndroidBuddyLibExtension
import com.likethesalad.android.buddylib.modules.createmetadata.data.CreateMetadataTaskArgs
import com.likethesalad.android.buddylib.modules.createmetadata.utils.CreateMetadataTaskNameGenerator
import com.likethesalad.android.buddylib.providers.AndroidBuddyLibExtensionProvider
import com.likethesalad.android.buddylib.providers.PluginNamesProvider
import com.likethesalad.android.buddylib.providers.TaskContainerProvider
import com.likethesalad.android.common.utils.android.AndroidExtensionDataProvider
import org.gradle.api.tasks.TaskContainer
import javax.inject.Inject

@LibraryScope
class CreateMetadataTaskGenerator @Inject constructor(
    private val androidExtensionDataProvider: AndroidExtensionDataProvider,
    private val createMetadataTaskNameGenerator: CreateMetadataTaskNameGenerator,
    private val createMetadataTaskArgs: CreateMetadataTaskArgs,
    private val taskContainerProvider: TaskContainerProvider,
    private val extensionProvider: AndroidBuddyLibExtensionProvider
) {

    private val taskContainer: TaskContainer by lazy {
        taskContainerProvider.getTaskContainer()
    }

    private val extension: AndroidBuddyLibExtension by lazy {
        extensionProvider.getExtension()
    }

    fun createTaskPerVariant() {
        androidExtensionDataProvider.allVariants {
            createTaskForVariant(it)
        }
    }

    private fun createTaskForVariant(variant: BaseVariant) {
        val taskName = createMetadataTaskNameGenerator.generateTaskName(variant.name)
        val taskProvider = taskContainer.register(
            taskName, CreateAndroidBuddyLibraryMetadata::class.java, createMetadataTaskArgs
        )
        taskProvider.configure {
            it.inputClassNames = extension.pluginNames
        }
    }
}