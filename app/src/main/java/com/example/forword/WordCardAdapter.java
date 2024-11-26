package com.example.forword;

import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import androidx.recyclerview.widget.RecyclerView;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import android.view.View;
import android.widget.TextView;

public class WordCardAdapter extends RecyclerView.Adapter<WordCardAdapter.WordCardViewHolder> {

    private List<WordCard> wordCards;
    private Set<Integer> flippedCardPosition = new HashSet<>();

    public WordCardAdapter(List<WordCard> wordCards) {
        this.wordCards = wordCards;
    }

    @Override
    public WordCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_word_card, parent, false);
        return new WordCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WordCardViewHolder holder, int position) {
        WordCard wordCard = wordCards.get(position);
        holder.bind(wordCard, position);
    }

    @Override
    public int getItemCount() {
        return wordCards.size();
    }

    public class WordCardViewHolder extends RecyclerView.ViewHolder {

        private TextView frontText;
        private TextView backText;

        public WordCardViewHolder(View itemView) {
            super(itemView);
            frontText = itemView.findViewById(R.id.frontText);
            backText = itemView.findViewById(R.id.backText);
        }

        public void bind(final WordCard wordCard, final int position) {
            frontText.setText(wordCard.getWord());
            backText.setText(wordCard.getTranslation());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (flippedCardPosition.contains(position)) {
                        backText.setVisibility(View.GONE);
                    } else {
                        backText.setVisibility(View.VISIBLE);
                    }
                    if (flippedCardPosition.contains(position)) {
                        flippedCardPosition.remove(position);
                    } else {
                        flippedCardPosition.add(position);
                    }
                }
            });

            itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // Обработка свайпов
                    return true;
                }
            });
        }
    }
}
