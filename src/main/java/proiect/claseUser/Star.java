package proiect.claseUser;

import javafx.animation.FillTransition;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

/**
 * Represents a visual star UI component that extends the Button class.
 * The Star component is primarily used for ratings, where each star can be filled or empty
 * to represent user selection or an application's rating logic.
 */
public class Star extends Button {
    private static final String STAR_SHAPE = "M12 .587l3.668 7.568 8.332 1.151-6.064 5.828 1.48 8.279-7.416-3.967-7.417 3.967 1.481-8.279-6.064-5.828 8.332-1.151z";
    private static final Color FILLED_COLOR = Color.BLACK;
    private static final Color EMPTY_COLOR = Color.LIGHTGRAY;

    private final int ratingValue;
    private boolean isFilled = false;

    /**
     * Constructs a Star object with the specified rating value and initializes its properties.
     *
     * @param ratingValue the rating value associated with this star. This value can be used
     *                    for determining the star's position in a rating system.
     */
    public Star(int ratingValue) {
        this.ratingValue = ratingValue;
        initializeStar();
    }

    /**
     * Resets the state of the star to unfilled and updates its visual appearance
     * to reflect the empty state.
     *
     * The method sets the `isFilled` property to false and triggers an animation
     * to fill the star with the empty color.
     */
    public void reset() {
        this.isFilled = false;
        animateFill(EMPTY_COLOR);
    }

    /**
     * Initializes the visual and functional properties of the star UI component.
     *
     * The method performs the following:
     * - Sets the star shape using an SVG path defined by the `STAR_SHAPE` constant.
     * - Configures the button properties such as minimum and maximum size, and its style to
     *   have a transparent background and border.
     * - Sets the initial state of the star to be unfilled by calling the `setFilled` method.
     *
     * This method is invoked during the construction of the `Star` object to ensure the star
     * is properly initialized before it is used in the application.
     */
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

    /**
     * Updates the filled state of the star and triggers an animation to visually reflect the change.
     *
     * @param filled a boolean indicating the desired filled state of the star.
     *               If true, the star is marked as filled and an animation will fill it with the `FILLED_COLOR`.
     *               If false, the star is marked as unfilled and an animation will fill it with the `EMPTY_COLOR`.
     */
    public void setFilled(boolean filled) {
        this.isFilled = filled;
        animateFill(filled ? FILLED_COLOR : EMPTY_COLOR);
    }

    /**
     * Animates the fill transition of the star's shape and updates its background color.
     *
     * This method applies a smooth color transition to the star's shape using a `FillTransition`.
     * The transition changes the shape's fill color to the specified target color.
     * Additionally, the method updates the star's CSS background color property to match
     * the provided color.
     *
     * @param color the target color to which the star's shape should transition. This also determines
     *              the background color of the star.
     */
    private void animateFill(Color color) {
        FillTransition ft = new FillTransition(Duration.millis(200));
        ft.setShape(this.getShape());
        ft.setToValue(color);
        ft.play();

        // Also change the background color
        this.setStyle("-fx-background-color: " + toHex(color) + ";");
    }

    /**
     * Retrieves the rating value associated with this star.
     *
     * @return the integer value representing the rating of the star.
     */
    public int getRatingValue() {
        return ratingValue;
    }

    public boolean isFilled() {
        return isFilled;
    }

    /**
     * Converts a {@code Color} object to its hexadecimal string representation.
     * The resulting string is formatted as {@code #RRGGBB}, where {@code RR}, {@code GG}, and {@code BB}
     * are the two-digit hexadecimal values representing the red, green, and blue components of the color.
     *
     * @param color the {@code Color} object to be converted to hexadecimal representation.
     * @return a {@code String} representing the hexadecimal value of the color in the format {@code #RRGGBB}.
     */
    private String toHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int)(color.getRed() * 255),
                (int)(color.getGreen() * 255),
                (int)(color.getBlue() * 255));
    }
}