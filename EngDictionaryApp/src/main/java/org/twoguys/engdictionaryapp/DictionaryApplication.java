package org.twoguys.engdictionaryapp;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.twoguys.engdictionaryapp.AlertInfo.AlertInfo;

import java.io.IOException;

public class DictionaryApplication extends Application {
    private Stage stage;

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(DictionaryApplication.class.getResource("app.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1080, 720);
        stage.setTitle("Từ điển Anh - Việt");
        stage.getIcons().add(new Image(DictionaryApplication.class.getResourceAsStream("dictionaryIcon.png")));
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                windowEvent.consume();
                close();
            }
        });
        stage.setScene(scene);
        stage.show();
    }

    public void close() {
        AlertInfo alert = new AlertInfo();
        if (alert.showConfirmation("Bạn có chắc chắn muốn thoát ?")) {
            stage.close();
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}