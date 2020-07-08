package com.likethesalad.android.testutils

import io.mockk.MockKAnnotations
import org.junit.Before

open class BaseMockable {

    @Before
    fun setUpMocks() {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    inline fun <reified T : Any> mockk(): T = io.mockk.mockk(relaxUnitFun = true)
}