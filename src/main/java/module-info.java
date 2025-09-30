module unknown.oopptt {
    requires javafx.controls;
    requires javafx.fxml;


    opens unknown.oopptt to javafx.fxml;
    exports unknown.oopptt;
}