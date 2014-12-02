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
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
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
    private StringProperty gameTime;
    @Getter
    private FloatProperty puckSpeedProperty;
    @Getter
    private ObjectProperty<GameStatus> gameStatusProperty;
    @Getter
    private DoubleProperty puckXProperty, puckYProperty;
    @Getter
    private DoubleProperty player1XProperty, player1YProperty, player2XProperty,
            player2YProperty, player3XProperty, player3YProperty;

    public GameClient(IGame myGame) throws RemoteException {
        this.myGame = myGame;
        this.myPlayers = new ArrayList<>();
        this.mySpectators = new ArrayList<>();
        this.chat = new ArrayList<>();
        this.oChat = FXCollections.observableArrayList(chat);
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
    }

    @Override
    public void setContinueRun(boolean input) {
        myGame.setContinueRun(input);
    }

    @Override
    public boolean addChatMessage(String message, String from) {
        return myGame.addChatMessage(message, from);
    }

    @Override
    public boolean beginGame() {
        return myGame.beginGame();
    }

    @Override
    public boolean adjustDifficulty(float puckSpeed) {
        return myGame.adjustDifficulty(puckSpeed);
    }

    @Override
    public boolean adjustDifficulty() {
        return myGame.adjustDifficulty();
    }

    @Override
    public boolean pauseGame(boolean isPaused) {
        return myGame.pauseGame(isPaused);
    }

    @Override
    public void startRound() {
        myGame.startRound();
    }

    public ObservableList<String> getChat() {
        return oChat;
    }

    @Override
    public void setRoundNo(int round) {
        roundNoProperty.set(round);
    }

    @Override
    public void setPlayer(List<IPlayer> players) {
        myPlayers = players;
    }

    @Override
    public void setChat(List<String> chat) {
        this.chat = chat;
    }

    @Override
    public void setSpectators(List<ISpectator> spectators) {
        mySpectators = spectators;
    }

    @Override
    public void setPuckX(double x) {
        this.puckXProperty.set(x);
    }

    @Override
    public void setPuckY(double y) {
        this.puckYProperty.set(y);
    }

    @Override
    public void setPlayer1X(double x) {
        this.player1XProperty.set(x);
    }

    @Override
    public void setPlayer1Y(double y) {
        this.player1XProperty.set(y);
    }

    @Override
    public void setPlayer2X(double x) {
        this.player2XProperty.set(x);
    }

    @Override
    public void setPlayer2Y(double y) {
        this.player2XProperty.set(y);
    }

    @Override
    public void setPlayer3X(double x) {
        this.player3XProperty.set(x);
    }

    @Override
    public void setPlayer3Y(double y) {
       this.player3XProperty.set(y);
    }

    @Override
    public void setPlayer1Score(int score) {
        this.player1Score.set(score);
    }

    @Override
    public void setPlayer2Score(int score) {
        this.player2Score.set(score);
    }

    @Override
    public void setPlayer3Score(int score) {
        this.player3Score.set(score);
    }
}
