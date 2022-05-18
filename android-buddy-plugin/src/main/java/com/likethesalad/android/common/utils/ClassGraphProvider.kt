package com.likethesalad.android.common.utils

import com.likethesalad.android.common.providers.ClassGraphFilesProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.github.classgraph.ClassGraph

class ClassGraphProvider @AssistedInject constructor(
    private val instantiatorWrapper: InstantiatorWrapper,
    @Assisted private val classDirs: ClassGraphFilesProvider
) {

    @AssistedFactory
    interface Factory {
        fun create(classDirs: ClassGraphFilesProvider): ClassGraphProvider
    }

    val classGraph: ClassGraph by lazy {
        instantiatorWrapper.getClassGraph().overrideClasspath(classDirs.provideFiles())
    }
}