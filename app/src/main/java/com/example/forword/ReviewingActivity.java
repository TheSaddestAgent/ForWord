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
    ImageButton btn_check;
    String cur;
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
        btn_check = findViewById(R.id.btn_check);
        String json = user_activity.getString("learningMap", null);
        Gson gson = new Gson();

        Type learningMapType = new TypeToken<TreeMap<String, String>>() {
        }.getType();
        learningMap = gson.fromJson(json, learningMapType);

        if (learningMap == null) {
            Toast.makeText(this, "В данный момент у вас нет текущих слов", Toast.LENGTH_SHORT).show();
        } else {
            String s = learningMap.firstKey();
            tv_translation.setText(learningMap.get(s));
            this.cur = s;
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
        btn_check.setOnClickListener(view ->{
            String userGuess = et_word.getText().toString();
            if(learningMap.isEmpty() || !((tv_translation.getText().toString()).equals(learningMap.get(userGuess)))){
                Toast.makeText(this,"Неправильно, попробуйте еще", Toast.LENGTH_SHORT).show();
            } else{
                //Toast.makeText(this,"Верно!", Toast.LENGTH_SHORT).show();
                this.tv_translation.setText("!!!");
                nextWord();
            }
        });
    }

    private void nextWord(){
        if(this.cur == null || this.cur.isEmpty()){
            Toast.makeText(this, "ERROR: this.cur пустой", Toast.LENGTH_SHORT).show();
            return;
        }
        learningMap.remove(this.cur);
        Toast.makeText(this, this.cur, Toast.LENGTH_SHORT).show();
        if(learningMap.isEmpty()){
            Toast.makeText(this, "Закончились слова для повторения!", Toast.LENGTH_SHORT).show();
            return;

        }
        Map.Entry<String,String> nxt = learningMap.firstEntry();
        this.tv_translation.setText(nxt.getValue().toString());
        this.et_word.setText("");
    }
}