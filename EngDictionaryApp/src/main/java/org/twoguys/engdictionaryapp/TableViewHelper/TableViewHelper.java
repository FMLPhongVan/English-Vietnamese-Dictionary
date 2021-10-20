package org.twoguys.engdictionaryapp.TableViewHelper;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class TableViewHelper {
    public TableViewHelper() {};

    public void initWordTable(TableView<WordDataTable> tableView) {
        TableColumn<WordDataTable, String> wordTypeCol = new TableColumn<>("Loại từ");
        TableColumn<WordDataTable, String> wordDescriptionCol = new TableColumn<>("Nghĩa");
        TableColumn<WordDataTable, String> wordExampleCol = new TableColumn<>("Ví dụ");
        wordTypeCol.setCellValueFactory(new PropertyValueFactory<>("wordType"));
        wordDescriptionCol.setCellValueFactory(new PropertyValueFactory<>("wordDescription"));
        wordExampleCol.setCellValueFactory(new PropertyValueFactory<>("wordExample"));
        tableView.getColumns().addAll(wordTypeCol, wordDescriptionCol, wordExampleCol);
    }

    public void initHistoryTable(TableView<HistoryTable> tableView) {
        TableColumn<HistoryTable, String> wordCol = new TableColumn<>("Từ");
        TableColumn<HistoryTable, String> timeCol = new TableColumn<>("Thời gian");
        TableColumn<HistoryTable, String> dateCol = new TableColumn<>("Ngày");
        wordCol.setCellValueFactory(new PropertyValueFactory<>("word"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        tableView.getColumns().addAll(wordCol, timeCol, dateCol);
    }

}
