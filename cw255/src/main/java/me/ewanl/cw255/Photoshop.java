package me.ewanl.cw255;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class Photoshop extends Application {
    private static double[] gammaLUT = new double[256];

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Photoshop.class.getResource("photoshop-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1920, 1080);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
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

    public static void setGammaLUT(double gamma){
        for (int i = 0; i<256; i++){
            gammaLUT[i] = Math.pow((double) i/255.0, 1.0/gamma);
        }
    }

    public static Image gammaCorrect(Image originalImage) {
        int newWidth = (int) originalImage.getWidth();
        int newHeight = (int) originalImage.getHeight();
        
        // Create a new WritableImage
        WritableImage gammaCorrectedImage = new WritableImage(newWidth, newHeight);
        PixelWriter writeableImage = gammaCorrectedImage.getPixelWriter();

        Color colour;

        for (int j=0; j < newHeight; j++)
            for (int i=0; i < newWidth; i++) {
                colour = originalImage.getPixelReader().getColor(i, j);

                double newRed = gammaLUT[(int) (colour.getRed()*255.0)];
                double newGreen = gammaLUT[(int) (colour.getGreen()*255.0)];
                double newBlue = gammaLUT[(int) (colour.getBlue()*255.0)];

                colour = Color.color(newRed, newGreen, newBlue);
                writeableImage.setColor(i, j, colour);
            }

        return gammaCorrectedImage;
    }

    public static Image resizeImage(Image imageToChange, double resizeScale, boolean nn){
        int newWidth = (int) ((double) imageToChange.getWidth()*resizeScale);
        int newHeight = (int) ((double) imageToChange.getHeight()*resizeScale);

        // Create a new WritableImage
        WritableImage resizedImage = new WritableImage(newWidth, newHeight);
        PixelWriter writeableImage = resizedImage.getPixelWriter();

        Color colour;

        for (int j=0; j < newHeight; j++) {
            for (int i=0; i < newWidth; i++) {
                double x = (double) imageToChange.getWidth()* (double) i / (double) newWidth;
                double y = (double) imageToChange.getHeight()* (double) j / (double) newHeight;

                int ix = (int) x;
                int iy = (int) y;

                colour = imageToChange.getPixelReader().getColor(ix, iy);
                writeableImage.setColor(i, j, colour);


            }


        }



        return resizedImage;
    }


}
