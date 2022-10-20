module com.example.toast {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;
    requires java.desktop;
    requires jdk.jconsole;
    requires javafx.media;


    opens com.example.toast to javafx.fxml;
    exports com.example.toast;
}