/**
 * Controller class to manage interaction with the UI
 * @Author Ewan Lewis
 * @Version 1.0
 */
package me.ewanl.cw255;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.text.DecimalFormat;

public class PhotoshopController {

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

    private Image originalImage;

    /**
     * Initialises listeners for all objects in the scene
     */
    public void initialize(){

        try {
            originalImage = new Image(getClass().getResourceAsStream("raytrace.jpg"));
        } catch (NullPointerException e){
            System.out.println("Image not found :(");
        }

        DecimalFormat df = new DecimalFormat("0.000");

        sldGamma.valueProperty().addListener((observableValue, oldVal, newVal) -> {
            double gammaVal = newVal.doubleValue();
            lblGammaValue.setText(df.format(gammaVal));

            Photoshop.setGammaLUT(gammaVal);
            imgView.setImage(Photoshop.gammaCorrect(originalImage));
        });

        sldScale.valueProperty().addListener((observableValue, oldVal, newVal) -> {
            double scaleVal = newVal.doubleValue();
            lblScaleValue.setText(df.format(scaleVal));


        });

        btnReset.setOnAction(event -> reset());
    }

    private void reset(){
        sldGamma.setValue(1);
        sldScale.setValue(1);
        lblScaleValue.setText("1");
        lblGammaValue.setText("1");
        imgView.setImage(originalImage);
        rdoBilinear.setSelected(true);
        chkCrossCorrelation.setSelected(false);
    }


}


