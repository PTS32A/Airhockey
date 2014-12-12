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
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import s32a.Client.GUI.AirhockeyGUI;
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

    @Getter
    private ObservableList<String> chatProperty;
    private List<String> chat;
    @Getter
    private ObservableList<IPerson> rankings;
    @Getter
    private DoubleProperty playerRatingProperty;
    private List<IGame> backingActiveGames;
    @Getter
    private ObservableList<IGame> oActiveGames;

    private ILobby myLobby;
    private ObjectProperty<HashMap<String, IPerson>> activePersonsProperty;
    private ObjectProperty<HashMap<String, Object>> settingsProperty;
    private ObjectProperty<HashMap<String, IGame>> activeGamesProperty;

    public LobbyClient(ILobby myLobby) throws RemoteException {
        if (myLobby == null) {
            throw new RemoteException();
        }
        this.myLobby = myLobby;
        this.activeGamesProperty = new SimpleObjectProperty<>(new HashMap<>());
        this.rankings = FXCollections.observableArrayList(new ArrayList<IPerson>());
        this.activePersonsProperty = new SimpleObjectProperty<>(new HashMap<>());
        this.settingsProperty = new SimpleObjectProperty<>(new HashMap<>());
        this.chat = new ArrayList<>();
        this.chatProperty = FXCollections.observableArrayList(chat);
        this.playerRatingProperty = new SimpleDoubleProperty(0);

        this.backingActiveGames = new ArrayList<>();
        this.oActiveGames = FXCollections.observableArrayList(this.backingActiveGames);
    }

    @Override
    public IPerson getMyPerson(String playerName) throws RemoteException {
        return myLobby.getMyPerson(playerName);
    }

    @Override
    public HashMap<String, IGame> getActiveGames() throws RemoteException {
        return this.activeGamesProperty.get();
    }

    @Override
    public HashMap getAirhockeySettings() throws RemoteException {
        return this.settingsProperty.get();
    }

    @Override
    public HashMap<String, IPerson> getActivePersons() throws RemoteException {
        return this.activePersonsProperty.get();
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

    //----------------------------------- INCOMING FROM SERVER -----------------------------------------------
    @Override
    public void setActiveGames(HashMap<String, IGame> activeGames) throws RemoteException {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                oActiveGames.clear();
                oActiveGames.setAll(activeGames.values());
                activeGamesProperty.set(activeGames);
            }
        });

    }

    @Override
    public void setMyLobby(ILobby myLobbyInput) throws RemoteException {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                myLobby = myLobbyInput;
            }
        });
    }

    @Override
    public void setChat(List<String> chat) throws RemoteException {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                chatProperty.setAll(chat);
            }
        });
    }

    @Override
    public void setSettings(HashMap<String, Object> settings) throws RemoteException {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                settingsProperty.set(settings);
            }
        });
    }

    @Override
    public void setPersons(HashMap<String, IPerson> persons) throws RemoteException {
        final double rating;
        if(persons.get(AirhockeyGUI.me) != null){
            rating = persons.get(AirhockeyGUI.me).getRating();
        } else {
            rating = -1.0;
        }

        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                activePersonsProperty.set(persons);
                playerRatingProperty.set(rating);
            }
        });
    }

    @Override
    public void setRankings(List<IPerson> persons) throws RemoteException {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                rankings = FXCollections.observableArrayList(persons);
            }
        });
    }

}
