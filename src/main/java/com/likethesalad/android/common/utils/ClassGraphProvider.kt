package com.likethesalad.android.common.utils

import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import com.likethesalad.android.common.providers.ClassGraphFilesProvider
import io.github.classgraph.ClassGraph

@AutoFactory
class ClassGraphProvider(
    @Provided private val instantiatorWrapper: InstantiatorWrapper,
    private val classDirs: ClassGraphFilesProvider
) {

    val classGraph: ClassGraph by lazy {
        instantiatorWrapper.getClassGraph().overrideClasspath(classDirs.provideFiles())
    }
}