/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.airhockey.gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import s32a.airhockey.*;

/**
 *
 * @author Kargathia
 */
public class LobbyFX extends AirhockeyGUI implements Initializable
{
    @FXML TableView tvHighscores;
    @FXML TableColumn tcHSName, tcHSRating;
    
    @FXML ListView lvChatbox;
    @FXML TextField tfChatbox;
    
    @FXML TableView tvGameDisplay;
    @FXML TableColumn tcGDDifficulty, tcGDPlayer1, 
            tcGDPlayer2, tcGDPlayer3, tcGDStatus;
    
    @FXML ListView lvPlayerInfo;
    ObservableList<Person> highScores;
    ObservableList<String> messages;
    ObservableList<Game> games;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        try
        {
            highScores = FXCollections.observableArrayList(Lobby.getSingle().getRankings());
            messages = FXCollections.observableArrayList(Lobby.getSingle().getMychatbox().getChat());
            games = FXCollections.observableArrayList(Lobby.getSingle().getActiveGames());

            tcHSName.setCellValueFactory(new PropertyValueFactory<Person,String>("name"));
            tcHSRating.setCellValueFactory(new PropertyValueFactory<Person,String>("rating"));
            
            
            //Need to figure out best way to do this
            //tcGDDifficulty.setCellValueFactory(new PropertyValueFactory<Game,Puck>("myPuck"));
            //tcGDPlayer1.setCellValueFactory(new PropertyValueFactory<Game,List>("myPlayers"));
            //tcGDPlayer2.setCellValueFactory(new PropertyValueFactory<Game,List>("myPlayers"));
            //tcGDPlayer3.setCellValueFactory(new PropertyValueFactory<Game,List>("myPlayers"));
            //tcGDStatus.setCellValueFactory(new PropertyValueFactory<Game,Boolean>("isPaused"));
            if (games != null) 
            {
              tvGameDisplay.setItems(games);  
            }
            if (highScores != null) 
            {
              tvHighscores.setItems(highScores); 
            }
            if (messages != null) 
            {
              lvChatbox.setItems(messages); 
            }
        }
        catch(Exception ex)
        {
            super.showDialog("Error", "Unable to open Lobby: " + ex.getMessage());
        }
    }
    
    /**
     * updates relevant screens in display
     */
    private void update()
    {
        
    }
    
    /**
     * starts a new game. opens game window in new window
     * @param evt 
     */
    public void newGame(Event evt)
    {
        try 
        {
            openNew(evt);
        } 
        catch (Exception ex) 
        {
            Logger.getLogger(LoginFX.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * joins an already existing game. opens game in new window
     * @param evt 
     */
    public void joinGame(Event evt)
    {
        try 
        {
            super.goToGame(getThisStage());
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(LoginFX.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * spectates a game. opens in new window
     * @param evt 
     */
    public void spectateGame(Event evt)
    {
        openNew(evt);
    }
    
    /**
     * logs out current user, ends his active game
     * @param evt 
     */
    public void logOut(Event evt)
    {
        try 
        {
            super.goToLogin(getThisStage());
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(RegisterFX.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * sends a lobby chat message
     * @param evt 
     */
    public void sendChatMessage(Event evt)
    {
        Lobby l = Lobby.getSingle();
        l.addChatMessage(tfChatbox.getText(), l.getCurrentPerson());
    }
    
    private Stage getThisStage() 
    {
        return (Stage) tfChatbox.getScene().getWindow();
    }
    
    // template code for opening an additional window, in this case showing Game
    // for merely switching windows, base.goTo<Something>() should be called
    public void openNew(Event evt)
    {
        final AirhockeyGUI base = this;        
        javafx.application.Platform.runLater(new Runnable() 
        {
            @Override
            public void run() 
            {           
                try
                {
                    Stage stage = new Stage();
                    base.goToGame(stage);
                    stage.show();
                } 
                catch (IOException ex)
                {
                    Logger.getLogger(LoginFX.class.getName()).log(Level.SEVERE, null, ex);
                }          
            }
        });
    }
}
