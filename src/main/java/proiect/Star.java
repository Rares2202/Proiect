package proiect;

import javafx.animation.FillTransition;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

public class Star extends Button {
    private static final String STAR_SHAPE = "M12 .587l3.668 7.568 8.332 1.151-6.064 5.828 1.48 8.279-7.416-3.967-7.417 3.967 1.481-8.279-6.064-5.828 8.332-1.151z";
    private static final Color FILLED_COLOR = Color.GOLD;
    private static final Color EMPTY_COLOR = Color.LIGHTGRAY;

    private final int ratingValue;
    private boolean isFilled = false;

    public Star(int ratingValue) {
        this.ratingValue = ratingValue;
        initializeStar();
    }

    private void initializeStar() {
        // Set star shape
        SVGPath starShape = new SVGPath();
        starShape.setContent(STAR_SHAPE);
        this.setShape(starShape);

        // Set button properties
        this.setMinSize(30, 30);
        this.setMaxSize(30, 30);
        this.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        // Initial state
        setFilled(false);
    }

    public void setFilled(boolean filled) {
        this.isFilled = filled;
        animateFill(filled ? FILLED_COLOR : EMPTY_COLOR);
    }

    private void animateFill(Color color) {
        FillTransition ft = new FillTransition(Duration.millis(200));
        ft.setShape(this.getShape());
        ft.setToValue(color);
        ft.play();

        // Also change the background color
        this.setStyle("-fx-background-color: " + toHex(color) + ";");
    }

    public int getRatingValue() {
        return ratingValue;
    }

    public boolean isFilled() {
        return isFilled;
    }

    private String toHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int)(color.getRed() * 255),
                (int)(color.getGreen() * 255),
                (int)(color.getBlue() * 255));
    }
}