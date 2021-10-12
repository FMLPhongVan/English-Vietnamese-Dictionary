module org.twoguys.engdictionaryapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires com.jfoenix;
    requires javafx.base;
    requires javafx.web;
    requires java.sql;
    requires javafx.graphics;

    opens org.twoguys.engdictionaryapp to javafx.fxml;
    exports org.twoguys.engdictionaryapp;
    exports org.twoguys.engdictionaryapp.TrieNode;
    opens org.twoguys.engdictionaryapp.TrieNode to javafx.fxml;
}