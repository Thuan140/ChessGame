module com.example.fx {
    requires javafx.controls;
    requires javafx.fxml;

    exports com.example.fx.ui;
    exports com.example.fx.model;
    exports com.example.fx.game;

    opens com.example.fx.ui to javafx.fxml;
    opens com.example.fx.model to javafx.fxml;
    opens com.example.fx.game to javafx.fxml;
}
