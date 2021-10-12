package org.twoguys.engdictionaryapp;

import javafx.concurrent.WorkerStateEvent;
import org.twoguys.engdictionaryapp.TrieNode.TrieNode;
import org.twoguys.engdictionaryapp.TrieNode.Word;

import java.util.ArrayList;
import java.util.Objects;

public class DictionaryManagement {

    private int nNode = 0;
    private ArrayList<Word> wordList = new ArrayList<Word>();
    private ArrayList<TrieNode> trieList = new ArrayList<TrieNode>();
    private DictionaryDatabase dictionaryDatabase = new DictionaryDatabase();

    private void addWord(Word newWord) {
        int r = 0;
        for (int i = 0; i < newWord.getWord().length(); ++i) {
            int k = (int) newWord.getWord().charAt(i);
            int l = k;
            if (l >= (int) '0' && l <= '9') k = k - (int) '0';
            else if (l >= (int) 'A' && l <= 'Z') k = k - (int) 'A' + 10;
            else if (l >= (int) 'a' && l <= 'z') k = k - (int) 'a' + 36;
            else if (l == (int) '.') k = 63;
            else if (l == (int) '\'') k = 64;
            else if (l == (int) '-') k = 65;
            else if (l == (int) '(' || l == (int) '[') k = 66;
            else if (l == (int) ')' || l == (int) ']') k = 67;
            else if (l == (int) ' ') k = 69;
            else if (l == (int) '/') k = 70;
            else if (l == 246) k = 68;
            else break;

            if (trieList.get(r).getChild(k) == 0) {
                ++nNode;
                trieList.get(r).setChild(k, nNode);
                trieList.add(nNode, new TrieNode());
            }
            r = trieList.get(r).getChild(k);
        }
        if (Objects.isNull(trieList.get(r).getWord())) {
            trieList.get(r).setWord(newWord);
        }
    }

    public DictionaryManagement() {}

    public void init() {
        trieList.add(new TrieNode());
        dictionaryDatabase.selectAll(wordList);
        for (int i = 0; i < wordList.size(); ++i) {
            addWord(wordList.get(i));
        }
    }
}
