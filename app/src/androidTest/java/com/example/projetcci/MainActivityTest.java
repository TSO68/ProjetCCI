package com.example.projetcci;

import android.support.design.widget.NavigationView;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void checkIfHeaderViewIsPresent() {

        MainActivity activity = rule.getActivity();
        NavigationView navigationView = activity.findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView userEmail = headerView.findViewById(R.id.txtUserEmail);

        assertThat(headerView, notNullValue());
        assertThat(headerView, instanceOf(View.class));

        assertThat(userEmail, notNullValue());
        assertThat(userEmail, instanceOf(View.class));
    }

    @Test
    public void checkIfUserEmailIsDisplayed() {
        MainActivity activity = rule.getActivity();
        NavigationView navigationView = activity.findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView userEmail = headerView.findViewById(R.id.txtUserEmail);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserEmail;
        if (currentUser != null) {
            currentUserEmail = currentUser.getEmail();
        } else {
            currentUserEmail = "android.studio@android.com";
        }

        assertThat(userEmail.getText().toString(), is(currentUserEmail));
    }
}
