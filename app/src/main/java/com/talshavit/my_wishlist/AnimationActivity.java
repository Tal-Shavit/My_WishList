package com.talshavit.my_wishlist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.talshavit.my_wishlist.Signup_Login.StartActivity;

public class AnimationActivity extends AppCompatActivity {

    private TextView textAnimation;
    private TextView textAnimation1;
    private static final int DELAY_TIME = 3200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);

        textAnimation = findViewById(R.id.textAnimation);
        startAnimation();

        // Post a delayed message to switch activities after 3.2 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switchActivity();
            }
        }, DELAY_TIME);
    }

    private void switchActivity() {
        Intent myIntent = new Intent(this, StartActivity.class);
        startActivity(myIntent);
        finish();
    }

    private void startAnimation(){
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim);
        textAnimation.startAnimation(animation);
    }

}