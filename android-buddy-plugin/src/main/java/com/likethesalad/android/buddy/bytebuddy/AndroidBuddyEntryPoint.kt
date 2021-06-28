package com.likethesalad.android.buddy.bytebuddy

import com.likethesalad.android.buddy.di.AppScope
import com.likethesalad.android.common.utils.bytebuddy.ByteBuddyClassesInstantiator
import net.bytebuddy.ByteBuddy
import net.bytebuddy.ClassFileVersion
import net.bytebuddy.build.EntryPoint
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.dynamic.ClassFileLocator
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.dynamic.scaffold.TypeValidation
import net.bytebuddy.dynamic.scaffold.inline.MethodNameTransformer
import javax.inject.Inject

@AppScope
class AndroidBuddyEntryPoint
@Inject constructor(private val byteBuddyClassesInstantiator: ByteBuddyClassesInstantiator) : EntryPoint {

    override fun byteBuddy(classFileVersion: ClassFileVersion?): ByteBuddy {
        return byteBuddyClassesInstantiator.makeByteBuddy(classFileVersion).with(TypeValidation.DISABLED)
    }

    override fun transform(
        typeDescription: TypeDescription?,
        byteBuddy: ByteBuddy,
        classFileLocator: ClassFileLocator?,
        methodNameTransformer: MethodNameTransformer?
    ): DynamicType.Builder<*> {
        return byteBuddy.rebase<Any>(typeDescription, classFileLocator, methodNameTransformer)
    }
}