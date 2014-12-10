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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import s32a.Shared.IGame;
import s32a.Shared.IGameClient;
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
    private ObservableList<IGame> oActiveGames;
    @Getter
    private ObservableList<String> chatProperty;
    private List<String> chat;
//    private ObjectProperty<List<IPerson>> rankings;
    @Getter
    private ObservableList<IPerson> rankings;
    @Getter
    private DoubleProperty playerRatingProperty;
    
    private ILobby myLobby;
    private ObjectProperty<HashMap<String, IPerson>> activePersons;
    private ObjectProperty<HashMap<String, Object>> settingsProperty;
    

    public LobbyClient(ILobby myLobby) throws RemoteException {
        this.myLobby = myLobby;
        this.activeGames = new ArrayList<>();
//        this.rankings = new SimpleObjectProperty(new ArrayList<>());
        this.rankings = FXCollections.observableArrayList(new ArrayList<IPerson>());
        this.activePersons = new SimpleObjectProperty(new HashMap<>());
        this.settingsProperty = new SimpleObjectProperty(new HashMap<>());
        this.oActiveGames = FXCollections.observableList(activeGames);
        this.chat = new ArrayList<>();
        this.chatProperty = FXCollections.observableList(chat);
        this.playerRatingProperty = new SimpleDoubleProperty(0);
    }
    
    public IPerson getMyPerson(String playerName) throws RemoteException {
        return this.activePersons.get().get(playerName);
    }

    @Override
    public HashMap getAirhockeySettings() throws RemoteException {
        return this.settingsProperty.get();
    }

    @Override
    public HashMap<String, IPerson> getActivePersons() throws RemoteException {
        return this.activePersons.get();
    }

    @Override
    public List<IGame> getActiveGames() throws RemoteException {
        return this.activeGames;
    }

    @Override
    public boolean addPerson(String playerName, String passWord) 
            throws IllegalArgumentException, SQLException, RemoteException {
        return myLobby.addPerson(playerName, passWord);
    }

    @Override
    public boolean checkLogin(String playerName, String password, ILobbyClient client)
            throws IllegalArgumentException, SQLException, RemoteException {
        return myLobby.checkLogin(playerName, password, client);
    }

    @Override
    public boolean logOut(IPerson input) throws RemoteException {
        return myLobby.logOut(input);
    }

    @Override
    public IGame startGame(IPerson person, IGameClient client) throws RemoteException {
        return myLobby.startGame(person, client);
    }

    @Override
    public IGame joinGame(IGame game, IPerson person, IGameClient client) throws RemoteException {
        return myLobby.joinGame(game, person, client);
    }

    @Override
    public IGame spectateGame(IGame gameInput, IPerson personInput, IGameClient client)
            throws RemoteException {
        return myLobby.spectateGame(gameInput, personInput, client);
    }

    @Override
    public boolean addChatMessage(String message, String from) throws IllegalArgumentException, RemoteException {
        return myLobby.addChatMessage(message, from);
    }

    @Override
    public boolean endGame(IGame game, IPlayer hasLeft) throws RemoteException {
        return myLobby.endGame(game, hasLeft);
    }

    @Override
    public void stopSpectating(IGame game, IPerson spectator) throws RemoteException {
        myLobby.stopSpectating(game, spectator);
    }

    @Override
    public IGame getMyGame(String gameID) throws RemoteException {
        return myLobby.getMyGame(gameID);
    }

    @Override
    public void populate() throws RemoteException {
        myLobby.populate();
    }

    @Override
    public void setActiveGames(List<IGame> activeGames) throws RemoteException {
        this.oActiveGames.clear();
        this.oActiveGames.addAll(activeGames);
    }


    @Override
    public void setMyLobby(ILobby myLobby) throws RemoteException {
        this.myLobby = myLobby;
    }

    @Override
    public void setChat(List<String> chat) throws RemoteException {
        this.chatProperty.clear();
        this.chatProperty.addAll(chat);
    }

    @Override
    public void setRating(Double rating) throws RemoteException {
        this.playerRatingProperty.set(rating);
    }

    @Override
    public void setSettings(HashMap<String, Object> settings) throws RemoteException {
        this.settingsProperty.set(settings);
    }

    @Override
    public void setPersons(HashMap<String, IPerson> persons) throws RemoteException {
        this.activePersons.set(persons);
    }

    @Override
    public void setRankings(List<IPerson> persons) throws RemoteException {
        this.rankings = FXCollections.observableArrayList(persons);
    }

}
