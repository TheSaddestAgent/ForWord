package com.example.forword;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class LearningActivity extends AppCompatActivity {
    private FrameLayout cardStackLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning);

        cardStackLayout = findViewById(R.id.cardStackLayout);

        // Данные для карточек
        List<WordCard> wordCards = new ArrayList<>();
        wordCards.add(new WordCard("Apple", "Яблоко"));
        wordCards.add(new WordCard("Banana", "Банан"));
        wordCards.add(new WordCard("Orange", "Апельсин"));

        // Создаем и добавляем карточки в стопку
        CardStackAdapter cardStackAdapter = new CardStackAdapter(wordCards, this);
        cardStackAdapter.createCardStack(cardStackLayout);
    }
}