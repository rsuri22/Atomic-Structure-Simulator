module com.example.chickennugget {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.chickennugget to javafx.fxml;
    exports com.example.chickennugget;
}