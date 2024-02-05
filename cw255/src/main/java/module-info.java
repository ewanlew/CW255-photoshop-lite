module me.ewanl.cw255 {
    requires javafx.controls;
    requires javafx.fxml;


    opens me.ewanl.cw255 to javafx.fxml;
    exports me.ewanl.cw255;
}