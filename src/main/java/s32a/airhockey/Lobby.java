/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package s32a.airhockey;

import java.util.ArrayList;
import java.util.Calendar;
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
     * Lobby is used as singleton
     * Also responsible for calling InternetConnection.populate() in iteration 1
     */
    public Lobby()
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
     * @return True if everything went well
     * false if anything went wrong
     * - or IllegalArgumentException when
     * parameter(s) is/are null or contain trailing / leading whitespaces
     */
    public boolean addPerson(String playerName, String password) 
            throws IllegalArgumentException
    {
        if(!playerName.trim().equals(playerName) || !password.trim().equals(password) ||
                playerName == null || password == null)
        {
            throw new IllegalArgumentException();
        }
        
        return (this.myDatabaseControls.addPerson(playerName, password) != null);
    }
    
    /**
     * Checks with the database class whether provided parameters correspond to a 
     * player in the database
     * Adds person returned from database call to the lists of active persons
     * @param playerName can't be null or whitespace
     * can't contain whitespaces
     * @param password can't be null or whitespace
     * can't contain trailing / leading whitespaces
     * @return 
     * True if DatabaseControls.checkLogin() returned a person
     * false if .checkLogin() returned null
     * IllegalArgumentException when parameter was null or empty, or contained
     * trailing / leading whitespaces
     */
    public boolean checkLogin(String playerName, String password) 
            throws IllegalArgumentException
    {
        if(!playerName.trim().equals(playerName) || !password.trim().equals(password) ||
                playerName == null || password == null)
        {
            throw new IllegalArgumentException();
        }
        
        Person newPerson = this.myDatabaseControls.checkLogin(playerName, password);
        try
        {
            return (activePersons.put(playerName, newPerson) != null);
        }
        catch(IllegalArgumentException exc)
        {
            return false;
        }
    }
    
    /**
     * calls the database to remove a player with given player name
     * @param playerName
     * @return DatabaseControls.removePerson()
     * throws IllegalArgumentException if playerName is null or contains 
     * trailing / leading whitespaces
     */
    public boolean removePerson(String playerName)
    {
        if(playerName == null || !playerName.trim().equals(playerName))
        {
            throw new IllegalArgumentException();
        }
        return myDatabaseControls.removePerson(playerName);
    }
    
    /**
     * Starts a new game, and adds this to the activeGames list
     * Person is converted to Player
     * Person is used as gamestarter for Game constructor parameter
     * person can't already be a Player or Spectator
     * @param person should be Lobby.currentPerson if called by GUI
     * @return 
     * - True if everything went well
     * - False otherwise
     */
    public boolean startGame(Person person)
    {
        if(person == null || (person instanceof Player) 
                || (person instanceof Spectator))
        {
            return false;
        }
        
        try
        {
            person = new Player(person.getName(), person.getRating(), "rood");
            this.activePersons.replace(person.getName(), person);
            this.playedGame = new Game((Player)person);
            this.activeGames.add(this.playedGame);
            if(this.currentPerson == person)
            {
                this.currentPerson = person;
            }
        }
        catch(Exception ex)
        {
            this.returnToLobby(person);
            this.endGame(this.playedGame, null);
            return false;
        }
        return true;
    }
    
    /**
     * lets currentPerson join an existing game
     * currentPerson is converted to Player
     * currentPerson can't already be a Player or Spectator
     * @param game can't be null
     * @param person should be Lobby.currentPerson if called by GUI
     * @return 
     * True if everything went well
     * False otherwise
     */
    public boolean joinGame(Game game, Person person)
    {
        if(person == null || (person instanceof Player) 
                || (person instanceof Spectator) || game == null)
        {
            return false;
        }
        
        try
        {           
            person = new Player(person.getName(), person.getRating(), game.getNextColor());
            this.activePersons.replace(person.getName(), person);
            this.playedGame = game;
        }
        catch(Exception ex)
        {
            this.returnToLobby(person);
            this.playedGame = null;
            return false;
        }
        return true;
    }
    
    /**
     * lets currentPerson spectate an existing game
     * currentPerson is converted to Spectator
     * currentPerson can't already be a Player
     * currentPerson can already be a Spectator
     * @param game can't be null
     * @param person should be currentPerson if called by GUI
     * @return 
     * - True if everything went well
     * - False otherwise
     */
    public boolean spectateGame(Game game, Person person)
    {
        if(person == null || (person instanceof Player))
        {
            return false;
        }
        
        try
        {
            person = new Spectator(person.getName(), 
                    person.getRating(), game);
            game.addSpectator((Spectator)person);
            this.activePersons.replace(person.getName(), person);
        }
        catch (Exception ex)
        {
            return false;
        }
        return true;
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
        if(message == null || from == null)
        {
            return false;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("<").append(from.getName()).append(">");
        builder.append("[").append(String.valueOf(Calendar.getInstance().getTime())).append("]"); // TODO TEST WHETHER TIMESTAMP DOESN'T FUCK UP
        builder.append(": ").append(message);
        message = builder.toString();
        return this.mychatbox.addChatMessage(message);       
    }
    
    /**
     * ends the provided game, and returns all participants to the lobby
     * chucks a request to database to calculate new ratings
     * if player was the 
     * @param game can be null but will return false
     * @param hasLeft can be null, if game ended normally
     * is not null when a player leaves the game
     * @return 
     * - True if everything went well
     * - False otherwise
     */
    public boolean endGame(Game game, Player hasLeft)
    {
        if(game == null)
        {
            return false;
        }
        
        try
        {
            for (Player player : game.getMyPlayers())
            {
                player.setRating(this.myDatabaseControls.getNewRating(player, game, hasLeft));
                this.returnToLobby(player); 
            }
            
        }
        catch(Exception ex)
        {
            return false;
        }
        return true;
    }
    
    /**
     * returns a specific person's status from Player or Spectator back to Person
     * if called on currentPerson, it will reset that too
     * updates activePersons to reflect this change
     * @param participant can be null, but it won't do anything then either
     */
    public void returnToLobby(Person participant)
    {
        if(participant == null)
        {
            return;   
        }
        try
        {
            this.activePersons.replace(participant.getName(), new Person(participant.getName(), participant.getRating()));
            if(this.currentPerson == participant)
            {
                this.currentPerson = (Person)this.activePersons.get(participant.getName());
            }
        }
        catch(Exception ex){}
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
        return myGame.addChatMessage(message, from);
    }
    
    /**
     * returns the game associated with a gameID
     * @param gameID can't be null
     * @return 
     * Game when a game was found
     * null otherwise
     * IllegalArgumentException when gameID was null
     */
    public Game getMyGame(String gameID)
    {
        if(gameID.trim() == null)
        {
            throw new IllegalArgumentException();
        }
        for(Game game : this.activeGames)
        {
            if(game.getGameInfo().get("gameID").equals(gameID))
            {
                return game;
            }
        }
        return null;
    }
    
    /**
     * retrieves a list of persons from the database, sorted by their ranking
     * Amount of players retrieved is to be determined by the database
     * @return a sorted list of highest ranking players
     */
    public ArrayList<Person> getRankings()
    {
        return this.myDatabaseControls.getRankings();
    }
    
    /**
     * Used in iteration 1 of the project for testing and demonstration purposes
     * This tries to register and log in a handful of Persons
     * Is also responsible for setting Person.isBot to true
     * Next up it starts multiple games, some full with bots, some 2/3 full
     * @return True if it did its job
     * Being unable to register the bots due to them being already present in
     * the database will not return false
     */
    public boolean populate()
    {
        
    }
}
