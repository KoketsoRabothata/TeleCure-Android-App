
package com.telecure.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TeleCureUnitTest {

    @Test
    fun appName_isTeleCure() {
        val appName = "TeleCure"

        assertEquals("TeleCure", appName)
    }

    @Test
    fun appName_isNotEmpty() {
        val appName = "TeleCure"

        assertTrue(appName.isNotEmpty())
    }
}
