package com.example.projetcci;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.TextView;

import com.example.projetcci.activities.LoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> rule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void checkIfTextViewIsPresent() {

        LoginActivity activity = rule.getActivity();
        TextView tvIntro = activity.findViewById(R.id.titleLogin);

        assertThat(tvIntro, notNullValue());
        assertThat(tvIntro, instanceOf(TextView.class));
    }

    @Test
    public void checkIfTitleIsDisplayed() {
        LoginActivity activity = rule.getActivity();
        TextView tvIntro = activity.findViewById(R.id.titleLogin);

        assertThat(tvIntro.getText().toString(), is("Se connecter à ProjetCCI"));
    }

}
