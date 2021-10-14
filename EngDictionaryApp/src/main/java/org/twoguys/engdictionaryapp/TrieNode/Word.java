package org.twoguys.engdictionaryapp.TrieNode;

public class Word {
    private int id = -1;
    private String word = "";
    private String pronounce = "";

    public Word() {}

    public Word(Word newWord) {
        this.id = newWord.id;
        this.word = newWord.word;
        this.pronounce = newWord.pronounce;
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

    public int getID() { return id; }
    public String getWord() { return word; }
    public String getPronounce() { return pronounce; }
}
