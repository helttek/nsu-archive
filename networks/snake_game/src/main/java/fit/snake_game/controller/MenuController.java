package fit.snake_game.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import fit.snake_game.model.GameModel;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MenuController implements Initializable {
    private final GameModel gameModel = GameModel.getInstance();

    @FXML
    private TextField playerNameField;

    public MenuController() throws IOException {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playerNameField.setText(gameModel.getMasterPlayerName());
    }


    public void newGameButtonPressed(MouseEvent event) throws IOException {
        Parent gameViewParent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fit.snake_game/fxml/new_game.fxml")));
        Scene gameViewScene = new Scene(gameViewParent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(gameViewScene);
        window.show();
    }

    public void connectButtonPressed(MouseEvent event) throws IOException {
        gameModel.receiveAnnouncementMessages();
        Parent gameViewParent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fit.snake_game/fxml/connect.fxml")));
        Scene gameViewScene = new Scene(gameViewParent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(gameViewScene);
        window.show();
    }

    public void exitButtonPressed(MouseEvent event) {
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.close();
    }

    public void applyButtonPressed(MouseEvent event) throws IOException {
        gameModel.setMasterPlayerName(playerNameField.getText());
    }
}
