package com.example.user.lovewall;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        TextView hello = (TextView)findViewById(R.id.hello);
        Typeface textFont = Typeface.createFromAsset(getAssets(),"fonts/cloud.TTF");
        hello.setTypeface(textFont);

        View view  = View.inflate(WelcomeActivity.this, R.layout.welcome, null);
        setContentView(view);
        //动画效果参数直接定义
        Animation animation = new AlphaAnimation(0.1f, 1.0f);
        animation.setDuration(5000);

        //动画效果从XMl文件中定义
     //   Animation animation = AnimationUtils.loadAnimation(this, R.animator.alpha);
        view.setAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(WelcomeActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
