module unknown.oopptt {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.graphics;
    requires java.desktop;
    requires java.sql;
    requires javafx.base;
    requires org.json;
    //requires unknown.oopptt;

    opens unknown.oopptt to javafx.graphics;
    opens unknown.oopptt.controller to javafx.fxml;

    opens unknown.oopptt.api.render to javafx.graphics;
    exports unknown.oopptt.api.render;

    exports unknown.oopptt;
    exports unknown.oopptt.controller;
}
