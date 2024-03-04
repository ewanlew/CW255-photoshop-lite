/**
 * Controller class to manage interaction with the UI
 * @Author Ewan Lewis
 * @Version 1.0
 */
package me.ewanl.cw255;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.text.DecimalFormat;
import java.util.Objects;

public class PhotoshopController {

    /**
     * Initial/default gamma value
     */
    private final double INIT_GAMMA_VAL = 1;

    /**
     * Initial/default scale value
     */
    private final double INIT_SCALE_VAL = 1;

    /**
     * Label that represents current gamma value
     */
    public Label lblGammaValue;

    /**
     * Label that represents current scale value
     */
    public Label lblScaleValue;

    /**
     * Slider to control gamma value
     */
    public Slider sldGamma;

    /**
     * Slider to control scale value
     */
    public Slider sldScale;

    /**
     * Radio button to toggle if bilinear interpolation
     */
    public RadioButton rdoBilinear;

    /**
     * Radio button to toggle if nearest neighbour interpolation
     */
    public RadioButton rdoNearestNeighbour;

    /**
     * Toggle group housing the radio buttons
     */
    public ToggleGroup interpolation;

    /**
     * Checkbox to apply/remove the cross correlation filter
     */
    public CheckBox chkCrossCorrelation;

    /**
     * Button to reset all values
     */
    public Button btnReset;

    /**
     * Image that is viewed to the side of the controls
     */
    public ImageView imgView;

    /**
     * Original image to be modified
     */
    private Image originalImage;

    /**
     * Original image with laplacian filter applied
     */
    private Image laplacianImage;

    /**
     * Current scale of the image
     */
    private double currentScale = INIT_SCALE_VAL;

    /**
     * Initialises listeners for all objects in the scene
     */
    public void initialize(){

        // Loads image, stores into originalImage
        try {
            originalImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("raytrace.jpg")));
        } catch (NullPointerException e){
            System.out.println("Image not found :(");
        }

        // Sets the decimal format for the slider labels, rounding to 3dp
        DecimalFormat df = new DecimalFormat("0.000");

        // Sets the gamma look-up table to the initial gamma value
        Photoshop.setGammaLUT(INIT_GAMMA_VAL);

        // Adds the events to the listener of the gamma slider
        sldGamma.valueProperty().addListener((observableValue, oldVal, newVal) -> {

            // Update appropriate variables to new value
            double gammaVal = newVal.doubleValue();
            lblGammaValue.setText(df.format(gammaVal));
            Photoshop.setGammaLUT(gammaVal);

            // Updates image to incorporate new gamma value
            updateImage(null);
        });

        // Adds the events to the listener of the scale slider
        sldScale.valueProperty().addListener((observableValue, oldVal, newVal) -> {

            // Update appropriate variables to new value
            double scaleVal = newVal.doubleValue();
            currentScale = scaleVal;
            lblScaleValue.setText(df.format(scaleVal));


            // Updates image to incorporate new scale value
            updateImage(null);
        });

        /* Adds event to listener of cross correlation filter checkbox,
        calls to updateCrossCorrelation method
         */
        chkCrossCorrelation.setOnAction(this::updateImage);

        // Sets an image to have the original image with laplacian filter
        laplacianImage = Photoshop.applyLaplace(originalImage);

        // Adds event to button listener, calls reset method
        btnReset.setOnAction(event -> reset());
    }

    /**
     * Updates image, using the current scale value, current gamma value, scale interpolation style, and laplacian filter
     * @param actionEvent Action Event handler
     */
    private void updateImage(ActionEvent actionEvent){
        if (chkCrossCorrelation.isSelected()){
            if (sldGamma.getValue() == 1){
                if (sldScale.getValue() == 1){
                    imgView.setImage(laplacianImage);
                }
                else {
                    imgView.setImage(Photoshop.resizeImage(laplacianImage, currentScale, rdoNearestNeighbour.isSelected()));
                }
            } else {
                if (sldScale.getValue() == 1){
                    imgView.setImage(Photoshop.gammaCorrect(laplacianImage));
                }
                else {
                    imgView.setImage(Photoshop.gammaCorrect(Photoshop.resizeImage(laplacianImage, currentScale, rdoNearestNeighbour.isSelected())));
                }
            }
        } else {
            imgView.setImage(Photoshop.gammaCorrect(Photoshop.resizeImage(originalImage, currentScale, rdoNearestNeighbour.isSelected())));
        }
    }


    /**
     * Resets all values to their default state
     */
    private void reset(){
        sldGamma.setValue(INIT_GAMMA_VAL);
        sldScale.setValue(INIT_SCALE_VAL);
        lblScaleValue.setText("1");
        lblGammaValue.setText("1");
        imgView.setImage(originalImage);
        rdoNearestNeighbour.setSelected(true);
        chkCrossCorrelation.setSelected(false);
    }


}


