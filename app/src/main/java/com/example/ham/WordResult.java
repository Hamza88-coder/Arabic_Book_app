package com.example.ham;



import java.util.List;

public class WordResult {
    private final String word;
    private final String phonetic;
    private final List<Meaning> meanings;

    public WordResult(String word, String phonetic, List<Meaning> meanings) {
        this.word = word;
        this.phonetic = phonetic;
        this.meanings = meanings;
    }

    public String getWord() {
        return word;
    }

    public String getPhonetic() {
        return phonetic;
    }

    public List<Meaning> getMeanings() {
        return meanings;
    }
}
