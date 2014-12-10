/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Server.Publishers;

import com.badlogic.gdx.math.Vector2;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import s32a.Server.Player;
import s32a.Shared.IGameClient;
import s32a.Shared.ISpectator;
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
    private ObservableList<ISpectator> spectators;
    private ObjectProperty<Vector2> puckPosition;
    private IntegerProperty player1Score, player2Score, player3Score, roundNo;
    private ObservableList<String> chatbox;
    private ObjectProperty<GameStatus> statusProp;
    private StringProperty difficultyProp;

    /**
     * Creates a new publisher associated with given game. Observers need to be
     * manually registered.
     *
     * @param myGame
     */
    public GamePublisher(Game myGame) {
        this.observers = new ConcurrentHashMap<>();
        this.myGame = myGame;
        this.spectators = null;
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
        if (!this.observers.containsKey(name)) {
            this.observers.put(name, client);
        } else {
            return false;
        }

        client.setGame(myGame);
        this.pushChat();
        this.pushDifficulty();
        this.pushPlayer1Position();
        this.pushPlayer1Score();
        this.pushPlayer2Position();
        this.pushPlayer2Score();
        this.pushPlayer3Position();
        this.pushPlayer3Score();
        this.pushPuckPosition();
        this.pushRoundNo();
        this.pushSpectators();
        this.pushStatus();
        return true;
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
        this.pushSpectators();
        this.spectators.addListener(new ListChangeListener() {

            @Override
            public void onChanged(ListChangeListener.Change c) {
                pushSpectators();
            }
        });
    }

    /**
     * Pushes the (updated) list of spectators to all observers.
     */
    private void pushSpectators() {
        for (Iterator<String> it = observers.keySet().iterator(); it.hasNext();) {
            String key = it.next();
            try {
                observers.get(key).setSpectators(new ArrayList(spectators));
            }
            catch (RemoteException ex) {
                System.out.println("RemoteException pushing spectators to " + key + ": " + ex.getMessage());
                Logger.getLogger(GamePublisher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Binds roundNo to given roundNo. Adds Listener responsible for pushing
     * updates.
     *
     * @param roundNo
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

    /**
     * Binds difficulty provided as string - Double version not required. Only
     * needs to be done once.
     *
     * @param difficulty
     */
    public void bindDifficulty(StringProperty difficulty) {
        this.difficultyProp.bind(difficulty);
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

    /**
     * Sets chatbox observableList to point to actual chatbox. Adds
     * ListChangeListener responsible for pushing updates to clients.
     *
     * @param chat
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

    /**
     * Binds Vector2 property containing puck x and y. Assigns listeners
     * responsible for pushing value changes.
     *
     * @param position
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
        for (String key : observers.keySet()) {
            try {
                observers.get(key).setPuck(puckPosition.get().x, puckPosition.get().y);
            }
            catch (RemoteException ex) {
                System.out.println("remoteException on setting puck location for " + key);
                Logger.getLogger(GamePublisher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Binds statusproperty. Only needs to be done once.
     *
     * @param input
     */
    public void bindStatus(ObjectProperty<GameStatus> input) {
        this.statusProp.bind(input);
        this.pushStatus();
        this.statusProp.addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {

            }
        });
    }

    /**
     * pushes updated status to all observers.
     */
    private void pushStatus() {
        for (String key : observers.keySet()) {
            try {
                observers.get(key).setStatus(statusProp.get());
            }
            catch (RemoteException ex) {
                System.out.println("remoteException on setting status for " + key);
                Logger.getLogger(GamePublisher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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
        this.pushPlayer1Position();
        this.pushPlayer1Score();

        ChangeListener posListener = new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                pushPlayer1Position();
            }
        };
        this.player1Prop.get().getPosX().addListener(posListener);
        this.player1Prop.get().getPosY().addListener(posListener);

        this.player1Score.addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                pushPlayer1Score();
            }
        });
    }

    /**
     * Pushes player1 puck position to all observers.
     */
    private void pushPlayer1Position() {
        for (String key : observers.keySet()) {
            try {
                observers.get(key).setPlayer1Bat(player1Prop.get().getPosX().get(), player1Prop.get().getPosX().get());
            }
            catch (RemoteException ex) {
                System.out.println("remoteException on setting player 1 bat location for " + key);
                Logger.getLogger(GamePublisher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Pushes player1 score to all observers.
     */
    private void pushPlayer1Score() {
        for (String key : observers.keySet()) {
            try {
                observers.get(key).setPlayer1Score(player1Score.get());
            }
            catch (RemoteException ex) {
                System.out.println("remoteException on setting player 1 score for " + key);
                Logger.getLogger(GamePublisher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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
        this.pushPlayer2Position();
        this.pushPlayer2Score();

        ChangeListener posListener = new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                pushPlayer2Position();
            }
        };
        this.player2Prop.get().getPosX().addListener(posListener);
        this.player2Prop.get().getPosY().addListener(posListener);

        this.player2Score.addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                pushPlayer2Score();
            }
        });
    }

    /**
     * Pushes player 2 puck position (x and y) to all observers.
     */
    private void pushPlayer2Position() {
        for (String key : observers.keySet()) {
            try {
                observers.get(key).setPlayer2Bat(player2Prop.get().getPosX().get(), player2Prop.get().getPosY().get());
            }
            catch (RemoteException ex) {
                System.out.println("remoteException on setting player 2 bat location for " + key);
                Logger.getLogger(GamePublisher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Pushes player 2 score to all observers.
     */
    private void pushPlayer2Score() {
        for (String key : observers.keySet()) {
            try {
                observers.get(key).setPlayer2Score(player2Score.get());
            }
            catch (RemoteException ex) {
                System.out.println("remoteException on setting player 2 score for " + key);
                Logger.getLogger(GamePublisher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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
        this.pushPlayer3Position();
        this.pushPlayer3Score();

        ChangeListener posListener = new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                pushPlayer3Position();
            }
        };
        this.player3Prop.get().getPosX().addListener(posListener);
        this.player3Prop.get().getPosY().addListener(posListener);

        this.player3Score.addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                pushPlayer3Score();
            }
        });
    }

    /**
     * Pushes position of player 3 puck (x + y) to all observers.
     */
    private void pushPlayer3Position() {
        for (String key : observers.keySet()) {
            try {
                observers.get(key).setPlayer3Bat(player3Prop.get().getPosX().get(), player3Prop.get().getPosY().get());
            }
            catch (RemoteException ex) {
                System.out.println("remoteException on setting player 3 bat location for " + key);
                Logger.getLogger(GamePublisher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Pushes player 3 score to all observers.
     */
    private void pushPlayer3Score() {
        for (String key : observers.keySet()) {
            try {
                observers.get(key).setPlayer3Score(player3Score.get());
            }
            catch (RemoteException ex) {
                System.out.println("remoteException on setting player 3 score for " + key);
                Logger.getLogger(GamePublisher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
