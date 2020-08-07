package com.likethesalad.android.buddylib.providers

import org.gradle.api.tasks.TaskContainer

interface TaskContainerProvider {

    fun getTaskContainer(): TaskContainer
}