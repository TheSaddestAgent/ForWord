package com.example.forword;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.cardview.widget.CardView;
import java.util.List;

public class CardStackAdapter {

    private List<WordCard> wordCards;
    private Context context;

    public CardStackAdapter(List<WordCard> wordCards, Context context) {
        this.wordCards = wordCards;
        this.context = context;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void createCardStack(FrameLayout cardStackLayout) {
        for (int i = 0; i < wordCards.size(); i++) {
            WordCard wordCard = wordCards.get(i);
            // Создание карточки
            CardView cardView = (CardView) LayoutInflater.from(context).inflate(R.layout.item_word_card, cardStackLayout, false);
            TextView frontText = cardView.findViewById(R.id.frontText);
            TextView backText = cardView.findViewById(R.id.backText);

            frontText.setText(wordCard.getWord());
            backText.setText(wordCard.getTranslation());

            // Обработка свайпов и переворота карточки
            int finalI = i;
            cardView.setOnTouchListener(new View.OnTouchListener() {
                private float startX;
                private float startY;
                private float deltaX = 0;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            startX = event.getX();
                            startY = event.getY();
                            return true;

                        case MotionEvent.ACTION_MOVE:
                            // Перемещение карточки
                            deltaX = event.getX() - startX;
                            v.setTranslationX(deltaX);

                            // Показываем частично следующую карточку
                            if (Math.abs(deltaX) > 200) {
                                cardStackLayout.getChildAt(finalI + 1).setTranslationX(deltaX > 0 ? 100 : -100); // Смещение следующей карточки
                            }
                            return true;

                        case MotionEvent.ACTION_UP:
                            // Если свайп слишком мал, возвращаем карточку на место
                            if (Math.abs(deltaX) < 100) {
                                v.setTranslationX(0);
                            } else {
                                // Если свайп завершён
                                if (deltaX > 0) {
                                    Toast.makeText(context, "Свайп вправо", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Свайп влево", Toast.LENGTH_SHORT).show();
                                }
                                // Убираем карточку из стопки
                                cardStackLayout.removeView(cardView);
                            }
                            // Вернуть следующую карточку на место
                            if (finalI + 1 < cardStackLayout.getChildCount()) {
                                cardStackLayout.getChildAt(finalI + 1).setTranslationX(0);
                            }
                            return true;
                    }
                    return false;
                }
            });

            // Переворот карточки при нажатии
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Переворачиваем карточку с анимацией
                    ObjectAnimator rotation = ObjectAnimator.ofFloat(cardView, "rotationY", 0f, 180f);
                    rotation.setDuration(500);
                    rotation.start();

                    // Переключаем видимость текста
                    frontText.setVisibility(frontText.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
                    backText.setVisibility(backText.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
                }
            });

            // Переопределяем performClick для обеспечения кликабельности
            cardView.setOnTouchListener((v, event) -> {
                v.performClick();
                return false;
            });

            // Добавляем карточку в стопку
            cardStackLayout.addView(cardView);
        }
    }
}
