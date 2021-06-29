package com.likethesalad.android.testutils

import io.mockk.every
import io.mockk.mockk
import net.bytebuddy.build.Plugin

object MockUtils {

    fun createSourceElementMock(name: String): Plugin.Engine.Source.Element {
        val mock = mockk<Plugin.Engine.Source.Element>()
        every { mock.name }.returns(name)

        return mock
    }
}