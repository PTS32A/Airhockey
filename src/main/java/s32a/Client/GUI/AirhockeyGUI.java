/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Client.GUI;

import s32a.Server.Spectator;
import s32a.Server.Player;
import s32a.Server.Person;
import s32a.Server.Lobby;
import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.Getter;

/**
 * NOTES: find out what game is currently active for the closeGame click event
 *
 * @author Kargathia
 */
public class AirhockeyGUI extends Application {

    @Getter
    private Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        Lobby.getSingle();

        this.stage = stage;
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Login.fxml"));

        stage.setOnCloseRequest((WindowEvent event) -> {
            Person person = Lobby.getSingle().getCurrentPerson();
            if (person != null) {
                Lobby.getSingle().logOut(person);
            }
            Platform.exit();
            System.exit(0);
        });

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        // populates lobby
        Thread thread = new Thread(() -> {
            Lobby.getSingle().populate();
        });
        thread.start();
        //goToLobby(stage);
    }

    /**
     *
     */
    void goToLogin(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Login.fxml"));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     *
     * @param stage
     * @throws java.io.IOException
     */
    public void goToRegister(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Register.fxml"));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     *
     */
    void goToLobby(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Lobby.fxml"));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     *
     */
    void goToGame(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Game.fxml"));

        Scene scene = new Scene(root);
        stage.setScene(scene);
//        stage.setResizable(false);
        stage.setMinHeight(root.minHeight(600));
        stage.setMinWidth(root.minWidth(1100));

        stage.setOnCloseRequest((WindowEvent event) -> {
            Lobby lobby = Lobby.getSingle();

            if (lobby.getCurrentPerson() instanceof Spectator) { // TODO FIND OUT WHAT GAME CURRENTLY IS ACTIVE
                lobby.stopSpectating(lobby.getSpectatedGames().get(0), lobby.getCurrentPerson());
            } else if (lobby.getCurrentPerson() instanceof Player) {
                Player person = (Player) Lobby.getSingle().getCurrentPerson();
                if (person != null) {
                    Lobby.getSingle().endGame(Lobby.getSingle().getPlayedGame(), person);
                }
            }
            stage.close();
        });

        stage.show();
    }

    void showDialog(String type, String message) {
        Stage myDialog = new Dialog(getStage(), type, message);
        myDialog.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
