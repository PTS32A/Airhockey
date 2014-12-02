/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Client.ClientData;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import s32a.Shared.IGame;
import s32a.Shared.ILobby;
import s32a.Shared.ILobbyClient;
import s32a.Shared.IPerson;
import s32a.Shared.IPlayer;

/**
 *
 * @author Kargathia
 */
public class LobbyClient extends UnicastRemoteObject implements ILobbyClient, ILobby {

    private List<IGame> activeGames;
    @Getter
    ObservableList<IGame> oActiveGames;
    @Getter
    ObservableList<String> chatProperty;
    private List<String> chat;
    @Getter
    private DoubleProperty playerRatingProperty;
    
    private ILobby myLobby;
    

    public LobbyClient(ILobby myLobby) throws RemoteException {
        this.myLobby = myLobby;
        this.activeGames = new ArrayList<>();
        this.oActiveGames = FXCollections.observableList(activeGames);
        this.chat = new ArrayList<>();
        this.chatProperty = FXCollections.observableList(chat);
        this.playerRatingProperty = new SimpleDoubleProperty(0);
    }

    @Override
    public IPerson getMyPerson(String playerName) {
        return myLobby.getMyPerson(playerName);
    }

    @Override
    public HashMap getAirhockeySettings() {
        return myLobby.getAirhockeySettings();
    }

    @Override
    public HashMap<String, IPerson> getActivePersons() {
        return myLobby.getActivePersons();
    }

    @Override
    public List<IGame> getActiveGames() {
        return myLobby.getActiveGames();
    }

    @Override
    public boolean addPerson(String playerName, String passWord) throws IllegalArgumentException, SQLException {
        return myLobby.addPerson(playerName, passWord);
    }

    @Override
    public boolean checkLogin(String playerName, String password) throws IllegalArgumentException, SQLException {
        return myLobby.checkLogin(playerName, password);
    }

    @Override
    public boolean logOut(IPerson input) {
        return myLobby.logOut(input);
    }

    @Override
    public IGame startGame(IPerson person) {
        return myLobby.startGame(person);
    }

    @Override
    public IGame joinGame(IGame game, IPerson person) {
        return myLobby.joinGame(game, person);
    }

    @Override
    public IGame spectateGame(IGame gameInput, IPerson personInput) {
        return myLobby.spectateGame(gameInput, personInput);
    }

    @Override
    public boolean addChatMessage(String message, String from) throws IllegalArgumentException {
        return myLobby.addChatMessage(message, from);
    }

    @Override
    public boolean endGame(IGame game, IPlayer hasLeft) {
        return myLobby.endGame(game, hasLeft);
    }

    @Override
    public void stopSpectating(IGame game, IPerson spectator) {
        myLobby.stopSpectating(game, spectator);
    }

    @Override
    public IGame getMyGame(String gameID) {
        return myLobby.getMyGame(gameID);
    }

    @Override
    public List<IPerson> getRankings() throws SQLException {
        return myLobby.getRankings();
    }

    @Override
    public void populate() {
        myLobby.populate();
    }

    @Override
    public void setActiveGames(List<IGame> activeGames) {
        this.oActiveGames.clear();
        this.oActiveGames.addAll(activeGames);
    }


    @Override
    public void setMyLobby(ILobby myLobby) {
        this.myLobby = myLobby;
    }

    @Override
    public void setChat(List<String> chat) {
        this.chatProperty.clear();
        this.chatProperty.addAll(chat);
    }

    @Override
    public void setRating(Double rating) {
        this.playerRatingProperty.set(rating);
    }

}
