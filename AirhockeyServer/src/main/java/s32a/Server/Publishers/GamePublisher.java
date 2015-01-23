/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Server.Publishers;

import com.badlogic.gdx.math.Vector2;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import s32a.Server.Game;
import s32a.Server.Lobby;
import s32a.Server.Player;
import s32a.Shared.IGameClient;
import s32a.Shared.IPlayer;
import s32a.Shared.enums.GameStatus;

/**
 * NOTES: ConcurrentHashMap is implemented to prevent synchronisation issues on
 * reading / writing observers. It is not clear whether this is sufficient.
 *
 * @author Kargathia
 */
public class GamePublisher {

    /**
     * Collection of observers of this game (Players + Spectators) Key = name
     */
    private ConcurrentHashMap<String, IGameClient> observers;

    /**
     * the Game associated with this publisher
     */
    private Game myGame;

    // all properties / collections being tracked by this publisher
    // all copies of / bound to original values
    private ObjectProperty<Player> player1Prop, player2Prop, player3Prop;
    private ObservableList<IPlayer> players;
    private ObjectProperty<Vector2> puckPosition;
    private IntegerProperty player1Score, player2Score, player3Score, roundNo;
    private ObservableList<String> chatbox;
    private ObjectProperty<GameStatus> statusProp;
    private StringProperty difficultyProp;

    private ScheduledExecutorService pool = Executors.newScheduledThreadPool(3);
    private AtomicBoolean publisherShutDown, scoreUpdated, batUpdated;
    private final long scoreUpdateDelay = 1000L;
    private final long batUpdateDelay = 10L;

    private ChangeListener scoreListener = null, batListener = null;
    Map<String, Double> batPosMap;
    Map<String, Integer> scoresMap;

    /**
     * Creates a new publisher associated with given game. Observers need to be
     * manually registered.
     *
     * @param myGame The game to used by the gamepublisher
     */
    public GamePublisher(Game myGame) {
        this.observers = new ConcurrentHashMap<>();
        this.myGame = myGame;
        this.players = null;
        this.chatbox = null;
        this.statusProp = new SimpleObjectProperty<>(null);
        this.puckPosition = new SimpleObjectProperty<>(new Vector2(0f, 0f));

        this.player1Prop = new SimpleObjectProperty<>(null);
        this.player2Prop = new SimpleObjectProperty<>(null);
        this.player3Prop = new SimpleObjectProperty<>(null);

        this.player1Score = new SimpleIntegerProperty(-1);
        this.player2Score = new SimpleIntegerProperty(-1);
        this.player3Score = new SimpleIntegerProperty(-1);

        this.roundNo = new SimpleIntegerProperty(-1);
        this.difficultyProp = new SimpleStringProperty("");

        this.batUpdated = new AtomicBoolean(false);
        this.scoreUpdated = new AtomicBoolean(false);
        this.publisherShutDown = new AtomicBoolean(false);

        this.initTimer();
    }

    /**
     * Starts a scheduled execution of tasks governing the pushing of score and
     * bat positions, as they're updated simultaneously.
     *
     */
    private void initTimer() {
        batPosMap = new HashMap<>();
        // governing pushing bat positions
        Runnable posTask = new Runnable() {

            @Override
            public void run() {

                if (batUpdated.getAndSet(false)) {
                    pushBatPositions();
                }

            }
        };

        scoresMap = new HashMap<>();
        // pushing score
        Runnable scoreTask = new Runnable() {

            @Override
            public void run() {
                if (publisherShutDown.get()) {
                    pool.shutdownNow();
                    return;
                }
                if (scoreUpdated.getAndSet(false)) {
                    pushScore();
                }
            }
        };

        pool.scheduleWithFixedDelay(posTask, 100L, batUpdateDelay, TimeUnit.MILLISECONDS);
        pool.scheduleAtFixedRate(scoreTask, 500L, scoreUpdateDelay, TimeUnit.MILLISECONDS);
    }

    /**
     * pool.execute, wrapped in a try-catch
     *
     * @param r The runnable to be executed
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
     * Tries to add an observer to the list. If the list already contains given
     * observer, return false;
     *
     * @param name The name of the observer
     * @param client The gameclient of the observer
     * @throws java.rmi.RemoteException
     */
    public void addObserver(String name, IGameClient client) throws RemoteException {
        this.observers.put(name, client);
        pushToNewObserver(name, client);
    }

    /**
     * Pushes all data to the new observer - and only to him.
     *
     * @param name The name of the observer
     * @param client The gameclient of the observer
     * @throws RemoteException
     */
    private void pushToNewObserver(String name, IGameClient client) {
        tryExecute(new Runnable() {

            @Override
            public void run() {
                try {
                    client.setChat(new ArrayList<>(chatbox));
                    client.setDifficulty(difficultyProp.get());
                    client.setGame(myGame);
                    client.setPlayers(new ArrayList<>(players));
                    client.setPuck(puckPosition.get().x, puckPosition.get().y);
                    client.setRoundNo(roundNo.get());
                    client.setStatus(statusProp.get());

                    pushBatPositions();
                } catch (RemoteException ex) {
                    System.out.println("RemoteException pushing values to new observer " + name);
                    removeObserver(name);
                }
            }
        });
    }

    /**
     * Removes observer of given name from the list. Realistically will only be
     * called on a spectator leaving, as a player leaving triggers deletion of
     * the entire game.
     *
     * @param name The name of the observer
     */
    public void removeObserver(String name) {
        System.out.println("Removed " + name + " from observers.");
        this.observers.remove(name);
    }

    /**
     * Binds given list of players to players. Event only pushes if a player was
     * added or removed, to prevent firing on every bat position change.
     *
     * @param players The observable list of players
     */
    public void bindPlayers(ObservableList<IPlayer> players) {
        this.players = players;
        this.pushPlayers();
        this.players.addListener(new ListChangeListener() {

            @Override
            public void onChanged(ListChangeListener.Change c) {
                if (c.next() && (c.wasAdded() || c.wasRemoved())) {
                    pushPlayers();
                }
            }
        });
    }

    /**
     * Pushes the entire list of players. Used for general availability of the
     * proxy, not for updating score / bat position.
     */
    private void pushPlayers() {
        tryExecute(new Runnable() {

            @Override
            public void run() {
                List<IPlayer> playersArray = new ArrayList<>(players);
                for (String key : observers.keySet()) {
                    try {
                        observers.get(key).setPlayers(playersArray);
                    } catch (RemoteException ex) {
                        System.out.println("RemoteException pushing players to " + key + ": " + ex.getMessage());
                        removeObserver(key);
                    }
                }
            }
        });
    }

    /**
     * Binds roundNo to given roundNo. Adds Listener responsible for pushing
     * updates.
     *
     * @param roundNo The round number
     */
    public void bindRoundNo(IntegerProperty roundNo) {
        this.roundNo.bind(roundNo);
        this.pushRoundNo();
        this.roundNo.addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                pushRoundNo();
            }
        });
    }

    /**
     * Pushes (updated) roundNo to all observers
     */
    private void pushRoundNo() {
        tryExecute(new Runnable() {

            @Override
            public void run() {
                for (String key : observers.keySet()) {
                    try {
                        observers.get(key).setRoundNo(roundNo.get());
                    } catch (RemoteException ex) {
                        System.out.println("RemoteException setting roundNo for "
                                + key + ": " + ex.getMessage());
                        removeObserver(key);
                    }
                }
            }
        });
    }

    /**
     * Binds difficulty provided as string - Double version not required. Only
     * needs to be done once.
     *
     * @param difficultyAsFloatProp The difficulty of the game as a
     * floatproperty
     */
    public void bindDifficulty(FloatProperty difficultyAsFloatProp) {
        this.difficultyProp.bind(difficultyAsFloatProp.asString());
        this.pushDifficulty();
        this.difficultyProp.addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                pushDifficulty();
            }
        });
    }

    /**
     * Pushes (updated) difficulty to all observers.
     */
    private void pushDifficulty() {
        tryExecute(new Runnable() {

            @Override
            public void run() {
                for (String key : observers.keySet()) {
                    try {
                        observers.get(key).setDifficulty(difficultyProp.get());
                    } catch (RemoteException ex) {
                        System.out.println("RemoteException setting Difficulty for "
                                + key + ": " + ex.getMessage());
                        removeObserver(key);
                    }
                }
            }
        });
    }

    /**
     * Sets chatbox observableList to point to actual chatbox. Adds
     * ListChangeListener responsible for pushing updates to clients.
     *
     * @param chat The observable list of strings containing chat messages
     */
    public void bindChat(ObservableList<String> chat) {
        this.chatbox = chat;
        this.pushChat();
        this.chatbox.addListener(new ListChangeListener() {

            @Override
            public void onChanged(ListChangeListener.Change c) {
                pushChat();
            }
        });
    }

    /**
     * Pushes the (updated) list of chatmessages to all observers
     */
    private void pushChat() {
        tryExecute(new Runnable() {

            @Override
            public void run() {
                ArrayList<String> chatArray = new ArrayList<>(chatbox);
                for (String key : observers.keySet()) {
                    try {
                        observers.get(key).setChat(chatArray);
                    } catch (RemoteException ex) {
                        System.out.println("RemoteException on pushing chatbox to "
                                + key + ": " + ex.getMessage());
                        removeObserver(key);
                    }
                }
            }
        });
    }

    /**
     * Binds Vector2 property containing puck x and y. Assigns listeners
     * responsible for pushing value changes.
     *
     * @param position The puck's position
     */
    public void bindPuckPosition(ObjectProperty<Vector2> position) {
        this.puckPosition.bind(position);
        this.pushPuckPosition();
        this.puckPosition.addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                pushPuckPosition();
            }
        });
    }

    /**
     * Pushes updated puck position values to Client. Values are split into x
     * and y.
     */
    private void pushPuckPosition() {
        tryExecute(new Runnable() {

            @Override
            public void run() {
                for (String key : observers.keySet()) {
                    try {
                        observers.get(key).setPuck(puckPosition.get().x, puckPosition.get().y);
                    } catch (RemoteException ex) {
                        System.out.println("remoteException on setting puck location for " + key);
                        removeObserver(key);
                    }
                }
            }
        });
    }

    /**
     * Binds statusproperty. Only needs to be done once.
     *
     * @param input The status of the game
     */
    public void bindStatus(ObjectProperty<GameStatus> input) {
        this.statusProp.bind(input);
        this.pushStatus();
        this.statusProp.addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                pushStatus();
                try {
                    Lobby.getSingle().forceMapUpdate(Lobby.getSingle().getActiveGames());
                } catch (RemoteException ex) {
                    Logger.getLogger(GamePublisher.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    /**
     * pushes updated status to all observers.
     */
    private void pushStatus() {
        tryExecute(new Runnable() {

            @Override
            public void run() {
                for (String key : observers.keySet()) {
                    try {
                        observers.get(key).setStatus(statusProp.get());
                    } catch (RemoteException ex) {
                        System.out.println("remoteException on setting status for " + key);
                        removeObserver(key);
                    }
                }
            }
        });
    }

    /**
     * Pushes score to observers. Called initially, and periodically by timer.
     * Timer already is on non-JFX thread.
     */
    private void pushScore() {
        if (this.player1Prop.get() == null
                || this.player2Prop.get() == null
                || this.player3Prop.get() == null) {
            return;
        }

        scoresMap.clear();
        scoresMap.put("player1", player1Score.get());
        scoresMap.put("player2", player2Score.get());
        scoresMap.put("player3", player3Score.get());
        for (String key : observers.keySet()) {
            try {
                observers.get(key).setPlayerScores(scoresMap);
            } catch (RemoteException ex) {
                System.out.println("remoteException on setting player score for " + key);
                this.removeObserver(key);
            }
        }
    }

    /**
     * Pushes bat positions of all three players. Called by timer.
     */
    private void pushBatPositions() {
        if (this.player1Prop.get() == null
                || this.player2Prop.get() == null
                || this.player3Prop.get() == null) {
            return;
        }

        batPosMap.clear();
        batPosMap.put("player1x", player1Prop.get().getPosX().get());
        batPosMap.put("player2x", player2Prop.get().getPosX().get());
        batPosMap.put("player3x", player3Prop.get().getPosX().get());

        batPosMap.put("player1y", player1Prop.get().getPosY().get());
        batPosMap.put("player2y", player2Prop.get().getPosY().get());
        batPosMap.put("player3y", player3Prop.get().getPosY().get());
        for (String key : observers.keySet()) {
            try {
                observers.get(key).setPlayerBatPositions(batPosMap);
            } catch (RemoteException ex) {
                System.out.println("remoteException on setting player bat locations for " + key);
                this.removeObserver(key);
            }
        }
    }

    /**
     * Attempts to bind the next player. returns false if there are three
     * players present already. Initiates bat and score listeners if null.
     *
     * @param input The next player
     * @return
     */
    public boolean bindNextPlayer(Player input) {
        // starts batListener
        if (this.batListener == null) {
            this.batListener = new ChangeListener() {

                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    batUpdated.set(true);
                }
            };
        }

        // starts scoreListener
        if (this.scoreListener == null) {
            this.scoreListener = new ChangeListener() {

                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    scoreUpdated.set(true);
                }
            };
        }

        if (this.player1Prop.get() == null) {
            // binds player 1
            this.player1Prop.set(input);
            this.player1Score.bind(input.getScore());
            this.player1Prop.get().getPosX().addListener(batListener);
            this.player1Prop.get().getPosY().addListener(batListener);
            this.player1Score.addListener(scoreListener);
        } else if (this.player2Prop.get() == null) {
            // binds player 2
            this.player2Prop.set(input);
            this.player2Score.bind(input.getScore());
            this.player2Prop.get().getPosX().addListener(batListener);
            this.player2Prop.get().getPosY().addListener(batListener);
            this.player2Score.addListener(scoreListener);
        } else if (this.player3Prop.get() == null) {
            // binds player 3
            this.player3Prop.set(input);
            this.player3Score.bind(input.getScore());
            this.player3Prop.get().getPosX().addListener(batListener);
            this.player3Prop.get().getPosY().addListener(batListener);
            this.player3Score.addListener(scoreListener);

            // pushes player stuff after all three players have joined
            this.pushBatPositions();
            this.pushScore();
        } else {
            return false;
        }
        return true;
    }

    /**
     * Broadcasts end of game to all listeners. Declares end of game for
     * scheduled updater.
     *
     * @param hasLeft The name of the player who left, causing the end of the 
     * game, if any
     */
    public void broadcastEndGame(String hasLeft) {
        System.out.println("broadcasting end game");
        tryExecute(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    System.out.println("Error in thread.sleep on game endgame");
                }
                for (String key : observers.keySet()) {
                    try {
                        if (hasLeft == null || !key.equals(hasLeft)) {
                            observers.get(key).endGame();
                        }
                    } catch (RemoteException ex) {
                        Logger.getLogger(GamePublisher.class.getName()).log(Level.SEVERE, null, ex);
                        System.out.println("RemoteException ending game for " + key);
                    }
                }
                publisherShutDown.set(true);
            }
        });
    }
}
