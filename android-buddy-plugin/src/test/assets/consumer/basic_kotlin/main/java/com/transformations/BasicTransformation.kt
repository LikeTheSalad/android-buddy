package com.transformations

import com.likethesalad.android.buddy.tools.Transformation
import net.bytebuddy.build.Plugin
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.dynamic.ClassFileLocator
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.implementation.FixedValue
import net.bytebuddy.implementation.MethodDelegation
import net.bytebuddy.matcher.ElementMatchers
import java.io.IOException

@Transformation
class BasicTransformation : Plugin {

    override fun apply(
        builder: DynamicType.Builder<*>,
        typeDescription: TypeDescription?,
        classFileLocator: ClassFileLocator?
    ): DynamicType.Builder<*> {
        return builder.method(ElementMatchers.named("getMessage"))
            .intercept(FixedValue.value("Instrumented message"))
    }

    override fun matches(target: TypeDescription): Boolean {
        return target.getName().equals("com.thepackage.HelloK")
    }

    override fun close() {
    }
}