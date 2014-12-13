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
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
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
    private ObservableList<String> oChatList;
    @Getter
    private ObservableList<IPerson> oRankingsList;
    @Getter
    private ObservableList<IGame> oActiveGamesList;
    @Getter
    private ObservableList<String> playerInfo;

    private ILobby myLobby;
    @Getter
    private ObservableMap<String, IPerson> oActivePersonsMap;
    @Getter
    private ObservableMap<String, Object> oSettingsMap;
    @Getter
    private ObservableMap<String, IGame> oActiveGamesMap;

    public LobbyClient(ILobby myLobby) throws RemoteException {
        if (myLobby == null) {
            throw new RemoteException();
        }
        this.myLobby = myLobby;
        this.oRankingsList = FXCollections.observableArrayList(new ArrayList<IPerson>());
        this.oChatList = FXCollections.observableArrayList(new ArrayList<String>());
        this.playerInfo = FXCollections.observableArrayList(new ArrayList<String>());
        this.oActiveGamesList = FXCollections.observableArrayList(new ArrayList<>());
        this.oActivePersonsMap = FXCollections.observableHashMap();
        this.oActiveGamesMap = FXCollections.observableHashMap();
        this.oSettingsMap = FXCollections.observableHashMap();

        this.playerInfo.add("Name: " + AirhockeyGUI.me);
        this.playerInfo.add("Rating: -5");
    }

    @Override
    public IPerson getMyPerson(String playerName) throws RemoteException {
        return myLobby.getMyPerson(playerName);
    }

    @Override
    public HashMap<String, IGame> getActiveGames() throws RemoteException {
        return new HashMap<>(oActiveGamesMap);
    }

    @Override
    public HashMap<String, Object> getAirhockeySettings() throws RemoteException {
        return new HashMap<>(oSettingsMap);
    }

    @Override
    public HashMap<String, IPerson> getActivePersons() throws RemoteException {
        return new HashMap<>(oActivePersonsMap);
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
    public List<IPerson> getRankings() throws SQLException, RemoteException {
        return this.oRankingsList;
    }

    @Override
    public void populate() throws RemoteException {
        myLobby.populate();
    }

    //----------------------------------- INCOMING FROM SERVER -----------------------------------------------
    @Override
    public synchronized void setActiveGames(HashMap<String, IGame> activeGames) throws RemoteException {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                oActiveGamesList.setAll(activeGames.values());
                oActiveGamesMap.clear();
                oActiveGamesMap.putAll(activeGames);
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
                oChatList.setAll(chat);
            }
        });
    }

    @Override
    public void setSettings(HashMap<String, Object> settings) throws RemoteException {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                oSettingsMap.clear();
                oSettingsMap.putAll(settings);
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
                oActivePersonsMap.clear();
                oActivePersonsMap.putAll(persons);

                playerInfo.set(0, "Name: " + AirhockeyGUI.me);
                playerInfo.set(1, "Rating: " + String.valueOf(rating));
            }
        });
    }

    @Override
    public void setRankings(List<IPerson> persons) throws RemoteException {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                oRankingsList.setAll(persons);
            }
        });
    }



}
