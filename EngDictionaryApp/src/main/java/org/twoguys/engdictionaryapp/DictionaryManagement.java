package org.twoguys.engdictionaryapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.twoguys.engdictionaryapp.TrieNode.TrieNode;
import org.twoguys.engdictionaryapp.TrieNode.Word;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class DictionaryManagement {

    private int nNode = 0;
    private ArrayList<Word> wordList;
    private ArrayList<TrieNode> trieList;
    private ArrayDeque<Integer> trieNodeQueue;
    private DictionaryDatabase dictionaryDatabase;

    private WebEngine webEngine;
    public final static String ALL_WORDS = "";

    public DictionaryManagement() {
        wordList = new ArrayList<Word>();
        trieList = new ArrayList<TrieNode>();
        trieNodeQueue = new ArrayDeque<Integer>();
        dictionaryDatabase = new DictionaryDatabase();
    }

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
        return l;
    }

    private void addWord(Word newWord) {
        int r = 0;
        for (int i = 0; i < newWord.getWord().length(); ++i) {
            int k = getCharacterCode(newWord.getWord().charAt(i));
            if (trieList.get(r).getChild(k) == 0) {
                ++nNode;
                trieList.get(r).setChild(k, nNode);
                trieList.add(nNode, new TrieNode());
            }
            r = trieList.get(r).getChild(k);
        }
        if (Objects.isNull(trieList.get(r).getWord())) {
            trieList.get(r).setWord(newWord);
        } else {
            System.out.println(newWord.getWord());
        }
    }

    private int searchWordPos(String word) {
        int r = 0;
        for (int i = 0; i < word.length(); ++i) {
            int k = getCharacterCode(word.charAt(i));
            if (trieList.get(r).getChild(k) == 0) {
                return -1;
            }
            r = trieList.get(r).getChild(k);
        }
        return r;
    }

    public void init(WebView webView) {
        trieList.add(new TrieNode());
        webEngine = webView.getEngine();
        dictionaryDatabase.getAllWord(wordList);
        for (int i = 0; i < wordList.size(); ++i) {
            addWord(wordList.get(i));
        }

        webEngine.setUserStyleSheetLocation(DictionaryManagement.class.getResource("webStyle.css").toString());

        wordList.sort(Comparator.comparing(Word::getWord));
    }

    public void loadWordListView(ListView listView) {
        loadWordListView(listView, ALL_WORDS);
    }

    public void loadWordListView(ListView listView, String word) {
        ObservableList<String> tmp =FXCollections.observableArrayList();
        if (word.equals(ALL_WORDS)) {
            for (int i = 0; i < wordList.size(); ++i) {
                tmp.add(wordList.get(i).getWord());
                listView.setItems(tmp);
            }
        } else {
            int rootWord = searchWordPos(word);
            if (rootWord == -1) {
                tmp.add("Không tìm thấy!");
                listView.setItems(tmp);
                return;
            }

            trieNodeQueue.add(rootWord);
            while (!trieNodeQueue.isEmpty()) {
                int u = trieNodeQueue.peek();
                if (!Objects.isNull(trieList.get(u).getWord())) {
                    tmp.add(trieList.get(u).getWord().getWord());
                    listView.setItems(tmp);
                }
                trieNodeQueue.poll();
                for (int k = 0; k < 71; ++k) {
                    if (trieList.get(u).getChild(k) != 0) {
                        trieNodeQueue.add(trieList.get(u).getChild(k));
                    }
                }
            }
        }
    }

    public void setSearchedWordInfo(String word) {
        int r = searchWordPos(word);
        String htmlData = dictionaryDatabase.getWordDescriptionData(trieList.get(r).getWord().getID());
        webEngine.loadContent(htmlData);
    }

    public void playSoundWord(String word) {

    }
}
