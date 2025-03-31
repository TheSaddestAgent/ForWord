package com.example.forword;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.Set;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    Button btn_startLearning, btn_startReviewing, btn_exit;
    SharedPreferences user_activity;
    TextView tv_win_streak;
    int win_streak;
    GridLayout gridLayout; // Контейнер для кружочков для дней недели
    String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    String[] activeDays = {"Monday", "Friday"}; // Массив активных дней как в shared preference (для дебага)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.user_activity = getSharedPreferences("user_activity", Context.MODE_PRIVATE);
        /*
        // проверка что работает отображение для > 0
        SharedPreferences.Editor editor = user_activity.edit();
        editor.putInt("cnt_days", 0);
        editor.apply();
        */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_win_streak = findViewById(R.id.tv_win_streak);
        btn_startLearning = findViewById(R.id.btn_startLearning);
        btn_exit = findViewById(R.id.btn_exit);
        btn_startLearning.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LearningActivity.class);
            startActivity(intent);
        });
        btn_exit.setOnClickListener(v -> finishAffinity());
        btn_startReviewing = findViewById(R.id.btn_startReviewing);
        btn_startReviewing.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ReviewingActivity.class);
            startActivity(intent);
        });
        int cntDays = user_activity.getInt("cnt_days", 0);
        String lastDate = user_activity.getString("last_activity_date", "");
        String currentDate = getCurrentDate();
        if(cntDays > 0){
            this.tv_win_streak.setText("Вы изучали слова " + cntDays + " дней подряд! Так держать! ;)");
        } else{
            this.tv_win_streak.setText("У вас пока что нет серии дней подряд когда вы успешно повторили слова :(");
        }
        // Обнуляем, чтобы сбросить отображение с новой недели
        if(lastDate.isEmpty() || !isSameWeek(lastDate, currentDate)){
            SharedPreferences.Editor editor = user_activity.edit();
            editor.remove("weekly_activity");
            editor.apply();
        }

        LinearLayout row1 = findViewById(R.id.row1);
        LinearLayout row2 = findViewById(R.id.row2);

        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < 4; i++) {
            RelativeLayout dayItem = (RelativeLayout) inflater.inflate(R.layout.item_day, row1, false);
            ImageView dayCircle = dayItem.findViewById(R.id.day_circle);
            TextView dayName = dayItem.findViewById(R.id.day_name);

            dayName.setText(daysOfWeek[i].substring(0, 3));

            // Проверяем, нужно ли сделать кружок зеленым
            if (isDayActive(daysOfWeek[i])) {
                dayCircle.setImageResource(R.drawable.completed_circle_day);
            }

            // Настраиваем параметры для равномерного распределения
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0, // Ширина равна 0, чтобы учитывать вес
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f // Вес равен 1
            );
            dayItem.setLayoutParams(params);

            row1.addView(dayItem);
        }
        for (int i = 0; i < 3; i++) {
            RelativeLayout dayItem = (RelativeLayout) inflater.inflate(R.layout.item_day, row2, false);
            ImageView dayCircle = dayItem.findViewById(R.id.day_circle);
            TextView dayName = dayItem.findViewById(R.id.day_name);

            dayName.setText(daysOfWeek[i + 4].substring(0, 3));

            // Проверяем, нужно ли сделать кружок зеленым
            if (isDayActive(daysOfWeek[i + 4])) {
                dayCircle.setImageResource(R.drawable.completed_circle_day);
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
            );
            dayItem.setLayoutParams(params);

            row2.addView(dayItem);
        }
    }

    public String[] getWeeklyActivity(){
        // Смотрим дни за неделю, когда пользователь учил слова. Формат - массив строк по типу {"Monday", "Friday"}
        Set<String> set = user_activity.getStringSet("weekly_activity", new HashSet<String>());

        return set.toArray(new String[0]);
    }
    private boolean hasDoneLearningToday() {
        String lastActivityDate = this.user_activity.getString("last_activity_date", "");
        String todayDate = getCurrentDate();

        return todayDate.equals(lastActivityDate);
    }
    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();

        return DateFormat.format("yyyy-MM-dd", calendar).toString();
    }
    public String getCurrentDayOfWeek(){
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        String dayName = "";
        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                dayName = "Sunday";
                break;
            case Calendar.MONDAY:
                dayName = "Monday";
                break;
            case Calendar.TUESDAY:
                dayName = "Tuesday";
                break;
            case Calendar.WEDNESDAY:
                dayName = "Wednesday";
                break;
            case Calendar.THURSDAY:
                dayName = "Thursday";
                break;
            case Calendar.FRIDAY:
                dayName = "Friday";
                break;
            case Calendar.SATURDAY:
                dayName = "Saturday";
                break;
        }
        return dayName;
    }
    public boolean isDayActive(String day) {
        activeDays = getWeeklyActivity(); // Для дебага закоментить
        for (String activeDay : activeDays) {
            if (activeDay.equals(day)) {
                return true;
            }
        }
        return false;
    }
    private boolean isSameWeek(String date1, String date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        // Преобразуем строки в даты
        try {
            cal1.setTime(Objects.requireNonNull(new SimpleDateFormat("yyyy-MM-dd").parse(date1)));
            cal2.setTime(Objects.requireNonNull(new SimpleDateFormat("yyyy-MM-dd").parse(date2)));
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

        // Сравниваем номера недель и годы
        return cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }
}