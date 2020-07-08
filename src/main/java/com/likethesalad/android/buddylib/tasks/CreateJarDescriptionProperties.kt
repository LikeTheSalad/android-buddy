package com.likethesalad.android.buddylib.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

class CreateJarDescriptionProperties
@Inject constructor() : DefaultTask() {

    companion object {
        private const val PLUGIN_NAMES_PROPERTY = "pluginNames"
    }

    @TaskAction
    fun doAction() {

    }
}