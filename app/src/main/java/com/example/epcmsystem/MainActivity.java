package com.example.epcmsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private int[] imgs = {R.drawable.m1, R.drawable.c1, R.drawable.c2, R.drawable.c3, R.drawable.c4, R.drawable.c5, R.drawable.c6, R.drawable.c7};
    ImageView main_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();// 隐藏ActionBar
        }
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        main_img = findViewById(R.id.main_iv);

        Random random = new Random();
        int index = random.nextInt(imgs.length);
        if(index!=0) {
            main_img.setBackgroundColor(Color.WHITE);
            main_img.setScaleX(1.5F);
            main_img.setScaleY(1.5F);
        }
        Glide.with(getApplicationContext()).load(imgs[index]).into(main_img);

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this,NavigationActivity.class));
                finish();
            }
        };
        timer.schedule(task,2000);
    }
}