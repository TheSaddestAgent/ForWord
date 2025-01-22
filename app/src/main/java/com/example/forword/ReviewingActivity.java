package com.example.forword;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.TreeMap;

public class ReviewingActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    EditText et_word;
    TextView tv_translation;
    ImageView iv_eye;
    SharedPreferences user_activity;
    TreeMap<String, String> learningMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviewing);

        this.user_activity = getSharedPreferences("user_activity", Context.MODE_PRIVATE);

        et_word = findViewById(R.id.et_word);
        iv_eye = findViewById(R.id.iv_eye);
        tv_translation = findViewById(R.id.tv_translation);
        tabLayout = findViewById(R.id.tab_layout);


        // Вкладки
        tabLayout.addTab(tabLayout.newTab().setText("Первая активность"));
        tabLayout.addTab(tabLayout.newTab().setText("Вторая активность"));

        // Listener для вкладок
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {// Переход к первой активности
                    startActivity(new Intent(ReviewingActivity.this, LearningActivity.class));
                }
                else{
                    startActivity(new Intent(ReviewingActivity.this, ReviewingActivity.class));

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Не требуется
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Не требуется
            }
        });

        String json = user_activity.getString("learningMap", null);
        Gson gson = new Gson();

        Type learningMapType = new TypeToken<TreeMap<String, String>>() {
        }.getType();
        learningMap = gson.fromJson(json, learningMapType);

        if (learningMap == null) {
            Toast.makeText(this, "В данный момент у вас нет текущих слов", Toast.LENGTH_SHORT).show();
        } else {
            tv_translation.setText(learningMap.get(learningMap.firstKey()));
        }


    }
}