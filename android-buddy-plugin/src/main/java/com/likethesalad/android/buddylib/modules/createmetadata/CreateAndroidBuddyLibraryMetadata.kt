package com.likethesalad.android.buddylib.modules.createmetadata

import com.likethesalad.android.buddylib.modules.createmetadata.data.CreateMetadataTaskArgs
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
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
    private val libraryGnvProviders = args.libraryGnvProviders

    @get:OutputDirectory
    val outputDir: DirectoryProperty by lazy {
        getObjectFactory().directoryProperty()
    }

    @get:Input
    lateinit var id: Property<String>

    @get:Input
    lateinit var inputClassNames: SetProperty<String>

    @get:Input
    val group: Provider<String> by lazy { getProviderFactory().provider(libraryGnvProviders.groupProvider) }

    @get:Input
    val name: Provider<String> by lazy { getProviderFactory().provider(libraryGnvProviders.nameProvider) }

    @get:Input
    val version: Provider<String> by lazy { getProviderFactory().provider(libraryGnvProviders.versionProvider) }

    @Inject
    open fun getObjectFactory(): ObjectFactory {
        throw UnsupportedOperationException()
    }

    @Inject
    open fun getProviderFactory(): ProviderFactory {
        throw UnsupportedOperationException()
    }

    @TaskAction
    fun doAction() {
        val info = androidBuddyLibraryInfoMaker.make(
            id.get(),
            group.get(),
            name.get(),
            version.get(),
            inputClassNames.get()
        )

        createAndroidBuddyLibraryMetadataActionFactory.create(
            info,
            outputDir.asFile.get()
        ).execute()
    }
}