module com.example.DictionaryGUI {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.DictionaryGUI to javafx.fxml;
    exports com.example.DictionaryGUI;
}