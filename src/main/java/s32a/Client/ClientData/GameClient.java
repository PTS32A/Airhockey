/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Client.ClientData;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import s32a.Shared.*;
import s32a.Shared.enums.GameStatus;

/**
 *
 * @author Kargathia
 */
public class GameClient extends UnicastRemoteObject implements IGameClient, IGame{

    private IGame myGame;
    

    public GameClient() throws RemoteException{

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

    @Override
    public List<IPlayer> getMyPlayers() {
        return this.myGame.getMyPlayers();
    }

    @Override
    public IntegerProperty getRoundNo() {
        return myGame.getRoundNo();
    }
    
    @Override
    public ObservableList<String> getChatProperty(){
        return myGame.getChatProperty();
    }

    @Override
    public void setChatProperty(ObservableList<String> messages) {
        myGame.setChatProperty(messages);
    }

    @Override
    public IntegerProperty setRoundNo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public StringProperty getGameTime() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public FloatProperty getPuckSpeed() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public DoubleProperty getPuckXPos() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public DoubleProperty getPuckYPos() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public GameStatus getStatusProp() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
