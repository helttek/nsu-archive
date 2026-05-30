package fit.snake_game.controller;

import fit.snake_game.model.SnakesProto;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import fit.snake_game.model.GameModel;
import fit.snake_game.model.exceptions.WrongArgException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class NewGameController implements Initializable {
    private final GameModel gameModel = GameModel.getInstance();

    private Alert alert;


    @FXML
    private TextField sizeField1;


    @FXML
    private TextField stateDelayMs;

    public NewGameController() throws IOException {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setTitle("Warning");

        sizeField1.setText(Integer.toString(gameModel.getFieldHeight()));
        stateDelayMs.setText(Integer.toString(gameModel.getStateDelayMs()));
    }

    public void startButtonPressed(MouseEvent event) throws IOException {
        try {
            gameModel.setFieldHeight(Integer.parseInt(sizeField1.getText()));
            gameModel.setFieldWidth(Integer.parseInt(sizeField1.getText()));
            gameModel.setStateDelayMs(Integer.parseInt(stateDelayMs.getText()));
            gameModel.setNodeRole(SnakesProto.NodeRole.MASTER);

            gameModel.startServerLogic();

            gameModel.sendMessage(InetAddress.getByName("127.0.0.1"), gameModel.getSelfPort(),
                    gameModel.createJoinMsg());

            Parent gameViewParent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fit.snake_game/fxml/game.fxml")));

            Scene gameViewScene = new Scene(gameViewParent);

            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

            window.setScene(gameViewScene);
            window.show();
        } catch (WrongArgException | InterruptedException ex) {
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    public void backButtonPressed(MouseEvent event) throws IOException {

        Parent gameViewParent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fit.snake_game/fxml/menu.fxml")));

        Scene gameViewScene = new Scene(gameViewParent);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

        window.setScene(gameViewScene);
        window.show();
    }
}
