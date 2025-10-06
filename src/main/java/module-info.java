module unknown.oopptt {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.graphics;
    requires java.desktop;
    requires java.sql;

    opens unknown.oopptt to javafx.graphics;
    opens unknown.oopptt.controller to javafx.fxml;

    exports unknown.oopptt;
    exports unknown.oopptt.controller;
}
