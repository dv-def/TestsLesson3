package com.geekbrains.tests

import android.content.Context
import android.os.Build
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.geekbrains.tests.view.search.MainActivity
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class MainActivityTest {
    private lateinit var scenario: ActivityScenario<MainActivity>
    private lateinit var context: Context

    @Before
    fun setup() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun activity_IsNotNull() {
        scenario.onActivity {
            assertNotNull(it)
        }
    }

    @Test
    fun activity_hasProgressBar() {
        scenario.onActivity {
            val progressBar = it.findViewById<ProgressBar>(R.id.progressBar)
            assertNotNull(progressBar)
        }
    }

    @Test
    fun activity_progressBarIsHidden() {
        scenario.onActivity {
            val progressBar = it.findViewById<ProgressBar>(R.id.progressBar)
            assertEquals(View.GONE, progressBar.visibility)
        }
    }

    @Test
    fun activity_hasEditText() {
        scenario.onActivity {
            val editText = it.findViewById<EditText>(R.id.searchEditText)
            assertNotNull(editText)
        }
    }

    @Test
    fun activity_editTextIsVisible() {
        scenario.onActivity {
            val editText = it.findViewById<EditText>(R.id.searchEditText)
            assertEquals(View.VISIBLE, editText.visibility)
        }
    }

    @Test
    fun activity_editTextIsEmpty() {
        scenario.onActivity {
            val editText = it.findViewById<EditText>(R.id.searchEditText)
            val text = editText.text.toString()

            assertEquals("", text)
        }
    }

    @Test
    fun activity_hasButtonToDetails() {
        scenario.onActivity {
            val button = it.findViewById<Button>(R.id.toDetailsActivityButton)
            assertNotNull(button)
        }
    }

    @Test
    fun activity_buttonIsVisible() {
        scenario.onActivity {
            val button = it.findViewById<Button>(R.id.toDetailsActivityButton)
            assertEquals(View.VISIBLE, button.visibility)
        }
    }

    @After
    fun close() {
        scenario.close()
    }
}