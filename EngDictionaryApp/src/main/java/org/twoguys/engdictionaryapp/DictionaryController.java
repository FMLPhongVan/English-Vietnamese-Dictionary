package org.twoguys.engdictionaryapp;

import com.jfoenix.controls.JFXButton;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.twoguys.engdictionaryapp.AlertInfo.AlertInfo;
import org.twoguys.engdictionaryapp.TableViewHelper.HistoryTable;
import org.twoguys.engdictionaryapp.TableViewHelper.WordDataTable;
import org.twoguys.engdictionaryapp.VoiceHandle.VoiceHandler;

import java.beans.EventHandler;
import java.net.URL;
import java.util.ResourceBundle;

public class DictionaryController implements Initializable {
    @FXML private AnchorPane searchPane;
    @FXML private AnchorPane menuPane;
    @FXML private AnchorPane editPane;
    @FXML private AnchorPane editListView;
    @FXML private AnchorPane editWordPane;
    @FXML private AnchorPane historyPane;
    @FXML private AnchorPane ggTranslatePane;
    @FXML private FontIcon menuIcon;
    @FXML private FontIcon searchIcon;
    @FXML private FontIcon historyIcon;
    @FXML private FontIcon historyIcon1;
    @FXML private FontIcon editIcon;
    @FXML private FontIcon favouriteIcon;
    @FXML private JFXButton menuExtend;
    @FXML private JFXButton searchMiniButton;
    @FXML private JFXButton editButton;
    @FXML private JFXButton menuClose;
    @FXML private JFXButton searchLargeButton;
    @FXML private JFXButton bigEditButton;
    @FXML private JFXButton inpaneAddButton;
    @FXML private JFXButton inpaneEditButton;
    @FXML private JFXButton inpaneDeleteButton;
    @FXML private JFXButton inpaneConfirmButton;
    @FXML private JFXButton inpaneCancelButton;
    @FXML private JFXButton inpaneSaveButton;
    @FXML private JFXButton usSoundButton;
    @FXML private JFXButton goToDefinitionButton;
    @FXML private JFXButton favouriteButton;
    @FXML private JFXButton ukSoundButton;
    @FXML private JFXButton addNewRow;
    @FXML private JFXButton deleteSelectRowButton;
    @FXML private JFXButton unfavouredButton;
    @FXML private JFXButton googleAPIButton;
    @FXML private JFXButton bigGoogleAPIButton;
    @FXML private JFXButton convertToVieButton;
    @FXML private JFXButton convertToEngButton;
    @FXML private JFXButton deleteBothTextArea;
    @FXML private JFXButton bigHistoryButton;
    @FXML private JFXButton historyButton;
    @FXML private JFXButton editSelectRowButton;
    @FXML private ListView<String> suggestedWordList;
    @FXML private ListView<String> allWordList;
    @FXML private ListView<String> eraseAndEditWordList;
    @FXML private ListView<String> antonymsWordList;
    @FXML private ListView<String> synonymsWordList;
    @FXML private ListView<String> favouriteList;
    @FXML private TableView<WordDataTable> modifiedWordDataTable;
    @FXML private TableView<HistoryTable> searchHistoryTable;
    @FXML private TableView<HistoryTable> addHistoryTable;
    @FXML private TableView<HistoryTable> editHistoryTable;
    @FXML private TableView<HistoryTable> deleteHistoryTable;
    @FXML private TabPane wordInfoTabPane;
    @FXML private TabPane historyTabPane;
    @FXML private Text foundWord;
    @FXML private Text wordPronounce;
    @FXML private Text searchResultInfo;
    @FXML private TextField searchTextField;
    @FXML private TextField editSearchPane;
    @FXML private TextField wordTypeField;
    @FXML private TextArea wordDescriptionField;
    @FXML private TextArea wordExampleField;
    @FXML private TextArea englishTextArea;
    @FXML private TextArea vietnameseTextArea;
    @FXML private VBox miniMenuPane;
    @FXML private WebView searchedWordInfo;

    public final int NOT_IN_ANY_MODE_EDIT_PANE = -1;
    public final int IN_ADD_MODE_EDIT_PANE = 0;
    public final int IN_EDIT_MODE_EDIT_PANE = 1;
    public final int IN_DELETE_MODE_EDIT_PANE = 2;

    private int currentEditPaneMode = NOT_IN_ANY_MODE_EDIT_PANE;
    private boolean relatedLoaded = false;
    private String newWordNeedEdit = "";

    DictionaryManagement dictionaryManagement;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inpaneAddButton.setDisable(false);
        inpaneEditButton.setDisable(false);
        inpaneDeleteButton.setDisable(false);
        inpaneConfirmButton.setDisable(true);
        inpaneCancelButton.setDisable(true);
        inpaneSaveButton.setDisable(true);
        inpaneSaveButton.setVisible(false);
        eraseAndEditWordList.setDisable(true);
        eraseAndEditWordList.setVisible(false);
        editSearchPane.setDisable(true);
        editSearchPane.setText("");
        editSearchPane.setPromptText("");
        editWordPane.setDisable(true);
        editWordPane.setVisible(false);
        editWordPane.toBack();
        editPane.toBack();
        currentEditPaneMode = NOT_IN_ANY_MODE_EDIT_PANE;

        wordInfoTabPane.getSelectionModel().select(1);
        menuPane.toBack();
        menuPane.setTranslateX(-600);
        searchPane.toFront();

        reloadWordInfoPane(true);
        relatedLoaded = false;

        dictionaryManagement = new DictionaryManagement();
        dictionaryManagement.init(searchedWordInfo);
        dictionaryManagement.initHistoryTable(searchHistoryTable);
        dictionaryManagement.initHistoryTable(addHistoryTable);
        dictionaryManagement.initHistoryTable(editHistoryTable);
        dictionaryManagement.initHistoryTable(deleteHistoryTable);
        dictionaryManagement.initWordDataTable(modifiedWordDataTable);
        dictionaryManagement.loadWordListView(allWordList);
        allWordList.setVisible(true);
        allWordList.toFront();

    }

    private void changeMenuPanePos(double duration, double fadeFromValue, double fadeToValue, int posChanges) {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(duration), menuPane);
        fadeTransition.setFromValue(fadeFromValue);
        fadeTransition.setToValue(fadeToValue);
        fadeTransition.play();

        TranslateTransition movePane = new TranslateTransition(Duration.seconds(duration), menuPane);
        movePane.setByX(posChanges);
        movePane.play();
        menuPane.setTranslateX(menuPane.getTranslateX() + posChanges);
    }

    public void onMenuExtendClicked(MouseEvent event) {
        System.out.println(menuPane.getTranslateX());
        if (menuPane.getTranslateX() != -600) return;
        System.out.println("Extend");
        miniMenuPane.setVisible(false);
        changeMenuPanePos(0.5, 0.5, 1, +600);
        menuPane.toFront();
    }

    public void onMenuCloseClicked(MouseEvent event) {
        if (menuPane.getTranslateX() != 0) return;
        System.out.println("Close");
        changeMenuPanePos(0.5, 1, 0.5, -600);
        menuPane.toBack();
        miniMenuPane.setVisible(true);
    }

    /// Handle search pane. --------------------------------------------------------------------------------------------

    public void onMiniSearchButtonClicked(MouseEvent e) {
        searchPane.toFront();
    }

    public void onLargeSearchButtonClicked(MouseEvent e) {
        if (menuPane.getTranslateX() != 0) return;
        changeMenuPanePos(0.5, 1, 0.5, -600);
        menuPane.toBack();
        miniMenuPane.setVisible(true);
        searchPane.toFront();
    }

    public void onEnterSearchField(KeyEvent e) {
        String typeWord = searchTextField.getCharacters().toString();
        if (typeWord == null || typeWord.equals("")) {
            allWordList.toFront();
            searchResultInfo.setText("");
        }
        dictionaryManagement.loadWordListView(suggestedWordList, searchTextField.getCharacters().toString(), searchResultInfo);
        allWordList.toBack();
        suggestedWordList.toFront();
    }

    private void reloadWordInfoPane(boolean state) {
        wordInfoTabPane.getSelectionModel().select(1);
        ukSoundButton.setDisable(state);
        usSoundButton.setDisable(state);
        favouriteButton.setDisable(state);
        antonymsWordList.getItems().clear();
        synonymsWordList.getItems().clear();
        goToDefinitionButton.setDisable(true);
        relatedLoaded = state;
    }

    private void setUpFoundResult(String word) {
        dictionaryManagement.setSearchedWordInfo(word, foundWord, wordPronounce, favouriteIcon);
        dictionaryManagement.updateHistory(foundWord.getText(), searchHistoryTable, HistoryTable.SEARCH);
        searchPane.toFront();
        searchedWordInfo.toFront();
        reloadWordInfoPane(false);
    }

    public void onAllWordListClicked(MouseEvent e) {
        if (allWordList.getSelectionModel().getSelectedItems().isEmpty()) return;
        String selectedWord = allWordList.getSelectionModel().getSelectedItems().get(0);
        setUpFoundResult(selectedWord);
    }

    public void onSuggestedWordListClicked(MouseEvent e) {
        System.out.println(1);
        if (suggestedWordList.getSelectionModel().getSelectedItems().isEmpty()) return;
        String selectedWord = suggestedWordList.getSelectionModel().getSelectedItems().get(0);
        setUpFoundResult(selectedWord);
    }

    public void onRelatedTabClicked() {
        if (wordInfoTabPane.getSelectionModel().getSelectedIndex() == 0) {
            if (!relatedLoaded && !foundWord.getText().equals("")) {
                dictionaryManagement.getRelatedWords(foundWord.getText(), antonymsWordList, synonymsWordList);
                relatedLoaded = true;
            }
        }
    }

    public void onUSSoundButtonClicked() {
        dictionaryManagement.playSoundWord(foundWord.getText(), VoiceHandler.US);
    }

    public void onUKSoundButtonClicked() {
        dictionaryManagement.playSoundWord(foundWord.getText(), VoiceHandler.UK);
    }

    public void onFavouriteButtonClicked() {
        dictionaryManagement.changeFavouriteButton(favouriteIcon);
    }

    public void onSynonymsListClicked() {
        antonymsWordList.getSelectionModel().clearSelection();
        goToDefinitionButton.setText("Đi đến định nghĩa \""
                + synonymsWordList.getSelectionModel().getSelectedItems().get(0) + "\"");
        if (dictionaryManagement.setSearchedWordInfo(synonymsWordList.getSelectionModel().getSelectedItems().get(0),
                null, null, null)) {
            goToDefinitionButton.setDisable(false);
        }
    }

    public void onAntonymsListClicked() {
        synonymsWordList.getSelectionModel().clearSelection();
        goToDefinitionButton.setText("Đi đến định nghĩa \""
                + antonymsWordList.getSelectionModel().getSelectedItems().get(0) + "\"");
        if (dictionaryManagement.setSearchedWordInfo(antonymsWordList.getSelectionModel().getSelectedItems().get(0),
                null, null, null)) {
            goToDefinitionButton.setDisable(false);
        }
    }

    public void onGoToDefinitionClicked() {
        String selectedWord;
        if (synonymsWordList.getSelectionModel().getSelectedItems().isEmpty()) {
            selectedWord = antonymsWordList.getSelectionModel().getSelectedItems().get(0);
        } else {
            selectedWord = synonymsWordList.getSelectionModel().getSelectedItems().get(0);
        }
        dictionaryManagement.setSearchedWordInfo(selectedWord, foundWord, wordPronounce, favouriteIcon);
        searchedWordInfo.toFront();
        reloadWordInfoPane(false);
        dictionaryManagement.updateHistory(foundWord.getText(), searchHistoryTable, HistoryTable.SEARCH);
    }

    /// Handle history pane. -------------------------------------------------------------------------------------------

    private void loadHistory() {
        dictionaryManagement.loadFavouriteList(favouriteList);
        dictionaryManagement.loadHistoryData(addHistoryTable, HistoryTable.ADD);
        dictionaryManagement.loadHistoryData(editHistoryTable, HistoryTable.EDIT);
        dictionaryManagement.loadHistoryData(deleteHistoryTable, HistoryTable.DELETE);
        dictionaryManagement.loadHistoryData(searchHistoryTable, HistoryTable.SEARCH);
        unfavouredButton.setDisable(true);
    }

    public void onHistoryButtonClicked() {
        historyPane.toFront();
        loadHistory();
    }

    public void onBigHistoryButtonClicked() {
        if (menuPane.getTranslateX() != 0) return;
        changeMenuPanePos(0.5, 1, 0.5, -600);
        menuPane.toBack();
        miniMenuPane.setVisible(true);
        historyPane.toFront();
        loadHistory();
    }

    public void onFavouriteListClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            if (favouriteList.getSelectionModel().getSelectedItems().isEmpty()) return;
            String selectedWord = favouriteList.getSelectionModel().getSelectedItems().get(0);
            setUpFoundResult(selectedWord);
        } else {
            unfavouredButton.setDisable(false);
        }
    }

    private void onHistoryTableClicked(TableView<HistoryTable> table, MouseEvent e) {
        if (e.getClickCount() == 2) {
            if (table.getSelectionModel().getSelectedItems().isEmpty()) return;
            String selectedWord = table.getSelectionModel().getSelectedItems().get(0).getWord();
            setUpFoundResult(selectedWord);
        }
    }

    public void onSearchTableClicked(MouseEvent e) {
        onHistoryTableClicked(searchHistoryTable, e);
    }

    public void onAddTableClicked(MouseEvent e) {
        onHistoryTableClicked(addHistoryTable, e);
    }

    public void onEditTableClicked(MouseEvent e) {
        onHistoryTableClicked(editHistoryTable, e);
    }

    public void onUnfavouredButtonClicked() {
        String word = favouriteList.getSelectionModel().getSelectedItems().get(0);
        favouriteList.getItems().remove(favouriteList.getSelectionModel().getSelectedIndex());
        dictionaryManagement.changeFavouriteStatus(word, foundWord.getText(), favouriteIcon);
        unfavouredButton.setDisable(true);
    }

    /// Handle edit pane. ----------------------------------------------------------------------------------------------

    public void onEditButtonClicked(MouseEvent e) {
        editPane.toFront();
    }

    public void onBigEditButtonClicked(MouseEvent e) {
        if (menuPane.getTranslateX() != 0) return;
        changeMenuPanePos(0.5, 1, 0.5, -600);
        menuPane.toBack();
        miniMenuPane.setVisible(true);
        editPane.toFront();
    }

    public void onInPaneCancelButtonClicked() {
        inpaneAddButton.setDisable(false);
        inpaneEditButton.setDisable(false);
        inpaneDeleteButton.setDisable(false);
        inpaneConfirmButton.setDisable(true);
        inpaneCancelButton.setDisable(true);
        inpaneSaveButton.setDisable(true);
        inpaneSaveButton.setVisible(false);
        eraseAndEditWordList.setDisable(true);
        eraseAndEditWordList.setVisible(false);
        editSearchPane.setDisable(true);
        editSearchPane.setText("");
        editSearchPane.setPromptText("");
        editSearchPane.setEditable(true);
        editWordPane.setDisable(true);
        editWordPane.setVisible(false);
        editWordPane.toBack();
        editPane.toFront();
        wordTypeField.setText("");
        wordExampleField.setText("");
        wordDescriptionField.setText("");
        modifiedWordDataTable.getItems().clear();
        currentEditPaneMode = NOT_IN_ANY_MODE_EDIT_PANE;
    }

    public void onInPaneAddWordClicked() {
        if (currentEditPaneMode != IN_ADD_MODE_EDIT_PANE) {
            currentEditPaneMode = IN_ADD_MODE_EDIT_PANE;
            inpaneEditButton.setDisable(true);
            inpaneDeleteButton.setDisable(true);
            inpaneConfirmButton.setDisable(true);
            inpaneCancelButton.setDisable(false);
            inpaneSaveButton.setDisable(true);
            inpaneSaveButton.setVisible(false);
            editSearchPane.setDisable(false);
            editSearchPane.setEditable(true);
            editSearchPane.setPromptText("Nhập từ muốn thêm...");
        }
    }

    public void onInPaneEditWordClicked() {
        if (currentEditPaneMode != IN_EDIT_MODE_EDIT_PANE) {
            currentEditPaneMode = IN_EDIT_MODE_EDIT_PANE;
            inpaneAddButton.setDisable(true);
            inpaneDeleteButton.setDisable(true);
            inpaneCancelButton.setDisable(false);
            inpaneSaveButton.setVisible(true);
            eraseAndEditWordList.setDisable(false);
            eraseAndEditWordList.setVisible(true);
            dictionaryManagement.loadWordListView(eraseAndEditWordList);
            editSearchPane.setDisable(false);
            editSearchPane.setEditable(true);
            editSearchPane.setPromptText("Nhập và chọn từ muốn sửa...");
        }
    }

    public void onInPanelDeleteWordClicked() {
        if (currentEditPaneMode != IN_DELETE_MODE_EDIT_PANE) {
            currentEditPaneMode = IN_DELETE_MODE_EDIT_PANE;
            inpaneAddButton.setDisable(true);
            inpaneEditButton.setDisable(true);
            inpaneConfirmButton.setDisable(true);
            inpaneCancelButton.setDisable(false);
            eraseAndEditWordList.setDisable(false);
            eraseAndEditWordList.setVisible(true);
            dictionaryManagement.loadWordListView(eraseAndEditWordList);
            editSearchPane.setDisable(false);
            editSearchPane.setEditable(true);
            editSearchPane.setPromptText("Nhập và chọn từ muốn xóa...");
        }
    }

    public void onEnterEditSearchPane() {
        String word = "";
        word = editSearchPane.getCharacters().toString();
        switch (currentEditPaneMode) {
            case IN_ADD_MODE_EDIT_PANE:
                inpaneConfirmButton.setDisable(word.length() == 0);
                newWordNeedEdit = word.trim();
                break;
            case IN_EDIT_MODE_EDIT_PANE:
            case IN_DELETE_MODE_EDIT_PANE:
                if (word.length() != 0) {
                    eraseAndEditWordList.setVisible(true);
                    dictionaryManagement.loadWordListView(eraseAndEditWordList, word, null);
                    eraseAndEditWordList.toFront();
                } else {
                    eraseAndEditWordList.getItems().clear();
                    dictionaryManagement.loadWordListView(eraseAndEditWordList);
                    //eraseAndEditWordList.setVisible(false);
                    eraseAndEditWordList.toBack();
                }
                break;
        }
    }

    public void onEditWordListClicked() {
        if (eraseAndEditWordList.getSelectionModel().getSelectedItems().isEmpty()) return;
        String selectedWord = eraseAndEditWordList.getSelectionModel().getSelectedItems().get(0);
        if (selectedWord.equals("Không tìm thấy!")) return;
        editSearchPane.setText(selectedWord);
        inpaneConfirmButton.setDisable(false);
    }

    public void onInPaneConfirmButtonClicked() {
        switch (currentEditPaneMode) {
            case IN_ADD_MODE_EDIT_PANE:
                if (!dictionaryManagement.checkWordExist(newWordNeedEdit)) {
                    inpaneConfirmButton.setDisable(true);
                    inpaneSaveButton.setVisible(true);
                    eraseAndEditWordList.setDisable(true);
                    eraseAndEditWordList.setVisible(false);
                    eraseAndEditWordList.toBack();
                    editSearchPane.setEditable(false);
                    editWordPane.setDisable(false);
                    editWordPane.setVisible(true);
                }
                break;
            case IN_EDIT_MODE_EDIT_PANE:
                inpaneConfirmButton.setDisable(true);
                inpaneSaveButton.setVisible(true);
                eraseAndEditWordList.setDisable(true);
                eraseAndEditWordList.setVisible(false);
                eraseAndEditWordList.toBack();
                editSearchPane.setEditable(false);
                editWordPane.setDisable(false);
                editWordPane.setVisible(true);
                newWordNeedEdit = eraseAndEditWordList.getSelectionModel().getSelectedItems().get(0);
                dictionaryManagement.loadTableView(newWordNeedEdit, modifiedWordDataTable);
                break;
            case IN_DELETE_MODE_EDIT_PANE:
                newWordNeedEdit = eraseAndEditWordList.getSelectionModel().getSelectedItems().get(0);
                dictionaryManagement.deleteWord(newWordNeedEdit);
                dictionaryManagement.updateHistory(newWordNeedEdit, deleteHistoryTable, HistoryTable.DELETE);
                editSearchPane.setText("");
                eraseAndEditWordList.getItems().clear();
                eraseAndEditWordList.setVisible(false);
                inpaneConfirmButton.setDisable(true);
                break;
        }
    }

    public void onEditSelectRowClicked() {
        if (!modifiedWordDataTable.getSelectionModel().getSelectedItems().isEmpty()) {
            WordDataTable data = modifiedWordDataTable.getSelectionModel().getSelectedItems().get(0);
            wordTypeField.setText(data.getWordType());
            wordDescriptionField.setText(data.getWordDescription());
            wordExampleField.setText(data.getWordExample());
            addNewRow.setText("Lưu thay đổi");
        }
    }

    public void onDeleteSelectRowClicked() {
        if (modifiedWordDataTable.getSelectionModel().getSelectedItems().isEmpty()) return;
        dictionaryManagement.deleteTableRow(modifiedWordDataTable);
        if (!modifiedWordDataTable.getItems().isEmpty()) {
            inpaneSaveButton.setDisable(false);
        }
    }

    public void onAddOrEditButtonClicked() {
        if (addNewRow.getText().equals("Lưu thay đổi")) {
            if (dictionaryManagement.updateWordDataInTable(
                    modifiedWordDataTable, wordTypeField.getText(), wordDescriptionField.getText(), wordExampleField.getText())) {
                inpaneSaveButton.setDisable(false);
                wordTypeField.setText("");
                wordExampleField.setText("");
                wordDescriptionField.setText("");
                addNewRow.setText("Thêm nghĩa mới");
            }
        } else {
            if (dictionaryManagement.insertWordDataInTable(
                    modifiedWordDataTable, wordTypeField.getText(), wordDescriptionField.getText(), wordExampleField.getText())) {
                inpaneSaveButton.setDisable(false);
                wordTypeField.setText("");
                wordExampleField.setText("");
                wordDescriptionField.setText("");
            }
        }
    }

    public void onSaveButtonClicked() {
        newWordNeedEdit = editSearchPane.getText();
        if (currentEditPaneMode == IN_EDIT_MODE_EDIT_PANE) {
            dictionaryManagement.updateWordData(newWordNeedEdit, modifiedWordDataTable);
            dictionaryManagement.updateHistory(newWordNeedEdit, editHistoryTable, HistoryTable.EDIT);
        } else {
            dictionaryManagement.addNewWordData(newWordNeedEdit, modifiedWordDataTable);
            dictionaryManagement.updateHistory(newWordNeedEdit, addHistoryTable, HistoryTable.ADD);
        }
        inpaneAddButton.setDisable(false);
        inpaneEditButton.setDisable(false);
        inpaneDeleteButton.setDisable(false);
        inpaneConfirmButton.setDisable(true);
        inpaneCancelButton.setDisable(true);
        inpaneSaveButton.setDisable(true);
        inpaneSaveButton.setVisible(false);
        eraseAndEditWordList.setDisable(true);
        eraseAndEditWordList.setVisible(false);
        editSearchPane.setDisable(true);
        editSearchPane.setEditable(true);
        editSearchPane.setText("");
        editSearchPane.setPromptText("");
        editSearchPane.setEditable(true);
        editWordPane.setDisable(true);
        editWordPane.setVisible(false);
        editWordPane.toBack();
        editPane.toFront();
        wordTypeField.setText("");
        wordExampleField.setText("");
        wordDescriptionField.setText("");
        modifiedWordDataTable.getItems().clear();
        currentEditPaneMode = NOT_IN_ANY_MODE_EDIT_PANE;
    }

    /// Handle translate pane. -----------------------------------------------------------------------------------------

    public void onDeleteBothAreaClicked() {
        englishTextArea.setText("");
        vietnameseTextArea.setText("");
    }

    public void onConvertToVieClicked() {
        dictionaryManagement.convertToVie(englishTextArea, vietnameseTextArea);
    }

    public void onConvertToEngClicked() {
        dictionaryManagement.convertToEng(vietnameseTextArea, englishTextArea);
    }

    public void onGoogleAPIButtonClicked() {
        ggTranslatePane.toFront();
    }

    public void onBigGoogleAPIButtonClicked() {
        if (menuPane.getTranslateX() != 0) return;
        changeMenuPanePos(0.5, 1, 0.5, -600);
        menuPane.toBack();
        miniMenuPane.setVisible(true);
        ggTranslatePane.toFront();
    }
}