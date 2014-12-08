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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import s32a.Server.Game;
import s32a.Server.Player;
import s32a.Shared.IGameClient;
import s32a.Shared.IPlayer;
import s32a.Shared.ISpectator;
import s32a.Shared.enums.GameStatus;

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

    private ObjectProperty<Player> player1Prop, player2Prop, player3Prop;
    private ObservableList<ISpectator> spectators;
    private DoubleProperty puckX, puckY;
    private IntegerProperty player1Score, player2Score, player3Score, roundNo;
    private ObservableList<String> chatbox = null;
    private ObjectProperty<GameStatus> statusProp;
    private StringProperty difficultyProp;

    /**
     * Creates a new publisher associated with given game. Observers need to be
     * manually registered.
     *
     * @param myGame
     */
    public GamePublisher(Game myGame) {
        this.observers = new HashMap<>();
        this.myGame = myGame;
        this.spectators = null;
        this.statusProp = new SimpleObjectProperty<>(null);

        this.puckX = new SimpleDoubleProperty(-1);
        this.puckY = new SimpleDoubleProperty(-1);

        this.player1Prop = new SimpleObjectProperty<>(null);
        this.player2Prop = new SimpleObjectProperty<>(null);
        this.player3Prop = new SimpleObjectProperty<>(null);

        this.player1Score = new SimpleIntegerProperty(-1);
        this.player2Score = new SimpleIntegerProperty(-1);
        this.player3Score = new SimpleIntegerProperty(-1);

        this.roundNo = new SimpleIntegerProperty(-1);
        this.difficultyProp = new SimpleStringProperty("");
    }

    /**
     * Tries to add an observer to the list. If the list already contains given
     * observer, return false;
     *
     * @param name
     * @param client
     * @return
     * @throws java.rmi.RemoteException
     */
    public boolean addObserver(String name, IGameClient client) throws RemoteException {
        if (this.observers.containsKey(name)) {
            return false;
        }
        if (this.observers.put(name, client) == null){
            client.setGame(myGame);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Removes observer of given name from the list. Realistically will only be
     * called on a spectator leaving, as a player leaving triggers deletion of
     * the entire game.
     *
     * @param name
     * @throws java.rmi.RemoteException
     */
    public void removeObserver(String name) throws RemoteException {
        this.observers.remove(name);
    }

    /**
     * Binds the list of spectators to the publisher. only needs to be done
     * once.
     *
     * @param spectators
     */
    public void bindSpectators(ObservableList<ISpectator> spectators) {
        this.spectators = spectators;

        this.spectators.addListener(new ListChangeListener() {

            @Override
            public void onChanged(ListChangeListener.Change c) {
                for (Iterator<String> it = observers.keySet().iterator(); it.hasNext();) {
                    String key = it.next();
                    try {
                        observers.get(key).setSpectators(new ArrayList(spectators));
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
     * Binds roundNo to given roundNo. Adds Listener responsible for pushing
     * updates.
     *
     * @param roundNo
     */
    public void bindRoundNo(IntegerProperty roundNo) {
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
     * Binds difficulty provided as string - Double version not required
     * @param difficulty
     */
    public void bindDifficulty(StringProperty difficulty){
        this.difficultyProp.bind(difficulty);
        this.difficultyProp.addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                for (Iterator<String> it = observers.keySet().iterator(); it.hasNext();) {
                    String key = it.next();
                    try {
                        observers.get(key).setDifficulty(difficultyProp.get());
                    }
                    catch (RemoteException ex) {
                        System.out.println("RemoteException setting Difficulty for " + key + ": " + ex.getMessage());
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
     * Binds statusproperty
     * @param input
     */
    public void bindStatus(ObjectProperty<GameStatus> input){
        this.statusProp.bind(input);

        this.statusProp.addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                for (String key : observers.keySet()) {
                    try {
                        observers.get(key).setStatus(statusProp.get());
                    }
                    catch (RemoteException ex) {
                        System.out.println("remoteException on setting status");
                        Logger.getLogger(GamePublisher.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
    }

    /**
     * Attempts to bind the next player. returns false if there are three
     * players present already.
     *
     * @param input
     * @return
     */
    public boolean bindNextPlayer(Player input) {
        if (this.player1Prop.get() == null) {
            this.bindPlayer1(input);
        } else if (this.player2Prop.get() == null) {
            this.bindPlayer2(input);
        } else if (this.player3Prop.get() == null) {
            this.bindPlayer3(input);
        } else {
            return false;
        }
        return true;
    }

    /**
     * Binds properties belonging to player 1. Adds ChangeListeners responsible
     * for pushing updates.
     *
     * @param input
     * @param score
     */
    private void bindPlayer1(Player input) {

        this.player1Prop.set(input);
        this.player1Score.bind(input.getScore());

        ChangeListener posListener = new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                for (String key : observers.keySet()) {
                    try {
                        observers.get(key).setPlayer1Bat(player1Prop.get().getPosX().get(), player1Prop.get().getPosX().get());
                    }
                    catch (RemoteException ex) {
                        System.out.println("remoteException on setting player 1 bat location");
                        Logger.getLogger(GamePublisher.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        this.player1Prop.get().getPosX().addListener(posListener);
        this.player1Prop.get().getPosY().addListener(posListener);

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
     * @param input
     * @param score
     */
    private void bindPlayer2(Player input) {

        this.player2Prop.set(input);
        this.player2Score.bind(input.getScore());

        ChangeListener posListener = new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                for (String key : observers.keySet()) {
                    try {
                        observers.get(key).setPlayer2Bat(player2Prop.get().getPosX().get(), player2Prop.get().getPosY().get());
                    }
                    catch (RemoteException ex) {
                        System.out.println("remoteException on setting player 2 bat location");
                        Logger.getLogger(GamePublisher.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        this.player2Prop.get().getPosX().addListener(posListener);
        this.player2Prop.get().getPosY().addListener(posListener);

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
     * @param input
     * @param score
     */
    private void bindPlayer3(Player input) {

        this.player3Prop.set(input);
        this.player3Score.bind(input.getScore());

        ChangeListener posListener = new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                for (String key : observers.keySet()) {
                    try {
                        observers.get(key).setPlayer3Bat(player3Prop.get().getPosX().get(), player3Prop.get().getPosY().get());
                    }
                    catch (RemoteException ex) {
                        System.out.println("remoteException on setting player 3 bat location");
                        Logger.getLogger(GamePublisher.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        this.player3Prop.get().getPosX().addListener(posListener);
        this.player3Prop.get().getPosY().addListener(posListener);

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
