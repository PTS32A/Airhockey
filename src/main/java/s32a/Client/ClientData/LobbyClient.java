/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Client.ClientData;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import javafx.collections.ObservableList;
import lombok.Getter;
import s32a.Shared.IGame;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import s32a.Shared.IChatbox;
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

    List<IGame> activeGames;
    @Getter
    ObservableList<IGame> oActiveGames;
    
    
    private ILobby myLobby;

    public LobbyClient() throws RemoteException {

    }

    @Override
    public IPerson getMyPerson(String playerName) {
        return myLobby.getMyPerson(playerName);
    }

    @Override
    public IChatbox getMychatbox() {
        return myLobby.getMychatbox();
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

}
