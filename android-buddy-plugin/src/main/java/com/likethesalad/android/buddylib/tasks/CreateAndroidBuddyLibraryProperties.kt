package com.likethesalad.android.buddylib.tasks

import com.likethesalad.android.buddylib.modules.createproperties.data.CreateJarDescriptionPropertiesArgs
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

@Suppress("UnstableApiUsage")
open class CreateAndroidBuddyLibraryProperties
@Inject constructor(args: CreateJarDescriptionPropertiesArgs) : DefaultTask() {

    private val createJarDescriptionPropertiesActionFactory = args.createAndroidBuddyLibraryPropertiesActionFactory

    @get:OutputDirectory
    val outputDir: DirectoryProperty by lazy {
        getObjectFactory().directoryProperty()
    }

    @get:Input
    lateinit var inputClassNames: SetProperty<String>

    @Inject
    open fun getObjectFactory(): ObjectFactory {
        throw UnsupportedOperationException()
    }

    @TaskAction
    fun doAction() {
        val pluginNames = inputClassNames.get()
        createJarDescriptionPropertiesActionFactory.create(
            pluginNames,
            outputDir.asFile.get()
        ).execute()
    }
}