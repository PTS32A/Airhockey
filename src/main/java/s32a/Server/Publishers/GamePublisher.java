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
import java.util.Set;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import s32a.Server.Game;
import s32a.Shared.IGameClient;
import s32a.Shared.IPlayer;
import s32a.Shared.ISpectator;

/**
 * NOTES: operations are currently not threadsafe. Some sort of monitor solution
 * needs to be implemented to prevent concurrent updates / reads on observers.
 *
 * @author Kargathia
 */
public class GamePublisher {

    /**
     * Collection of observers of this game (Players + Spectators) Key = name
     */
    private HashMap<String, IGameClient> observers;

    /**
     * the Game associated with this publisher
     */
    private Game myGame;

    private DoubleProperty puckX, puckY;
    private DoubleProperty player1X, player1Y;
    private DoubleProperty player2X, player2Y;
    private DoubleProperty player3X, player3Y;
    private IntegerProperty player1Score, player2Score, player3Score, roundNo;
    private ObservableList<String> chatbox = null;

    /**
     * Creates a new publisher associated with given game. Observers need to be
     * manually registered.
     *
     * @param myGame
     */
    public GamePublisher(Game myGame) {
        this.observers = new HashMap<>();
        this.myGame = myGame;

        this.puckX = new SimpleDoubleProperty(-1);
        this.puckY = new SimpleDoubleProperty(-1);

        this.player1X = new SimpleDoubleProperty(-1);
        this.player1Y = new SimpleDoubleProperty(-1);

        this.player2X = new SimpleDoubleProperty(-1);
        this.player2Y = new SimpleDoubleProperty(-1);

        this.player3X = new SimpleDoubleProperty(-1);
        this.player3Y = new SimpleDoubleProperty(-1);

        this.player1Score = new SimpleIntegerProperty(-1);
        this.player2Score = new SimpleIntegerProperty(-1);
        this.player3Score = new SimpleIntegerProperty(-1);

        this.roundNo = new SimpleIntegerProperty(-1);
    }

    /**
     * Tries to add an observer to the list. If the list already contains given
     * observer, return false;
     *
     * @param name
     * @param client
     * @return
     */
    public boolean addObserver(String name, IGameClient client) {
        if (this.observers.containsKey(name)) {
            return false;
        }
        return (this.observers.put(name, client) == null);
    }

    /**
     * Removes observer of given name from the list. Realistically will only be
     * called on a spectator leaving, as a player leaving triggers deletion of
     * the entire game.
     *
     * @param name
     */
    public void removeObserver(String name) {
        this.observers.remove(name);
    }

    public void setPlayers(List<IPlayer> players) {
        // TODO
    }

    public void setSpectators(List<ISpectator> spectators) {
        // TODO
    }

    /**
     * Binds roundNo to given roundNo. Adds Listener responsible for pushing
     * updates.
     *
     * @param roundNo
     */
    public void setRoundNo(IntegerProperty roundNo) {
        this.roundNo.bind(roundNo);
        this.roundNo.addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                for (Iterator<String> it = observers.keySet().iterator(); it.hasNext();) {
                    String key = it.next();
                    try {
                        observers.get(key).setRoundNo(roundNo.get());
                    }
                    catch (RemoteException ex) {
                        System.out.println("RemoteException setting roundNo for " + key + ": " + ex.getMessage());
                        Logger.getLogger(GamePublisher.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
    }

    /**
     * Sets chatbox observableList to point to actual chatbox. Adds
     * ListChangeListener responsible for pushing updates to clients.
     *
     * @param chat
     */
    public void setChat(ObservableList<String> chat) {
        this.chatbox = chat;
        this.chatbox.addListener(new ListChangeListener() {

            @Override
            public void onChanged(ListChangeListener.Change c) {
                ArrayList<String> chatArray = new ArrayList<>(chatbox);
                for (String key : observers.keySet()) {
                    try {
                        observers.get(key).setChat(chatArray);
                    }
                    catch (RemoteException ex) {
                        System.out.println("RemoteException on pushing chatbox to " + key + ": " + ex.getMessage());
                        Logger.getLogger(GamePublisher.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }
        });
    }

    /**
     * Binds properties bound with Puck X and y coords. Assigns listeners and
     * pushes value changes.
     *
     * @param puckX
     * @param puckY
     */
    public void bindPuck(DoubleProperty puckX, DoubleProperty puckY) {
        this.puckX.bind(puckX);
        this.puckY.bind(puckY);

        ChangeListener listener = new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                for (String key : observers.keySet()) {
                    try {
                        observers.get(key).setPuck(puckX.get(), puckY.get());
                    }
                    catch (RemoteException ex) {
                        System.out.println("remoteException on setting puck location");
                        Logger.getLogger(GamePublisher.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };

        this.puckX.addListener(listener);
        this.puckY.addListener(listener);
    }

    /**
     * Binds properties belonging to player 1. Adds ChangeListeners responsible
     * for pushing updates.
     *
     * @param x
     * @param y
     * @param score
     */
    public void bindPlayer1(DoubleProperty x, DoubleProperty y, IntegerProperty score) {
        this.player1X.bind(x);
        this.player1Y.bind(y);
        this.player1Score.bind(score);

        ChangeListener posListener = new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                for (String key : observers.keySet()) {
                    try {
                        observers.get(key).setPlayer1Bat(player1X.get(), player1Y.get());
                    }
                    catch (RemoteException ex) {
                        System.out.println("remoteException on setting player 1 bat location");
                        Logger.getLogger(GamePublisher.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        this.player1X.addListener(posListener);
        this.player1Y.addListener(posListener);

        this.player1Score.addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                for (String key : observers.keySet()) {
                    try {
                        observers.get(key).setPlayer1Score(player1Score.get());
                    }
                    catch (RemoteException ex) {
                        System.out.println("remoteException on setting player score");
                        Logger.getLogger(GamePublisher.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
    }

    /**
     * Binds properties belonging to player 2. Adds ChangeListeners responsible
     * for pushing updates.
     *
     * @param x
     * @param y
     * @param score
     */
    public void bindPlayer2(DoubleProperty x, DoubleProperty y, IntegerProperty score) {
        this.player2X.bind(x);
        this.player2Y.bind(y);
        this.player2Score.bind(score);

        ChangeListener posListener = new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                for (String key : observers.keySet()) {
                    try {
                        observers.get(key).setPlayer2Bat(player2X.get(), player2Y.get());
                    }
                    catch (RemoteException ex) {
                        System.out.println("remoteException on setting player 2 bat location");
                        Logger.getLogger(GamePublisher.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        this.player2X.addListener(posListener);
        this.player2Y.addListener(posListener);

        this.player2Score.addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                for (String key : observers.keySet()) {
                    try {
                        observers.get(key).setPlayer2Score(player2Score.get());
                    }
                    catch (RemoteException ex) {
                        System.out.println("remoteException on setting player 2 score");
                        Logger.getLogger(GamePublisher.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
    }

    /**
     * Binds properties belonging to player 3. Adds ChangeListeners responsible
     * for pushing updates.
     *
     * @param x
     * @param y
     * @param score
     */
    public void bindPlayer3(DoubleProperty x, DoubleProperty y, IntegerProperty score) {
        this.player3X.bind(x);
        this.player3Y.bind(y);
        this.player3Score.bind(score);

        ChangeListener posListener = new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                for (String key : observers.keySet()) {
                    try {
                        observers.get(key).setPlayer3Bat(player3X.get(), player3Y.get());
                    }
                    catch (RemoteException ex) {
                        System.out.println("remoteException on setting player 3 bat location");
                        Logger.getLogger(GamePublisher.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        this.player3X.addListener(posListener);
        this.player3Y.addListener(posListener);

        this.player3Score.addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                for (String key : observers.keySet()) {
                    try {
                        observers.get(key).setPlayer3Score(player3Score.get());
                    }
                    catch (RemoteException ex) {
                        System.out.println("remoteException on setting player 3 score");
                        Logger.getLogger(GamePublisher.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
    }

}
