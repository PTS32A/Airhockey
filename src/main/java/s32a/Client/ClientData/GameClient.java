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
import javafx.beans.property.ObjectProperty;
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
    private List<IPlayer> myPlayers;
    private List<String> chat;
    private ObservableList<String> oChat;
    private IntegerProperty roundNo;
    

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

   
    public List<IPlayer> getMyPlayers() {
        return this.myGame.getMyPlayers();
    }

    
    public IntegerProperty getRoundNo() {
        return myGame.getRoundNo();
    }
    
    
    public ObservableList<String> getChatProperty(){
        return myGame.getChatProperty();
    }

    
    public IntegerProperty setRoundNo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    public StringProperty getGameTime() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    public FloatProperty getPuckSpeed() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    public DoubleProperty getPuckXPos() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    public DoubleProperty getPuckYPos() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    public ObjectProperty<GameStatus> getStatusProp() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setPlayer(List<IPlayer> players) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setSpectators(List<ISpectator> spectators) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setRoundNo(Integer roundNo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setChat(List<String> chat) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
