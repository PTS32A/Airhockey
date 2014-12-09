/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Server.Publishers;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import s32a.Server.Lobby;
import s32a.Shared.IGame;
import s32a.Shared.ILobbyClient;
import s32a.Shared.IPerson;

/**
 *
 * @author Kargathia
 */
public class LobbyPublisher {

    private HashMap<String, ILobbyClient> observers;
    private Lobby lobby;
    private ObservableList<IGame> games;
    private ObservableList<IPerson> rankings;
    private ObjectProperty<HashMap<String, Object>> settings;
    private ObjectProperty<HashMap<String, IPerson>> persons;

    public LobbyPublisher() throws RemoteException {
        this.observers = new HashMap<>();
        this.lobby = Lobby.getSingle();
    }

    public boolean addObserver(String name, ILobbyClient input) {
        if (observers.containsKey(name)) {
            return false;
        }
        return (observers.put(name, input) == null);
    }

    public void removeObserver(String name) {
        observers.remove(name);
    }
    
    private void bindSettings(ObjectProperty<HashMap<String, Object>> input){
        this.settings = input;

        this.settings.addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                for (Iterator<String> it = observers.keySet().iterator(); it.hasNext();) {
                    String key = it.next();
                    try {
                        observers.get(key).setSettings((HashMap<String, Object>) newValue);
                    }
                    catch (RemoteException ex) {
                        System.out.println("RemoteException setting roundNo for " + key + ": " + ex.getMessage());
                        Logger.getLogger(GamePublisher.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            
        });
    }
    
    private void bindPersons(ObjectProperty<HashMap<String, IPerson>> input){
        this.persons = input;

        this.persons.addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                for (Iterator<String> it = observers.keySet().iterator(); it.hasNext();) {
                    String key = it.next();
                    try {
                        observers.get(key).setPersons((HashMap<String, IPerson>) newValue);
                    }
                    catch (RemoteException ex) {
                        System.out.println("RemoteException setting roundNo for " + key + ": " + ex.getMessage());
                        Logger.getLogger(GamePublisher.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            
        });
    }

    private void bindActiveGames(ObservableList<IGame> input) {
        this.games = input;

        this.games.addListener(new ListChangeListener() {

            @Override
            public void onChanged(ListChangeListener.Change c) {
                for (Iterator<String> it = observers.keySet().iterator(); it.hasNext();) {
                    String key = it.next();
                    try {
                        observers.get(key).setActiveGames(new ArrayList<>(games));
                    }
                    catch (RemoteException ex) {
                        System.out.println("RemoteException setting roundNo for " + key + ": " + ex.getMessage());
                        Logger.getLogger(GamePublisher.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
    }
    
    private void bindRankings(ObservableList<IPerson> input) {
        this.rankings = input;

        this.rankings.addListener(new ListChangeListener() {

            @Override
            public void onChanged(ListChangeListener.Change c) {
                for (Iterator<String> it = observers.keySet().iterator(); it.hasNext();) {
                    String key = it.next();
                    try {
                        observers.get(key).setRankings(new ArrayList<>(rankings));
                    }
                    catch (RemoteException ex) {
                        System.out.println("RemoteException setting roundNo for " + key + ": " + ex.getMessage());
                        Logger.getLogger(GamePublisher.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
    }
}
