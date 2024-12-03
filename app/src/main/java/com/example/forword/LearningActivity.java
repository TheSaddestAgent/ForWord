package com.example.forword;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.cardview.widget.CardView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class LearningActivity extends AppCompatActivity {
    private GestureDetector gestureDetector;
    private CardView cardView;
    private TextView wordText, translationText;
    private Button knowButton, nextWordButton;

    // Список слов и переводов
    private ArrayList<String[]> wordsList;
    private int currentWordIndex = 0;
    private boolean isSwiping = false;
    private boolean isFrontSide = true;
    SharedPreferences user_activity;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning);

        // Инициализация компонентов UI
        cardView = findViewById(R.id.cardView);
        wordText = findViewById(R.id.wordText);
        translationText = findViewById(R.id.translationText);
        knowButton = findViewById(R.id.knowButton);
        nextWordButton = findViewById(R.id.nextWordButton);

        // Инициализация списка слов
        wordsList = new ArrayList<>();
        wordsList.add(new String[]{"deal", "сделка"});
        wordsList.add(new String[]{"house", "дом"});
        wordsList.add(new String[]{"car", "машина"});
        wordsList.add(new String[]{"apple", "яблоко"});

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
                    case MotionEvent.ACTION_DOWN: // Начало касания
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
                            cardView.setTranslationX(deltaX); // Перемещаем карточку
                            cardView.setRotation(deltaX / 20); // Поворачиваем карточку
                        }
                        return true;

                    case MotionEvent.ACTION_UP: // Конец касания
                        float totalDeltaX = event.getRawX() - downX;

                        if (isMoving) {
                            // Если карточка была свайпнута
                            if (Math.abs(totalDeltaX) > (int)(cardView.getWidth() / 2)) {
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

        nextWordButton.setOnClickListener(v -> {
            nextWord();
        });
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
        String[] currentWord = wordsList.get(currentWordIndex);
        wordText.setText(currentWord[0]);
        translationText.setText(currentWord[1]);
    }

    private void flipCard() {
        if (translationText.getVisibility() == View.GONE) {
            translationText.setVisibility(View.VISIBLE);
            wordText.setVisibility(View.GONE);
        } else {
            translationText.setVisibility(View.GONE);
            wordText.setVisibility(View.VISIBLE);
        }

        int timing = 500;

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
        //textAnimator.start();
        flipAnimator.start();
    }

    private void swipeCardRight() {
        Toast.makeText(LearningActivity.this, "Свайп вправо", Toast.LENGTH_SHORT).show();
        //nextWord();
        swipeCardOffScreen();
        isSwiping = false;
    }

    private void swipeCardLeft() {
        Toast.makeText(LearningActivity.this, "Свайп влево", Toast.LENGTH_SHORT).show();
        //nextWord();
        swipeCardOffScreen();
        isSwiping = false;
    }

    private void nextWord() {
        currentWordIndex++;
        if (currentWordIndex >= wordsList.size()) {
            currentWordIndex = 0;
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
