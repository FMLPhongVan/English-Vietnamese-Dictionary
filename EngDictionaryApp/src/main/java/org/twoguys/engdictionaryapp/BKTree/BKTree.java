package org.twoguys.engdictionaryapp.BKTree;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.twoguys.engdictionaryapp.TrieTree.Word;

import java.util.ArrayList;

public class BKTree {
    public final int TOLERANCE = 2;
    private BKNode root;

    private void insert(BKNode curRoot, BKNode addNode) {
        if (curRoot == null) {
            curRoot = addNode;
            return;
        }

        int dist = curRoot.levenshteinDistance(addNode.getWord());
        if (curRoot.getNext(dist) == null) {
            curRoot.setNext(dist, addNode);
        } else {
            insert(curRoot.getNext(dist), addNode);
        }
    }

    public void initBKTree(ArrayList<Word> wordList) {
        //bkTree = new BKNode[(int) wordList.size() + 5];
        root = new BKNode(wordList.get(0).getWord());
        for (int i = 1; i < wordList.size(); ++i) {
            insert(root, new BKNode(wordList.get(i).getWord()));
        }
    }

    private ObservableList<String> getSimilarWords(BKNode curRoot, String word) {
        ObservableList<String> res = FXCollections.observableArrayList();
        if (curRoot == null) return res;

        int dist = curRoot.levenshteinDistance(word);
        if (dist <= TOLERANCE) {
            res.add(curRoot.getWord());
        }
        int start = dist - TOLERANCE;
        if (start < 0) start = 1;

        while (start < dist + TOLERANCE) {
            ObservableList<String> tmp = getSimilarWords(curRoot.getNext(start), word);
            res.addAll(tmp);
            ++start;
        }
        return res;
    }

    public void fixSpellError(String word, ObservableList<String> listviewData) {
        listviewData.addAll(getSimilarWords(root, word));
    }
}