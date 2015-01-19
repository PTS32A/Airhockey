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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    private AtomicBoolean gamesUpdated, rankingUpdated;
    private final long timerPeriod = 2000L;

    private ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);

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

        this.gamesUpdated = new AtomicBoolean(false);
        this.rankingUpdated = new AtomicBoolean(false);

        this.initTimer();
    }

    /**
     * Starts a timed push mechanic to update all clients of changes. Prevents
     * spam. Only used for collections liable to have bursts of updates at once.
     *
     */
    private void initTimer() {
        Runnable pusher = new Runnable() {

            @Override
            public void run() {
                if (gamesUpdated.getAndSet(false)) {
                    pushActiveGames();
                }
                if (rankingUpdated.getAndSet(false)) {
                    pushRankings();
                }
            }
        };

        this.pool.scheduleAtFixedRate(pusher, 1000L, timerPeriod, TimeUnit.MILLISECONDS);
    }

    /**
     * pool.execute, wrapped in a try-catch
     *
     * @param r
     */
    private void tryExecute(Runnable r) {
        try {
            pool.execute(r);
        } catch (RejectedExecutionException ex) {
            System.out.println("RejectedExecutionException caught and handled: "
                    + ex.getMessage());
        }
    }

    /**
     * Adds an ILobbyClient as observer to the server.
     *
     * @param name The name corresponding with the ILobbyClient
     * @param input The ILobbyClient to be added
     * @return Returns a boolean indicating success of the addition
     */
    public void addObserver(String name, ILobbyClient input) {
        if (observers.containsKey(name)) {
            return;
        }
        observers.put(name, input);
        this.pushToNewObserver(name, input);
    }

    /**
     * Pushes all values to a newly registered observer. Avoids mass pushing
     * everything to everyone.
     *
     * @param name
     * @param newObsv
     * @return
     */
    private void pushToNewObserver(String name, ILobbyClient newObsv) {
        tryExecute(new Runnable() {

            @Override
            public void run() {
                try {
                    newObsv.setActiveGames(new HashMap<>(games));
                    newObsv.setChat(new ArrayList<>(chat));
                    newObsv.setPersonRanking(Lobby.getSingle().getMyPerson(name));
                    newObsv.setRankings(new ArrayList<>(rankings));
                } catch (RemoteException ex) {
                    System.out.println("RemoteException pushing values to new observer "
                            + name + ": " + ex.getMessage());
                    enforceLogout(name, false);
                }
            }
        });
    }

    /**
     * Removes an observer
     *
     * @param name The name corresponding with the observer
     * @param notifyClient
     */
    public void enforceLogout(String name, boolean notifyClient) {
        if (!observers.containsKey(name)) {
            return;
        }
        if (notifyClient) {
            try {
                observers.get(name).enforceLogout();
            } catch (RemoteException ex) {
            }
        }
        System.out.println("Removed observer " + name + " from LobbyPublisher");
        try {
            observers.remove(name);
            Lobby.getSingle().logOut(name);
        } catch (RemoteException ex) {
            System.out.println("remoteException on calling logout "
                    + "from LobbyPublisher.removeObserver for "
                    + name + ", ex: " + ex.getMessage());
            Logger.getLogger(LobbyPublisher.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        tryExecute(new Runnable() {

            @Override
            public void run() {
                if (chat == null || chat.isEmpty()) {
                    return;
                }
                List<String> chatArray = new ArrayList<>(chat);
                for (Iterator<String> it = observers.keySet().iterator(); it.hasNext();) {
                    String key = it.next();
                    try {
                        observers.get(key).setChat(chatArray);
                    } catch (RemoteException ex) {
                        System.out.println("RemoteException setting chat for " + key + ": " + ex.getMessage());
                        enforceLogout(key, false);
                    }
                }
            }
        });
    }

    /**
     * Called whenever rating updates for a player (postgame).
     *
     * @param person
     */
    public void pushNewRanking(IPerson person) {
        if (person == null) {
            return;
        }
        tryExecute(new Runnable() {

            @Override
            public void run() {
                try {
                    ILobbyClient client = observers.get(person.getName());
                    if (client != null) {
                        client.setPersonRanking(person);
                    }
                } catch (RemoteException ex) {
                    Logger.getLogger(LobbyPublisher.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    /**
     * Binds the active Games to the observers' active Games
     *
     * @param input An observable list of Games to be bound
     */
    public void bindActiveGames(ObservableMap<String, IGame> input) {
        this.games = input;
        this.games.addListener(new MapChangeListener() {

            @Override
            public void onChanged(MapChangeListener.Change change) {
                gamesUpdated.set(true);
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
            } catch (RemoteException ex) {
                System.out.println("RemoteException setting active Games for " + key + ": " + ex.getMessage());
                System.out.println("Removed observer " + key + " from LobbyPublisher");
                this.enforceLogout(key, false);
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
        this.rankings.addListener(new ListChangeListener() {

            @Override
            public void onChanged(ListChangeListener.Change c) {
                rankingUpdated.set(true);
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
            } catch (RemoteException ex) {
                System.out.println("RemoteException setting rankings for " + key + ": " + ex.getMessage());
                System.out.println("Removed observer " + key + " from LobbyPublisher");
                this.enforceLogout(key, false);
            }
        }
    }
}
