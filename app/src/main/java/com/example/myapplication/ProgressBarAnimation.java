package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProgressBarAnimation extends Animation {

    boolean isBegin = false;

    private Context context;
    private ProgressBar progressBar;
    private TextView tvPresents;
    private float from;
    private float to;

    public ProgressBarAnimation(Context context, ProgressBar progressBar, TextView textView, float from, float to){
        this.context = context;
        this. progressBar = progressBar;
        this.tvPresents = textView;
        this.from = from;
        this.to = to;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        // מחשב את הערך של הפרוגרס בר
        super.applyTransformation(interpolatedTime, t);
        float value = from + (to - from) * interpolatedTime;
        progressBar.setProgress((int) value);
        tvPresents.setText((int) value+ " %");
        if (value == to && !isBegin){
            Intent intent = new Intent(context,MainActivity.class);
            Log.i("", "applyTransformation: "+value +" "+to);
            context.startActivity(intent);
            value = 0;
            isBegin = true;
        }

    }
}
