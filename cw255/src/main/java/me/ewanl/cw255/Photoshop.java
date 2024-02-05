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

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("photoshop-view.fxml"));
//        Scene scene = new Scene (fxmlLoader.load(), 600, 400);
        primaryStage.setTitle("Photoshop Lite");

        // Create an ImageView
        imageView = new ImageView();

        // Load the image from a file
        try {
            originalImage = new Image(getClass().getResourceAsStream("raytrace.jpg"));
            imageView.setImage(originalImage);
        } catch (NullPointerException e) {
            System.out.println(">>>The image could not be located in directory: "+System.getProperty("user.dir")+"<<<");
            System.exit(-1);
        }

        // Create a Slider for gamma correction
        Slider gammaSlider = new Slider(0.1, 3.0, 1.0);

        // Create a Label to display the current gamma value
        Label gammaLabel = new Label("Gamma: " + gammaSlider.getValue());

        // Add a listener to update the image with gamma correction when the slider is changed
        gammaSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double gammaValue = newValue.doubleValue();
            gammaLabel.setText("Gamma: " + gammaValue);

            // Apply gamma correction to the image and update the ImageView
            Image correctedImage = applyGammaCorrection(originalImage, gammaValue);
            imageView.setImage(correctedImage);
        });

        Slider resizeSlider = new Slider(0.1, 2.0, 1.0);
        Label resizeLabel = new Label("Resize: " + resizeSlider.getValue());

        CheckBox nn = new CheckBox("Nearest neighbour interpolation");
        nn.setSelected(false);

        // Add a listener to update the image with resizing when the slider is changed
        resizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double resizeValue = newValue.doubleValue();
            resizeLabel.setText("Resize: " + resizeValue);

        });

        Separator separator1 = new Separator();
        Separator separator2 = new Separator();
        Separator separator3 = new Separator();

        // Create a VBox to hold the components
        VBox vbox = new VBox(gammaSlider, gammaLabel, separator1, resizeSlider, resizeLabel, separator2, nn, separator3, imageView);

        // Create a scene with the VBox
        Scene scene = new Scene(vbox, 400, 600);

        // Set the scene to the stage
        primaryStage.setScene(scene);

        // Show the stage
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
