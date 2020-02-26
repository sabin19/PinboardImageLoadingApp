package com.sbn.pinboard.ui.home


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.sbn.pinboard.MainActivity
import com.sbn.pinboard.R
import com.sbn.pinboard.SyncTaskExecutorRule
import com.sbn.test.data.MainCoroutineRule
import org.hamcrest.core.AllOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeFragmentTest {

    @get:Rule
    var activityRule = ActivityTestRule(MainActivity::class.java)

    // Executes tasks in the Architecture Components in the same thread


    // Executes tasks in a synchronous [TaskScheduler]
    @get:Rule
    var syncTaskExecutorRule = SyncTaskExecutorRule()


/*    @get:Rule
    var coroutineRule = MainCoroutineRule()*/

    @Test
    fun testRecyclerView() {
        onView(withId(R.id.recyclerView))
            .perform(RecyclerViewActions.actionOnItemAtPosition<HomeAdapter.UserViewHolder>(0, click()))
    }

    @Test
    fun testRecycleVisibility(){
        onView(AllOf.allOf(withId(R.id.recyclerView), isDisplayed()))
    }

}