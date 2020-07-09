package com.likethesalad.android.buddylib.tasks

import com.likethesalad.android.buddylib.actions.CreateJarDescriptionPropertiesActionFactory
import com.likethesalad.android.buddylib.actions.VerifyPluginClassesProvidedActionFactory
import com.likethesalad.android.common.utils.ClassGraphProvider
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
    private val classGraphProvider: ClassGraphProvider
) : DefaultTask() {

    @Inject
    fun getObjectFactory(): ObjectFactory {
        throw UnsupportedOperationException()
    }

    @OutputDirectory
    fun getOutputDir(): DirectoryProperty {
        return getObjectFactory().directoryProperty()
    }

    @Input
    fun getInputClassNames(): SetProperty<String> {
        return getObjectFactory().setProperty(String::class.java)
    }

    @InputFiles
    fun getInputClassPaths(): FileCollection {
        return getObjectFactory().fileCollection()
    }

    @TaskAction
    fun doAction() {
        val pluginNames = getInputClassNames().get()
        verifyPluginClassesProvidedActionFactory.create(
            pluginNames,
            pluginsFinderFactory.create(classGraphProvider)
        ).execute()
        createJarDescriptionPropertiesActionFactory.create(
            pluginNames,
            getOutputDir().asFile.get()
        ).execute()
    }
}