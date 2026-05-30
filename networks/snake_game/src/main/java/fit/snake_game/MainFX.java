package fit.snake_game;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainFX extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(("/fit.snake_game/fxml/menu.fxml"))));
        primaryStage.setScene(new Scene(root, 850, 700));
        primaryStage.setResizable(true);
        primaryStage.show();
    }
}