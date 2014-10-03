/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package s32a.airhockey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.Getter;

/**
 * NOTES: 
 * implemented HashMaps for active games and spectated games, key being gameID
 * implemented HashMap for active persons, key being playername
 * @author Kargathia
 */
public class Lobby
{
    private static Lobby _singleton;
    @Getter private Chatbox mychatbox;
    @Getter private DatabaseControls myDatabaseControls;
    @Getter private Person currentPerson;
    @Getter private HashMap activePersons;
    @Getter private List<Game> activeGames, spectatedGames;
    @Getter private Game playedGame;
    
    /**
     * Lobby is used as singleton, and is therefore private
     * Also responsible for calling InternetConnection.populate() in iteration 1
     */
    private Lobby()
    {
        this.mychatbox = new Chatbox();
        this.myDatabaseControls = new DatabaseControls();
        this.currentPerson = null;
        this.activePersons = new HashMap<>();
        this.activeGames = new ArrayList<>();
        this.playedGame = null;
        this.spectatedGames = new ArrayList<>();
    }
    
    /**
     * if _singleton is null, initialises it
     * @return instance of lobby (_singleton) 
     */
    public static Lobby getSingle()
    {
        if(_singleton == null)
        {
            _singleton = new Lobby();
        }
        return _singleton;
    }
    
    /**
     * Adds a new person to the database. Does not add them to active persons yet
     * the database checks for uniqueness
     * @param playerName can not be null or whitespace
     * can't contain whitespaces
     * @param password can not be null or whitespace
     * can't contain whitespaces
     * @return DatabaseControls.addPerson() - or IllegalArgumentException when
     * parameter(s) is/are null or contain whitespaces
     */
    public boolean addPerson(String playerName, String password)
    {
        
    }
    
    /**
     * Checks with the database class whether provided parameters correspond to a 
     * player in the database
     * Adds person returned from database call to the lists of active persons
     * @param playerName can't be null or whitespace
     * can't contain whitespaces
     * @param password can't be null or whitespace
     * can't contain whitespaces
     * @return 
     * True if DatabaseControls.checkLogin() returned a person
     * false if .checkLogin() returned null
     * IllegalArgumentException when parameter was null or empty, or contained whitespaces
     */
    public boolean checkLogin(String playerName, String password)
    {
        
    }
    
    /**
     * calls the database to remove a player with given player name
     * @param playerName
     * @return DatabaseControls.removePerson()
     */
    public boolean removePerson(String playerName)
    {
        return myDatabaseControls.removePerson(playerName);
    }
    
    /**
     * Starts a new game, and adds this to the activeGames list
     * currentPerson is converted to Player
     * currentPerson is used as gamestarter for Game constructor parameter
     * currentPerson can't already be a Player or Spectator
     * @return 
     * - True if everything went well
     * - False otherwise
     */
    public boolean startGame()
    {
        
    }
    
    /**
     * lets currentPerson join an existing game
     * currentPerson is converted to Player
     * currentPerson can't already be a Player or Spectator
     * @param game game to join
     * @return 
     * True if everything went well
     * False otherwise
     */
    public boolean joinGame(Game game)
    {
        
    }
    
    /**
     * lets currentPerson spectate an existing game
     * currentPerson is converted to Spectator
     * currentPerson can't already be a Player
     * currentPerson can already be a Spectator
     * @param game
     * @return 
     * - True if everything went well
     * - False otherwise
     */
    public boolean spectateGame(Game game)
    {
        
    }
    
    /**
     * adds a chat message to the lobbychatbox
     * @param message can't be null
     * @param from if method is called from GUI it should always be currentPerson
     * alternative would be if method is called from internetconnection (irrelevant in iteration 1)
     * @return 
     * - True if everything went well
     * - False otherwise
     */
    public boolean addChatMessage(String message, Person from)
    {
        
    }
    
    /**
     * ends the provided game, and returns all participants to the lobby
     * calculates post-game stats
     * @param game can't be null
     * @param hasLeft can be null, if game ended normally
     * is not null when a player leaves the game
     * @return 
     * - True if everything went well
     * - False otherwise
     * - IllegalArgumentException when game is null
     */
    public boolean endGame(Game game, Player hasLeft)
    {
        
    }
    
    /**
     * returns a specific person's status from Player or Spectator back to Person
     * updates activePersons to reflect this change
     * @param participant can't be null
     * throws IllegalArgumentException when participant is null
     */
    public void returnToLobby(Person participant)
    {
        
    }
    
    /**
     * Adds a message to a specific game's chatbox
     * @param myGame can't be null
     * @param message can't be null or whitespaces
     * @param from can't be null
     * when called from the GUI, this should be currentPerson
     * @return 
     * - True if everything went well
     * - False otherwise
     */
    public boolean addGameChatMessage(Game myGame, String message, Person from)
    {
        
    }
    
    /**
     * returns the game associated with a gameID
     * TODO: implement hashable list of games, with key being gameID
     * @param gameID can't be null
     * @return 
     * Game when a game was found
     * null otherwise
     * IllegalArgumentException when gameID was null
     */
    public Game getMyGame(String gameID)
    {
        
    }
    
    /**
     * retrieves a list of persons from the database, sorted by their ranking
     * Amount of players retrieved is to be determined by the database
     * @return a sorted list of highest ranking players
     */
    public ArrayList<Person> getRankings()
    {
        
    }
}
