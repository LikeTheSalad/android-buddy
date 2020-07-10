package com.likethesalad.android.buddylib.tasks

import com.likethesalad.android.buddylib.actions.CreateJarDescriptionPropertiesActionFactory
import com.likethesalad.android.buddylib.actions.VerifyPluginClassesProvidedActionFactory
import com.likethesalad.android.common.providers.impl.FileCollectionFileSetProvider
import com.likethesalad.android.common.utils.ClassGraphProvider
import com.likethesalad.android.common.utils.ClassGraphProviderFactory
import com.likethesalad.android.common.utils.PluginsFinderFactory
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
class CreateJarDescriptionProperties
@Inject constructor(
    private val createJarDescriptionPropertiesActionFactory: CreateJarDescriptionPropertiesActionFactory,
    private val verifyPluginClassesProvidedActionFactory: VerifyPluginClassesProvidedActionFactory,
    private val pluginsFinderFactory: PluginsFinderFactory,
    private val classGraphProviderFactory: ClassGraphProviderFactory
) : DefaultTask() {

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
    fun getObjectFactory(): ObjectFactory {
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