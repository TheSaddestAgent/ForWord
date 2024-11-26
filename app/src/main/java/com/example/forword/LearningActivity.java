package com.example.forword;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.cardview.widget.CardView;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
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

        // Инициализация GestureDetector для обработки свайпов
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(@NonNull MotionEvent e) {
                // Переворачиваем карточку по клику
                flipCard();
                return super.onSingleTapUp(e);
            }

            // Добавьте обработку свайпов по необходимости
            @Override
            public boolean onFling(MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
                if (velocityX > 0) {
                    swipeCardRight();
                } else {
                    swipeCardLeft();
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });

        cardView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        knowButton.setOnClickListener(v -> {
            nextWord();
        });

        nextWordButton.setOnClickListener(v -> {
            nextWord();
        });
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

        ObjectAnimator flipAnimator = ObjectAnimator.ofFloat(cardView, "rotationY", 0f, 180f);
        flipAnimator.setDuration(500);
        flipAnimator.start();
    }

    private void swipeCardRight() {
        Toast.makeText(LearningActivity.this, "Свайп влево", Toast.LENGTH_SHORT).show();
        nextWord();
    }

    private void swipeCardLeft() {
        Toast.makeText(LearningActivity.this, "Свайп вправо", Toast.LENGTH_SHORT).show();
        nextWord();
    }

    private void nextWord() {
        currentWordIndex++;
        if (currentWordIndex >= wordsList.size()) {
            currentWordIndex = 0;
        }
        setWord(); // Обновляем отображаемое слово
    }
}
