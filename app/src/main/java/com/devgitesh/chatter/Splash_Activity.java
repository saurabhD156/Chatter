package com.devgitesh.chatter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.devgitesh.chatter.login_form.Sign_in_Activity;

public class Splash_Activity extends AppCompatActivity {

    Animation textAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        textAnim = AnimationUtils.loadAnimation(this,R.anim.splash_text_anim);

        TextView appName = findViewById(R.id.app_Name);
        TextView slogan2 = findViewById(R.id.slogan_2);

        appName.setAnimation(textAnim);
        slogan2.setAnimation(textAnim);

        new Handler().postDelayed(() -> {
            Intent i = new Intent(Splash_Activity.this, Sign_in_Activity.class);
            startActivity(i);
            finish();
        },3000);

    }
}