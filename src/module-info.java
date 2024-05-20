module Gestion_de_bibliothéque {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
	requires javafx.graphics;
	requires javafx.base;

    opens Controller to javafx.fxml;
    exports Controller;

    opens application to javafx.graphics;
    exports application;
    opens Models to javafx.base;
}
