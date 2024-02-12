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
            {{-4, -1, 0, -1, -4},
                    {-1, 2, 3, 2, -1},
                    {0, 3, 4, 3, 0},
                    {-1, 2, 3, 2, -1},
                    {-4, -1, 0, -1, -4}};

    /**
     * Main method to start application
     *
     * @param args args
     */
    public static void main(String[] args) {
        launch(args);
    }


    /**
     * Loads FXML & CSS for application window
     *
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
     *
     * @param gamma Gamma value
     */
    public static void setGammaLUT(double gamma) {
        for (int i = 0; i < 256; i++) {
            gammaLUT[i] = Math.pow((double) i / 255.0, 1.0 / gamma);
        }
    }

    /**
     * Corrects the gamma in the image when requested
     *
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
        for (int j = 0; j < newHeight; j++)
            for (int i = 0; i < newWidth; i++) {

                // Gets current colour of pixel
                colour = originalImage.getPixelReader().getColor(i, j);

                // Sets the new gamma-adjusted red, green, and blue values of pixel
                double newRed = gammaLUT[(int) (colour.getRed() * 255.0)];
                double newGreen = gammaLUT[(int) (colour.getGreen() * 255.0)];
                double newBlue = gammaLUT[(int) (colour.getBlue() * 255.0)];

                // Sets the new colour, and writes it to the new image
                colour = Color.color(newRed, newGreen, newBlue);
                writeableImage.setColor(i, j, colour);
            }

        // Returns full gamma-adjusted image
        return gammaCorrectedImage;
    }

    /**
     * Resizes the image to a value requested
     *
     * @param imageToChange Image to be size-adjusted
     * @param resizeScale   Scale of resizing
     * @param nn            Whether nearest-neighbour interpolation is being used
     * @return Resized image
     */
    public static Image resizeImage(Image imageToChange, double resizeScale, boolean nn) {

        // New width & height of resized image
        int newWidth = (int) ((double) imageToChange.getWidth() * resizeScale);
        int newHeight = (int) ((double) imageToChange.getHeight() * resizeScale);

        // Create a new WritableImage
        WritableImage resizedImage = new WritableImage(newWidth, newHeight);
        PixelWriter writeableImage = resizedImage.getPixelWriter();

        // Colour of pixel being adjusted
        Color colour;

        if (resizeScale > 0) {
            // Loops through all rows & columns of image of new dimensions
            for (int j = 0; j < newHeight; j++) {
                for (int i = 0; i < newWidth; i++) {

                    // Finds pixel within original image, that will be used in the new image
                    double x = (double) imageToChange.getWidth() * (double) i / (double) newWidth;
                    double y = (double) imageToChange.getHeight() * (double) j / (double) newHeight;

                    // Adjusts them to be integers, to actually be playable onto new image
                    int ix = (int) x;
                    int iy = (int) y;

                    // Sets new colour, and writes to new image
                    colour = imageToChange.getPixelReader().getColor(ix, iy);
                    writeableImage.setColor(i, j, colour);
                }
            }
        }

        // Returns full resized image
        return resizedImage;
    }

    /**
     * Applies laplacian cross correlation filter to the images
     * @param imageToChange Image to apply filter to
     * @return Image with filter applied
     */
    public static Image applyLaplace(Image imageToChange) {
        // New width and height of the image
        int newWidth = (int) imageToChange.getWidth();
        int newHeight = (int) imageToChange.getHeight();

        // Max red, green, and blue values - used to apply the filter
        double maxRed = Integer.MIN_VALUE;
        double maxGreen = Integer.MIN_VALUE;
        double maxBlue = Integer.MIN_VALUE;

        double minRed = Integer.MAX_VALUE;
        double minGreen = Integer.MAX_VALUE;
        double minBlue = Integer.MAX_VALUE;

        // Array of all of the raw values of the filter, before being normalised
        FauxColor[][] bigColours = new FauxColor[newWidth - 4][newWidth - 4];

        // Image being returned and to wrote to later
        WritableImage filteredImage = new WritableImage(newWidth - 4, newHeight - 4);
        PixelWriter writeableImage = filteredImage.getPixelWriter();

        // Loops through every pixel in the original image
        for (int j = 2; j < newHeight - 2; j++) {
            for (int i = 2; i < newWidth - 2; i++) {

                // Gets the raw red, green, and blue values of the pixel (and surrounding)
                // after applying filter stored in a [r, g, b] within a double
                double[] colorVals = getTotalColours(imageToChange, i, j);

                // Stores these values into a variable that holds all 3
                FauxColor pixelVals = new FauxColor(
                        (int) colorVals[0],
                        (int) colorVals[1],
                        (int) colorVals[2]
                );

                // Adjust the max/min values as/if needed
                if (pixelVals.getR() < minRed) {
                    minRed = pixelVals.getR();
                }

                if (pixelVals.getR() > maxRed) {
                    maxRed = pixelVals.getR();
                }

                if (pixelVals.getG() < minGreen) {
                    minGreen = pixelVals.getG();
                }

                if (pixelVals.getG() > maxGreen) {
                    maxGreen = pixelVals.getG();
                }

                if (pixelVals.getB() < minBlue) {
                    minBlue = pixelVals.getB();
                }

                if (pixelVals.getB() > maxBlue) {
                    maxBlue = pixelVals.getB();
                }

                bigColours[i - 2][j - 2] = pixelVals;
            }
        }

        // Loops through the pixels of the new image again, but to write to
        // instead of read
        for (int i = 0; i < newWidth - 4; i++) {
            for (int j = 0; j < newHeight - 4; j++) {

                // Calculates the normals of the reds, greens, and blues
                int redVal = (int) ((bigColours[i][j].getR() - minRed) * (255 / (maxRed - minRed)));
                int greenVal = (int) ((bigColours[i][j].getG() - minGreen) * (255 / (maxGreen - minGreen)));
                int blueVal = (int) ((bigColours[i][j].getB() - minBlue) * (255 / (maxBlue - minBlue)));

                // Writes the new values to the image
                writeableImage.setColor(i, j, Color.rgb(redVal, greenVal, blueVal));
            }
        }
        return filteredImage;
    }

    /**
     * Gets the raw values of applying the laplacian filter
     * @param image Image that's being read from
     * @param x x value of centre pixel
     * @param y y value of centre pixel
     * @return List of raw rgb filtered values
     */
    private static double[] getTotalColours(Image image, int x, int y) {
        double colorVals[] = new double[3];
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                colorVals[0] += (image.getPixelReader().getColor(i + x, j + y).getRed() * laplacianMatrix[i + 2][j + 2]);
                colorVals[1] += (image.getPixelReader().getColor(i + x, j + y).getGreen() * laplacianMatrix[i + 2][j + 2]);
                colorVals[2] += (image.getPixelReader().getColor(i + x, j + y).getBlue() * laplacianMatrix[i + 2][j + 2]);
            }
        }
        return colorVals;
    }

}