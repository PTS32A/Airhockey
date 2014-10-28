/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.airhockey.gui;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import static javafx.collections.FXCollections.observableArrayList;
import javafx.collections.ListChangeListener;
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
    ObservableList<String> playerInfo;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        try
        {
            highScores = FXCollections.observableArrayList(Lobby.getSingle().getRankings());
            messages = FXCollections.observableArrayList(Lobby.getSingle().getMychatbox().getChat());
            games = FXCollections.observableArrayList(Lobby.getSingle().getActiveGames());
        } 
        catch (SQLException ex)
        {
            super.showDialog("Error", "Unable to retrieve data: " + ex.getMessage());
        }
        finally
        {
            highScores = FXCollections.observableArrayList(new ArrayList<Person>());
            highScores.addListener(new ListChangeListener() 
            {
            @Override
                public void onChanged(ListChangeListener.Change change) 
                {
                    tvHighscores.setItems(highScores);
                }
            });
            messages = FXCollections.observableArrayList(new ArrayList<String>());
            messages.addListener(new ListChangeListener() 
            {
            @Override
                public void onChanged(ListChangeListener.Change change) 
                {
                    lvChatbox.setItems(messages); 
                }
            });
            games = FXCollections.observableArrayList(new ArrayList<Game>());
            games.addListener(new ListChangeListener() 
            {
            @Override
                public void onChanged(ListChangeListener.Change change) 
                {
                    tvGameDisplay.setItems(games); 
                }
            });
        }
        
        try
        {

            tcHSName.setCellValueFactory(new PropertyValueFactory<>("name"));
            tcHSRating.setCellValueFactory(new PropertyValueFactory<>("rating"));
            
            
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
            update();
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
        Person p = Lobby.getSingle().getCurrentPerson();
        playerInfo = FXCollections.observableArrayList("Name: " + p.getName(), "Rating: " + Double.toString(p.getRating()));
        lvPlayerInfo.setItems(playerInfo);
    }
    
    /**
     * starts a new game. opens game window in new window
     * @param evt 
     */
    public void newGame(Event evt)
    {
        try 
        {
            if (Lobby.getSingle().getCurrentPerson() instanceof Person)
            {
                Lobby.getSingle().startGame(Lobby.getSingle().getCurrentPerson());
                openNew(evt);
            }
            else
            {
                super.showDialog("Error", "You are currently spectating or playing a game");
            }
        } 
        catch (Exception ex) 
        {
            super.showDialog("Error", "Unable to open new game: " + ex.getMessage());
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
            if (Lobby.getSingle().getCurrentPerson() instanceof Person)
            {
                Lobby.getSingle().joinGame((Game)this.tvGameDisplay.getSelectionModel().getSelectedItem(), Lobby.getSingle().getCurrentPerson());
                openNew(evt);
            }
        } 
        catch (Exception ex) 
        {
            super.showDialog("Error", "Unable to join game: " + ex.getMessage());
        }
    }
    
    /**
     * spectates a game. opens in new window
     * @param evt 
     */
    public void spectateGame(Event evt)
    {
        if (Lobby.getSingle().getCurrentPerson() instanceof Player)
        {
            super.showDialog("Error", "You are playing a game and cant spectate at the same time");
            return;
        }
        Lobby.getSingle().spectateGame((Game)this.tvGameDisplay.getSelectionModel().getSelectedItem(), Lobby.getSingle().getCurrentPerson());
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
            Lobby.getSingle().logOut(Lobby.getSingle().getCurrentPerson());
            super.goToLogin(getThisStage());
        } 
        catch (IOException ex) 
        {
            super.showDialog("Error", "Unable to log out: " + ex.getMessage());
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
    
    //Opening new game window
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
                } 
                catch (IOException ex)
                {
                    Logger.getLogger(LoginFX.class.getName()).log(Level.SEVERE, null, ex);
                }          
            }
        });
    }
}
