package com.example.forword;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LearningActivity extends AppCompatActivity {
    private GestureDetector gestureDetector;
    private CardView cardView;
    private TextView wordText, translationText;
    private Button knowButton, addButton;

    // Список слов и переводов
    private ArrayList<String[]> wordsList;
    private int currentWordIndex = 0;
    private boolean isSwiping = false;
    private boolean isFrontSide = true;
    SharedPreferences user_activity;
    public HashMap wordsMap = new HashMap<>();
    public HashMap learningMap = new HashMap<>();

    Button btn_toLearningActivity, btn_toReviewingActivity;
    ImageButton ib_back;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning);
        this.user_activity = getSharedPreferences("user_activity", Context.MODE_PRIVATE);

        cardView = findViewById(R.id.cardView);
        wordText = findViewById(R.id.wordText);
        translationText = findViewById(R.id.translationText);
        knowButton = findViewById(R.id.knowButton);
        addButton = findViewById(R.id.addButton);

        wordsList = new ArrayList<>();

        ib_back = findViewById(R.id.ib_back);
        btn_toLearningActivity = findViewById(R.id.btn_toLearningActivity);
        btn_toReviewingActivity = findViewById(R.id.btn_toReviewingActivity);

        String json = user_activity.getString("wordsMap", null);

        if (json != null) {
            Gson gson = new Gson();
            Type mapType = new TypeToken<Map<String, String>>() {
            }.getType();

            Map<String, String> map = gson.fromJson(json, mapType);

            wordsMap = new HashMap<>(map);
            //wordsMap = gson.fromJson(json, new TypeToken<HashMap<String, String>>() {
            //}.getType())
        } else {
            loadWords();
            if (wordsMap == null) {
                // Заглушка
                Toast.makeText(this, "Ошибка загрузки файла", Toast.LENGTH_SHORT).show();
                wordsList.add(new String[]{"deal", "сделка"});
                wordsList.add(new String[]{"house", "дом"});
                wordsList.add(new String[]{"car", "машина"});
                wordsList.add(new String[]{"apple", "яблоко"});
            } else {
                wordsMap.forEach((key, value) -> {
                    wordsList.add(new String[]{(String) key, (String) value});
                });
                SharedPreferences.Editor editor = user_activity.edit();

                Gson gson = new Gson();
                json = gson.toJson(wordsMap);

                editor.putString("wordsMap", json);
                editor.apply();
            }
        }
        setWord();

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(@NonNull MotionEvent e) {
                // Переворачиваем карточку по клику
                if (!isSwiping)
                    flipCard();
                return super.onSingleTapUp(e);
            }

            @Override
            public boolean onFling(MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
                isSwiping = true;
                if (velocityX > 0) {
                    swipeCardRight();
                } else {
                    swipeCardLeft();
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }

            @Override
            public void onLongPress(@NonNull MotionEvent e) {
                super.onLongPress(e);
                flipCard();

                isSwiping = true; // Свайп также должен срабатывать на долгий пресс
            }
        });

        cardView.setOnTouchListener(new View.OnTouchListener() {
            private float downX;  // Начальная позиция по X
            private float downY;  // Начальная позиция по Y
            private long downTime; // Время касания
            private boolean isMoving = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getRawX();
                        downY = event.getRawY();
                        downTime = System.currentTimeMillis();
                        isMoving = false;
                        return true;

                    case MotionEvent.ACTION_MOVE: // Перемещение
                        float deltaX = event.getRawX() - downX;
                        float deltaY = event.getRawY() - downY;

                        if (Math.abs(deltaX) > Math.abs(deltaY) && Math.abs(deltaX) > 20) {
                            // Считаем движение как свайп, если смещение достаточно велико
                            isMoving = true;
                            cardView.setTranslationX(deltaX);
                            cardView.setRotation(deltaX / 20);
                        }
                        return true;

                    case MotionEvent.ACTION_UP: // Конец касания
                        float totalDeltaX = event.getRawX() - downX;

                        if (isMoving) {
                            // Если карточка была свайпнута
                            if (Math.abs(totalDeltaX) > (int) (cardView.getWidth() / 2)) {
                                if (totalDeltaX > 0) {
                                    swipeCardRight();
                                } else {
                                    swipeCardLeft();
                                }
                            } else {
                                resetCardPosition(); // Если свайп был недостаточно длинный
                            }
                        } else {
                            // Если это был короткий тап, а не свайп
                            long upTime = System.currentTimeMillis();
                            if (upTime - downTime < 200) { // Считаем действие как тап (менее 200 мс)
                                flipCard(); // Переворачиваем карточку
                            }
                        }
                        return true;
                }
                return false;
            }
        });

        knowButton.setOnClickListener(v -> {
            nextWord();
        });

        addButton.setOnClickListener(v -> {
            addToReview();
            nextWord();
        });
        ib_back.setOnClickListener(view -> {
            Intent intent = new Intent(LearningActivity.this, MainActivity.class);
            startActivity(intent);
        });
        btn_toLearningActivity.setOnClickListener(view -> {
            Toast.makeText(this,"Вы уже находитесь в данной вкладке", Toast.LENGTH_SHORT).show();


        });
        btn_toReviewingActivity.setOnClickListener(view -> {
            Intent intent = new Intent(LearningActivity.this, ReviewingActivity.class);
            startActivity(intent);
        });
    }


    static class Word {
        String english;
        String russian;
    }

    public void loadWords() {

        try {
            InputStream is = getClass().getResourceAsStream("/res/raw/words.json");

            if (is == null) {
                throw new IOException("Файл words.json не найден");
            }

            // Преобразовать содержимое файла в строку ByteArrayOutputStream
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int n;
            byte[] buffer = new byte[1024];
            while ((n = is.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, n);
            }
            String json = byteArrayOutputStream.toString("UTF-8");

            Word[] wordsArray = new Gson().fromJson(json, Word[].class);

            for (Word word : wordsArray) {
                wordsMap.put(word.english, word.russian);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resetCardPosition() {
        // Анимация возврата карточки на исходную позицию
        cardView.animate()
                .translationX(0)
                .rotation(0)
                .setDuration(300)
                .start();
    }

    private void setWord() {
        // Получаем текущее слово и его перевод
        if (this.wordsMap == null || this.wordsMap.isEmpty()) {
            String[] currentWord = wordsList.get(currentWordIndex);
            wordText.setText(currentWord[0]);
            translationText.setText(currentWord[1]);
        } else {
            // Преобразуем entrySet в список
            List<Map.Entry<String, String>> entryList = new ArrayList<>(wordsMap.entrySet());
            Map.Entry<String, String> entry = entryList.get(currentWordIndex);
            wordText.setText(entry.getKey());
            translationText.setText(entry.getValue());
        }
    }

    private void flipCard() {
        if (translationText.getVisibility() == View.GONE) {
            translationText.setVisibility(View.VISIBLE);
            wordText.setVisibility(View.GONE);
        } else {
            translationText.setVisibility(View.GONE);
            wordText.setVisibility(View.VISIBLE);
        }

        int timing = 100;

        // Анимация переворота карточки
        ObjectAnimator flipAnimator = ObjectAnimator.ofFloat(cardView, "rotationY", 0f, 180f);
        flipAnimator.setDuration(timing);

        ObjectAnimator textAnimator;
        if (isFrontSide) {
            // Анимация отзеркаливания текста (переворот к переводу)
            textAnimator = ObjectAnimator.ofFloat(translationText, "scaleX", 1f, -1f);
            textAnimator.setDuration(timing);
            isFrontSide = false;
        } else {
            // Анимация отзеркаливания текста (переворот к слову)
            textAnimator = ObjectAnimator.ofFloat(wordText, "scaleX", 1f, -1f);
            textAnimator.setDuration(timing);
            isFrontSide = true;
        }

        // Добавляем сброс трансформаций после завершения анимации
        flipAnimator.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                // Сброс поворотов и смещений
                cardView.setRotationY(0);    // Сбрасываем вращение Y
                cardView.setTranslationX(0); // Сбрасываем смещение по X
                cardView.setRotation(0);     // Сбрасываем общий поворот
            }
        });

        // Запуск анимаций
        flipAnimator.start();
    }

    private void swipeCardLeft() {
        Toast.makeText(LearningActivity.this, "Свайп влево", Toast.LENGTH_SHORT).show();
        swipeCardOffScreen();
        isSwiping = false;
        if (this.wordsMap != null && !wordsMap.isEmpty()) {
            wordsMap.remove(wordText.getText().toString());
            Gson gson = new Gson();
            String json = gson.toJson(wordsMap);
            SharedPreferences.Editor editor = user_activity.edit();
            editor.putString("wordsMap", json);
            editor.apply();

            addToReview();
        }
    }
    private void addToReview(){
        SharedPreferences.Editor editor = user_activity.edit();
        String json = user_activity.getString("learningMap", null);
        Gson gson = new Gson();
        if(json == null || json.isEmpty()){
            HashMap<String, String> tmp = new HashMap<>();
            tmp.put(wordText.getText().toString(), translationText.getText().toString());
            json = gson.toJson(tmp);
            editor.putString("learningMap", json);
            editor.apply();
            return;
        }
        Type learningMapType = new TypeToken<HashMap<String, String>>() {
        }.getType();
        learningMap = gson.fromJson(json, learningMapType);

        if (learningMap == null) {
            HashMap<String, String> tmp = new HashMap<>();
            tmp.put(wordText.getText().toString(), translationText.getText().toString());
            json = gson.toJson(tmp);
        } else {
            learningMap.put(wordText.getText().toString(), translationText.getText().toString());

            json = gson.toJson(learningMap);
        }
        editor.putString("learningMap", json);
        editor.apply();
    }
    private void swipeCardRight() {
        Toast.makeText(LearningActivity.this, "Свайп вправо", Toast.LENGTH_SHORT).show();
        swipeCardOffScreen();
        isSwiping = false;

        if (wordsMap != null && !wordsMap.isEmpty()) {
            wordsMap.remove(wordText.getText().toString());
        }
    }

    private void nextWord() {
        currentWordIndex++;
        if ((this.wordsMap == null || this.wordsMap.isEmpty()) && currentWordIndex >= wordsList.size()) {
            currentWordIndex = 0;
            Toast.makeText(this, "Вы исследовали весь список слов!", Toast.LENGTH_SHORT).show();
        }
        if (this.wordsMap != null && !this.wordsMap.isEmpty() && currentWordIndex >= wordsMap.size()) {
            currentWordIndex = 0;
            Toast.makeText(this, "Вы исследовали весь список слов!", Toast.LENGTH_SHORT).show();
        }
        if (!isFrontSide) {
            flipCard();
        }
        setWord(); // Обновляем отображаемое слово
    }

    private void swipeCardOffScreen() {
        ObjectAnimator moveOffScreen = ObjectAnimator.ofFloat(cardView, "translationX", cardView.getWidth());
        moveOffScreen.setDuration(500); // Длительность анимации

        // Когда анимация завершена, обновляем содержимое и возвращаем карточку в центр
        moveOffScreen.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                nextWord();
                cardView.setTranslationX(0);
                cardView.setRotationY(0);
                cardView.setRotation(0);
                cardView.setTranslationY(0);
            }
        });

        moveOffScreen.start();
    }

}
