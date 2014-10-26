package com.drownedinsound;

import com.drownedinsound.ui.activity.LoginActivity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.util.ActivityController;

import android.view.View;

import static org.junit.Assert.assertEquals;

/**
 * Created by gregmcgowan on 22/10/14.
 */
@RunWith(RobolectricGradleTestRunner.class)
public class LoginActivityTest {


    @Test
    public void testLogin() throws Exception {
        LoginActivity loginActivity = new LoginActivity();

        ActivityController.of(loginActivity).attach().create();

        int loginButtonVisiblitity = loginActivity.findViewById(R.id.login_button).getVisibility();
        assertEquals(loginButtonVisiblitity, View.VISIBLE);
    }
}
