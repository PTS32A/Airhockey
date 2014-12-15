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
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import s32a.Server.Lobby;
import s32a.Shared.IGame;
import s32a.Shared.ILobbyClient;
import s32a.Shared.IPerson;

/**
 *
 * @author Kargathia
 */
public class LobbyPublisher {

    private ConcurrentHashMap<String, ILobbyClient> observers;
    private Lobby lobby;
    private ObservableList<IPerson> rankings;
    private ObservableList<String> chat;
    private ObservableMap<String, Object> settings;
    private ObservableMap<String, IPerson> persons;
    private ObservableMap<String, IGame> games;

    /**
     * Constructor. Initialises Properties that get bound.
     *
     * @throws RemoteException
     */
    public LobbyPublisher() throws RemoteException {
        this.observers = new ConcurrentHashMap<>();
        this.lobby = Lobby.getSingle();
        this.settings = FXCollections.observableHashMap();
        this.persons = FXCollections.observableHashMap();
        this.games = FXCollections.observableHashMap();
    }

    /**
     * Adds an ILobbyClient as observer to the server.
     *
     * @param name The name corresponding with the ILobbyClient
     * @param input The ILobbyClient to be added
     * @return Returns a boolean indicating success of the addition
     */
    public boolean addObserver(String name, ILobbyClient input) {
        if (observers.containsKey(name)) {
            return false;
        }
        if (observers.put(name, input) == null) {
            return this.pushToNewObserver(name, input);
        }
        return false;
    }

    /**
     * Pushes all values to a newly registered observer. Avoids mass pushing
     * everything to everyone.
     *
     * @param name
     * @param newObsv
     * @return
     */
    private boolean pushToNewObserver(String name, ILobbyClient newObsv) {
        try {
            newObsv.setActiveGames(new HashMap<>(this.games));
            newObsv.setChat(new ArrayList<>(this.chat));
            newObsv.setPersons(new HashMap<>(this.persons));
            newObsv.setRankings(new ArrayList<>(this.rankings));
            newObsv.setSettings(new HashMap<>(this.settings));
            return true;
        }
        catch (RemoteException ex) {
            System.out.println("RemoteException pushing values to new observer " + name);
            this.removeObserver(name);
            return false;
        }

    }

    /**
     * Removes an observer
     *
     * @param name The name corresponding with the observer
     */
    public void removeObserver(String name) {
        System.out.println("Removed observer " + name + " from LobbyPublisher");
        observers.remove(name);
    }

    /**
     * Binds the list of the chat messages of the server to the client
     *
     * @param chat An observable list of chat messages to be bound
     */
    public void bindChat(ObservableList<String> chat) {
        this.chat = chat;
        this.pushChat();
        this.chat.addListener(new ListChangeListener() {

            @Override
            public void onChanged(ListChangeListener.Change c) {
                pushChat();
            }
        });
    }

    /**
     * Pushes an update in the ChatBox to the observers. Only does so if the
     * list is not null or empty.
     */
    private void pushChat() {
        if (this.chat == null || this.chat.isEmpty()) {
            return;
        }
        List<String> chatArray = new ArrayList<>(chat);
        for (Iterator<String> it = observers.keySet().iterator(); it.hasNext();) {
            String key = it.next();
            try {
                observers.get(key).setChat(chatArray);
            }
            catch (RemoteException ex) {
                System.out.println("RemoteException setting chat for " + key + ": " + ex.getMessage());
                this.removeObserver(key);
            }
        }
    }

    /**
     * Binds the server's settings to the observers' settings
     *
     * @param input The settings to be bound
     */
    public void bindSettings(ObservableMap<String, Object> input) {
        this.settings = input;
        this.pushSettings();
        this.settings.addListener(new MapChangeListener() {

            @Override
            public void onChanged(MapChangeListener.Change change) {
                pushSettings();
            }
        });
    }

    /**
     * Pushes the updated settings to the observers
     *
     * @param newValue An updated setting
     */
    private void pushSettings() {
        HashMap<String, Object> output = new HashMap<>(this.settings);
        for (Iterator<String> it = observers.keySet().iterator(); it.hasNext();) {
            String key = it.next();
            try {
                observers.get(key).setSettings(output);
            }
            catch (RemoteException ex) {
                System.out.println("RemoteException setting settings for " + key + ": " + ex.getMessage());              
                this.removeObserver(key);
            }
        }
    }

    /**
     * Binds the Persons to the observers' Persons
     *
     * @param input A HashMap containing Persons to bound
     */
    public void bindPersons(ObservableMap<String, IPerson> input) {
        this.persons = input;
        this.pushPersons();
        this.persons.addListener(new MapChangeListener() {

            @Override
            public void onChanged(MapChangeListener.Change change) {
                pushPersons();
                pushActiveGames();
            }
        });
    }

    /**
     * Pushes an update in a Person to the observers
     *
     * @param newValue An object containing the update to be pushed
     */
    private void pushPersons() {
        HashMap<String, IPerson> output = new HashMap<>(this.persons);
        for (Iterator<String> it = observers.keySet().iterator(); it.hasNext();) {
            String key = it.next();
            try {
                observers.get(key).setPersons(output);
            }
            catch (RemoteException ex) {
                System.out.println("RemoteException setting Persons for " + key + ": " + ex.getMessage());
                this.removeObserver(key);
            }
        }
    }

    /**
     * Binds the active Games to the observers' active Games
     *
     * @param input An observable list of Games to be bound
     */
    public void bindActiveGames(ObservableMap<String, IGame> input) {
        this.games = input;
        this.pushActiveGames();
        this.games.addListener(new MapChangeListener() {

            @Override
            public void onChanged(MapChangeListener.Change change) {
                pushActiveGames();
            }
        });
    }

    /**
     * Pushes updates within the active Games
     */
    private void pushActiveGames() {
        HashMap<String, IGame> output = new HashMap<>(this.games);
        for (Iterator<String> it = observers.keySet().iterator(); it.hasNext();) {
            String key = it.next();
            try {
                observers.get(key).setActiveGames(output);
            }
            catch (RemoteException ex) {
                System.out.println("RemoteException setting active Games for " + key + ": " + ex.getMessage());
                System.out.println("Removed observer " + key + " from LobbyPublisher");
                this.removeObserver(key);
            }
        }
    }

    /**
     * Binds the rankings of the Persons to the observers' rankings
     *
     * @param input An observable list of Persons, containing the rankings
     */
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

    /**
     * Pushed the rankings to the observers
     */
    private void pushRankings() {
        List<IPerson> rankingsArray = new ArrayList<>(rankings);
        for (Iterator<String> it = observers.keySet().iterator(); it.hasNext();) {
            String key = it.next();
            try {
                observers.get(key).setRankings(rankingsArray);
            }
            catch (RemoteException ex) {
                System.out.println("RemoteException setting rankings for " + key + ": " + ex.getMessage());
                System.out.println("Removed observer " + key + " from LobbyPublisher");
                this.removeObserver(key);
            }
        }
    }
}
