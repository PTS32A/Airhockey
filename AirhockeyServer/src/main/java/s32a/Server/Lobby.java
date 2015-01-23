/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Server;

import com.badlogic.gdx.math.Vector2;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import lombok.Getter;
import s32a.Server.Publishers.LobbyPublisher;
import s32a.Shared.IGame;
import s32a.Shared.IGameClient;
import s32a.Shared.ILobby;
import s32a.Shared.ILobbyClient;
import s32a.Shared.IPerson;
import s32a.Shared.IPlayer;
import s32a.Shared.enums.LobbySetting;
import s32a.Shared.enums.Colors;
import s32a.Shared.enums.GameStatus;

/**
 *
 * @author Kargathia
 */
public class Lobby extends UnicastRemoteObject implements ILobby {

    private static Lobby _singleton;

    /**
     * @return instance of lobby (_singleton). Starts new if it was null.
     * @throws java.rmi.RemoteException
     */
    public static Lobby getSingle() throws RemoteException {
        if (_singleton == null) {
            _singleton = new Lobby();
        }
        return _singleton;
    }

    @Getter
    private Chatbox mychatbox;
    private DatabaseControls myDatabaseControls;
    @Getter
    private ObservableMap<LobbySetting, Object> airhockeySettings;
    @Getter
    private ObservableMap<String, IPerson> activePersons;
    @Getter
    private ObservableMap<String, IGame> activeGames;
    @Getter
    private ObservableList<IPerson> oRankings;
    private LobbyPublisher publisher;

    /**
     * Lobby is used as singleton. Public for unit tests.
     *
     * @throws java.rmi.RemoteException
     */
    public Lobby() throws RemoteException {
        this.mychatbox = new Chatbox();
        this.myDatabaseControls = new DatabaseControls();
        this.activeGames = FXCollections.observableMap(new HashMap<String, IGame>());
        this.activePersons = FXCollections.observableMap(new HashMap<String, IPerson>());
        this.airhockeySettings = FXCollections.observableMap(new HashMap<LobbySetting, Object>());
        this.airhockeySettings.put(LobbySetting.GoalDefault, new Vector2(0, 0));
        this.airhockeySettings.put(LobbySetting.SideLength, 500f);

        try {
            this.oRankings = FXCollections.observableArrayList(myDatabaseControls.getRankings());
        } catch (SQLException ex) {
            System.out.println("SQL exception retrieving getRankings in lobby constructor: "
                    + ex.getMessage());
        }
    }

    /**
     * Starts the associated lobbyPublisher. Can't be done in constructor, as it
     * references lobby instance.
     *
     * @throws RemoteException
     */
    public void startPublisher() throws RemoteException {
        this.publisher = new LobbyPublisher();

        this.publisher.bindActiveGames(activeGames);
        this.publisher.bindRankings(oRankings);
        this.publisher.bindChat(this.mychatbox.chatProperty());
    }

    /**
     * Replaces an item in the list with itself, forcing an ObservableList to
     * fire change events.
     *
     * @param list The observable list to be updated
     */
    public void forceListUpdate(ObservableList list) {
        if (list == null) {
            return;
        }
        list.add(0, null);
        list.remove(0);
    }

    /**
     * Replaces item with given key with itself. Forces an ObservableMap to fire
     * changeEvents.
     *
     * @param map The observablemap to be updated
     */
    public void forceMapUpdate(ObservableMap map) {
        if (map == null) {
            return;
        }
        // TODO: REPLACE THIS WITH SOMETHING LESS UGLY
        map.put(" ", null);
        map.remove(" ", null);
    }

    /**
     * Adds a new person to the database. Does not add them to active persons
     * yet. The database checks for uniqueness.
     *
     * @param playerName can not be null or whitespace can't contain white
     * spaces
     * @param password can not be null or whitespace can't contain white spaces
     * @return True if everything went well false if anything went wrong - or
     * IllegalArgumentException when parameter(s) is/are null or contain
     * trailing / leading white spaces
     * @throws java.sql.SQLException
     * @throws java.rmi.RemoteException
     */
    @Override
    public boolean addPerson(String playerName, String password)
            throws IllegalArgumentException, SQLException, RemoteException {
        if (playerName == null
                || password == null
                || !playerName.trim().equals(playerName)
                || !password.trim().equals(password)) {
            throw new IllegalArgumentException("incorrect input");
        }

        return (this.myDatabaseControls.addPerson(playerName, password) != null);
    }

    /**
     * Checks with the database class whether provided parameters correspond to
     * a player in the database. Adds person returned from database call to the
     * lists of active persons.
     *
     * @param playerName can't be null or whitespace can't contain white spaces
     * @param password can't be null or whitespace can't contain trailing /
     * leading white spaces
     * @param client the provided lobbyclient
     * @throws java.sql.SQLException
     * @throws java.rmi.RemoteException
     * @return the String
     */
    @Override
    public boolean checkLogin(String playerName, String password, ILobbyClient client)
            throws IllegalArgumentException, SQLException, RemoteException {
        if (playerName == null || password == null
                || !playerName.trim().equals(playerName) || !password.trim().equals(password)) {
            throw new IllegalArgumentException("input formatted wrong or null");
        }
        if (client == null) {
            throw new IllegalArgumentException("LobbyClient is null");
        }

        IPerson newPerson = this.myDatabaseControls.checkLogin(playerName, password);
        if (newPerson == null) {
            return false;
        }
        // logs out existing user with this account.
        // does not prevent duplicate login attempt in case of bugs preventing him
        // from using existing session.
        if (this.activePersons.containsKey(playerName)) {
            this.logOut(playerName);
        }

        this.activePersons.put(playerName, newPerson);
        this.publisher.addObserver(playerName, client);
        this.addChatMessage("logged in", playerName);
        return true;
    }

    /**
     * logs out given player. Sets currentPerson to null if it was him. Should
     * always be called with currentPerson by the GUI
     *
     * @param name the name of the player logging out
     * @return whether logout succeeded
     * @throws java.rmi.RemoteException
     */
    @Override
    public boolean logOut(String name) throws RemoteException {
        if (name == null || !this.activePersons.containsKey(name)) {
            return false;
        }

        if (this.activePersons.get(name) instanceof Player) {
            Player playerInput = (Player) this.activePersons.get(name);
            this.endGame(playerInput.getMyGame().getID(), name);
        } else if (this.activePersons.get(name) instanceof Spectator) {
            Spectator spectInput = (Spectator) this.activePersons.get(name);
            for (IGame g : activeGames.values()) {
                ((Game) g).removeSpectator(spectInput);
            }
        }
        this.activePersons.remove(name);
        this.publisher.enforceLogout(name, true);
        this.addChatMessage("logged out", name);

        return true;
    }

    /**
     * clears the entire database - should only be used for reset and debugging
     *
     * @throws java.rmi.RemoteException
     */
    public void clearDatabase() throws RemoteException {
        try {
            myDatabaseControls.clearDatabase();
        } catch (SQLException ex) {
            System.out.println("Error clearing database: " + ex.getMessage());
            Logger.getLogger(Lobby.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Starts a new game, and adds this to the activeGames list Person is
     * converted to Player Person is used as gamestarter for Game constructor
     * parameter person can't already be a Player or Spectator
     *
     * @param name the name of player starting the game
     * @param client the gameclient of the player starting the game
     * @return - the freshly started game if everything went well - null
     * @throws java.rmi.RemoteException
     */
    @Override
    public Game startGame(String name, IGameClient client)
            throws RemoteException, IllegalArgumentException {
        if (name == null || (this.activePersons.get(name) instanceof Player)
                || (this.activePersons.get(name) instanceof Spectator)) {
            return null;
        }
        if (client == null) {
            return null;
        }

        Person person = null;
        Game newGame = null;
        try {
            person = (Person) this.activePersons.get(name);
            Player player = new Player(person.getName(), person.ratingProperty().get(),
                    Colors.Red);
            newGame = new Game(player);
            newGame.startPublisher(player, client);
            this.activePersons.replace(person.getName(), player);
            this.activeGames.put(newGame.getID(), newGame);
        } catch (Exception ex) {
            this.returnToLobby(person);
            this.endGame(newGame.getID(), null);
            throw new IllegalArgumentException("Exception caught: " + ex.getMessage());
        }
        return newGame;
    }

    /**
     * lets currentPerson join an existing game currentPerson is converted to
     * Player currentPerson can't already be a Player or Spectator
     *
     * @param gameInput can't be null
     * @param personInput should be Lobby.currentPerson if called by GUI
     * @param client the gameclient of the player joining the game
     * @return joined game if everything went well null otherwise
     * @throws java.rmi.RemoteException
     */
    @Override
    public Game joinGame(String gameInput, String personInput, IGameClient client)
            throws RemoteException, IllegalArgumentException {
        if (personInput == null || (this.activePersons.get(personInput) instanceof Player)
                || (this.activePersons.get(personInput) instanceof Spectator) || gameInput == null) {
            return null;
        }
        if (client == null) {
            return null;
        }

        Game game = (Game) this.activeGames.get(gameInput);
        Person person = (Person) this.activePersons.get(personInput);
        try {
            Player player;
            if (person.isBot()) {
                player = new Bot(person.getName(), person.ratingProperty().get(),
                        game.getNextColor());
            } else {
                player = new Player(person.getName(), person.ratingProperty().get(),
                        game.getNextColor());
            }
            if (game.addPlayer(player, client)) {
                this.activePersons.replace(person.getName(), player);
                this.forceMapUpdate(activeGames);
            } else {
                return null;
            }
        } catch (Exception ex) {
            for (IPlayer player : game.getMyPlayers()) {
                if (player.getName().equals(personInput)) {
                    this.endGame(game.getID(), player.getName());
                    break;
                }
            }
            this.returnToLobby(person);
            throw new IllegalArgumentException("Exception caught: " + ex.getMessage());
        }
        return game;
    }

    /**
     * lets currentPerson spectate an existing game currentPerson is converted
     * to Spectator. currentPerson can't already be a Player. currentPerson can
     * already be a Spectator
     *
     * @param gameInput can't be null
     * @param personInput should be currentPerson if called by GUI
     * @param client the gameclient of the spectator joining the game
     * @return - Game if everything went well - Null otherwise
     * @throws java.rmi.RemoteException
     */
    @Override
    public IGame spectateGame(String gameInput, String personInput, IGameClient client)
            throws IllegalArgumentException, RemoteException {
        if (personInput == null) {
            throw new IllegalArgumentException("Input is null");
        }
        if (this.activePersons.get(personInput) instanceof Player) {
            throw new IllegalArgumentException("Input is already a Player");
        }
        if (gameInput == null) {
            throw new IllegalArgumentException("Game input is null");
        }
        Game game = (Game) this.activeGames.get(gameInput);
        if (game.statusProperty().get() == GameStatus.GameOver) {
            throw new IllegalArgumentException("Unable to watch a finished game");
        }
        if (game.getRoundNo().get() < 1) {
            throw new IllegalArgumentException("Unable to spectate a game still waiting to start");
        }
        if (client == null) {
            throw new IllegalArgumentException("Game client was null");
        }

        Person person = (Person) this.activePersons.get(personInput);
        try {
            person = new Spectator(person.getName(), person.ratingProperty().get());
            if (!game.addSpectator((Spectator) person, client)) {
                return null;
            }
            this.forceMapUpdate(activeGames);
            this.activePersons.replace(person.getName(), person);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("IllegalArgumentException thrown in spectateGame: " + ex.getMessage());
        }
        return game;
    }

    /**
     * adds a chat message to the lobbychatbox
     *
     * @param message can't be null
     * @param from if method is called from GUI it should always be
     * currentPerson name
     * @return - True if everything went well - False otherwise
     * @throws IllegalArgumentException
     * @throws java.rmi.RemoteException
     */
    @Override
    public boolean addChatMessage(String message, String from)
            throws IllegalArgumentException, RemoteException {
        if (message == null || from == null || from.trim().isEmpty()) {
            throw new IllegalArgumentException("message or poster is null");
        }
        return this.mychatbox.addChatMessage(message, from);
    }

    /**
     * ends the provided game, and returns all participants to the lobby chucks
     * a request to database to calculate new ratings if player was the
     *
     * @param gameInput can be null but will return false
     * @param hasLeft can be null, if game ended normally is not null when a
     * player leaves the game
     * @return - True if everything went well - False otherwise
     * @throws java.rmi.RemoteException
     */
    @Override
    public boolean endGame(String gameInput, String hasLeft) throws RemoteException {
        if (gameInput == null || !this.activeGames.containsKey(gameInput)) {
            return false;
        }
        Game game = (Game) this.activeGames.get(gameInput);

        GameStatus status = game.getStatusProp().get();
        if (!status.equals(GameStatus.Preparing)) {
            try {
                game = (Game) this.adjustScore(game, (hasLeft != null && !hasLeft.isEmpty()));
                if (game == null) {
                    return false;
                }
            } catch (IllegalArgumentException ex) {
                return false;
            }
        }

        try {
            for (IPlayer Iplayer : game.getMyPlayers()) {
                Person p = (Person) activePersons.get(Iplayer.getName());
                if (p == null) {
                    return false;
                }
                p.setRating(this.myDatabaseControls.getNewRating(p, (Person) this.activePersons.get(hasLeft)));
                this.returnToLobby(p);
            }

        } catch (IllegalArgumentException ex) {
            System.out.println("IllegalArgumentException in endgame: " + ex.getMessage());
        } catch (SQLException ex) {
            System.out.println("SQLException in EndGame: " + ex.getMessage());
        }

        try {
            game.broadcastEndGame(hasLeft);
            this.activeGames.remove(gameInput);
            this.oRankings.setAll(this.myDatabaseControls.getRankings());
        } catch (SQLException ex) {
            System.out.println("SQLException in getRankings (EndGame): " + ex.getMessage());
        }
        return true;
    }

    /**
     * returns given person to lobby, and removes him from the spectators of
     * given game.
     *
     * @param gameInput The game as string
     * @param spectator The name of the spectator stopping spectating
     * @throws java.rmi.RemoteException
     */
    @Override
    public void stopSpectating(String gameInput, String spectator) throws RemoteException {
        if (spectator == null
                || gameInput == null
                || !(this.activePersons.get(spectator) instanceof Spectator)) {
            return;
        }
        Game game = (Game) this.activeGames.get(gameInput);

        game.removeSpectator((Spectator) this.activePersons.get(spectator));
        this.returnToLobby(this.activePersons.get(spectator));
    }

    /**
     * returns a specific person's status from Player or Spectator back to
     * Person if called on currentPerson, it will reset that too updates
     * activePersons to reflect this change
     *
     * @param participant can be null, but it won't do anything then either
     */
    private void returnToLobby(IPerson participantInput) throws RemoteException {
        if (participantInput == null
                || !(participantInput instanceof Player
                || participantInput instanceof Spectator)) {
            return;
        }
        Person participant = (Person) this.activePersons.get(participantInput.getName());
        try {
            if (participant instanceof Spectator && ((Spectator) participant).getMyGames().size() > 1) {
            } else {
                boolean isBot = participant.isBot();
                this.activePersons.replace(participant.getName(),
                        new Person(participant.getName(), participant.ratingProperty().get()));
                ((Person) this.activePersons.get(participant.getName())).setBot(isBot);
            }
        } catch (Exception ex) {
        }
    }

    /**
     * Adjusts end of game scores according to the URS
     *
     * @param game The game whose score should be adjusted
     * @param earlyEnding Whether the game is ending before is should
     * @return Returns the game with the adjusted score
     */
    private IGame adjustScore(Game game, boolean earlyEnding)
            throws IllegalArgumentException, RemoteException {
        if (game == null) {
            throw new IllegalArgumentException("input was null");
        }

        if (game.getMyPlayers().size() < 3) {
            throw new IllegalArgumentException("game wasn't full");
        }
        int player1score = ((Player) game.getMyPlayers().get(0)).getScore().get();
        int player2score = ((Player) game.getMyPlayers().get(1)).getScore().get();
        int player3score = ((Player) game.getMyPlayers().get(2)).getScore().get();

        double player1rating = ((Player) game.getMyPlayers().get(0)).ratingProperty().get();
        double player2rating = ((Player) game.getMyPlayers().get(1)).ratingProperty().get();
        double player3rating = ((Player) game.getMyPlayers().get(2)).ratingProperty().get();

        double averageRating = (player1rating + player2rating + player3rating) / 3;
        double speedRating;
        try {
            speedRating = Math.round(game.getMyPuck().getSpeed().get());
            if (speedRating > averageRating) {
                player1rating = speedRating;
                player2rating = speedRating;
                player3rating = speedRating;
            }
        } catch (Exception ex) {
            // do nothing, and just let player ratings sort it out
        }

        player1score += (player2rating + player3rating - 2 * player1rating) / 8;
        player2score += (player1rating + player3rating - 2 * player2rating) / 8;
        player3score += (player1rating + player2rating - 2 * player3rating) / 8;

        int roundNo = game.getRoundNo().get();
        // adjusts score based on whether the game ended prematurely
        if (earlyEnding && roundNo > 0) {
            player1score = (player1score - 20) * 10 / roundNo + 20;
            player2score = (player2score - 20) * 10 / roundNo + 20;
            player3score = (player3score - 20) * 10 / roundNo + 20;
        } else if (roundNo > 0) {
            ((Player) game.getMyPlayers().get(0)).setScore(player1score);
            ((Player) game.getMyPlayers().get(1)).setScore(player2score);
            ((Player) game.getMyPlayers().get(2)).setScore(player3score);
        }

        try {
            // saves game
            this.myDatabaseControls.saveGame(game);
        } catch (SQLException ex) {
            throw new IllegalArgumentException("failed to save game: "
                    + ex.getMessage());
        }

        // Notifies observers
        String playerName = game.getMyPlayers().get(0).getName();
        this.publisher.pushNewRanking(this.activePersons.get(playerName));

        playerName = game.getMyPlayers().get(1).getName();
        this.publisher.pushNewRanking(this.activePersons.get(playerName));

        playerName = game.getMyPlayers().get(2).getName();
        this.publisher.pushNewRanking(this.activePersons.get(playerName));

        return game;
    }

    /**
     * returns the game associated with a gameID.
     *
     * @param gameID can't be null
     * @return Game when a game was found null otherwise
     * IllegalArgumentException when gameID was null
     * @throws java.rmi.RemoteException
     */
    @Override
    public IGame getMyGame(String gameID) throws RemoteException {
        if (gameID == null) {
            throw new IllegalArgumentException();
        }
        return this.activeGames.get(gameID);
    }

    /**
     * retrieves a list of persons from the database, sorted by their ranking
     * Amount of players retrieved is to be determined by the database
     *
     * @return a sorted list of highest ranking players
     * @throws java.sql.SQLException
     * @throws java.rmi.RemoteException
     */
    @Override
    public List<IPerson> getRankings() throws SQLException, RemoteException {
        List<IPerson> rankingsOutput = this.myDatabaseControls.getRankings();
        this.oRankings.clear();
        this.oRankings.addAll(rankingsOutput);
        return rankingsOutput;
    }

    /**
     *
     * @return airhockeysettings cast to a non-observable hashmap. This as
     * ObservableMap is not serializable.
     * @throws RemoteException
     */
    @Override
    public HashMap getRMIAirhockeySettings() throws RemoteException {
        return new HashMap<>(this.airhockeySettings);
    }

    /**
     *
     * @return activePersons cast to a non-observable hashmap.
     * @throws RemoteException
     */
    @Override
    public HashMap<String, IPerson> getRMIActivePersons() throws RemoteException {
        return new HashMap<>(this.activePersons);
    }

    /**
     *
     * @return activeGames cast to a non-observable hashmap. This as
     * ObservableMap is not serializable.
     * @throws RemoteException
     */
    @Override
    public HashMap<String, IGame> getRMIActiveGames() throws RemoteException {
        return new HashMap<>(this.activeGames);
    }

    /**
     * @param name The name of the person
     * @return Up-to-date version of person with given name
     * @throws RemoteException
     */
    @Override
    public IPerson getMyPerson(String name) throws RemoteException {
        return this.activePersons.get(name);
    }

}
