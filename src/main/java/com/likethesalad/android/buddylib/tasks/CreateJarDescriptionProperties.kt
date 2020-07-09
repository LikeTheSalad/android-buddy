package com.likethesalad.android.buddylib.tasks

import com.likethesalad.android.buddylib.actions.CreateJarDescriptionPropertiesActionFactory
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

@Suppress("UnstableApiUsage")
class CreateJarDescriptionProperties
@Inject constructor(
    private val createJarDescriptionPropertiesActionFactory: CreateJarDescriptionPropertiesActionFactory
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

    @TaskAction
    fun doAction() {
        val action = createJarDescriptionPropertiesActionFactory.create(
            getInputClassNames().get(),
            getOutputDir().asFile.get()
        )
        action.execute()
    }
}