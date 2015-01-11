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
import java.util.Map;
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
import static s32a.Client.GUI.AirhockeyGUI.me;
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

    public LobbyClient(ILobby myLobby) throws RemoteException {
        if (myLobby == null) {
            throw new RemoteException();
        }
        this.myLobby = myLobby;
        this.oRankingsList = FXCollections.observableArrayList(new ArrayList<IPerson>());
        this.oChatList = FXCollections.observableArrayList(new ArrayList<String>());
        this.playerInfo = FXCollections.observableArrayList(new ArrayList<String>());
        this.oActiveGamesList = FXCollections.observableArrayList(new ArrayList<>());

        this.playerInfo.add("Name: " + AirhockeyGUI.me);
        this.playerInfo.add("Rating: -5");
    }

    @Override
    public IPerson getMyPerson(String playerName) throws RemoteException {
        return myLobby.getMyPerson(playerName);
    }

    /**
     * Returns up-to-date data - actual RMI query, and not from local stub.
     * @return
     * @throws RemoteException
     */
    @Override
    public Map<String, IGame> getRMIActiveGames() throws RemoteException {
        return myLobby.getRMIActiveGames();
    }

    /**
     * Returns up-to-date data - actual RMI query, and not from local stub.
     * @return
     * @throws RemoteException
     */
    @Override
    public Map<String, Object> getRMIAirhockeySettings() throws RemoteException {
        return myLobby.getRMIAirhockeySettings();
    }

    /**
     * Returns up-to-date data - actual RMI query, and not from local stub.
     * @return
     * @throws RemoteException
     */
    @Override
    public Map<String, IPerson> getRMIActivePersons() throws RemoteException {
        return myLobby.getRMIActivePersons();
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
    public boolean logOut(String input) throws RemoteException {
        return myLobby.logOut(input);
    }

    @Override
    public IGame startGame(String person, IGameClient client) throws RemoteException {
        return myLobby.startGame(person, client);
    }

    @Override
    public IGame joinGame(String game, String person, IGameClient client) throws RemoteException {
        return myLobby.joinGame(game, person, client);
    }

    @Override
    public IGame spectateGame(String gameInput, String personInput, IGameClient client)
            throws RemoteException {
        return myLobby.spectateGame(gameInput, personInput, client);
    }

    @Override
    public boolean addChatMessage(String message, String from) throws IllegalArgumentException, RemoteException {
        return myLobby.addChatMessage(message, from);
    }

    @Override
    public boolean endGame(String game, String hasLeft) throws RemoteException {
        return myLobby.endGame(game, hasLeft);
    }

    @Override
    public void stopSpectating(String game, String spectator) throws RemoteException {
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
                oActiveGamesList.clear();
                oActiveGamesList.addAll(activeGames.values());
            }
        });

    }

    @Override
    public synchronized void setMyLobby(ILobby myLobbyInput) throws RemoteException {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                myLobby = myLobbyInput;
            }
        });
    }

    @Override
    public synchronized void setChat(List<String> chat) throws RemoteException {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                oChatList.setAll(chat);
            }
        });
    }

    @Override
    public synchronized void setRankings(List<IPerson> persons) throws RemoteException {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                oRankingsList.setAll(persons);
            }
        });
    }

    @Override
    public void setPersonRanking(IPerson person) throws RemoteException {
        if (person.getName().equals(me)) {
            playerInfo.set(0, "Name: " + AirhockeyGUI.me);
            playerInfo.set(1, "Rating: " + String.valueOf(person.getRating()));
        }
    }

}
