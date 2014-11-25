/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Server;

import com.badlogic.gdx.math.Vector2;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import s32a.Shared.IGame;
import s32a.Shared.ILobby;
import s32a.Shared.IPerson;
import s32a.Shared.IPlayer;
import s32a.Shared.ISpectator;
import s32a.Shared.enums.Colors;

/**
 *
 * @author Kargathia
 */
class Lobby implements ILobby {

    /**
     * The Lobby Singleton. Called by everything except some unit tests.
     */
    private static Lobby _singleton;

    /**
     * If _singleton is null, initializes it.
     *
     * @return instance of lobby (_singleton)
     */
    public static Lobby getSingle() {
        if (_singleton == null) {
            _singleton = new Lobby();
        }
        return _singleton;
    }
    @Getter
    private Chatbox mychatbox;
    private DatabaseControls myDatabaseControls;
    @Getter
    private HashMap airhockeySettings;
    @Getter
    private HashMap<String, IPerson> activePersons;
    @Getter
    private List<IGame> activeGames;

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
     * Lobby is used as singleton. Public for unit tests.
     */
    public Lobby() {
        this.mychatbox = new Chatbox();
        this.myDatabaseControls = new DatabaseControls();
        this.activePersons = new HashMap<>();
        this.airhockeySettings = new HashMap<>();
        this.activeGames = new ArrayList<>();
        airhockeySettings.put("Goal Default", new Vector2(0, 0));
        airhockeySettings.put("Side Length", 500f);
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
     */
    @Override
    public boolean addPerson(String playerName, String password)
            throws IllegalArgumentException, SQLException {
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
     * @return True if DatabaseControls.checkLogin() returned a person false if
     * .checkLogin() returned null IllegalArgumentException when parameter was
     * null or empty, or contained trailing / leading white spaces
     * @throws java.sql.SQLException
     */
    @Override
    public boolean checkLogin(String playerName, String password)
            throws IllegalArgumentException, SQLException {
        if (playerName == null || password == null
                || !playerName.trim().equals(playerName) || !password.trim().equals(password)) {
            throw new IllegalArgumentException();
        }

        IPerson newPerson = this.myDatabaseControls.checkLogin(playerName, password);
        if (newPerson == null) {
            return false;
        }

        return this.activePersons.put(playerName, newPerson) != null;
    }

    /**
     * logs out given player. Sets currentPerson to null if it was him. Should
     * always be called with currentPerson by the GUI
     *
     * @param input
     * @return whether logout succeeded
     */
    @Override
    public boolean logOut(IPerson input) {
        if (input == null) {
            return false; // update this in unit tests when I get around to
        }

        if (input instanceof Player) {
            Player playerInput = (Player) input;
            this.endGame(playerInput.getMyGame(), playerInput);
        } else if (input instanceof Spectator) {
            Spectator spectInput = (Spectator) input;
            for (IGame g : activeGames) {
                g.removeSpectator(spectInput);
            }
        }
        this.activePersons.remove(input.getName());
        return true;
    }

    /**
     * clears the entire database - should only be used for reset and debugging
     */
    public void clearDatabase() {
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
     * @return - the freshly started game if everything went well - null
     */
    @Override
    public Game startGame(IPerson input) {
        if (input == null || (input instanceof Player)
                || (input instanceof Spectator)) {
            return null;
        }

        Person person = (Person) input;
        Game newGame = null;
        try {
            person = new Player(person.getName(), person.ratingProperty().get(),
                    Colors.Red);
            newGame = new Game((Player) person);
            this.activePersons.replace(person.getName(), person);
            this.activeGames.add(newGame);
        }
        catch (Exception ex) {
            this.returnToLobby(person);
            this.endGame(newGame, null);
            return null;
        }
        return newGame;
    }

    /**
     * lets currentPerson join an existing game currentPerson is converted to
     * Player currentPerson can't already be a Player or Spectator
     *
     * @param gameInput can't be null
     * @param personInput should be Lobby.currentPerson if called by GUI
     * @return joined game if everything went well null otherwise
     */
    @Override
    public Game joinGame(IGame gameInput, IPerson personInput) {
        if (personInput == null || (personInput instanceof Player)
                || (personInput instanceof Spectator) || gameInput == null) {
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
            if (game.addPlayer(player)) {
                this.activePersons.replace(person.getName(), player);
            } else {
                return null;
            }
        }
        catch (Exception ex) {
            this.returnToLobby(person);
            return null;
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
     * @return - Game if everything went well - Null otherwise
     */
    @Override
    public IGame spectateGame(IGame gameInput, IPerson personInput) {
        if (personInput == null || (personInput instanceof Player)) {
            return null;
        }

        Game game = (Game) gameInput;
        Person person = (Person) personInput;
        try {
            person = new Spectator(person.getName(), person.ratingProperty().get());
            if (!game.addSpectator((Spectator) person)) {
                return null;
            }
            this.activePersons.replace(person.getName(), person);
        }
        catch (Exception ex) {
            return null;
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
     */
    @Override
    public boolean addChatMessage(String message, String from) throws IllegalArgumentException {
        if (message == null || from == null || from.trim().isEmpty()) {
            throw new IllegalArgumentException("message or poster is null");
        }
        return this.mychatbox.addChatMessage(message, from);
    }

    /**
     * ends the provided game, and returns all participants to the lobby chucks
     * a request to database to calculate new ratings if player was the
     *
     * @param game can be null but will return false
     * @param hasLeft can be null, if game ended normally is not null when a
     * player leaves the game
     * @return - True if everything went well - False otherwise
     */
    @Override
    public boolean endGame(IGame game, IPlayer hasLeft) {
        if (game == null || !this.activeGames.contains(game)) {
            return false;
        }

        if (game.getMyPlayers().size() == 3 && game.getRoundNo().get() > 0) {
            try {
                game = this.adjustScore(game, (hasLeft != null));
                if (game == null) {
                    return false;
                }
            }
            catch (IllegalArgumentException ex) {
                return false;
            }
        }

        try {
            for (IPlayer player : game.getMyPlayers()) {
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
     * @param game
     * @param spectator
     */
    @Override
    public void stopSpectating(IGame game, IPerson spectator) {
        if (spectator == null || game == null || !(spectator instanceof Spectator)) {
            return;
        }
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
    private void returnToLobby(IPerson participant) {
        if (participant == null || !(participant instanceof Player || participant instanceof Spectator)) {
            return;
        }
        try {
            boolean isBot = participant.isBot();
            if (participant instanceof Spectator && ((Spectator) participant).getMyGames().size() > 1) {
            } else {
                this.activePersons.replace(participant.getName(), new Person(participant.getName(), participant.ratingProperty().get()));
                this.activePersons.get(participant.getName()).setBot(isBot);
            }
        }
        catch (Exception ex) {
        }
    }

    /**
     * Adjusts end of game scores according to the URS
     *
     * @param input
     * @return
     */
    private IGame adjustScore(IGame input, boolean earlyEnding) throws IllegalArgumentException {
        if (input.getMyPlayers().size() < 3) {
            throw new IllegalArgumentException("game wasn't full");
        }
        int player1score = input.getMyPlayers().get(0).getScore().get();
        int player2score = input.getMyPlayers().get(1).getScore().get();
        int player3score = input.getMyPlayers().get(2).getScore().get();

        double player1rating = input.getMyPlayers().get(0).ratingProperty().get();
        double player2rating = input.getMyPlayers().get(1).ratingProperty().get();
        double player3rating = input.getMyPlayers().get(2).ratingProperty().get();

        double averageRating = (player1rating + player2rating + player3rating) / 3;
        double speedRating;
        try {
            speedRating = Math.round(input.getMyPuck().getSpeed().get());
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
            player1score = (player1score - 20) * 10 / input.getRoundNo().get() + 20;
            player2score = (player2score - 20) * 10 / input.getRoundNo().get() + 20;
            player3score = (player3score - 20) * 10 / input.getRoundNo().get() + 20;
        }

        input.getMyPlayers().get(0).setScore(player1score);
        input.getMyPlayers().get(1).setScore(player2score);
        input.getMyPlayers().get(2).setScore(player3score);

        try {
            // saves game
            this.myDatabaseControls.saveGame(input);
        }
        catch (SQLException ex) {
            throw new IllegalArgumentException("failed to save game: "
                    + ex.getMessage());
        }

        return input;
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
     */
    @Override
    public IGame getMyGame(String gameID) {
        if (gameID.trim() == null) {
            throw new IllegalArgumentException();
        }
        for (IGame game : this.activeGames) {
            if (game.getGameInfo().get("gameID").equals(gameID)) {
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
     */
    @Override
    public List<IPerson> getRankings() throws SQLException {
        return this.myDatabaseControls.getRankings();
    }

    /**
     * Used in iteration 1 of the project for testing and demonstration purposes
     * This adds a handful of persons to activePersons it assumes these persons
     * already have been added to the database Is also responsible for setting
     * Person.isBot to true Next up it starts multiple games, some full with
     * bots, some 2/3 full
     */
    @Override
    public void populate() {
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

        //game 1
        IPerson bot = this.activePersons.get("bot1");
        bot.setBot(true);
        Game game = this.startGame(bot);

        bot = this.activePersons.get("bot2");
        bot.setBot(true);
        this.joinGame(game, bot);

        bot = this.activePersons.get("bot3");
        bot.setBot(true);
        this.joinGame(game, bot);
        game.beginGame();

        //game 2
        bot = this.activePersons.get("bot4");
        bot.setBot(true);
        game = this.startGame(bot);

        bot = this.activePersons.get("bot5");
        bot.setBot(true);
        this.joinGame(game, bot);

        bot = this.activePersons.get("bot6");
        bot.setBot(true);
        this.joinGame(game, bot);
        game.beginGame();

        // game 3
        bot = this.activePersons.get("bot7");
        bot.setBot(true);
        game = this.startGame(bot);

        bot = this.activePersons.get("bot8");
        bot.setBot(true);
        this.joinGame(game, bot);

        bot = this.activePersons.get("bot9");
        bot.setBot(true);
        this.joinGame(game, bot);
        game.pauseGame(true);

        // loose change
        bot = this.activePersons.get("bot10");
        bot.setBot(true);

        bot = this.activePersons.get("bot11");
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
