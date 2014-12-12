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

    private ConcurrentHashMap<String, ILobbyClient> observers;
    private Lobby lobby;
    private ObservableList<IPerson> rankings;
    private ObservableList<String> chat;
    private ObjectProperty<HashMap<String, Object>> settings;
    private ObjectProperty<HashMap<String, IPerson>> persons;
    private ObjectProperty<HashMap<String, IGame>> games;

    private AtomicBoolean isNewRankings, isNewChat, isNewSettings, isNewPersons, isNewGames;

    /**
     * Constructor. Initialises Properties that get bound.
     *
     * @throws RemoteException
     */
    public LobbyPublisher() throws RemoteException {
        this.observers = new ConcurrentHashMap<>();
        this.lobby = Lobby.getSingle();
        this.settings = new SimpleObjectProperty<>(null);
        this.persons = new SimpleObjectProperty<>(null);
        this.games = new SimpleObjectProperty<>(null);

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
            newObsv.setActiveGames(this.games.get());
            newObsv.setChat(new ArrayList<>(this.chat));
            newObsv.setPersons(this.persons.get());
            newObsv.setRankings(new ArrayList<>(this.rankings));
            newObsv.setSettings(this.settings.get());
            return true;
        }
        catch (RemoteException ex) {
            System.out.println("RemoteException pushing values to new observer " + name
                    + " - removing observer");
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
                System.out.println("Removed observer " + key + " from LobbyPublisher");
                this.removeObserver(key);
            }
        }
    }

    /**
     * Binds the server's settings to the observers' settings
     *
     * @param input The settings to be bound
     */
    public void bindSettings(ObjectProperty<HashMap<String, Object>> input) {
        this.settings.bind(input);
        this.pushSettings();
        this.settings.addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
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
        for (Iterator<String> it = observers.keySet().iterator(); it.hasNext();) {
            String key = it.next();
            try {
                observers.get(key).setSettings(settings.get());
            }
            catch (RemoteException ex) {
                System.out.println("RemoteException setting settings for " + key + ": " + ex.getMessage());
                System.out.println("Removed observer " + key + " from LobbyPublisher");
                this.removeObserver(key);
            }
        }
    }

    /**
     * Binds the Persons to the observers' Persons
     *
     * @param input A HashMap containing Persons to bound
     */
    public void bindPersons(ObjectProperty<HashMap<String, IPerson>> input) {
        this.persons = input;
        this.pushPersons();
        this.persons.addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                pushPersons();
            }

        });
    }

    /**
     * Pushes an update in a Person to the observers
     *
     * @param newValue An object containing the update to be pushed
     */
    private void pushPersons() {
        for (Iterator<String> it = observers.keySet().iterator(); it.hasNext();) {
            String key = it.next();
            try {
                observers.get(key).setPersons(persons.get());
            }
            catch (RemoteException ex) {
                System.out.println("RemoteException setting Persons for " + key + ": " + ex.getMessage());
                System.out.println("Removed observer " + key + " from LobbyPublisher");
                this.removeObserver(key);
            }
        }
    }

    /**
     * Binds the active Games to the observers' active Games
     *
     * @param input An observable list of Games to be bound
     */
    public void bindActiveGames(ObjectProperty<HashMap<String, IGame>> input) {
        this.games.bind(input);
        this.pushActiveGames();
        this.games.addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                pushActiveGames();
            }
        });
    }

    /**
     * Pushes updates within the active Games
     */
    private void pushActiveGames() {
        for (Iterator<String> it = observers.keySet().iterator(); it.hasNext();) {
            String key = it.next();
            try {
                observers.get(key).setActiveGames(games.get());
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
