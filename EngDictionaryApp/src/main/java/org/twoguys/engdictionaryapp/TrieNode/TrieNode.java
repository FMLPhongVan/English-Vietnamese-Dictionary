package org.twoguys.engdictionaryapp.TrieNode;

public class TrieNode {
    private int[] child = new int[71];
    private Word word;

    public TrieNode() {
        for (int j = 0; j < 71; ++j) {
            child[j] = 0;
        }
    }

    public int getChild(int k) {
        return child[k];
    }

    public Word getWord() {
        return word;
    }

    public void setChild(int k, int nNode) {
        child[k] = nNode;
    }

    public void setWord(Word newWord) {
        word = new Word(newWord);
    }
}
