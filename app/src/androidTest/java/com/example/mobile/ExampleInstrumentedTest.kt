package com.example.mobile

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.mobile.authentication.RegisterFragment
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.mobile", appContext.packageName)


    }


    private var registerFragment = RegisterFragment()

    // Test per la validità dell'email
    @Test
    fun testIsEmailValid() {
        // Email valida
        assertTrue(registerFragment.isEmailValid("test@example.com"))

        // Email non valida
        assertFalse(registerFragment.isEmailValid("test@.com"))
        assertFalse(registerFragment.isEmailValid("test@example"))
        assertFalse(registerFragment.isEmailValid("test@."))
    }

    // Test per l'uguaglianza delle password
    @Test
    fun testArePasswordsEqual() {
        // Password uguali
        assertTrue(registerFragment.arePasswordsEqual("password123", "password123"))

        // Password diverse
        assertFalse(registerFragment.arePasswordsEqual("password123", "password456"))
    }


}