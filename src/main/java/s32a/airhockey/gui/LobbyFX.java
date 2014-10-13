/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.airhockey.gui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

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

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {

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
