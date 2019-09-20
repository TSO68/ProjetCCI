package com.example.projetcci.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.example.projetcci.R;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

/**
 * Activity with slides that presents the app
 */
public class IntroActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Create slides
        SliderPage sliderOne = new SliderPage();
        sliderOne.setTitle(getString(R.string.discover_movies));
        sliderOne.setDescription(getString(R.string.create_account));
        sliderOne.setImageDrawable(R.drawable.slider_one);
        sliderOne.setBgColor(getResources().getColor(R.color.gray));
        addSlide(AppIntroFragment.newInstance(sliderOne));

        SliderPage sliderTwo = new SliderPage();
        sliderTwo.setTitle(getString(R.string.movie_details));
        sliderTwo.setDescription(getString(R.string.get_details));
        sliderTwo.setImageDrawable(R.drawable.slider_two);
        sliderTwo.setBgColor(getResources().getColor(R.color.gray));
        addSlide(AppIntroFragment.newInstance(sliderTwo));

        SliderPage sliderThree = new SliderPage();
        sliderThree.setTitle(getString(R.string.like_movie));
        sliderThree.setDescription(getString(R.string.mark_movie));
        sliderThree.setImageDrawable(R.drawable.slider_three);
        sliderThree.setBgColor(getResources().getColor(R.color.gray));
        addSlide(AppIntroFragment.newInstance(sliderThree));

        SliderPage sliderFour = new SliderPage();
        sliderFour.setTitle(getString(R.string.vader_quote));
        sliderFour.setDescription(getString(R.string.share_quotes));
        sliderFour.setImageDrawable(R.drawable.slider_four);
        sliderFour.setBgColor(getResources().getColor(R.color.gray));
        addSlide(AppIntroFragment.newInstance(sliderFour));

        SliderPage sliderFive = new SliderPage();
        sliderFive.setTitle(getString(R.string.get_stats));
        sliderFive.setDescription(getString(R.string.get_statistics));
        sliderFive.setImageDrawable(R.drawable.slider_five);
        sliderFive.setBgColor(getResources().getColor(R.color.gray));
        addSlide(AppIntroFragment.newInstance(sliderFive));
    }

    /**
     * Action when Skip button is pressed
     * @param currentFragment the current slide
     */
    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);

        //Open LoginActivity
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Action when Done button is pressed
     * @param currentFragment the current slide
     */
    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        //Open LoginActivity
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Action when slide changes
     * @param oldFragment the previous slide
     * @param newFragment the new slide
     */
    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        //Nothing
    }
}
