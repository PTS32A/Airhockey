/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.airhockey.gui;

import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.Getter;
import s32a.airhockey.*;

/**
 *
 * @author Kargathia
 */
public class AirhockeyGUI extends Application
{

    @Getter
    private Stage stage;

    @Override
    public void start(Stage stage) throws Exception
    {
        Lobby.getSingle();

        this.stage = stage;
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Login.fxml"));

        stage.setOnCloseRequest(new EventHandler<WindowEvent>()
        {

            @Override
            public void handle(WindowEvent event)
            {
                Person person = Lobby.getSingle().getCurrentPerson();
                if (person != null)
                {
                    Lobby.getSingle().logOut(person);
                }
                Platform.exit();
                System.exit(0);
            }
        });

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        // populates lobby
        Thread thread = new Thread(() ->
        {
            Lobby.getSingle().populate();
        });
        thread.start();
        //goToLobby(stage);
    }

    /**
     *
     */
    void goToLogin(Stage stage) throws IOException
    {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Login.fxml"));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     *
     */
    public void goToRegister(Stage stage) throws IOException
    {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Register.fxml"));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     *
     */
    void goToLobby(Stage stage) throws IOException
    {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Lobby.fxml"));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     *
     */
    void goToGame(Stage stage) throws IOException
    {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Game.fxml"));

        Scene scene = new Scene(root);
        stage.setScene(scene);
//        stage.setResizable(false);
        stage.setMinHeight(root.minHeight(700));
        stage.setMinWidth(root.minWidth(1100));
        
        stage.widthProperty().addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldStageWidth, Number newStageWidth)
            {
                if(newStageWidth.doubleValue() < 1100)
                {
                    stage.setWidth(1100);
                }
            }
        });
        
        stage.show();
    }

    void showDialog(String type, String message)
    {
        Stage myDialog = new Dialog(getStage(), type, message);
        myDialog.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        launch(args);
    }

}
