package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoadingScreenActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView tvPresents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        progressAnimation();
    }

    public void progressAnimation(){

        //יוצר את האנימציה של הפרוגרס בר

        progressBar = findViewById(R.id.progress_bar);
        tvPresents = findViewById(R.id.tvPercents);

        ProgressBarAnimation animation = new ProgressBarAnimation(this, progressBar, tvPresents, 0f, 100f);
        animation.setDuration(5000);
        progressBar.setAnimation(animation);

    }
}