/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import javafx.collections.ObservableList;
import lombok.Getter;

/**
 *
 * @author Kargathia
 */
public interface ILobbyClient extends Remote{
    
    /**
     * Sets all active Games in this client's Lobby
     * @param activeGames A list of active Games
     * @throws RemoteException 
     */
    public void setActiveGames(HashMap<String, IGame> activeGames)
            throws RemoteException;
    
    /**
     * Sets the Lobby of this client to the server's Lobby
     * @param myLobby The server's Lobby
     * @throws RemoteException 
     */
    public void setMyLobby(ILobby myLobby)
            throws RemoteException;
    
    /**
     * Sets the ChatBox containing all the sent chat messages
     * @param chat A list of chat messages
     * @throws RemoteException 
     */
    public void setChat(List<String> chat)
            throws RemoteException;
    
    /**
     * Sets the rankings of all Persons in this client
     * @param persons A list of all Persons
     * @throws RemoteException 
     */
    public void setRankings(List<IPerson> persons)
            throws RemoteException;

    /**
     * Sets rating for an individual. Done on login and endgame.
     * @param person
     * @throws RemoteException
     */
    public void setPersonRanking(IPerson person)
            throws RemoteException;

    /**
     * Notifies client his session was terminated by server.
     * @throws RemoteException
     */
    public void enforceLogout()
            throws RemoteException;
}
