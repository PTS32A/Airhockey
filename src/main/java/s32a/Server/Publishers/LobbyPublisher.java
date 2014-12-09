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
    private ObservableList<String> chat;

    public LobbyPublisher() throws RemoteException {
        this.observers = new HashMap<>();
        this.lobby = Lobby.getSingle();
    }

    public boolean addObserver(String name, ILobbyClient input) {
        if (observers.containsKey(name)) {
            return false;
        }
        if (observers.put(name, input) == null){
            this.pushActiveGames();
            this.pushPersons(this.persons.get());
            this.pushRankings();
            this.pushSettings(this.settings.get());
            return true;
        }
        return false;
    }

    public void removeObserver(String name) {
        observers.remove(name);
    }

    public void bindChat(ObservableList<String> chat){
        this.chat = chat;
        this.pushChat();
        this.chat.addListener(new ListChangeListener() {

            @Override
            public void onChanged(ListChangeListener.Change c) {
                pushChat();
            }
        });
    }

    private void pushChat(){
        for (Iterator<String> it = observers.keySet().iterator(); it.hasNext();) {
            String key = it.next();
            try {
                observers.get(key).setChat(new ArrayList<>(chat));
            }
            catch (RemoteException ex) {
                System.out.println("RemoteException setting chat for " + key + ": " + ex.getMessage());
                Logger.getLogger(GamePublisher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void bindSettings(ObjectProperty<HashMap<String, Object>> input) {
        this.settings = input;
        this.pushSettings(input.get());
        this.settings.addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                pushSettings(newValue);
            }

        });
    }

    private void pushSettings(Object newValue) {
        for (Iterator<String> it = observers.keySet().iterator(); it.hasNext();) {
            String key = it.next();
            try {
                observers.get(key).setSettings((HashMap<String, Object>) newValue);
            }
            catch (RemoteException ex) {
                System.out.println("RemoteException setting settings for " + key + ": " + ex.getMessage());
                Logger.getLogger(GamePublisher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void bindPersons(ObjectProperty<HashMap<String, IPerson>> input) {
        this.persons = input;
        this.pushPersons(input.get());
        this.persons.addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                pushPersons(newValue);
            }

        });
    }

    private void pushPersons(Object newValue) {
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

    public void bindActiveGames(ObservableList<IGame> input) {
        this.games = input;
        this.pushActiveGames();
        this.games.addListener(new ListChangeListener() {

            @Override
            public void onChanged(ListChangeListener.Change c) {
                pushActiveGames();
            }
        });
    }

    private void pushActiveGames() {
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

    public void bindRankings(ObservableList<IPerson> input) {
        this.rankings = input;
        this.pushRankings();
        this.rankings.addListener(new ListChangeListener() {

            @Override
            public void onChanged(ListChangeListener.Change c) {
                pushRankings();
            }
        });
    }

    private void pushRankings() {
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
}
