package com.likethesalad.android.buddylib.providers

import org.gradle.api.file.ConfigurableFileCollection
import java.io.File

interface FileCollectionProvider {

    fun createCollectionForFiles(vararg files: File): ConfigurableFileCollection
}