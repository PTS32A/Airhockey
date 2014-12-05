/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Server.Publishers;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import s32a.Server.Lobby;
import s32a.Shared.IGame;
import s32a.Shared.ILobby;
import s32a.Shared.ILobbyClient;

/**
 *
 * @author Kargathia
 */
public class LobbyPublisher {

    private HashMap<String, ILobbyClient> observers;
    private Lobby lobby;

    public LobbyPublisher(){
        this.observers = new HashMap<>();
        try {
            this.lobby = Lobby.getSingle();
        }
        catch (RemoteException ex) {
            System.out.println("Failed to retrieve lobby: " + ex.getMessage());
            Logger.getLogger(LobbyPublisher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean addObserver(String name, ILobbyClient input){
        if(observers.containsKey(name)){
            return false;
        }
        return(observers.put(name, input) == null);
    }

    public void removeObserver(String name){
        observers.remove(name);
    }

    private void setActiveGames(){

    }

    private void setOActiveGames(){

    }

    private void setMyLobby(){
        
    }

    

}
