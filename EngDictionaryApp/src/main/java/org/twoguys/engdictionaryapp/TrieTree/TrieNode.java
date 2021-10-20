package org.twoguys.engdictionaryapp.TrieTree;

public class TrieNode {
    public final static int CHARACTER_SIZE = 71;
    private TrieNode[] child = new TrieNode[CHARACTER_SIZE];
    private Word word = null;
    private boolean isEndOfWord = false;

    public TrieNode() {
        for (int j = 0; j < CHARACTER_SIZE; ++j) {
            child[j] = null;
        }
    }

    public TrieNode getChild(int k) {
        return child[k];
    }

    public Word getWord() {
        return word;
    }

    public boolean isEndOfWord() {
        return isEndOfWord;
    }

    public void setChild(int k, TrieNode newNode) {
        this.child[k] = newNode;
    }

    public void setWord(Word newWord) {
        word = new Word(newWord);
    }

    public void setEndOfWord(boolean state) {
        isEndOfWord = state;
    }
}
