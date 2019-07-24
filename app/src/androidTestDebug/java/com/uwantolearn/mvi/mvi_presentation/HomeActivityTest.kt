package com.uwantolearn.mvi.mvi_presentation

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.uwantolearn.mvi.R
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeActivityTest {

    @get:Rule
    var homeActivityTestRule = ActivityTestRule(
        HomeActivity::class.java,
        true,
        true
    )

    @Test
    fun whenLaunchShouldShowProgressBar() {
        onView(withId(R.id.progressBar))
            .check(matches(isDisplayed()))

        withId(R.id.progressBar)
            .let(::onView)
            .check(matches(isDisplayed()))
    }

    @Test
    fun whenRandomButtonClickShouldChangeButtonValue() {

        withId(R.id.progressBar)
            .let(::onView)
            .check(matches(isDisplayed()))
        Thread.sleep(4000)

        withId(R.id.randomNumberClick)
            .let(::onView)
            .check(matches(withText("0")))

        withId(R.id.randomNumberClick)
            .let(::onView)
            .perform(ViewActions.click())

        withId(R.id.randomNumberClick)
            .let(::onView)
            .check(matches(not(withText("0"))))

    }

}