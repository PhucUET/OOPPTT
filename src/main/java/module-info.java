module unknown.oopptt {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires org.json;


    opens unknown.oopptt to javafx.fxml;
    exports unknown.oopptt;
}