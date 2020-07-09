package com.likethesalad.android.common.utils

import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import io.github.classgraph.ClassGraph
import org.gradle.api.file.FileCollection

@AutoFactory
class ClassGraphProvider(
    @Provided private val instantiatorWrapper: InstantiatorWrapper,
    private val classDirs: FileCollection
) {

    val classGraph: ClassGraph by lazy {
        instantiatorWrapper.getClassGraph().overrideClasspath(classDirs.files)
    }
}