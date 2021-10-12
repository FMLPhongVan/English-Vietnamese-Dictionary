package org.twoguys.engdictionaryapp.TrieNode;

public class Word {
    private String word = "";
    private String description = "";
    private String pronounce = "";
    private String htmlData = "";

    public Word() {}

    public Word(Word newWord) {
        this.word = newWord.word;
        this.description = newWord.description;
        this.htmlData = newWord.htmlData;
        this.pronounce = newWord.pronounce;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPronounce(String pronounce) {
        this.pronounce = pronounce;
    }

    public void setHtmlData(String htmlData) {
        this.htmlData = htmlData;
    }

    public String getWord() { return word; }
    public String getDescription() { return description; }
    public String getPronounce() { return pronounce; }
    public String getHtmlData() { return htmlData; }
}
