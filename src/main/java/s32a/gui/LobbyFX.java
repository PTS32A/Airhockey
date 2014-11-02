/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import s32a.airhockey.*;
import s32a.timers.LobbyTimer;

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

    private ObservableList<Person> highScores;
    private ObservableList<String> messages;
    private ObservableList<Game> games;

    private AnimationTimer lobbyTimer;

    /**
     * chucks a given list of persons into the observableList
     *
     * @param input
     */
    public void setHighScores(List<Person> input)
    {
        Platform.runLater(() ->
        {
            this.highScores.setAll(input);
        });
    }

    /**
     * chucks a given list of games into the observablelist
     *
     * @param input
     */
    public void setGames(List<Game> input)
    {
        Platform.runLater(() ->
        {
            this.games.setAll(input);
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        tfChatbox.setOnKeyPressed((KeyEvent ke) ->
        {
            if (ke.getCode() == KeyCode.ENTER)
            {
                sendChatMessage(null);
            }
        });

        highScores = FXCollections.observableArrayList(new ArrayList<Person>());
        messages = FXCollections.observableArrayList(new ArrayList<String>());
        games = FXCollections.observableArrayList(new ArrayList<Game>());
        ObservableList<Property> playerInfo;

        try
        {
            this.lvChatbox.setItems(Lobby.getSingle().getMychatbox().chatProperty());
            this.tvHighscores.setItems(highScores);
            this.tvGameDisplay.setItems(games);

            // sets valuefactories high scores
            this.tcHSName.setCellValueFactory(new PropertyValueFactory<>("name"));
            this.tcHSRating.setCellValueFactory(new PropertyValueFactory<>("rating"));

            // sets valuefactories for game display
            this.tcGDDifficulty.setCellValueFactory(new PropertyValueFactory<>("difficulty"));
            this.tcGDStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
            this.tcGDPlayer1.setCellValueFactory(new PropertyValueFactory<>("player1Name"));
            this.tcGDPlayer2.setCellValueFactory(new PropertyValueFactory<>("player2Name"));
            this.tcGDPlayer3.setCellValueFactory(new PropertyValueFactory<>("player3Name"));

            // binds lists for game and high score display
            this.tvGameDisplay.setItems(games);
            this.tvHighscores.setItems(highScores);

            this.updatePlayerInfo();
        } catch (Exception ex)
        {
            super.showDialog("Error", "Unable to initialise Lobby: " + ex.getMessage());
        }

        this.lobbyTimer = new LobbyTimer(this, 5000);
        this.lobbyTimer.start();
    }

    /**
     * updates relevant screens in display
     */
    public void updatePlayerInfo()
    {
        Person p = Lobby.getSingle().getCurrentPerson();
        if (p != null)
        {
            lvPlayerInfo.setItems(FXCollections.observableArrayList("Name: "
                    + p.nameProperty().get(), "Rating: " + Double.toString(p.ratingProperty().get())));
        }
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
                    openNewGameWindow(evt);
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
                    if (Lobby.getSingle().joinGame(
                            (Game) this.tvGameDisplay.getSelectionModel().getSelectedItem(),
                            Lobby.getSingle().getCurrentPerson()) != null)
                    {
                        openNewGameWindow(evt);
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
            super.showDialog("Error",
                    "You are playing a game and can't spectate at the same time");
            return;
        }

        if (this.tvGameDisplay.getSelectionModel().getSelectedItem() != null)
        {
            Game game = (Game) this.tvGameDisplay.getSelectionModel().getSelectedItem();
            if (game.isGameOver() || game.getRoundNo().get() == 0)
            {
                super.showDialog("Error", "Unable to watch inactive games");
            } else if (Lobby.getSingle().spectateGame(game,
                    Lobby.getSingle().getCurrentPerson()) != null)
            {
                openNewGameWindow(evt);
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
            this.lobbyTimer.stop();
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

    /**
     * Opens new game window
     *
     * @param evt
     */
    public void openNewGameWindow(Event evt)
    {
        final AirhockeyGUI base = this;
        javafx.application.Platform.runLater(() ->
        {
            try
            {
                Stage stage1 = new Stage();
                base.goToGame(stage1);
            } catch (IOException ex)
            {
                base.showDialog("Error", "Could not open game: " + ex.getMessage());
            }
        });
    }
}
