/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Client.ClientData;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
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
    private List<String> chat;
    private ObservableList<String> oChat;
    @Getter
    private IntegerProperty roundNoProperty, player1Score, player2Score,
            player3Score;
    @Getter
    private StringProperty gameTime, difficulty;
    @Getter
    private FloatProperty puckSpeedProperty;
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
        this.chat = new ArrayList<>();
        this.oChat = FXCollections.observableArrayList(chat);
        // TODO: replace sets with "new Simple<TYPE>Property(<initial value>)"
        this.roundNoProperty.set(0);
        this.puckSpeedProperty.set(0);
        this.puckXProperty.set(0);
        this.puckYProperty.set(0);
        this.player1XProperty.set(0);
        this.player1YProperty.set(0);
        this.player2XProperty.set(0);
        this.player2YProperty.set(0);
        this.player3XProperty.set(0);
        this.player3YProperty.set(0);
        this.gameStatusProperty.set(GameStatus.Waiting);
        this.difficulty = new SimpleStringProperty("");
    }

    @Override
    public void setGame(IGame game){
        this.myGame = game;
    }

    @Override
    public void setContinueRun(boolean input) throws RemoteException {
        myGame.setContinueRun(input);
    }

    @Override
    public boolean addChatMessage(String message, String from) throws RemoteException {
        return myGame.addChatMessage(message, from);
    }

    @Override
    public boolean beginGame() throws RemoteException {
        return myGame.beginGame();
    }

    @Override
    public boolean adjustDifficulty(float puckSpeed) throws RemoteException {
        return myGame.adjustDifficulty(puckSpeed);
    }

    @Override
    public boolean adjustDifficulty() throws RemoteException {
        return myGame.adjustDifficulty();
    }

    @Override
    public boolean pauseGame(boolean isPaused) throws RemoteException {
        return myGame.pauseGame(isPaused);
    }

    @Override
    public void startRound() throws RemoteException {
        this.myGame.startRound();
    }

    public ObservableList<String> getChat() {
        return oChat;
    }

    @Override
    public void setRoundNo(int round) throws RemoteException {
        this.roundNoProperty.set(round);
    }

    @Override
    public void setPlayer(List<IPlayer> players) throws RemoteException {
        this.myPlayers = players;
    }

    @Override
    public void setChat(List<String> chat) throws RemoteException {
        this.chat = chat;
    }

    @Override
    public void setSpectators(List<ISpectator> spectators) throws RemoteException {
        mySpectators = spectators;
    }

    @Override
    public void setPlayer1Score(int score) throws RemoteException {
        this.player1Score.set(score);
    }

    @Override
    public void setPlayer2Score(int score) throws RemoteException {
        this.player2Score.set(score);
    }

    @Override
    public void setPlayer3Score(int score) throws RemoteException {
        this.player3Score.set(score);
    }

    @Override
    public void setPuck(double x, double y) throws RemoteException {
        this.puckXProperty.set(x);
        this.puckYProperty.set(y);
    }

    @Override
    public void setPlayer1Bat(double x, double y) throws RemoteException {
        this.player1XProperty.set(x);
        this.player1YProperty.set(y);
    }

    @Override
    public void setPlayer2Bat(double x, double y) throws RemoteException {
        this.player2XProperty.set(x);
        this.player2YProperty.set(y);
    }

    @Override
    public void setPlayer3Bat(double x, double y) throws RemoteException {
        this.player3XProperty.set(x);
        this.player3YProperty.set(y);
    }

    @Override
    public void setDifficulty(String difficulty) throws RemoteException {
        this.difficulty.set(difficulty);
    }
}
