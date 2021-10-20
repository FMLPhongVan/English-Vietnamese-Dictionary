package org.twoguys.engdictionaryapp.AlertInfo;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.StageStyle;

import java.util.Optional;

public class AlertInfo {
    public boolean showConfirmation(String msg) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initStyle(StageStyle.UNIFIED);
        alert.setHeaderText(msg);
        alert.setContentText(null);

        ButtonType ok = new ButtonType("Đồng ý");
        ButtonType cancel = new ButtonType("Hủy");

        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(ok, cancel);

        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == ok) return true;
        return false;
    }

    public void showInformation(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initStyle(StageStyle.UNIFIED);
        alert.setHeaderText(msg);
        alert.setContentText(null);
        alert.showAndWait();
    }
}
