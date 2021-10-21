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
    requires java.desktop;
    requires voicersstts;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;

    opens org.twoguys.engdictionaryapp to javafx.fxml, javafx.controls;
    exports org.twoguys.engdictionaryapp;
    exports org.twoguys.engdictionaryapp.TrieTree;
    opens org.twoguys.engdictionaryapp.TrieTree to javafx.fxml;
    exports org.twoguys.engdictionaryapp.TableViewHelper;
    opens org.twoguys.engdictionaryapp.TableViewHelper to javafx.fxml;
    exports org.twoguys.engdictionaryapp.AlertInfo;
    opens org.twoguys.engdictionaryapp.AlertInfo to javafx.fxml;
    exports org.twoguys.engdictionaryapp.VoiceHandle;
    opens org.twoguys.engdictionaryapp.VoiceHandle to javafx.fxml;
    exports org.twoguys.engdictionaryapp.RelatedWords;
    opens org.twoguys.engdictionaryapp.RelatedWords to javafx.fxml;
    exports org.twoguys.engdictionaryapp.GoogleAPI;
    opens org.twoguys.engdictionaryapp.GoogleAPI to javafx.fxml;
}