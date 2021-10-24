module org.cpvisu {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;

    exports org.cpvisu.examples;
    opens org.cpvisu.examples to javafx.fxml;
}