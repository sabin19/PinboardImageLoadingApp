package com.sbn.pinboard.ui.home

import android.content.Context
import android.provider.Settings
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.sbn.pinboard.MainActivity
import com.sbn.pinboard.R
import com.sbn.pinboard.SyncTaskExecutorRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeFragmentTest {

    @get:Rule
    var activityRule = ActivityTestRule(MainActivity::class.java)

    // Executes tasks in a synchronous [TaskScheduler]
    @get:Rule
    var syncTaskExecutorRule = SyncTaskExecutorRule()

    @Test
    fun testRecyclerView() {
        checkAnimationsDisabled()
        val recyclerView = activityRule.activity.findViewById<RecyclerView>(R.id.recyclerView)
        /*onView(AllOf.allOf(withId(R.id.recyclerView), isDisplayed()))
            .perform(RecyclerViewActions.actionOnItemAtPosition<HomeAdapter.UserViewHolder>(0, click()))*/
        val itemCount = recyclerView.adapter!!.itemCount
        onView(withId(R.id.recyclerView)).perform(
            RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                itemCount - 1
            )
        )
    }

    private fun checkAnimationsDisabled() {
        val scale = Settings.Global.getFloat(
            ApplicationProvider.getApplicationContext<Context>().contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f
        )

        if (scale > 0) {
            throw Exception(
                "Device must have animations disabled. " +
                        "Developer options -> Animator duration scale"
            )
        }
    }
}