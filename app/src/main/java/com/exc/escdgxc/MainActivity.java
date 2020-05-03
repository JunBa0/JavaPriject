package com.exc.escdgxc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mImage;
    private TextView mText;
    private int num;
    private int index;
    private String[] title;
    private int[] images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化控件
        initView();
        //初始化数据
        initData();
    }
    private void initData(){
        title = new String[]{"第1张图片","第2张图片","第3张图片","第4张图片","第5张图片","第6张图片","第7张图片","第8张图片","第9张图片","第10张图片","第11张图片",
                "第12张图片","第13张图片","第14张图片","第15张图片","第16张图片","第17张图片","第18张图片","第19张图片"};
        images = new int[]{R.drawable.a,R.drawable.b,R.drawable.c,R.drawable.d,R.drawable.e,R.drawable.f,R.drawable.g,R.drawable.h,
                R.drawable.i,R.drawable.j,R.drawable.k,R.drawable.l,R.drawable.m,R.drawable.n,R.drawable.o,R.drawable.p,R.drawable.q,R.drawable.r,R.drawable.s};
        mImage.setImageResource(images[0]);
        mText.setText(title[0]);

        num = title.length;
        index = 0;


    }
    private void initView(){
        mImage = findViewById(R.id.iv_show);
        mText = findViewById(R.id.tv_show);
        findViewById(R.id.btn_previous).setOnClickListener(this);
        findViewById(R.id.btn_next).setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_previous:
                if(index == 0){
                    index = title.length - 1;
                }else {
                    index--;
                }
                break;
            case R.id.btn_next:
                if(index == 18){
                    index = 0;
                }else{
                    index++;
                }
                break;
        }
        updateImageAndTitle();
    }
    private void updateImageAndTitle(){
        mImage.setImageResource(images[index]);
        mText.setText(title[index]);
    }

}

