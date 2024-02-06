package me.ewanl.cw255;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class Photoshop extends Application {

    private Image originalImage;
    private ImageView imageView;
    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("photoshop-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1920, 1080);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        primaryStage.setResizable(false);
        primaryStage.setTitle("Photoshop Lite");
        primaryStage.getIcons().add(new Image("file:icon.png"));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /*
     * 
     * Gamma correction function
     * 
     * 
     */

    private Image applyGammaCorrection(Image originalImage, double gamma) {
        int newWidth = (int) originalImage.getWidth();
        int newHeight = (int) originalImage.getHeight();
        
        // Create a new WritableImage
        javafx.scene.image.WritableImage gammaCorrectedImage = new javafx.scene.image.WritableImage(newWidth, newHeight);
        PixelWriter writeableimage=gammaCorrectedImage.getPixelWriter();

        Color colour;

        for (int j=0; j<newHeight; j++)
            for (int i=0; i<newWidth; i++) {
                colour=originalImage.getPixelReader().getColor(i, j);
                colour=Color.color(gamma/3.0, gamma/3.0, gamma/3.0); // this is not gamma correction - it just demonstrates some computation using gamma to give a pixel colour (grey scale)
                writeableimage.setColor(i, j, colour);
            }

        return gammaCorrectedImage;
    }


    /*
     * 
     * Interpolation functions
     * 
     * 
     */


}
