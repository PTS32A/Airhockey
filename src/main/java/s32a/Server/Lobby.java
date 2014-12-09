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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import s32a.Server.Publishers.LobbyPublisher;
import s32a.Shared.IGame;
import s32a.Shared.IGameClient;
import s32a.Shared.ILobby;
import s32a.Shared.ILobbyClient;
import s32a.Shared.IPerson;
import s32a.Shared.IPlayer;
import s32a.Shared.ISpectator;
import s32a.Shared.enums.Colors;
import s32a.Shared.enums.GameStatus;

/**
 *
 * @author Kargathia
 */
public class Lobby extends UnicastRemoteObject implements ILobby {

    /**
     * The Lobby Singleton. Called by everything except some unit tests.
     */
    private static Lobby _singleton;

    /**
     * If _singleton is null, initializes it.
     *
     * @return instance of lobby (_singleton)
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
    private ObjectProperty<HashMap<String, Object>> airhockeySettings;
    @Getter
    private HashMap<String, IPerson> activePersons;
    @Getter
    private ObservableList<IGame> activeGames;
    private List<IGame> backingActiveGames;
    private LobbyPublisher publisher;

    /**
     * returns a person by name. Syntactic sugar for activepersons.get(string)
     *
     * @param playerName
     * @return
     */
    @Override
    public IPerson getMyPerson(String playerName) {
        return this.activePersons.get(playerName);
    }
    
    /**
     * @return airhockeysettings boxed hashmap
     */
    public HashMap<String, Object> getAirhockeySettings(){
        return this.airhockeySettings.get();
    }

    /**
     * Lobby is used as singleton. Public for unit tests.
     *
     * @throws java.rmi.RemoteException
     */
    public Lobby() throws RemoteException {
        this.mychatbox = new Chatbox();
        this.myDatabaseControls = new DatabaseControls();
        this.activePersons = new HashMap<>();
        this.airhockeySettings = new SimpleObjectProperty<>(new HashMap<>());
        this.backingActiveGames = new ArrayList<>();
        this.activeGames = FXCollections.observableArrayList(backingActiveGames);
        this.airhockeySettings.get().put("Goal Default", new Vector2(0, 0));
        this.airhockeySettings.get().put("Side Length", 500f);
    }

    /**
     * Starts the associated lobbyPublisher. Can't be done in constructor, as it
     * references lobby instance.
     *
     * @throws RemoteException
     */
    public void startPublisher() throws RemoteException {
        this.publisher = new LobbyPublisher();
    }

    /**
     * Adds a new person to the database. Does not add them to active persons
     * yet the database checks for uniqueness
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
     * @param client
     * @return True if DatabaseControls.checkLogin() returned a person false if
     * .checkLogin() returned null IllegalArgumentException when parameter was
     * null or empty, or contained trailing / leading white spaces
     * @throws java.sql.SQLException
     * @throws java.rmi.RemoteException
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

        if (this.activePersons.put(playerName, newPerson) == null
                && this.publisher.addObserver(playerName, client)) {
            return true;
        } else {
            this.activePersons.remove(playerName);
            this.publisher.removeObserver(playerName);
            return false;
        }
    }

    /**
     * logs out given player. Sets currentPerson to null if it was him. Should
     * always be called with currentPerson by the GUI
     *
     * @param input
     * @return whether logout succeeded
     * @throws java.rmi.RemoteException
     */
    @Override
    public boolean logOut(IPerson input) throws RemoteException {
        if (input == null) {
            return false; // update this in unit tests when I get around to
        }

        if (input instanceof Player) {
            Player playerInput = (Player) input;
            this.endGame(playerInput.getMyGame(), playerInput);
        } else if (input instanceof Spectator) {
            Spectator spectInput = (Spectator) input;
            for (IGame g : activeGames) {
                ((Game) g).removeSpectator(spectInput);
            }
        }
        this.activePersons.remove(input.getName());
        this.publisher.removeObserver(input.getName());
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
        }
        catch (SQLException ex) {
            Logger.getLogger(Lobby.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Starts a new game, and adds this to the activeGames list Person is
     * converted to Player Person is used as gamestarter for Game constructor
     * parameter person can't already be a Player or Spectator
     *
     * @param input should be Lobby.currentPerson if called by GUI
     * @param client
     * @return - the freshly started game if everything went well - null
     * @throws java.rmi.RemoteException
     */
    @Override
    public Game startGame(IPerson input, IGameClient client)
            throws RemoteException, IllegalArgumentException {
        if (input == null || (input instanceof Player)
                || (input instanceof Spectator)) {
            return null;
        }
        if (client == null) {
            return null;
        }

        Person person = (Person) input;
        Game newGame = null;
        try {
            person = new Player(person.getName(), person.ratingProperty().get(),
                    Colors.Red);
            newGame = new Game((Player) person);
            newGame.startPublisher((Player) person, client);
            this.activePersons.replace(person.getName(), person);
            this.activeGames.add(newGame);
        }
        catch (Exception ex) {
            this.returnToLobby(person);
            this.endGame(newGame, null);
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
     * @param client
     * @return joined game if everything went well null otherwise
     * @throws java.rmi.RemoteException
     */
    @Override
    public Game joinGame(IGame gameInput, IPerson personInput, IGameClient client)
            throws RemoteException, IllegalArgumentException {
        if (personInput == null || (personInput instanceof Player)
                || (personInput instanceof Spectator) || gameInput == null) {
            return null;
        }
        if (client == null) {
            return null;
        }

        Game game = (Game) gameInput;
        Person person = (Person) personInput;
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
            } else {
                return null;
            }
        }
        catch (Exception ex) {
            for (IPlayer player : game.getMyPlayers()) {
                if (player.getName().equals(personInput.getName())) {
                    this.endGame(game, player);
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
     * @param client
     * @return - Game if everything went well - Null otherwise
     * @throws java.rmi.RemoteException
     */
    @Override
    public IGame spectateGame(IGame gameInput, IPerson personInput, IGameClient client)
            throws IllegalArgumentException, RemoteException {
        if (personInput == null) {
            throw new IllegalArgumentException("Input is null");
        }
        if (personInput instanceof Player) {
            throw new IllegalArgumentException("Input is already a Player");
        }
        if (gameInput == null) {
            throw new IllegalArgumentException("Game input is null");
        }
        Game game = (Game) gameInput;
        if (game.statusProperty().get() == GameStatus.GameOver) {
            throw new IllegalArgumentException("Unable to watch a finished game");
        }
        if (game.getRoundNo().get() < 1) {
            throw new IllegalArgumentException("Unable to spectate a game still waiting to start");
        }
        if (client == null) {
            throw new IllegalArgumentException("Game client was null");
        }

        Person person = (Person) personInput;
        try {
            person = new Spectator(person.getName(), person.ratingProperty().get());
            if (!game.addSpectator((Spectator) person, client)) {
                return null;
            }
            this.activePersons.replace(person.getName(), person);
        }
        catch (IllegalArgumentException ex) {
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
    public boolean endGame(IGame gameInput, IPlayer hasLeft) throws RemoteException {
        if (gameInput == null || !this.activeGames.contains(gameInput)) {
            return false;
        }
        Game game = (Game) gameInput;

        if (game.getMyPlayers().size() == 3 && game.getRoundNo().get() > 0) {
            try {
                game = (Game) this.adjustScore(game, (hasLeft != null));
                if (game == null) {
                    return false;
                }
            }
            catch (IllegalArgumentException ex) {
                return false;
            }
        }

        try {
            for (IPlayer Iplayer : game.getMyPlayers()) {
                Player player = (Player) Iplayer;
                if (this.getActivePersons().get(player.getName()) instanceof Player) {
                    player.setRating(this.myDatabaseControls.getNewRating((Person) player, hasLeft));
                    this.returnToLobby(player);
                }
            }
            this.activeGames.remove(game);
        }
        catch (IllegalArgumentException | SQLException ex) {
            return false;
        }
        return true;
    }

    /**
     * returns given person to lobby, and removes him from the spectators of
     * given game.
     *
     * @param gameInput
     * @param spectator
     * @throws java.rmi.RemoteException
     */
    @Override
    public void stopSpectating(IGame gameInput, IPerson spectator) throws RemoteException {
        if (spectator == null || gameInput == null || !(spectator instanceof Spectator)) {
            return;
        }
        Game game = (Game) gameInput;

        game.removeSpectator((Spectator) spectator);
        this.returnToLobby(spectator);
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
        Person participant = (Person) participantInput;
        try {
            boolean isBot = participant.isBot();
            if (participant instanceof Spectator && ((Spectator) participant).getMyGames().size() > 1) {
            } else {
                this.activePersons.replace(participant.getName(), new Person(participant.getName(), participant.ratingProperty().get()));
                ((Person) this.activePersons.get(participant.getName())).setBot(isBot);
            }
        }
        catch (Exception ex) {
        }
    }

    /**
     * Adjusts end of game scores according to the URS
     *
     * @param game
     * @return
     */
    private IGame adjustScore(IGame gameInput, boolean earlyEnding)
            throws IllegalArgumentException, RemoteException {
        if (gameInput == null) {
            throw new IllegalArgumentException("input was null");
        }
        Game game = (Game) gameInput;

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
        }
        catch (Exception ex) {
            // do nothing, and just let player ratings sort it out
        }

        player1score += (player2rating + player3rating - 2 * player1rating) / 8;
        player2score += (player1rating + player3rating - 2 * player2rating) / 8;
        player3score += (player1rating + player2rating - 2 * player3rating) / 8;

        // adjusts score based on whether the game ended prematurely
        if (earlyEnding) {
            player1score = (player1score - 20) * 10 / game.getRoundNo().get() + 20;
            player2score = (player2score - 20) * 10 / game.getRoundNo().get() + 20;
            player3score = (player3score - 20) * 10 / game.getRoundNo().get() + 20;
        }

        ((Player) game.getMyPlayers().get(0)).setScore(player1score);
        ((Player) game.getMyPlayers().get(1)).setScore(player2score);
        ((Player) game.getMyPlayers().get(2)).setScore(player3score);

        try {
            // saves game
            this.myDatabaseControls.saveGame(game);
        }
        catch (SQLException ex) {
            throw new IllegalArgumentException("failed to save game: "
                    + ex.getMessage());
        }

        return game;
    }

    /**
     * Adds a message to a specific game's chatbox
     *
     * @param myGame can't be null
     * @param message can't be null or white spaces
     * @param from can't be null when called from the GUI, this should be
     * currentPerson
     * @return - True if everything went well - False otherwise
     */
//    public boolean addGameChatMessage(Game myGame, String message, Person from)
//    {
//        return myGame.addChatMessage(message, from);
//    }
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
        if (gameID.trim() == null) {
            throw new IllegalArgumentException();
        }
        for (IGame game : this.activeGames) {
            if (((Game) game).getGameInfo().get("gameID").equals(gameID)) {
                return game;
            }
        }
        return null;
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
        return this.myDatabaseControls.getRankings();
    }

    /**
     * Used in iteration 1 of the project for testing and demonstration purposes
     * This adds a handful of persons to activePersons it assumes these persons
     * already have been added to the database Is also responsible for setting
     * Person.isBot to true Next up it starts multiple games, some full with
     * bots, some 2/3 full
     *
     * @throws java.rmi.RemoteException
     */
    @Override
    public void populate() throws RemoteException {
        // adds bot 1-11
        this.activePersons.put("bot1", new Person("bot1", (double) 15));
        this.activePersons.put("bot2", new Person("bot2", (double) 15));
        this.activePersons.put("bot3", new Person("bot3", (double) 15));
        this.activePersons.put("bot4", new Person("bot4", (double) 15));
        this.activePersons.put("bot5", new Person("bot5", (double) 15));
        this.activePersons.put("bot6", new Person("bot6", (double) 15));
        this.activePersons.put("bot7", new Person("bot7", (double) 15));
        this.activePersons.put("bot8", new Person("bot8", (double) 15));
        this.activePersons.put("bot9", new Person("bot9", (double) 15));
        this.activePersons.put("bot10", new Person("bot10", (double) 15));
        this.activePersons.put("bot11", new Person("bot11", (double) 15));

        // the same gameclient is used for all bots, as they do not operate an actual client
        s32a.Client.ClientData.GameClient botClient = new s32a.Client.ClientData.GameClient();

        //game 1
        Person bot = (Person) this.activePersons.get("bot1");
        bot.setBot(true);
        Game game = this.startGame(bot, botClient);

        bot = (Person) this.activePersons.get("bot2");
        bot.setBot(true);
        this.joinGame(game, bot, botClient);

        bot = (Person) this.activePersons.get("bot3");
        bot.setBot(true);
        this.joinGame(game, bot, botClient);
        game.beginGame();

        //game 2
        bot = (Person) this.activePersons.get("bot4");
        bot.setBot(true);
        game = this.startGame(bot, botClient);

        bot = (Person) this.activePersons.get("bot5");
        bot.setBot(true);
        this.joinGame(game, bot, botClient);

        bot = (Person) this.activePersons.get("bot6");
        bot.setBot(true);
        this.joinGame(game, bot, botClient);
        game.beginGame();

        // game 3
        bot = (Person) this.activePersons.get("bot7");
        bot.setBot(true);
        game = this.startGame(bot, botClient);

        bot = (Person) this.activePersons.get("bot8");
        bot.setBot(true);
        this.joinGame(game, bot, botClient);

        bot = (Person) this.activePersons.get("bot9");
        bot.setBot(true);
        this.joinGame(game, bot, botClient);
        game.pauseGame(true);

        // loose change
        bot = (Person) this.activePersons.get("bot10");
        bot.setBot(true);

        bot = (Person) this.activePersons.get("bot11");
        bot.setBot(true);

        // adds two bots to the system.
        // should only be run on a fresh database
        try {
            Lobby.getSingle().addPerson("bot10", "test");
            Lobby.getSingle().addPerson("bot11", "test");
        }
        catch (IllegalArgumentException | SQLException ex) {
        }
    }

}
