/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.airhockey.gui;

import java.io.IOException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lombok.Getter;

/**
 *
 * @author Kargathia
 */
public class AirhockeyGUI extends Application
{
    @Getter private Stage stage;
    
    @Override
    public void start(Stage stage) throws Exception
    {
        this.stage = stage;
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Login.fxml"));
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
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
        stage.show();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        launch(args);
    }
    
}
