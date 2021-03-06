package org.twoguys.engdictionaryapp;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.kordamp.ikonli.javafx.FontIcon;
import org.twoguys.engdictionaryapp.AlertInfo.AlertInfo;
import org.twoguys.engdictionaryapp.BKTree.BKTree;
import org.twoguys.engdictionaryapp.GoogleAPI.GoogleAPI;
import org.twoguys.engdictionaryapp.RelatedWords.RelatedWordsAPI;
import org.twoguys.engdictionaryapp.TableViewHelper.HistoryTable;
import org.twoguys.engdictionaryapp.TableViewHelper.TableViewHelper;
import org.twoguys.engdictionaryapp.TableViewHelper.WordDataTable;
import org.twoguys.engdictionaryapp.TrieTree.TrieNode;
import org.twoguys.engdictionaryapp.TrieTree.TrieTree;
import org.twoguys.engdictionaryapp.TrieTree.Word;
import org.twoguys.engdictionaryapp.VoiceHandle.VoiceHandler;

import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

public class DictionaryManagement {

    private int nNode = 0, nWords = 0;
    private ArrayList<Word> wordList = new ArrayList<>();
    private ObservableList<String> listViewData = FXCollections.observableArrayList();
    private DictionaryDatabaseHandler databaseHandler = new DictionaryDatabaseHandler();
    private AlertInfo alertInfo = new AlertInfo();
    private VoiceHandler voiceHandler = new VoiceHandler();
    private TableViewHelper tableViewHelper = new TableViewHelper();
    private RelatedWordsAPI relatedWordsAPI = new RelatedWordsAPI();
    private TrieTree trieTree = new TrieTree();
    private BKTree bkTree = new BKTree();
    private String foundWord;

    private WebEngine webEngine;
    public final static String ALL_WORDS = "";

    public DictionaryManagement() {}

    public void init(WebView webView) {
        webEngine = webView.getEngine();
        databaseHandler.getAllWord(wordList);
        wordList.sort(Comparator.comparing(Word::getWord));
        for (int i = 0; i < wordList.size(); ++i) {
            trieTree.insert(wordList.get(i));
        }
        bkTree.initBKTree(wordList);
        webEngine.setUserStyleSheetLocation(DictionaryManagement.class.getResource("webStyle.css").toString());

    }

    public void loadWordListView(ListView<String> listView) {
        loadWordListView(listView, ALL_WORDS, null);
    }

    public void loadWordListView(ListView<String> listView, String word, Text resultInfo) {
        listViewData.clear();
        if (word.equals(ALL_WORDS)) {
            for (int i = 0; i < wordList.size(); ++i) {
                listViewData.add(wordList.get(i).getWord());
            }
        } else {
            TrieNode result = trieTree.search(word);
            if (result == null) {
                bkTree.fixSpellError(word, listViewData);
                listViewData.sort(Comparator.comparing(String::toString));
                if (resultInfo != null) {
                    if (listViewData.size() != 0)
                        resultInfo.setText("C?? ph???i ?? b???n l??:");
                    else
                        resultInfo.setText("Kh??ng t??m th???y t??? ph?? h???p.");
                }
            } else {
                trieTree.findSuggested(word, listViewData);
                if (resultInfo != null) {
                    if (listViewData.size() != 0) {
                        resultInfo.setText("T??m th???y " + listViewData.size() + " t??? ph?? h???p:");
                    }
                }
            }

        }
        listView.setItems(listViewData);
    }

    public boolean setSearchedWordInfo(String word, Text foundWord, Text pronounce, FontIcon favButton) {
        TrieNode wordNode = trieTree.search(word);
        if (wordNode == null) {
            return false;
        }
        String htmlData = databaseHandler.getWordDescriptionData(wordNode.getWord().getID());
        webEngine.loadContent(htmlData);
        if (foundWord != null) foundWord.setText(word);
        this.foundWord = word;

        if (pronounce != null) {
            if (wordNode.getWord().getPronounce().equals("")) pronounce.setText("");
            else pronounce.setText("/" + wordNode.getWord().getPronounce() + "/");
        }
        if (favButton != null) {
            if (wordNode.getWord().getFavourite() == 1) {
                favButton.setIconColor(Color.valueOf("#ff0000"));
            } else {
                favButton.setIconColor(Color.valueOf("#ffffff"));
            }
        }
        return true;
    }

    public void getRelatedWords(String word, ListView<String> antonyms, ListView<String> synonyms) {
        try {
            ArrayList<String> synonymsList = relatedWordsAPI.searchRelated(RelatedWordsAPI.SYNONYMS, word);
            ArrayList<String> antonymsList = relatedWordsAPI.searchRelated(RelatedWordsAPI.ANTONYMS, word);

            for (String t : synonymsList) {
                System.out.println(t);
            }

            ObservableList<String> synonymsListData = FXCollections.observableArrayList();
            ObservableList<String> antonymsListData = FXCollections.observableArrayList();
            synonymsListData.addAll(synonymsList);
            synonyms.setItems(synonymsListData);

            antonymsListData.addAll(antonymsList);
            antonyms.setItems(antonymsListData);

            synonymsList.clear();
            antonymsList.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playSoundWord(String word, String language) {
        try {
            voiceHandler.playSound(word, language);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeFavouriteButton(FontIcon favButton) {
        if (foundWord == null || foundWord.equals("")) return;
        TrieNode wordNode = trieTree.search(this.foundWord);
        if (wordNode.getWord().getFavourite() == 1) {
            favButton.setIconColor(Color.valueOf("#ffffff"));
            wordNode.getWord().setFavourite(0);
            databaseHandler.updateFavouriteStatus(wordNode.getWord().getID(), wordNode.getWord().getFavourite());
        } else {
            favButton.setIconColor(Color.valueOf("#ff0000"));
            wordNode.getWord().setFavourite(1);
            databaseHandler.updateFavouriteStatus(wordNode.getWord().getID(), wordNode.getWord().getFavourite());
        }
    }

    public void initHistoryTable(TableView<HistoryTable> historyTable) {
        tableViewHelper.initHistoryTable(historyTable);
    }

    public void initWordDataTable(TableView<WordDataTable> wordDataTable) {
        tableViewHelper.initWordTable(wordDataTable);
    }

    public void updateHistory(String word, TableView<HistoryTable> historyTable, final int historyType) {
        Date curDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        databaseHandler.updateHistoryTable(historyType, word, formatter.format(curDate));
    }

    public void loadHistoryData(TableView<HistoryTable> historyTable, final int historyType) {
        ObservableList<HistoryTable> data = FXCollections.observableArrayList();
        databaseHandler.getHistoryDataOf(historyType, data);
        historyTable.setItems(data);
    }

    public void loadFavouriteList(ListView<String> favList) {
        favList.getItems().clear();
        databaseHandler.getFavouriteData(favList);
    }

    public void changeFavouriteStatus(String word, String curFoundWord, FontIcon favIcon) {
        TrieNode wordNode = trieTree.search(word);
        wordNode.getWord().setFavourite(0);
        if (curFoundWord.equals(word)) {
            favIcon.setIconColor(Color.valueOf("#FFFFFF"));
        }
        databaseHandler.updateFavouriteStatus(wordNode.getWord().getID(), 0);
    }

    public void loadTableView(String word, TableView<WordDataTable> tableView) {
        int wordID =  trieTree.search(word).getWord().getID();
        ObservableList<WordDataTable> datalist = FXCollections.observableArrayList();
        databaseHandler.getDataOf(wordID, datalist);
        tableView.setItems(datalist);
    }

    private boolean handleUpdate(String type, String description, String example) {
        if (type.length() + description.length() + example.length() == 0) {
            alertInfo.showInformation("B???n kh??ng ???????c ????? tr???ng c??? 3 ??!");
            return false;
        }

        if (type.length() == 0) {
            alertInfo.showInformation("B???n kh??ng ???????c ????? tr???ng lo???i t???");
            return false;
        }

        if (description.length() == 0) {
            alertInfo.showInformation("B???n kh??ng ???????c ????? tr???ng ?????nh ngh??a");
            return false;
        }

        if (example.length() != 0) {
            String[] exs = example.split("\n");
            System.out.println(type + description + example);
            if (exs.length % 2 == 1) {
                alertInfo.showInformation("V?? d??? c???a b???n kh??ng h???p l??? do ch??a ????? s??? d??ng c???n thi???t. Ho???c kh??ng c?? ho???c c?? c??c v?? d??? ??i k??m 1 d??ng d???ch ??? d?????i !!!");
                return false;
            }
            String tmp = "";
            for (int i = 0; i < exs.length; i += 2) {
                tmp += exs[0] + "+" + exs[1] + "\n";
            }
            example = tmp;
        }
        return true;
    }

    public boolean updateWordDataInTable(TableView<WordDataTable> tableView, String type, String description, String example) {
        if (!handleUpdate(type, description, example)) {
            return false;
        } else {
            WordDataTable e = tableView.getItems().get(tableView.getSelectionModel().getSelectedIndex());
            e.setWordType(type);
            e.setWordDescription(description);
            e.setWordExample(example);
            tableView.getItems().set(tableView.getSelectionModel().getSelectedIndex(), e);
        }
        return true;
    }

    public boolean insertWordDataInTable(TableView<WordDataTable> tableView, String type, String description, String example) {
        if (!handleUpdate(type, description, example)) {
            return false;
        } else {
            tableView.getItems().add(new WordDataTable(type, description, example));
        }
        return true;
    }

    public void deleteTableRow(TableView<WordDataTable> tableView) {
        tableView.getItems().remove(tableView.getSelectionModel().getSelectedItem());
    }

    public void deleteWord(String word) {
        boolean agree = alertInfo.showConfirmation("B???n c?? ch???c ch???n mu???n x??a \"" + word + "\" ?");
        if (agree) {
            int wordID = trieTree.search(word).getWord().getID();
            trieTree.delete(word);
            databaseHandler.deleteWordInDatabase(wordID);
            alertInfo.showInformation("B???n ???? x??a th??nh c??ng !");
        }
    }

    public void updateWordData(String word, TableView<WordDataTable> tableView) {
        int wordID = trieTree.search(word).getWord().getID();
        databaseHandler.updateWordDataToDatabase(wordID, tableView.getItems());
    }

    public void addNewWordData(String newWordNeedEdit, TableView<WordDataTable> tableView) {
        Word newWord = new Word(wordList.size() + 1, newWordNeedEdit, "", 0);
        trieTree.insert(newWord);
        wordList.add(newWord);
        databaseHandler.addNewWordtoDatabase(newWord, tableView.getItems());
    }

    public boolean checkWordExist(String word) {
        if (trieTree.search(word) != null) {
            alertInfo.showInformation("T??? n??y ???? t???n t???i trong t??? ??i???n, vui l??ng chuy???n qua S???a t???.");
            return true;
        }
        return false;
    }

    public void convertToVie(TextArea englishTextArea, TextArea vietnameseTextArea) {
        try {
            vietnameseTextArea.setText(GoogleAPI.translate(GoogleAPI.ENGLISH, GoogleAPI.VIETNAMESE, englishTextArea.getText()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void convertToEng(TextArea vietnameseTextArea, TextArea englishTextArea) {
        try {
            englishTextArea.setText(GoogleAPI.translate(GoogleAPI.VIETNAMESE, GoogleAPI.ENGLISH, vietnameseTextArea.getText()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
