package com.echokinetic.taskmaster;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void mainActivityTest() {
        ViewInteraction extendedFloatingActionButton = onView(
                allOf(withId(R.id.floating_Add), withText("ADD"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        extendedFloatingActionButton.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.dialog_Title),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("New"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.dialog_Body),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("New description "), closeSoftKeyboard());

        ViewInteraction chip = onView(
                allOf(withId(R.id.state_New), withText("New"),
                        childAtPosition(
                                allOf(withId(R.id.dialog_state),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                1),
                        isDisplayed()));
        chip.perform(click());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.dialog_Body), withText("New description "),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText3.perform(click());

        ViewInteraction materialButton = onView(
                allOf(withId(android.R.id.button1), withText("ADD"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.buttonPanel),
                                        0),
                                3)));
        materialButton.perform(scrollTo(), click());

        ViewInteraction cardView = onView(
                allOf(withId(R.id.cardView),
                        childAtPosition(
                                allOf(withId(R.id.recyclerView),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                2)),
                                0),
                        isDisplayed()));
        cardView.perform(click());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.detail_Title), withText("New"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText4.perform(replaceText("New title extend"));

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.detail_Title), withText("New title extend"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText5.perform(closeSoftKeyboard());

        pressBack();

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.detail_Title), withText("New title extend"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText6.perform(replaceText("New title extended"));

        ViewInteraction appCompatEditText7 = onView(
                allOf(withId(R.id.detail_Title), withText("New title extended"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText7.perform(closeSoftKeyboard());

        ViewInteraction appCompatEditText8 = onView(
                allOf(withId(R.id.detail_Title), withText("New title extended"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText8.perform(click());

        ViewInteraction chip2 = onView(
                allOf(withId(R.id.detail_State_Complete), withText("Completed"),
                        childAtPosition(
                                allOf(withId(R.id.detail_Group_state),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                4),
                        isDisplayed()));
        chip2.perform(click());

        ViewInteraction appCompatEditText9 = onView(
                allOf(withId(R.id.detail_Body), withText("New description "),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText9.perform(replaceText("New description extended"));

        ViewInteraction appCompatEditText10 = onView(
                allOf(withId(R.id.detail_Body), withText("New description extended"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText10.perform(closeSoftKeyboard());

        pressBack();

        ViewInteraction materialButton2 = onView(
                allOf(withId(android.R.id.button1), withText("UPDATE"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.buttonPanel),
                                        0),
                                3)));
        materialButton2.perform(scrollTo(), click());

        ViewInteraction cardView2 = onView(
                allOf(withId(R.id.cardView),
                        childAtPosition(
                                allOf(withId(R.id.recyclerView),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                2)),
                                0),
                        isDisplayed()));
        cardView2.perform(click());

        ViewInteraction materialButton3 = onView(
                allOf(withId(android.R.id.button2), withText("DELETE"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.buttonPanel),
                                        0),
                                2)));
        materialButton3.perform(scrollTo(), click());

        ViewInteraction overflowMenuButton = onView(
                allOf(withContentDescription("More options"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.toolbar),
                                        2),
                                0),
                        isDisplayed()));
        overflowMenuButton.perform(click());

        ViewInteraction materialTextView = onView(
                allOf(withId(R.id.title), withText("All Tasks"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        materialTextView.perform(click());

        pressBack();

        ViewInteraction overflowMenuButton2 = onView(
                allOf(withContentDescription("More options"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.toolbar),
                                        2),
                                0),
                        isDisplayed()));
        overflowMenuButton2.perform(click());

        ViewInteraction materialTextView2 = onView(
                allOf(withId(R.id.title), withText("Settings"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        materialTextView2.perform(click());

        pressBack();

        ViewInteraction cardView3 = onView(
                allOf(withId(R.id.cardView),
                        childAtPosition(
                                allOf(withId(R.id.recyclerView),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                2)),
                                0),
                        isDisplayed()));
        cardView3.perform(click());

        ViewInteraction materialButton4 = onView(
                allOf(withId(android.R.id.button2), withText("DELETE"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.buttonPanel),
                                        0),
                                2)));
        materialButton4.perform(scrollTo(), click());

        ViewInteraction overflowMenuButton3 = onView(
                allOf(withContentDescription("More options"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.toolbar),
                                        2),
                                0),
                        isDisplayed()));
        overflowMenuButton3.perform(click());

        ViewInteraction materialTextView3 = onView(
                allOf(withId(R.id.title), withText("About"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        materialTextView3.perform(click());
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
