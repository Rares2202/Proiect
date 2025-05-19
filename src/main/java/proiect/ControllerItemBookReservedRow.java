package proiect;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcons;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class ControllerItemBookReservedRow {

    public ControllerLibrarian mainController;

    public AnchorPane background;
    public Label name;
    public Label author;
    public Label genre;
    public Button btn_add;
    public boolean selected;
    public FontAwesomeIcon btn_add_icon;

    public String id;

    @FXML
    void initialize() {
        btn_add.setStyle("-fx-background-color: transparent;");
        //btn_add.setStyle("-fx-background-radius: 0;");
        setDisabled();
        btn_add_icon.setVisible(false);
        selected = false;
    }

    public void setData(String id, String name, String author, String genre)
    {
        this.id = id;
        this.name.setText(name);
        this.author.setText(author);
        this.genre.setText(genre);
    }


    public void OnAddButtonClicked(MouseEvent mouseEvent) {

            if(!selected)
            {
                setEnabled();
                mainController.list_selected_reserved_books.add(this);
                mainController.btn_efectueaza.setDisable(false);
            }
            else
            {
                setDisabled();
                background.setStyle("-fx-background-color: rgba(0,255,0,0.25)");
                btn_add_icon.setFill(Color.WHITE);
                mainController.list_selected_reserved_books.remove(this);
                if(mainController.list_selected_reserved_books.size()==0 && mainController.list_selected_inventory_books.size()==0)
                    mainController.btn_efectueaza.setDisable(true);
            }
    }

    public void OnMouseEntered(MouseEvent mouseEvent) {
        if(!selected)
        {
            background.setStyle("-fx-background-color: rgba(0,255,0,0.25)");
            btn_add_icon.setVisible(true);
        }

    }

    public void OnMouseExited(MouseEvent mouseEvent) {
        if(!selected)
        {
            background.setStyle("-fx-background-color: white");
            btn_add_icon.setVisible(false);
        }
    }

    void setEnabled()
    {
        background.setStyle("-fx-background-color: green");
        name.setStyle("-fx-text-fill: white");
        author.setStyle("-fx-text-fill: white");
        genre.setStyle("-fx-text-fill: white");
        btn_add_icon.setFill(Color.WHITE);
        btn_add_icon.setGlyphName(String.valueOf(FontAwesomeIcons.MINUS));
        selected = true;
    }

    void setDisabled()
    {
        background.setStyle("-fx-background-color: white");
        name.setStyle("-fx-text-fill: black");
        author.setStyle("-fx-text-fill: black");
        genre.setStyle("-fx-text-fill: black");
        btn_add_icon.setFill(Color.GREEN);
        btn_add_icon.setGlyphName(String.valueOf(FontAwesomeIcons.PLUS));
        selected = false;
    }

    public void OnAddButtonEntered(MouseEvent mouseEvent) {
        btn_add.setStyle("-fx-background-color: rgb(0,75,0); -fx-cursor: hand;");
        btn_add_icon.setFill(Color.WHITE);
    }

    public void OnAddButtonExited(MouseEvent mouseEvent) {
        btn_add.setStyle("-fx-background-color: transparent; -fx-cursor: none;");
        if(!selected)
            btn_add_icon.setFill(Color.GREEN);
    }
}
