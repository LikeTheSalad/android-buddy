package com.likethesalad.android.buddylib.tasks

import com.likethesalad.android.buddylib.utils.CreateJarDescriptionPropertiesArgs
import com.likethesalad.android.common.providers.impl.FileCollectionFileSetProvider
import com.likethesalad.android.common.utils.ClassGraphProvider
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

@Suppress("UnstableApiUsage")
open class CreateJarDescriptionProperties
@Inject constructor(args: CreateJarDescriptionPropertiesArgs) : DefaultTask() {

    private val createJarDescriptionPropertiesActionFactory = args.createJarDescriptionPropertiesActionFactory
    private val verifyPluginClassesProvidedActionFactory = args.verifyPluginClassesProvidedActionFactory
    private val pluginsFinderFactory = args.pluginsFinderFactory
    private val classGraphProviderFactory = args.classGraphProviderFactory

    @get:OutputDirectory
    val outputDir: DirectoryProperty by lazy {
        getObjectFactory().directoryProperty()
    }

    @get:Input
    val inputClassNames: SetProperty<String> by lazy {
        getObjectFactory().setProperty(String::class.java)
    }

    @get:InputFiles
    val inputClassPaths: FileCollection by lazy {
        getObjectFactory().fileCollection()
    }

    @Inject
    open fun getObjectFactory(): ObjectFactory {
        throw UnsupportedOperationException()
    }

    @TaskAction
    fun doAction() {
        val pluginNames = inputClassNames.get()
        verifyPluginClassesProvidedActionFactory.create(
            pluginNames,
            pluginsFinderFactory.create(createClassGraphProvider())
        ).execute()
        createJarDescriptionPropertiesActionFactory.create(
            pluginNames,
            outputDir.asFile.get()
        ).execute()
    }

    private fun createClassGraphProvider(): ClassGraphProvider {
        return classGraphProviderFactory.create(
            FileCollectionFileSetProvider(inputClassPaths)
        )
    }
}