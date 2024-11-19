package com.example.forword;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    Button btn_startLearning, btn_exit;
    SharedPreferences user_activity;
    TextView tv_win_streak;
    int win_streak;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.user_activity = getSharedPreferences("user_activity", Context.MODE_PRIVATE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_win_streak = findViewById(R.id.tv_win_streak);
        btn_startLearning = findViewById(R.id.btn_startLearning);
        btn_exit = findViewById(R.id.btn_exit);
        btn_startLearning.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LearningActivity.class);
            startActivity(intent);
        });
        btn_exit.setOnClickListener(v -> {
            finishAffinity();
        });
        int cntDays = user_activity.getInt("cnt_days", 0);

        /*
        // Обнуляем, чтобы не было переполнения памяти
        if(cntDays > 100000){
            SharedPreferences.Editor editor = user_activity.edit();
            editor.clear();
            editor.apply();
        }
        */

        this.win_streak = user_activity.getInt("win_streak", 0);
        if(win_streak == 0)
            tv_win_streak.setText("У вас пока что нет серии побед :(");
        else
            tv_win_streak.setText("Ваша серия побед составила" + (this.win_streak) + " дней! Так держать!");
    }
    public String[] getWeeklyActivity(){
        // Смотрим дни за неделю, когда пользователь учил слова. Формат - массив строк по типу {"Monday", "Friday"}
        Set<String> set = user_activity.getStringSet("weekly_activity", new HashSet<String>());

        return set.toArray(new String[0]);
    }
    private boolean hasDoneLearningToday() {
        // Проверяем, запоминал ли пользователь слова сегодня
        String lastActivityDate = this.user_activity.getString("last_activity_date", "");
        String todayDate = getCurrentDate();

        return todayDate.equals(lastActivityDate);
    }
    private String getCurrentDate() {
        // Текущая дата
        Calendar calendar = Calendar.getInstance();

        return DateFormat.format("yyyy-MM-dd", calendar).toString();
    }
}