/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.airhockey.gui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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
        highScores = FXCollections.observableArrayList(Lobby.getSingle().getRankings());
        messages = FXCollections.observableArrayList(Lobby.getSingle().getMychatbox().getChat());
        games = FXCollections.observableArrayList(Lobby.getSingle().getActiveGames());
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
        
    }
    
    /**
     * joins an already existing game. opens game in new window
     * @param evt 
     */
    public void joinGame(Event evt)
    {
        
    }
    
    /**
     * spectates a game. opens in new window
     * @param evt 
     */
    public void spectateGame(Event evt)
    {
        
    }
    
    /**
     * logs out current user, ends his active game
     * @param evt 
     */
    public void logOut(Event evt)
    {
        
    }
    
    /**
     * sends a lobby chat message
     * @param evt 
     */
    public void sendChatMessage(Event evt)
    {
        
    }
}
