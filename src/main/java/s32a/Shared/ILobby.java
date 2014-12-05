/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Kargathia
 */
public interface ILobby extends Remote {

    public IPerson getMyPerson(String playerName)
            throws RemoteException;

    public HashMap getAirhockeySettings()
            throws RemoteException;

    public HashMap<String, IPerson> getActivePersons()
            throws RemoteException;

    public List<IGame> getActiveGames()
            throws RemoteException;

    public boolean addPerson(String playerName, String passWord)
            throws IllegalArgumentException, SQLException, RemoteException;

    public boolean checkLogin(String playerName, String password, ILobbyClient client)
            throws IllegalArgumentException, SQLException, RemoteException;

    public boolean logOut(IPerson input)
            throws RemoteException;

    public IGame startGame(IPerson person, IGameClient client)
            throws RemoteException;

    public IGame joinGame(IGame game, IPerson person, IGameClient client)
            throws RemoteException;

    public IGame spectateGame(IGame gameInput, IPerson personInput, IGameClient client)
            throws RemoteException;

    public boolean addChatMessage(String message, String from)
            throws IllegalArgumentException, RemoteException;

    public boolean endGame(IGame game, IPlayer hasLeft)
            throws RemoteException;

    public void stopSpectating(IGame game, IPerson spectator)
            throws RemoteException;

    public IGame getMyGame(String gameID) throws RemoteException;

    public List<IPerson> getRankings()
            throws SQLException, RemoteException;

    public void populate()
            throws RemoteException;
}
