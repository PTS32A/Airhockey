/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Client.ClientData;

import com.badlogic.gdx.math.Vector2;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import s32a.Server.Lobby;
import s32a.Shared.*;
import s32a.Shared.enums.GameStatus;

/**
 *
 * @author Kargathia
 */
public class GameClient extends UnicastRemoteObject implements IGameClient, IGame {

    private IGame myGame;
    @Getter
    private List<IPlayer> myPlayers;
    @Getter
    private List<ISpectator> mySpectators;
    @Getter
    private ObservableList<String> oChat;
    @Getter
    private IntegerProperty roundNoProperty, player1Score, player2Score,
            player3Score;
    @Getter
    private StringProperty gameTimeProperty, difficultyProperty,
            player1NameProperty, player2NameProperty, player3NameProperty;
    @Getter
    private ObjectProperty<GameStatus> gameStatusProperty;
    @Getter
    private DoubleProperty puckXProperty, puckYProperty;
    @Getter
    private DoubleProperty player1XProperty, player1YProperty, player2XProperty,
            player2YProperty, player3XProperty, player3YProperty;

    public GameClient() throws RemoteException {
        this.myPlayers = new ArrayList<>();
        this.mySpectators = new ArrayList<>();
        this.oChat = FXCollections.observableArrayList(new ArrayList<String>());
        this.roundNoProperty = new SimpleIntegerProperty();
        this.puckXProperty = new SimpleDoubleProperty();
        this.puckYProperty = new SimpleDoubleProperty();

        this.player1NameProperty = new SimpleStringProperty("-");
        this.player2NameProperty = new SimpleStringProperty("-");
        this.player3NameProperty = new SimpleStringProperty("-");

        float width = 500;
        float x;
        float y;
        // Left corner of triangle
        double aX = -width / 2;
        double aY = 0;
        // Top corner of triangle
        double bX = 0;
        double bY = width * Math.sin(Math.toRadians(60));
        // Right corner of triangle
        double cX = width / 2;
        double cY = 0;

        Vector2 batPos2 = new Vector2((float) (aX + ((bX - aX) / 100 * 50)),
                (float) ((aY + ((bY - aY) / 100 * 50))));
        Vector2 batPos3 = new Vector2((float) (cX + ((bX - cX) / 100 * 50)),
                (float) ((cY + ((bY - cY) / 100 * 50))));

        this.player1XProperty = new SimpleDoubleProperty();
        this.player1YProperty = new SimpleDoubleProperty();
        this.player2XProperty = new SimpleDoubleProperty(batPos2.x);
        this.player2YProperty = new SimpleDoubleProperty(batPos2.y);
        this.player3XProperty = new SimpleDoubleProperty(batPos3.x);
        this.player3YProperty = new SimpleDoubleProperty(batPos3.y);
        this.player1Score = new SimpleIntegerProperty(20);
        this.player2Score = new SimpleIntegerProperty(20);
        this.player3Score = new SimpleIntegerProperty(20);
        this.gameStatusProperty = new SimpleObjectProperty(GameStatus.Waiting);
        this.difficultyProperty = new SimpleStringProperty("");
        this.gameTimeProperty = new SimpleStringProperty("");
    }

    /**
     * Sets game for client. Incoming from Server
     *
     * @param game
     */
    @Override
    public void setGame(IGame game) {
        this.myGame = game;
    }

    /**
     * Outgoing to server to start nextRound.
     *
     * @param input true or false for round change
     * @throws RemoteException
     */
    @Override
    public void setContinueRun(boolean input) throws RemoteException {
        myGame.setContinueRun(input);
    }

    /**
     * Outgoing to server, sending message in chatbox
     *
     * @param message
     * @param from
     * @return if message was sent without issue.
     * @throws RemoteException
     */
    @Override
    public boolean addChatMessage(String message, String from) throws RemoteException {
        return myGame.addChatMessage(message, from);
    }

    /**
     * Outgoing to server. Starts games
     *
     * @return if game was able to start.
     * @throws RemoteException
     */
    @Override
    public boolean beginGame() throws RemoteException {
        return myGame.beginGame();
    }

    /**
     * Outgoing to server. Sets custom puck speed.
     *
     * @param puckSpeed
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean adjustDifficulty(float puckSpeed) throws RemoteException {
        return myGame.adjustDifficulty(puckSpeed);
    }

    /**
     * Outgoing to server. Sets speed depending on player ratings.
     *
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean adjustDifficulty() throws RemoteException {
        return myGame.adjustDifficulty();
    }

    /**
     * Outgoing to server. Pauses or unpauses game.
     *
     * @param isPaused
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean pauseGame(boolean isPaused) throws RemoteException {
        return myGame.pauseGame(isPaused);
    }

    /**
     * Outgoing to server. Starts next round.
     *
     * @throws RemoteException
     */
    @Override
    public void startRound() throws RemoteException {
        this.myGame.startRound();
    }

    /**
     * Incoming from server. Sets round number.
     *
     * @param round
     * @throws RemoteException
     */
    @Override
    public void setRoundNo(int round) throws RemoteException {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                roundNoProperty.set(round);
            }
        });
    }

    /**
     * Incoming from server. Sets the list of players for this game.
     *
     * @param players
     * @throws RemoteException
     */
    @Override
    public void setPlayers(List<IPlayer> players) throws RemoteException {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                myPlayers = players;
                setPlayerNameProperties();
            }
        });
    }

    /**
     * Sets player names based on most recent list of players
     */
    private void setPlayerNameProperties() {
        try {
            if (myPlayers.size() > 0) {
                this.player1NameProperty.set(myPlayers.get(0).getName());
            }
            if (myPlayers.size() > 1) {

                this.player2NameProperty.set(myPlayers.get(1).getName());

            }
            if (myPlayers.size() > 2) {
                this.player3NameProperty.set(myPlayers.get(2).getName());
            }
        }
        catch (RemoteException ex) {
            System.out.println("RemoteException setting playerNameProperty");
        }
    }

    /**
     * Incoming from server. Sets new chat messages.
     *
     * @param chat
     * @throws RemoteException
     */
    @Override
    public void setChat(List<String> chat) throws RemoteException {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                oChat.setAll(chat);
            }
        });

    }

    /**
     * Incoming from server. Sets the spectators viewing this game.
     *
     * @param spectators
     * @throws RemoteException
     */
    @Override
    public void setSpectators(List<ISpectator> spectators) throws RemoteException {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                mySpectators = spectators;
            }
        });
    }

    /**
     * Incoming from server. Sets player 1's score.
     *
     * @param score
     * @throws RemoteException
     */
    @Override
    public void setPlayer1Score(int score) throws RemoteException {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                player1Score.set(score);
            }
        });
    }

    /**
     * Incoming from server. Sets player 2's score.
     *
     * @param score
     * @throws RemoteException
     */
    @Override
    public void setPlayer2Score(int score) throws RemoteException {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                player2Score.set(score);
            }
        });
    }

    /**
     * Incoming from server. Sets player 3's score.
     *
     * @param score
     * @throws RemoteException
     */
    @Override
    public void setPlayer3Score(int score) throws RemoteException {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                player3Score.set(score);
            }
        });
    }

    /**
     * Incoming from server. Sets puck's position.
     *
     * @param x
     * @param y
     * @throws RemoteException
     */
    @Override
    public void setPuck(double x, double y) throws RemoteException {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                puckXProperty.set(x);
                puckYProperty.set(y);
            }
        });
    }

    /**
     * Incoming from server. Sets player1's position.
     *
     * @param x
     * @param y
     * @throws RemoteException
     */
    @Override
    public void setPlayer1Bat(double x, double y) throws RemoteException {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                player1XProperty.set(x);
                player1YProperty.set(y);
            }
        });

    }

    /**
     * Incoming from server. Sets player2's position.
     *
     * @param x
     * @param y
     * @throws RemoteException
     */
    @Override
    public void setPlayer2Bat(double x, double y) throws RemoteException {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                player2XProperty.set(x);
                player2YProperty.set(y);
            }
        });

    }

    /**
     * Incoming from server. Sets player3's position.
     *
     * @param x
     * @param y
     * @throws RemoteException
     */
    @Override
    public void setPlayer3Bat(double x, double y) throws RemoteException {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                player3XProperty.set(x);
                player3YProperty.set(y);
            }
        });
    }

    /**
     * Incoming from server. Sets difficulty/ PuckSpeed.
     *
     * @param difficulty
     * @throws RemoteException
     */
    @Override
    public void setDifficulty(String difficulty) throws RemoteException {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                difficultyProperty.set(difficulty);
            }
        });
    }

    /**
     * Incoming from server. sets game status
     *
     * @param status
     * @throws RemoteException
     */
    @Override
    public void setStatus(GameStatus status) throws RemoteException {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                gameStatusProperty.set(status);
            }
        });
    }

    @Override
    public void setGameTime(String gameTime) throws RemoteException {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                gameTimeProperty.set(gameTime);
            }
        });
    }

    // ----------------------------------- Methods querying game info, used for up-to-date game display in lobby -------------------
    @Override
    public String getDifficulty() throws RemoteException {
        return this.myGame.getDifficulty();
    }

    @Override
    public String getPlayer1Name() throws RemoteException {
        return this.myGame.getPlayer1Name();
    }

    @Override
    public String getPlayer2Name() throws RemoteException {
        return this.myGame.getPlayer2Name();
    }

    @Override
    public String getPlayer3Name() throws RemoteException {
        return this.myGame.getPlayer3Name();
    }

    @Override
    public String getStatus() throws RemoteException {
        return this.myGame.getStatus();
    }

    @Override
    public String getID() throws RemoteException {
        return this.myGame.getID();
    }

}
