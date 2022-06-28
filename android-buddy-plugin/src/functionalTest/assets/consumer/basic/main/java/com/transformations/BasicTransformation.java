package com.transformations;

import com.likethesalad.android.buddy.tools.Transformation;
import net.bytebuddy.build.Plugin;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.IOException;

@Transformation
public class BasicTransformation implements Plugin {

    @Override
    public DynamicType.Builder<?> apply(DynamicType.Builder<?> builder,
                                        TypeDescription typeDescription,
                                        ClassFileLocator classFileLocator) {
        return builder.method(ElementMatchers.named("getMessage"))
                .intercept(FixedValue.value("Instrumented message"));
    }

    @Override
    public boolean matches(TypeDescription target) {
        return target.getName().equals("com.thepackage.Hello");
    }

    @Override
    public void close() throws IOException {

    }
}
