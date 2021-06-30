package com.likethesalad.android.buddy.modules.transform.utils.bytebuddy

import com.google.common.truth.Truth
import com.likethesalad.android.testutils.MockUtils.createSourceElementMock
import org.junit.Test

class SourceElementTransformationSkipPolicyTest {

    @Test
    fun `Don't skip items if there's no exclude prefixes`() {
        verifyItemSkip("itemName", false)
        verifyItemSkip("anotherItem", false)
    }

    @Test
    fun `Skip items which names start with an excluded prefix`() {
        val excludedPrefixes = setOf("itemName/something", "other/itemType/more")
        verifyItemSkip("itemName/something/else", true, excludedPrefixes)
        verifyItemSkip("other/itemType/else/something.class", false, excludedPrefixes)
        verifyItemSkip("other/itemType/more/something.class", true, excludedPrefixes)
    }

    private fun verifyItemSkip(
        itemName: String, shouldBeSkipped: Boolean,
        excludedPrefixes: Set<String> = emptySet()
    ) {
        val item = createSourceElementMock(itemName)
        val instance = SourceElementTransformationSkipPolicy(excludedPrefixes)
        val shouldSkip = instance.shouldSkipItem(item)

        Truth.assertThat(shouldSkip).isEqualTo(shouldBeSkipped)
    }
}