/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Server;

import s32a.Shared.enums.GameSetting;
import s32a.Server.Lobby;
import com.badlogic.gdx.math.Vector2;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.Setter;
import s32a.Server.Publishers.GamePublisher;
import s32a.Shared.enums.Colors;
import s32a.Shared.enums.GameStatus;
import s32a.Shared.*;
import s32a.Shared.ISpectator;
import s32a.Shared.enums.LobbySetting;

/**
 * @author Kargathia
 */
public class Game extends UnicastRemoteObject implements IGame {

    private ObservableList<ISpectator> mySpectators;
    private ObservableList<IPlayer> myPlayers;

    @Getter
    private ObjectProperty<GameStatus> statusProp;
    @Getter
    private StringProperty gameTime;
    @Getter
    private IntegerProperty roundNo;

    @Getter
    private Map<GameSetting, Object> gameInfo;

    private Chatbox myChatbox;
    private Puck myPuck;
    private GamePublisher publisher;
    protected boolean continueRun = false;
    private Timer puckTimer;
    private AtomicInteger countDownTime;

    private final float defaultSpeed = 15f;
    private int maxRounds = 10;

    /**
     * Constructor. Initialises sideLength, isPaused, gameID and roundNo to
     * default values gameID is a combination of starting player, and exact
     * start date/time (should be put in gameInfo)
     *
     * @param starter The player that starts the game initially
     */
    Game(IPlayer starter) throws RemoteException {
        this.myPlayers = FXCollections.observableArrayList(new ArrayList<>());
        this.mySpectators = FXCollections.observableArrayList(new ArrayList<>());

        this.myPlayers.add(starter);

        setBatPosition(starter, 0);

        ((Player) starter).setMyGame(this);
        ((Player) starter).setStarter(true);

        this.gameInfo = new HashMap();
        this.gameInfo.put(GameSetting.GameID, starter.getName()
                + System.currentTimeMillis());
        this.gameInfo.put(GameSetting.NextColor, this.getNextColor());

        this.roundNo = new SimpleIntegerProperty(0);
        this.myPuck = new Puck(defaultSpeed, this);
        this.adjustDifficulty();
        this.puckTimer = new Timer();
        this.gameTime = new SimpleStringProperty("00:00");
        this.statusProp = new SimpleObjectProperty<>(GameStatus.Preparing);
        this.addForceListChangeListeners();
        this.countDownTime = new AtomicInteger(-1);

        this.myChatbox = new Chatbox();
    }

    /**
     * Forces the collection in lobby to fire change events. This is neccessary
     * for proper operation of JavaFX elements displaying games in Lobby.
     */
    private void addForceListChangeListeners() {
        ChangeListener propListener = new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                try {
                    Lobby lobby = Lobby.getSingle();
                    lobby.forceMapUpdate(lobby.getActiveGames());
                }
                catch (RemoteException ex) {
                    System.out.println("RemoteException on "
                            + "forcibly updating activegames from Game: "
                            + ex.getMessage());
                }
            }
        };

        this.statusProp.addListener(propListener);
        this.myPuck.getSpeed().addListener(propListener);
    }

    /**
     * Starts publisher associated with game. Done outside constructor to avoid
     * issues with trying to use not-yet-initialised fields.
     *
     * @param starter The player creating the game
     * @param starterClient The gameclient of the player creating the game
     * @throws RemoteException
     */
    public void startPublisher(Player starter, IGameClient starterClient) throws RemoteException {
        this.publisher = new GamePublisher(this);
        this.publisher.bindPuckPosition(this.myPuck.getPosition());
        this.publisher.bindChat(this.myChatbox.chatProperty());
        this.publisher.bindNextPlayer(starter);
        this.publisher.bindRoundNo(this.roundNo);
        this.publisher.bindStatus(this.statusProp);
        this.publisher.bindDifficulty(this.myPuck.getSpeed());
        this.publisher.bindPlayers(myPlayers);
        this.publisher.addObserver(starter.getName(), starterClient);
    }

    /**
     * Calls ChatBox.addMessage(string) with a pre-formatted message - this
     * includes player name and timestamp appended to the message string
     *
     * @param message The message that is going to be sent to the chat
     * @param from The player that is sending the message
     * @return True if everything went right, and chatbox.addchatmessage
     * returned true
     * @throws java.rmi.RemoteException
     */
    @Override
    public boolean addChatMessage(String message, String from) throws RemoteException {
        if (message == null || from == null) {
            throw new IllegalArgumentException("sender or message is null");
        }
        if (message.trim().isEmpty()) {
            throw new IllegalArgumentException("message is empty");
        }
        if (from.trim().isEmpty()) {
            throw new IllegalArgumentException("anonymous sender");
        }
        return myChatbox.addChatMessage(message, from);
    }

    /**
     * Adds the provided player to the next open player slot. If player is a
     * bot, then implement it as bot (iteration 1) sets nextColor in gameID to
     * the next available color
     *
     * @param playerInput The player that's going to be added to the active game
     * player color can be retrieved from gameID.get("nextColor")
     * @param client The gameclient of the added player
     * @return returns true when the player was successfully added returns false
     * when game is full, or player is already a participant also returns false
     * when anything wonky happens
     * @throws java.rmi.RemoteException
     */
    public boolean addPlayer(IPlayer playerInput, IGameClient client) throws RemoteException {
        if (playerInput != null && client != null) {
            Player player = (Player) playerInput;
            if (!myPlayers.contains(player)) {
                if (myPlayers.size() < 3) {
                    this.gameInfo.put(GameSetting.NextColor, getNextColor());

                    this.myPlayers.add(player);
                    this.setBatPosition(player, myPlayers.size() - 1);
                    this.publisher.addObserver(player.getName(), client);
                    this.publisher.bindNextPlayer(player);
                    player.setMyGame(this);
                    this.adjustDifficulty();
                    this.addChatMessage("has joined the game", player.getName());

                    if (myPlayers.size() == 3) {
                        this.statusProp.set(GameStatus.Ready);
                        this.addChatMessage("-- Ready to Start --", "GAME");
                    }
                    return true;
                }
            }
        } else {
            throw new IllegalArgumentException("Player or Client input was null");
        }
        return false;
    }

    /**
     * sets initial bat position for given player
     * @param pInput The player whose bat's position should be set
     * @param playerID The id of the player
     * @throws RemoteException 
     */
    private void setBatPosition(IPlayer pInput, int playerID) throws RemoteException {
        if (pInput == null) {
            return;
        }
        Player p = (Player) pInput;

        float width = (float) Lobby.getSingle().getAirhockeySettings().get(LobbySetting.SideLength);
        Vector2 batPos;

        if (playerID == 0) {
            //Player red
            batPos = new Vector2(0, 0);
        } else {
            // Left corner of triangle
            double aX = -width / 2;
            double aY = 0;
            // Top corner of triangle
            double bX = 0;
            double bY = width * Math.sin(Math.toRadians(60));
            // Right corner of triangle
            double cX = width / 2;
            double cY = 0;

            if (playerID == 1) {
                batPos = new Vector2((float) (aX + ((bX - aX) / 100 * 50)),
                        (float) ((aY + ((bY - aY) / 100 * 50))));
            } else {
                batPos = new Vector2((float) (cX + ((bX - cX) / 100 * 50)),
                        (float) ((cY + ((bY - cY) / 100 * 50))));
            }
        }
        p.setPosX(new SimpleDoubleProperty(batPos.x));
        p.setPosY(new SimpleDoubleProperty(batPos.y));
    }

    /**
     * Adds the provided player to the next
     *
     * @param spectator The spectator that's going to be added to the active
     * game
     * @param client The gameclient of the spectator to be added
     * @return returns true when the spectator was successfully added. false
     * when the spectator was already associated with this game also false if
     * the method failed to add for any other reason
     * @throws java.rmi.RemoteException
     */
    public boolean addSpectator(ISpectator spectator, IGameClient client) throws IllegalArgumentException, RemoteException {
        if (spectator != null && client != null) {
            for (ISpectator spect : this.mySpectators) {
                if (spect.getName().equals(spectator.getName())) {
                    return false;
                }
            }
            if (((Spectator) spectator).addGame(this)) {
                this.mySpectators.add(spectator);
                this.publisher.addObserver(spectator.getName(), client);
                this.addChatMessage(spectator.getName() + " is now spectating",
                        "GAME");
                return true;
            } else {
                return false;
            }
        } else {
            throw new IllegalArgumentException("spectator is null");
        }
    }

    /**
     * removes given spectator from the list. Gameclient is not needed, as it
     * can be removed on playername.
     *
     * @param spectator The spectator that needs to be removed from the active
     * game
     * @return returns true if the spectator was successfully removed
     * @throws java.rmi.RemoteException
     */
    public boolean removeSpectator(ISpectator spectator) throws RemoteException {
        if (spectator != null) {
            if (mySpectators.contains(spectator)) {
                ((Spectator) spectator).removeGame(this);
                mySpectators.remove(spectator);
                publisher.removeObserver(spectator.getName());
                this.addChatMessage(spectator.getName() + " stopped spectating", "GAME");
                return true;
            }
        } else {
            throw new IllegalArgumentException("spectator input was null");
        }
        return false;
    }

    /**
     * starts the entire game - startRound() is responsible for starting a new
     * round
     *
     * @return returns true if the game was started returns false if the game
     * was unable to start for any reason
     * @throws java.rmi.RemoteException
     */
    @Override
    public boolean beginGame() throws RemoteException {
        if ((myPlayers.size() == 3) && (roundNo.get() == 0)) {
                //Timer will keep going until game end
                long interval = 20;
                puckTimer.scheduleAtFixedRate(myPuck, 1000, interval);
                this.gameInfo.put(GameSetting.GameStartTime, System.currentTimeMillis());
                this.startCountDown();
                return true;
        } else {
            return false;
        }
    }

    /**
     * Puckspeed functions as difficulty lever - min and max values to be
     * determined Can only be called if the game has not yet begun
     *
     * @param puckSpeed The speed of the puck
     * @return returns true if the speed has been successfully adjusted. returns
     * false if it was unable to adjust puck speed throws
     * IllegalArgumentException when given puckspeed was outside min/max values
     * @throws java.rmi.RemoteException
     */
    @Override
    public boolean adjustDifficulty(float puckSpeed) throws RemoteException {
        if (this.roundNo.get() == 0) {
            float min = 10;
            float max = 40;

            if (puckSpeed >= min && puckSpeed <= max) {
                myPuck.setSpeed(Math.round(puckSpeed));
                return true;
            } else {
                throw new IllegalArgumentException("Puckspeed was too high or low");
            }
        } else {
            //Can't adjust difficulty if game has already begun
            return false;
        }
    }

    /**
     * lets the game decide what the difficulty should be for his players
     *
     * @return true if successfully set puckspeed
     * @throws java.rmi.RemoteException
     */
    @Override
    public boolean adjustDifficulty() throws RemoteException {
        if (myPlayers.isEmpty()) {
            return false;
        }
        double averageRating = 0;
        for (IPlayer p : myPlayers) {
            averageRating += ((Player) p).ratingProperty().get();
        }
        averageRating = averageRating / myPlayers.size();

        if (averageRating < 10) {
            averageRating = 10;
        } else if (averageRating > 40) {
            averageRating = 40;
        }
        return adjustDifficulty((float) averageRating);
    }

    /**
     * Pauses or unpauses the game, based on input a paused game does not update
     * puck, bat, or score. chatbox remains enabled
     *
     * @param isPaused Set true if the game needs to be paused, false for
     * un-pausing
     * @return returns true if the pause change was successful. return false if
     * desired pause state == Game.isPaused
     * @throws java.rmi.RemoteException
     */
    @Override
    public boolean pauseGame(boolean isPaused) throws RemoteException {

        if (this.statusProp.get().equals(GameStatus.Playing) && isPaused) {
            this.statusProp.set(GameStatus.Paused);
        } else if (this.statusProp.get().equals(GameStatus.Paused) && !isPaused) {
            this.statusProp.set(GameStatus.Playing);
        } else {
            return false;
        }

        return true;
    }

    /**
     * Starts a new round within the running game rounds are ended automatically
     * within Game.run() whenever someone scores
     *
     * @throws java.rmi.RemoteException
     */
    public void startRound() throws RemoteException {
        if(this.statusProp.get().equals(GameStatus.GameOver)){
            return;
        }
        //Start new round
        this.setRoundNo(this.roundNo.get() + 1);
        this.statusProp.set(GameStatus.Playing);

        if (myPuck != null) {
            //BEGIN PUCK MOVEMENT
            this.continueRun = true; //This allows Puck to be moved
        }
    }

    /**
     * Starts a 4-second countdown before each round - including the first.
     */
    private void startCountDown() throws RemoteException {
        if(this.statusProp.get().equals(GameStatus.GameOver)){
            return;
        }
        this.statusProp.set(GameStatus.Waiting);
        this.countDownTime.set(4);

        TimerTask countDown = new TimerTask() {

            @Override
            public void run() {
                if (countDownTime.decrementAndGet() < 0) {
                    countDownTime.set(-1);
                    try {
                        startRound();
                        this.cancel();
                    }
                    catch (RemoteException ex) {
                        System.out.println("RemoteException in startCountdown "
                                + "starting new round: " + ex.getMessage());
                    }
                }
            }
        };

        puckTimer.schedule(countDown, 1000, 1000);
    }

    // not featured in Interface, as it is only called by Puck
    void endRound() throws RemoteException {
        //END OF PUCK MOVEMENT
        this.continueRun = false;
        this.myPuck.resetPuck();

        if (roundNo.get() >= maxRounds) {
            //End game
            Lobby.getSingle().endGame(this.getID(), null);
        } else if(!this.statusProp.get().equals(GameStatus.GameOver)) {
            //new round is started at the end of countdown
            this.startCountDown();
        }
    }

    /**
     * Broadcasts end of game to all unaware clients.
     * @param hasLeft The player who has left, causing the game to end, if any
     */
    public void broadcastEndGame(String hasLeft) {
        try {
            this.addChatMessage("-- Game Over --", "GAME");
            this.statusProp.set(GameStatus.GameOver);
            this.endRound();
            publisher.broadcastEndGame(hasLeft);
        }
        catch (RemoteException ex) {
            System.out.println("RemoteException in broadcastEndGame: " + ex.getMessage());
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * gets the color the next player to join should be assigned Not featured in
     * interface, as it is called only in server setting
     *
     * @return the color the next player should have, cycling red, blue, green
     * returns null if game already has three players
     */
    Colors getNextColor() {
        switch (myPlayers.size()) {
            case 0:
                return Colors.Red;
            case 1:
                return Colors.Blue;
            case 2:
                return Colors.Green;
            default:
                return null;
        }
    }

    /**
     * toString method
     * @return gameID
     */
    @Override
    public String toString() {
        return (String) gameInfo.get(GameSetting.GameID);
    }

    /**
     * Used to set properties of Puck for customization of unit tests Not
     * implemented from interface
     *
     * @param position the start position (Vector2) of the Puck
     * @param puckSpeed the speed of the Puck
     * @param direction the start direction of the Puck
     * @param runCount the number of times the run() method of Puck should be
     * called
     * @param maxRounds the maximum number of rounds to be played in the game
     */
    public void customSetup(Vector2 position, float puckSpeed,
            float direction, int runCount, int maxRounds) {
        //Caution: puck position and direction are reset to default after the first round has ended

        if (position != null) {
            if (this.myPuck.isOutsideField(position) == null) {
                //Inside of field
                this.myPuck.getPosition().set(position);
            } else {
                //Outside of field
                throw new IllegalArgumentException();
            }
        }

        if (puckSpeed > 0 && puckSpeed < 100) {
            this.myPuck.setSpeed(puckSpeed);
        }

        if (direction >= 0 && direction < 360) {
            this.myPuck.setDirection(direction);
        }

        if (runCount > 0 && runCount < 5000) {
            this.myPuck.setRunCount(runCount);
        }

        if (maxRounds > 0 && maxRounds <= 10) {
            this.maxRounds = maxRounds;
        }
    }

    /**
     * Gets the puck
     * @return Puck
     */
    public Puck getMyPuck() {
        return this.myPuck;
    }

    /**
     * Sets gameTime - non threadsafe
     *
     * @param input Game time as string
     */
    public void setGameTime(String input) {
        this.gameTime.set(input);
    }

    /**
     * threadsafe set of roundNo
     *
     * @param input The round number
     */
    private void setRoundNo(int input) {
        roundNo.set(input);
    }

    /**
     *
     * @return game status, formatted as GameStatus enum, packed in a property
     */
    public ObjectProperty<GameStatus> statusProperty() {
        return this.statusProp;
    }

    /**
     * loads the name of player 1 in a StringProperty
     *
     * @return Returns the name of player 1
     */
    public StringProperty player1NameProperty() {
        return this.playerNameProp(0);
    }

    /**
     * loads the name of player 2 in a StringProperty
     *
     * @return Returns the name of player 2
     */
    public StringProperty player2NameProperty() {
        return this.playerNameProp(1);
    }

    /**
     * loads the name of Player 3 in a StringProperty
     *
     * @return Returns the name of player 3
     */
    public StringProperty player3NameProperty() {
        return this.playerNameProp(2);
    }

    /**
     * returns playername property at given index - used by player<X>Property
     *
     * @param index The index of the player
     * @return
     */
    private StringProperty playerNameProp(int index) {
        if (this.myPlayers.size() <= index) {
            return new SimpleStringProperty("--");
        } else {
            return ((Player) this.myPlayers.get(index)).nameProperty();
        }
    }

    /**
     * Gets the game's players
     * @return A list of the game's players
     */
    public List<IPlayer> getMyPlayers() {
        return new ArrayList<>(myPlayers);
    }

    /**
     * Gets the game's spectators
     * @return A list of the game's spectators
     */
    public List<ISpectator> getMySpectators() {
        return new ArrayList<>(mySpectators);
    }

    /**
     * Gets the difficulty of the game
     * @return
     * @throws RemoteException 
     */
    @Override
    public String getDifficulty() throws RemoteException {
//        return this.difficultyProp.get();
        if(this.myPuck == null || this.myPuck.getSpeed() == null){
            return "-1";
        }
        return String.valueOf(this.myPuck.getSpeed().get());
    }

    /**
     * Gets the name of player 1
     * @return Returns the name of player 1
     * Returns "-" if name cannot be found
     * @throws RemoteException 
     */
    @Override
    public String getPlayer1Name() throws RemoteException {
        if (this.myPlayers.size() > 0) {
            return this.myPlayers.get(0).getName();
        } else {
            return "-";
        }
    }

    /**
     * Gets the name of player 2
     * @return Returns the name of player 2
     * Returns "-" if name cannot be found
     * @throws RemoteException 
     */
    @Override
    public String getPlayer2Name() throws RemoteException {
        if (this.myPlayers.size() > 1) {
            return this.myPlayers.get(1).getName();
        } else {
            return "-";
        }
    }

    /**
     * Gets the name of player 3
     * @return Returns the name of player 3
     * Returns "-" if name cannot be found
     * @throws RemoteException 
     */
    @Override
    public String getPlayer3Name() throws RemoteException {
        if (this.myPlayers.size() > 2) {
            return this.myPlayers.get(2).getName();
        } else {
            return "-";
        }
    }

    /**
     * Gets the status of the game
     * @return Returns the status
     * @throws RemoteException 
     */
    @Override
    public String getStatus() throws RemoteException {
        return this.statusProp.get().toString();
    }

    /**
     * Gets the id of the game
     * @return Returns the id
     * @throws RemoteException 
     */
    @Override
    public String getID() throws RemoteException {
        return (String) this.gameInfo.get(GameSetting.GameID);
    }

    /**
     * Gets the countdown time of the game
     * @return Returns the countdown time
     * @throws RemoteException 
     */
    @Override
    public int getCountDownTime() throws RemoteException {
        return this.countDownTime.get();
    }

    /**
     * Gets the start time of the game
     * @return Returns the start time
     * @throws RemoteException 
     */
    @Override
    public long getGameStartTime() throws RemoteException {
        if(!this.gameInfo.containsKey(GameSetting.GameStartTime)){
            return -1L;
        }
        return (Long)this.gameInfo.get(GameSetting.GameStartTime);
    }
}
