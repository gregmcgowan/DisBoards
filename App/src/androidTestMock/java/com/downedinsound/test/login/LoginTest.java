package com.downedinsound.test.login;

import com.drownedinsound.R;
import com.drownedinsound.test.login.FakeDisRepo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

/**
 * Created by gregmcgowan on 06/12/15.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginTest {

    @Rule
    public IntentsTestRule<LoginActivity> loginActivityIntentsTestRule =
            new IntentsTestRule<>(LoginActivity.class,true, true);

    /**
     * Prepare your test fixture for this test. In this case we register an IdlingResources with
     * Espresso. IdlingResource resource is a great way to tell Espresso when your app is in an
     * idle state. This helps Espresso to synchronize your test actions, which makes tests significantly
     * more reliable.
     */
    @Before
    public void registerIdlingResource() {
        //Does this add anything?
        Espresso.registerIdlingResources(
                loginActivityIntentsTestRule.getActivity().getCountingIdlingResource());
    }

    @Test
    public void testSuccessfulLogin() throws Exception {
        List<BoardPost> boardPosts = new ArrayList<>();
        BoardPost boardPost = new BoardPost();
        boardPost.setId("1234");
        boardPost.setAuthorUsername("Test user");
        boardPost.setDateOfPost("Now ");
        boardPost.setNumberOfReplies(1);
        boardPost.setCreatedTime(new Date().getTime());

        FakeDisRepo.setBoardPostSummariesToReturn(boardPosts);


        onView(withId(R.id.login_activity_username_field)).perform(typeText("ripper"));
        onView(withId(R.id.login_password_field)).perform(typeText("password1"), closeSoftKeyboard());

        onView(withId(R.id.login_button)).perform(click());


        onView(withText("Test user")).check(matches(isDisplayed()));
    }

    @Test
    public void testLoginValidation() throws Exception {
        onView(withId(R.id.login_password_field)).perform(typeText("password1"), closeSoftKeyboard());

        onView(withId(R.id.login_button)).perform(click());

        onView(withText(R.string.please_enter_both_username_and_password))
                .inRoot(withDecorView(not(is(loginActivityIntentsTestRule.getActivity().getWindow()
                        .getDecorView()))))
                .check(matches(isDisplayed()));
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    public void unregisterIdlingResource() {
        Espresso.unregisterIdlingResources(
                loginActivityIntentsTestRule.getActivity().getCountingIdlingResource());
    }

}
