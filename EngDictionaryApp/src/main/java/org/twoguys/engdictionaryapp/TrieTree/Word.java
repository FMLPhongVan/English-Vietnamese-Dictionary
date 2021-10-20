package org.twoguys.engdictionaryapp.TrieTree;

public class Word {
    private int id = -1;
    private String word = "";
    private String pronounce = "";
    private int favourite = 0;

    public Word() {}

    public Word(Word newWord) {
        this.id = newWord.id;
        this.word = newWord.word;
        this.pronounce = newWord.pronounce;
        this.favourite = newWord.favourite;
    }

    public Word(int id, String word, String pronounce, int favourite) {
        this.id = id;
        this.word = word;
        this.pronounce = pronounce;
        this.favourite = favourite;
    }

    public void setID(int id) {
        this.id = id;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setPronounce(String pronounce) {
        this.pronounce = pronounce;
    }

    public void setFavourite(int favourite) {
        this.favourite = favourite;
    }

    public int getID() { return id; }
    public String getWord() { return word; }
    public String getPronounce() { return pronounce; }
    public int getFavourite() { return favourite; }
}
