package com.sbn.pinboard.ui.details

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import com.sbn.pinboard.MainActivity
import com.sbn.pinboard.R
import com.sbn.pinboard.SyncTaskExecutorRule
import org.junit.Rule
import org.junit.Test

class DetailsFragmentTest {
    @get:Rule
    var activityRule = ActivityTestRule(MainActivity::class.java)

    // Executes tasks in a synchronous [TaskScheduler]
    @get:Rule
    var syncTaskExecutorRule = SyncTaskExecutorRule()

    @Test
    fun imageViewIsVisible() {
        onView(withId(R.id.imageView))
            .check(matches(isDisplayed()))
    }


}