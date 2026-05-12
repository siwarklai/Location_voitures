module com.project.projet {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.project.projet to javafx.fxml;
    opens com.project.projet.controller to javafx.fxml;
    exports com.project.projet;
}