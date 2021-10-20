package org.twoguys.engdictionaryapp.TrieTree;

import javafx.collections.ObservableList;

import java.util.ArrayDeque;

public class TrieTree {
    private TrieNode root = null;

    private int getCharacterCode(Character c) {
        int l = (int) c;
        if (l >= (int) '0' && l <= '9') l = l - (int) '0';
        else if (l >= (int) 'A' && l <= 'Z') l = l - (int) 'A' + 10;
        else if (l >= (int) 'a' && l <= 'z') l = l - (int) 'a' + 36;
        else if (l == (int) '.') l = 63;
        else if (l == (int) '-') l = 64;
        else if (l == (int) '(' || l == (int) '[') l = 65;
        else if (l == (int) ')' || l == (int) ']') l = 66;
        else if (l == (int) ' ') l = 67;
        else if (l == (int) '\'') l = 68;
        else l = 67;
        return l;
    }

    public TrieTree() {
        root = new TrieNode();
    }

    public void insert(Word newWord) {
        TrieNode curRoot = root;

        for (int i = 0; i < newWord.getWord().length(); ++i) {
            int k = getCharacterCode(newWord.getWord().charAt(i));
            if (curRoot.getChild(k) == null) {
                curRoot.setChild(k, new TrieNode());
            }
            curRoot = curRoot.getChild(k);
        }

        curRoot.setEndOfWord(true);
        curRoot.setWord(newWord);
    }

    public TrieNode search(String word) {
        TrieNode curRoot = root;

        for (int i = 0; i < word.length(); ++i) {
            int k = getCharacterCode(word.charAt(i));
            if (curRoot.getChild(k) == null) {
                return null;
            }
            curRoot = curRoot.getChild(k);
        }

        return curRoot;
    }

    private boolean isEmptyNode(TrieNode curRoot) {
        for (int i = 0; i < TrieNode.CHARACTER_SIZE; ++i) {
            if (curRoot.getChild(i) != null) return false;
        }
        return true;
    }

    private TrieNode remove(TrieNode curRoot, String word, int depth) {
        if (curRoot == null) return null;

        if (depth == word.length()) {
            if (curRoot.isEndOfWord()) {
                curRoot.setEndOfWord(false);
            }
            if (isEmptyNode(curRoot)) {
                curRoot = null;
            }
            return curRoot;
        }

        int index = getCharacterCode(word.charAt(depth));
        curRoot.setChild(index, remove(curRoot.getChild(index), word, depth + 1));

        if (isEmptyNode(curRoot) && !curRoot.isEndOfWord()) {
            curRoot = null;
        }

        return curRoot;
    }

    public void delete(String word) {
        remove(root, word, 0);
    }

    public void findSuggested(String word, ObservableList<String> listViewData) {
        TrieNode curRoot = search(word);
        ArrayDeque<TrieNode> trieNodeQueue = new ArrayDeque<>();

        trieNodeQueue.add(curRoot);
        while (!trieNodeQueue.isEmpty()) {
            curRoot = trieNodeQueue.peek();
            if (curRoot.isEndOfWord()) {
                listViewData.add(curRoot.getWord().getWord());
            }

            trieNodeQueue.pop();
            for (int i = 0; i < TrieNode.CHARACTER_SIZE; ++i) {
                if (curRoot.getChild(i) != null) {
                    trieNodeQueue.add(curRoot.getChild(i));
                }
            }
        }
    }
}
