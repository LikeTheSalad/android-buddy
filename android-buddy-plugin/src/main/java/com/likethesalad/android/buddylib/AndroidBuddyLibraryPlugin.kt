package com.likethesalad.android.buddylib

import com.likethesalad.android.buddylib.di.LibraryInjector
import com.likethesalad.android.common.base.BuddyPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.logging.Logger


@Suppress("UnstableApiUsage")
open class AndroidBuddyLibraryPlugin : Plugin<Project>, BuddyPlugin {

    private lateinit var project: Project

    override fun apply(project: Project) {
        LibraryInjector.init(this)
        this.project = project
        LibraryInjector.getDependencyHandlerUtil().addDependencies()
    }

    override fun getLogger(): Logger {
        return project.logger
    }

    override fun getDependencyHandler(): DependencyHandler {
        return project.dependencies
    }

    override fun getRepositoryHandler(): RepositoryHandler {
        return project.repositories
    }
}