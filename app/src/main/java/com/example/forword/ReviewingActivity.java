package com.example.forword;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
    EditText et_word;
    TextView tv_translation;
    ImageButton ib_eye;
    SharedPreferences user_activity;
    TreeMap<String, String> learningMap;
    ImageButton ib_back;
    Button btn_toLearningActivity, btn_toReviewingActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviewing);

        this.user_activity = getSharedPreferences("user_activity", Context.MODE_PRIVATE);

        et_word = findViewById(R.id.et_word);
        ib_eye = findViewById(R.id.ib_eye);
        tv_translation = findViewById(R.id.tv_translation);
        ib_back = findViewById(R.id.ib_back);
        btn_toLearningActivity = findViewById(R.id.btn_toLearningActivity);
        btn_toReviewingActivity = findViewById(R.id.btn_toReviewingActivity);

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

        ib_back.setOnClickListener(view -> {
            Intent intent = new Intent(ReviewingActivity.this, MainActivity.class);
            startActivity(intent);
        });
        btn_toLearningActivity.setOnClickListener(view -> {
            Intent intent = new Intent(ReviewingActivity.this, LearningActivity.class);
            startActivity(intent);
        });
        btn_toReviewingActivity.setOnClickListener(view -> {
            Toast.makeText(this,"Вы уже находитесь в данной вкладке", Toast.LENGTH_SHORT).show();
        });
    }
}