/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Client.GUI;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import s32a.Client.ClientData.GameClient;
import s32a.Shared.IGame;
import s32a.Shared.IPerson;
import s32a.Shared.IPlayer;

/**
 *
 * @author Kargathia
 */
public class LobbyFX extends AirhockeyGUI implements Initializable {

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tfChatbox.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode() == KeyCode.ENTER) {
                    sendChatMessage(null);
                }
            }
        });

        try {
            this.lvChatbox.setItems(lobby.getOChatList());
            this.tvHighscores.setItems(lobby.getORankingsList());
            this.tvGameDisplay.setItems(lobby.getOActiveGamesList());

            // sets valuefactories high scores
            this.tcHSName.setCellValueFactory(new PropertyValueFactory<>("name"));
            this.tcHSRating.setCellValueFactory(new PropertyValueFactory<>("rating"));

            // sets valuefactories for game display
            this.tcGDDifficulty.setCellValueFactory(new PropertyValueFactory<>("difficulty"));
            this.tcGDStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
            this.tcGDPlayer1.setCellValueFactory(new PropertyValueFactory<>("player1Name"));
            this.tcGDPlayer2.setCellValueFactory(new PropertyValueFactory<>("player2Name"));
            this.tcGDPlayer3.setCellValueFactory(new PropertyValueFactory<>("player3Name"));

            this.updatePlayerInfo();
        }
        catch (Exception ex) {
            super.showDialog("Error", "Unable to initialise Lobby: " + ex.getMessage());
        }
    }

    /**
     * updates relevant screens in display
     *
     * @param pR
     */
    public void updatePlayerInfo() {
        IPerson p = super.getMe();
        if (p != null) {
            lvPlayerInfo.setItems(lobby.getPlayerInfo());
        }
    }

    /**
     * starts a new game. opens game window in new window
     *
     * @param evt
     */
    public void newGame(Event evt) {
        try {
            IPerson p = super.getMe();
            if (p == null) {
                super.showDialog("Error", "You are not logged in");
                return;
            }
            if (p instanceof IPerson) {
                GameClient client = new GameClient();
                IGame game = (IGame) lobby.startGame(p.getName(), client);
                if (game != null) {
                    openNewGameWindow(client);
                } else {
                    super.showDialog("Error", "Unable to create a new Game: NullPointer at game");
                }
            } else {
                super.showDialog("Error", "You are currently spectating or playing a game");
            }
        }
        catch (RemoteException ex) {
            super.showDialog("Error", "Unable to open new game due to RemoteException: " + ex.getMessage());
        }
    }

    /**
     * joins an already existing game. opens game in new window
     *
     * @param evt
     */
    public void joinGame(Event evt) {
        try {
            IPerson p = super.getMe();
            if (p instanceof IPerson) {
                if (this.tvGameDisplay.getSelectionModel().getSelectedItem() != null) {
                    GameClient client = new GameClient();
                    IGame game = (IGame) this.tvGameDisplay.getSelectionModel().getSelectedItem();
                    if (lobby.joinGame(
                            game.getID(),
                            p.getName(), client) != null) {
                        openNewGameWindow(client);
                    } else {
                        super.showDialog("Error", "Unable to create a new Game: NullPointer at game");
                    }
                }
            }
        }
        catch (Exception ex) {
            super.showDialog("Error", "Unable to join game: " + ex.getMessage());
        }
    }

    /**
     * spectates a game. opens in new window
     *
     * @param evt
     */
    public void spectateGame(Event evt) {
        IPerson p = super.getMe();
        if (p instanceof IPlayer) {
            super.showDialog("Error",
                    "You are playing a game and can't spectate at the same time");
            return;
        }

        if (this.tvGameDisplay.getSelectionModel().getSelectedItem() != null) {
            IGame game = (IGame) this.tvGameDisplay.getSelectionModel().getSelectedItem();

            try {
                GameClient client = new GameClient();
                lobby.spectateGame(game.getID(), super.getMe().getName(), client);
                openNewGameWindow(client);
            }
            catch (IllegalArgumentException ex) {
                super.showDialog("Error", ex.getMessage());
            }
            catch (RemoteException ex) {
                System.out.println("RemoteException on trying to spectate game: " + ex.getMessage());
                Logger.getLogger(LobbyFX.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void showControls(Event evt) {
        super.showDialog("Controls", "Use the left and right arrow keys or"
                + " the A and D keys to move your bat.");
    }

    /**
     * logs out current user, ends his active game
     *
     * @param evt
     */
    public void logOut(Event evt) {
        try {
            lobby.logOut(super.getMe().getName());
            super.goToLogin(getThisStage());
        }
        catch (IOException ex) {
            super.showDialog("Error", "Unable to log out: " + ex.getMessage());
        }
    }

    /**
     * sends a lobby chat message
     *
     * @param evt
     */
    public void sendChatMessage(Event evt) {
        if (!tfChatbox.getText().equals("")) {
            try {
                lobby.addChatMessage(tfChatbox.getText(), me);
                tfChatbox.setText("");
            }
            catch (IllegalArgumentException ex) {
                System.out.println("IllegalArgumentException on sendChatMessage: " + ex.getMessage());
                super.showDialog("Error", "Unable to send message: " + ex.getMessage());
                Logger.getLogger(LobbyFX.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (RemoteException ex) {
                System.out.println("RemoteException on sendChatMessage: " + ex.getMessage());
                Logger.getLogger(LobbyFX.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private Stage getThisStage() {
        return (Stage) tfChatbox.getScene().getWindow();
    }

    /**
     * Opens new game window
     *
     * @param client
     */
    public void openNewGameWindow(GameClient client) {
        final AirhockeyGUI base = this;
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                try {
                    Stage stage1 = new Stage();
                    base.goToGame(stage1, client);
                }
                catch (IOException ex) {
                    base.showDialog("Error", "Could not open game: " + ex.getMessage());
                }
            }
        });

    }
}