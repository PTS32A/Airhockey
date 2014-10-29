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
import javafx.animation.AnimationTimer;
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
import timers.LobbyTimer;

/**
 *
 * @author Kargathia
 */
public class LobbyFX extends AirhockeyGUI implements Initializable
{

    @FXML
    TableView tvHighscores;
    @FXML
    TableColumn tcHSName, tcHSRating;

    @FXML
    ListView lvChatbox;
    @FXML
    TextField tfChatbox;

    @FXML
    TableView tvGameDisplay;
    @FXML
    TableColumn tcGDDifficulty, tcGDPlayer1,
            tcGDPlayer2, tcGDPlayer3, tcGDStatus;

    @FXML
    ListView lvPlayerInfo;
    
    ObservableList<Person> highScores;
    ObservableList<String> messages;
    ObservableList<Game> games;
    ObservableList<String> playerInfo;
    
    private AnimationTimer lobbyTimer;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {    
        try
        {
            highScores = FXCollections.observableArrayList(Lobby.getSingle().getRankings());
            messages = FXCollections.observableArrayList(Lobby.getSingle().getMychatbox().getChat());
            games = FXCollections.observableArrayList(Lobby.getSingle().getActiveGames());
        } catch (SQLException ex)
        {
            super.showDialog("Error", "Unable to retrieve data: " + ex.getMessage());
        } 
        
        

        try
        {

            tcHSName.setCellValueFactory(new PropertyValueFactory<>("name"));
            tcHSRating.setCellValueFactory(new PropertyValueFactory<>("rating"));
            
//            this.tcGDPlayer1.setCellValueFactory(
//            new PropertyValueFactory<Person,String>("lastName");
            
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
        } catch (Exception ex)
        {
            super.showDialog("Error", "Unable to open Lobby: " + ex.getMessage());
        }
        
        this.lobbyTimer = new LobbyTimer(this, 3000);
        this.lobbyTimer.start();
    }

    /**
     * updates relevant screens in display
     */
    private void update()
    {
        Person p = Lobby.getSingle().getCurrentPerson();
        playerInfo = FXCollections.observableArrayList("Name: " 
                + p.getName(), "Rating: " + Double.toString(p.getRating()));
        lvPlayerInfo.setItems(playerInfo);
        
    }

    /**
     * starts a new game. opens game window in new window
     *
     * @param evt
     */
    public void newGame(Event evt)
    {
        try
        {
            if (Lobby.getSingle().getCurrentPerson() instanceof Person)
            {
                if (Lobby.getSingle().startGame(Lobby.getSingle().getCurrentPerson()) != null)
                {
                    openNew(evt);
                } else
                {
                    super.showDialog("Error", "Unable to create a new Game: NullPointer at game");
                }
            } else
            {
                super.showDialog("Error", "You are currently spectating or playing a game");
            }
        } catch (Exception ex)
        {
            super.showDialog("Error", "Unable to open new game: " + ex.getMessage());
        }
    }

    /**
     * joins an already existing game. opens game in new window
     *
     * @param evt
     */
    public void joinGame(Event evt)
    {
        try
        {
            if (Lobby.getSingle().getCurrentPerson() instanceof Person)
            {
                if (this.tvGameDisplay.getSelectionModel().getSelectedItem() != null)
                {
                    if (Lobby.getSingle().joinGame((Game) this.tvGameDisplay.getSelectionModel().getSelectedItem(), Lobby.getSingle().getCurrentPerson()) != null)
                    {
                        openNew(evt);
                    } else
                    {
                        super.showDialog("Error", "Unable to create a new Game: NullPointer at game");
                    }
                }
            }
        } catch (Exception ex)
        {
            super.showDialog("Error", "Unable to join game: " + ex.getMessage());
        }
    }

    /**
     * spectates a game. opens in new window
     *
     * @param evt
     */
    public void spectateGame(Event evt)
    {
        if (Lobby.getSingle().getCurrentPerson() instanceof Player)
        {
            super.showDialog("Error", "You are playing a game and can't spectate at the same time");
            return;
        }

        if (this.tvGameDisplay.getSelectionModel().getSelectedItem() != null)
        {
            if (Lobby.getSingle().spectateGame((Game) this.tvGameDisplay.getSelectionModel().getSelectedItem(), Lobby.getSingle().getCurrentPerson()) != null)
            {
                openNew(evt);
            } else
            {
                super.showDialog("Error", "Unable to create a new Game: NullPointer at game");
            }
        }
    }

    /**
     * logs out current user, ends his active game
     *
     * @param evt
     */
    public void logOut(Event evt)
    {
        try
        {
            Lobby.getSingle().logOut(Lobby.getSingle().getCurrentPerson());
            super.goToLogin(getThisStage());
        } catch (IOException ex)
        {
            super.showDialog("Error", "Unable to log out: " + ex.getMessage());
        }
    }

    /**
     * sends a lobby chat message
     *
     * @param evt
     */
    public void sendChatMessage(Event evt)
    {
        Lobby l = Lobby.getSingle();
        if (!tfChatbox.getText().equals(""))
        {
            l.addChatMessage(tfChatbox.getText(), l.getCurrentPerson());

            lvChatbox.setItems(FXCollections.observableArrayList(l.getMychatbox().getChat()));
            tfChatbox.setText("");
        }
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
                } catch (IOException ex)
                {
                    Logger.getLogger(LoginFX.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    /**
     * Updates the highscores view
     *
     * @param plist
     */
    public void setHighscore(List<Person> plist)
    {
        
    }

    /**
     * Updates the active games view
     *
     * @param glist
     */
    public void setActiveGames(List<Game> glist)
    {
        tvGameDisplay.setItems(FXCollections.observableArrayList(glist));
    }

    /**
     * Updates the chatbox
     *
     * @param chatMessages
     */
    public void setChatMessages(List<String> chatMessages)
    {
        lvChatbox.setItems(FXCollections.observableArrayList(chatMessages));
    }
}
