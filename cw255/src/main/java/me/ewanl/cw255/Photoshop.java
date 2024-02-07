package me.ewanl.cw255;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Photoshop extends Application {

    /**
     * Lookup-table for gamma values
     */
    private static double[] gammaLUT = new double[256];

    private static final int[][] laplacianMatrix =
                    { {-4, -1, 0, -1, -4},
                    {-1, 2, 3, 2, -1},
                    {0, 3, 4, 3, 0},
                    {-1, 2, 3, 2, -1},
                    {-4, -1, 0, -1, -4}};

    /**
     * Main method to start application
     * @param args args
     */
    public static void main(String[] args) {
        launch(args);
    }


    /**
     * Loads FXML & CSS for application window
     * @param primaryStage Stage to be shown
     * @throws IOException File may not be found
     */
    public void start(Stage primaryStage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Photoshop.class.getResource("photoshop-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1920, 1080);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
        primaryStage.setTitle("Photoshop Lite");
        primaryStage.getIcons().add(new Image("file:icon.png"));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Sets the gamma lookup-table whenever gamma is adjusted
     * @param gamma Gamma value
     */
    public static void setGammaLUT(double gamma){
        for (int i = 0; i<256; i++){
            gammaLUT[i] = Math.pow((double) i/255.0, 1.0/gamma);
        }
    }

    /**
     * Corrects the gamma in the image when requested
     * @param originalImage Image to adjust gamma of
     * @return Gamma-corrected image
     */
    public static Image gammaCorrect(Image originalImage) {

        // New width & height of image
        int newWidth = (int) originalImage.getWidth();
        int newHeight = (int) originalImage.getHeight();
        
        // Create a new WritableImage
        WritableImage gammaCorrectedImage = new WritableImage(newWidth, newHeight);
        PixelWriter writeableImage = gammaCorrectedImage.getPixelWriter();

        // Colour of pixel being changed
        Color colour;

        // Loops through all columns and rows of pixels
        for (int j=0; j < newHeight; j++)
            for (int i=0; i < newWidth; i++) {

                // Gets current colour of pixel
                colour = originalImage.getPixelReader().getColor(i, j);

                // Sets the new gamma-adjusted red, green, and blue values of pixel
                double newRed = gammaLUT[(int) (colour.getRed()*255.0)];
                double newGreen = gammaLUT[(int) (colour.getGreen()*255.0)];
                double newBlue = gammaLUT[(int) (colour.getBlue()*255.0)];

                // Sets the new colour, and writes it to the new image
                colour = Color.color(newRed, newGreen, newBlue);
                writeableImage.setColor(i, j, colour);
            }

        // Returns full gamma-adjusted image
        return gammaCorrectedImage;
    }

    /**
     * Resizes the image to a value requested
     * @param imageToChange Image to be size-adjusted
     * @param resizeScale Scale of resizing
     * @param nn Whether nearest-neighbour interpolation is being used
     * @return Resized image
     */
    public static Image resizeImage(Image imageToChange, double resizeScale, boolean nn){

        // New width & height of resized image
        int newWidth = (int) ((double) imageToChange.getWidth()*resizeScale);
        int newHeight = (int) ((double) imageToChange.getHeight()*resizeScale);

        // Create a new WritableImage
        WritableImage resizedImage = new WritableImage(newWidth, newHeight);
        PixelWriter writeableImage = resizedImage.getPixelWriter();

        // Colour of pixel being adjusted
        Color colour;

        // Loops through all rows & columns of image of new dimensions
        for (int j=0; j < newHeight; j++) {
            for (int i=0; i < newWidth; i++) {

                // Finds pixel within original image, that will be used in the new image
                double x = (double) imageToChange.getWidth()* (double) i / (double) newWidth;
                double y = (double) imageToChange.getHeight()* (double) j / (double) newHeight;

                // Adjusts them to be integers, to actually be playable onto new image
                int ix = (int) x;
                int iy = (int) y;

                // Sets new colour, and writes to new image
                colour = imageToChange.getPixelReader().getColor(ix, iy);
                writeableImage.setColor(i, j, colour);
            }
        }

        // Returns full resized image
        return resizedImage;
    }

    public static Image applyLaplace(Image imageToChange) {
        int newWidth = (int) imageToChange.getWidth();
        int newHeight = (int) imageToChange.getHeight();

        WritableImage filteredImage = new WritableImage(newWidth, newHeight);
        PixelWriter writeableImage = filteredImage.getPixelWriter();

        for (int j = 2; j < newHeight - 2 ; j++){
            for (int i = 2; i < newWidth - 2; i++){
                Color[][] colorMatrix = getColourMatrix(imageToChange);
                Color newColor = getAdjustedColour(colorMatrix);


                writeableImage.setColor(i, j, newColor);

            }
        }

        return filteredImage;
    }

    private static Color[][] getColourMatrix(Image image){
        Color[][] colourMatrix = new Color[5][5];
        for (int i = 0; i <= 4; i++){
            for (int j = 0; j <= 4; j++){

                colourMatrix[i][j] =
                        image.getPixelReader().getColor(i, j);
            }
        }
        return colourMatrix;
    }

    private static Color getAdjustedColour(Color[][] colourMatrix){
        double newRed = 0;
        double newBlue = 0;
        double newGreen = 0;

        for (int i = 0; i < 4; i++){
            for (int j = 0; j < 4; j++){
                newRed += (laplacianMatrix[i][j] * colourMatrix[i][j].getRed());
                newGreen += (laplacianMatrix[i][j] * colourMatrix[i][j].getGreen());
                newBlue += (laplacianMatrix[i][j] * colourMatrix[i][j].getGreen());
            }
        }

        newRed = Math.min(255, Math.max(0, newRed));
        newBlue = Math.min(255, Math.max(0, newBlue));
        newGreen = Math.min(255, Math.max(0, newGreen));

        return Color.color(newRed/255, newBlue/255, newGreen/255);
    }

}
