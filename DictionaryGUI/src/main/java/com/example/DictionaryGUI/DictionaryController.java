package com.example.DictionaryGUI;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.control.TextField;

public class DictionaryController {

    @FXML
    private TextField searchTextField;

    @FXML
    private ListView searchListView;

    @FXML
    private ListView searchHistory;

    @FXML
    private TextFlow searchedWord;

    @FXML
    protected void onSearchButtonClick() {
        searchListView.getSelectionModel().select(0);
        showSearchedWord();
        searchListView.getSelectionModel().clearSelection();
    }

    @FXML
    protected void onSearching() {
        searchListView.getSelectionModel().clearSelection();
        if (!searchListView.isVisible()) {
            searchListView.setVisible(true);
        }
        if (searchTextField.getLength() == 0) {
            searchListView.setVisible(false);
        }
        System.out.println(searchTextField.getCharacters());
        setSearchListView();
    }

    @FXML
    protected void setSearchListView() {
        ObservableList <CharSequence> template = FXCollections.observableArrayList();
        for(int i = 0; i < 100; i++) {
            template.add(searchTextField.getCharacters());
            searchListView.setItems(template);
        }
        setSearchHistory();
    }

    @FXML
    protected void setSearchHistory() {
        ObservableList <CharSequence> history = FXCollections.observableArrayList();
        for(int i = 0; i < 20; i++) {
            history.add(searchTextField.getCharacters());
            searchHistory.setItems(history);
        }
    }

    @FXML
    public void onEnter(){
        searchListView.getSelectionModel().select(0);
        showSearchedWord();
        searchListView.getSelectionModel().clearSelection();
    }

    @FXML
    protected void showSearchedWord() {
        searchedWord.getChildren().clear();
        Text word = new Text("");
        if (!searchListView.getItems().isEmpty() && !searchListView.getSelectionModel().isEmpty()) {
            word = new Text(String.valueOf(searchListView.getSelectionModel().getSelectedItem()));
        }
        word.setFill(Color.WHITE);
        word.setFont(new Font(24));

        searchedWord.getChildren().add(word);
        searchedWord.getChildren().add(new Text(System.lineSeparator()));
    }
}