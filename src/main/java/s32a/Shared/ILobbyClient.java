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
    
    public void setActiveGames(List<IGame> activeGames)
            throws RemoteException;
    
    public void setMyLobby(ILobby myLobby)
            throws RemoteException;
    
    public void setChat(List<String> chat)
            throws RemoteException;
    
    public void setRating(Double rating)
            throws RemoteException;
    
    public void setSettings(HashMap<String, Object> settings) 
            throws RemoteException;
    
    public void setPersons(HashMap<String, IPerson> settings) 
            throws RemoteException;
}
