package com.example.forword;

public class WordCard {
    private String word;
    private String translation;

    public WordCard(String word, String translation) {
        this.word = word;
        this.translation = translation;
    }

    public String getWord() {
        return word;
    }

    public String getTranslation() {
        return translation;
    }
}