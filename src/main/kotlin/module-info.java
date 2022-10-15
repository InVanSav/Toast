module com.example.toast {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;
    requires java.desktop;
    requires jdk.jconsole;


    opens com.example.toast to javafx.fxml;
    exports com.example.toast;
}