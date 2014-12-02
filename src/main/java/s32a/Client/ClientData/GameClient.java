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
import javafx.collections.ObservableList;
import lombok.Getter;
import s32a.Shared.*;
import s32a.Shared.enums.GameStatus;

/**
 *
 * @author Kargathia
 */
public class GameClient extends UnicastRemoteObject implements IGameClient, IGame{

    private IGame myGame;
    @Getter
    private List<IPlayer> myPlayers;
    @Getter
    private List<ISpectator> mySpectators;
    private List<String> chat;
    private ObservableList<String> oChat;
    @Getter
    private IntegerProperty roundNoProperty;
    private StringProperty gameTime;
    @Getter
    private FloatProperty puckSpeedProperty;
    @Getter
    private ObjectProperty<GameStatus> gameStatusProperty;
    @Getter
    private DoubleProperty puckXProperty, puckYProperty;
    

    public GameClient() throws RemoteException{
        this.myGame = null;
        this.myPlayers = new ArrayList<>();
        this.mySpectators = new ArrayList<>();
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
    
    public ObservableList<String> getChat(){
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
}
