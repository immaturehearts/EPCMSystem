package com.example.epcmsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Random;

public class ComicActivity extends AppCompatActivity {

    private int[] comics1 = {R.drawable.c10, R.drawable.c11, R.drawable.c12, R.drawable.c13, R.drawable.c14, R.drawable.c15,
            R.drawable.c16, R.drawable.c17, R.drawable.c18, R.drawable.c19, R.drawable.c20, R.drawable.c21, R.drawable.c22, R.drawable.c23,
            R.drawable.c24, R.drawable.c25, R.drawable.c26, R.drawable.c27, R.drawable.c28, R.drawable.c29, R.drawable.c30, R.drawable.c31,
            R.drawable.c32, R.drawable.c33, R.drawable.c34, R.drawable.c35, R.drawable.c36, R.drawable.c37, R.drawable.c38, R.drawable.c39,
            R.drawable.c40, R.drawable.c41, R.drawable.c42, R.drawable.c43, R.drawable.c44, R.drawable.c45,};
    private ArrayList<Integer> comicList = new ArrayList<>();
    private CardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic);
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();// 隐藏ActionBar
        }

        initCards();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.comic_rv);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CardAdapter(comicList, 1);
        recyclerView.setAdapter(adapter);
    }

    private void initCards(){
        comicList.clear();
        for (int i = 0; i < comics1.length; i++) {
            comicList.add(comics1[i]);
        }
    }
}