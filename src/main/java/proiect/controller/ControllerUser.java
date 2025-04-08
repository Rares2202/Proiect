package proiect.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;
import java.util.ArrayList;
import java.util.List;


public class ControllerUser {

    @FXML
    private CheckBox actionCheckBox;
    @FXML private CheckBox adventureCheckBox;
    @FXML private CheckBox biographyCheckBox;
    @FXML private CheckBox classicsCheckBox;
    @FXML private CheckBox comicBooksCheckBox;
    @FXML private CheckBox cookbooksCheckBox;
    @FXML private CheckBox dramaCheckBox;
    @FXML private CheckBox fairyTalesCheckBox;
    @FXML private CheckBox fantasyCheckBox;
    @FXML private CheckBox historicalCheckBox;
    @FXML private CheckBox horrorCheckBox;
    @FXML private CheckBox mysteryCheckBox;
    @FXML private CheckBox philosophyCheckBox;
    @FXML private CheckBox poetryCheckBox;
    @FXML private CheckBox psychologyCheckBox;
    @FXML private CheckBox religionCheckBox;
    @FXML private CheckBox romanceCheckBox;
    @FXML private CheckBox scienceCheckBox;
    @FXML private CheckBox scienceFictionCheckBox;
    @FXML private CheckBox selfImprovementCheckBox;
    public List<String> getSelectedGenres() {
        List<String> selectedGenres = new ArrayList<>();

        if (actionCheckBox.isSelected()) selectedGenres.add("Action");
        if (adventureCheckBox.isSelected()) selectedGenres.add("Adventure");
        if (biographyCheckBox.isSelected()) selectedGenres.add("Biography");
        if (classicsCheckBox.isSelected()) selectedGenres.add("Classics");
        if (comicBooksCheckBox.isSelected()) selectedGenres.add("Comic Books");
        if (cookbooksCheckBox.isSelected()) selectedGenres.add("Cookbooks");
        if (dramaCheckBox.isSelected()) selectedGenres.add("Drama");
        if (fairyTalesCheckBox.isSelected()) selectedGenres.add("Fairy Tales");
        if (fantasyCheckBox.isSelected()) selectedGenres.add("Fantasy");
        if (historicalCheckBox.isSelected()) selectedGenres.add("Historical");
        if (horrorCheckBox.isSelected()) selectedGenres.add("Horror");
        if (mysteryCheckBox.isSelected()) selectedGenres.add("Mystery");
        if (philosophyCheckBox.isSelected()) selectedGenres.add("Philosphy");
        if (poetryCheckBox.isSelected()) selectedGenres.add("Poetry");
        if (psychologyCheckBox.isSelected()) selectedGenres.add("Psychology");
        if (religionCheckBox.isSelected()) selectedGenres.add("Religion");
        if (romanceCheckBox.isSelected()) selectedGenres.add("Romance");
        if (scienceCheckBox.isSelected()) selectedGenres.add("Science");
        if (scienceFictionCheckBox.isSelected()) selectedGenres.add("Science Fiction");
        if (selfImprovementCheckBox.isSelected()) selectedGenres.add("Self Improvement");

        return selectedGenres;
    }
    @FXML
    public void quit_app(MouseEvent mouseEvent) {
        System.exit(0);
        System.out.println("Quit app");
    }
    @FXML
    public void handleSubmitGenres() {
        List<String> genres  = getSelectedGenres();

        System.out.println("Selected Genres: " + genres);
    }


}
