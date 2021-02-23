package com.likethesalad.android.buddylib.modules.createmetadata

import com.likethesalad.android.buddylib.modules.createmetadata.data.CreateMetadataTaskArgs
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

@Suppress("UnstableApiUsage")
open class CreateAndroidBuddyLibraryMetadata
@Inject constructor(args: CreateMetadataTaskArgs) : DefaultTask() {

    private val createAndroidBuddyLibraryMetadataActionFactory = args.createAndroidBuddyLibraryMetadataActionFactory
    private val androidBuddyLibraryInfoMaker = args.androidBuddyLibraryInfoMaker

    @get:OutputDirectory
    val outputDir: DirectoryProperty by lazy {
        getObjectFactory().directoryProperty()
    }

    @get:Input
    lateinit var id: Property<String>

    @get:Input
    lateinit var inputClassNames: SetProperty<String>

    @Inject
    open fun getObjectFactory(): ObjectFactory {
        throw UnsupportedOperationException()
    }

    @TaskAction
    fun doAction() {
        val id = id.get()
        val pluginNames = inputClassNames.get()
        val info = androidBuddyLibraryInfoMaker.make(id, pluginNames)

        createAndroidBuddyLibraryMetadataActionFactory.create(
            info,
            outputDir.asFile.get()
        ).execute()
    }
}