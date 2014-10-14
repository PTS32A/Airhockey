/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package s32a.airhockey;

import com.badlogic.gdx.math.Vector2;
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
    @Getter private HashMap activePersons, airhockeySettings;
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
        this.airhockeySettings = new HashMap<>();
        airhockeySettings.put("Goal Default", new Vector2(0,0));
        airhockeySettings.put("Side Length", 200f);
        this.populate();
    }
    
    /**
     * if _singleton is null, initializes it
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
     * can't contain white spaces
     * @param password can not be null or whitespace
     * can't contain white spaces
     * @return True if everything went well
     * false if anything went wrong
     * - or IllegalArgumentException when
     * parameter(s) is/are null or contain trailing / leading white spaces
     */
    public boolean addPerson(String playerName, String password) 
            throws IllegalArgumentException
    {
        if(playerName == null || password == null || 
                !playerName.trim().equals(playerName)|| !password.trim().equals(password))
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
     * can't contain white spaces
     * @param password can't be null or whitespace
     * can't contain trailing / leading white spaces
     * @return 
     * True if DatabaseControls.checkLogin() returned a person
     * false if .checkLogin() returned null
     * IllegalArgumentException when parameter was null or empty, or contained
     * trailing / leading white spaces
     */
    public boolean checkLogin(String playerName, String password) 
            throws IllegalArgumentException
    {
        if(playerName == null || password == null || 
                !playerName.trim().equals(playerName) || !password.trim().equals(password))
        {
            throw new IllegalArgumentException();
        }
        
        Person newPerson = this.myDatabaseControls.checkLogin(playerName, password);
        try
        {
            if (activePersons.put(playerName, newPerson) != null)
            {
                this.currentPerson = (Person)this.activePersons.get(playerName);
                this.playedGame = null;
                this.spectatedGames = new ArrayList<>();
            }
            else
            {
                return false;
            }
        }
        catch(IllegalArgumentException exc)
        {
            return false;
        }
        return true;
    }
    
    /**
     * calls the database to remove a player with given player name
     * @param playerName
     * @return DatabaseControls.removePerson()
     * throws IllegalArgumentException if playerName is null or contains 
     * trailing / leading white spaces
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
     * - the freshly started game if everything went well
     * - null
     */
    public Game startGame(Person person)
    {
        if(person == null || (person instanceof Player) 
                || (person instanceof Spectator))
        {
            return null;
        }
        
        Game newGame = null;
        try
        {
            newGame = new Game((Player)person);
            person = new Player(person.getName(), person.getRating(), Colors.Red);
            this.activePersons.replace(person.getName(), person);          
            this.activeGames.add(newGame);
            if(this.currentPerson == person)
            {
                this.currentPerson = person;
                this.playedGame = newGame;
            }
        }
        catch(Exception ex)
        {
            this.returnToLobby(person);
            this.endGame(newGame, null);
            return null;
        }
        return newGame;
    }
    
    /**
     * lets currentPerson join an existing game
     * currentPerson is converted to Player
     * currentPerson can't already be a Player or Spectator
     * @param game can't be null
     * @param person should be Lobby.currentPerson if called by GUI
     * @return 
     * joined game if everything went well
     * null otherwise
     */
    public Game joinGame(Game game, Person person)
    {
        if(person == null || (person instanceof Player) 
                || (person instanceof Spectator) || game == null)
        {
            return null;
        }
        
        try
        {           
            person = new Player(person.getName(), person.getRating(), game.getNextColor());
            this.activePersons.replace(person.getName(), person);
            if(person.equals(this.currentPerson))
            {
                this.playedGame = game;
            }          
        }
        catch(Exception ex)
        {
            this.returnToLobby(person);
            this.playedGame = null;
            return null;
        }
        return game;
    }
    
    /**
     * lets currentPerson spectate an existing game
     * currentPerson is converted to Spectator
     * currentPerson can't already be a Player
     * currentPerson can already be a Spectator
     * @param game can't be null
     * @param person should be currentPerson if called by GUI
     * @return 
     * - Game if everything went well
     * - Null otherwise
     */
    public Game spectateGame(Game game, Person person)
    {
        if(person == null || (person instanceof Player))
        {
            return null;
        }
        
        try
        {
            person = new Spectator(person.getName(), 
                    person.getRating(), game);
            game.addSpectator((Spectator)person);
            this.activePersons.replace(person.getName(), person);
            if(this.currentPerson.equals(person))
            {
                this.spectatedGames.add(game);
            }
        }
        catch (Exception ex)
        {
            return null;
        }
        return game;
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
        return this.mychatbox.addChatMessage(message, from);       
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
                if(this.getActivePersons().get(player.getName()) instanceof Player)
                {
                    player.setRating(this.myDatabaseControls.getNewRating(player, game, hasLeft));
                    this.returnToLobby(player); 
                }               
            }  
            this.activeGames.remove(game);
            this.spectatedGames.remove(game);
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
    private void returnToLobby(Person participant)
    {
        if(participant == null || !(participant instanceof Player || participant instanceof Spectator))
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
     * @param message can't be null or white spaces
     * @param from can't be null
     * when called from the GUI, this should be currentPerson
     * @return 
     * - True if everything went well
     * - False otherwise
     */
//    public boolean addGameChatMessage(Game myGame, String message, Person from)
//    {
//        return myGame.addChatMessage(message, from);
//    }
    
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
     * This adds a handful of persons to activePersons
     * it assumes these persons already have been added to the database
     * Is also responsible for setting Person.isBot to true
     * Next up it starts multiple games, some full with bots, some 2/3 full
     */
    public void populate()
    {
        // adds bot 1-11
        this.activePersons.put("bot1", new Person("bot1", 15));
        this.activePersons.put("bot2", new Person("bot2", 15));
        this.activePersons.put("bot3", new Person("bot3", 15));
        this.activePersons.put("bot4", new Person("bot4", 15));
        this.activePersons.put("bot5", new Person("bot5", 15));
        this.activePersons.put("bot6", new Person("bot6", 15));
        this.activePersons.put("bot7", new Person("bot7", 15));
        this.activePersons.put("bot8", new Person("bot8", 15));
        this.activePersons.put("bot9", new Person("bot9", 15));
        this.activePersons.put("bot10", new Person("bot10", 15));
        this.activePersons.put("bot11", new Person("bot11", 15));
        
        Game game = this.startGame((Person)this.activePersons.get("bot1"));
        this.joinGame(game, (Person)this.activePersons.get("bot2"));
        this.joinGame(game, (Person)this.activePersons.get("bot3"));
        
        game = this.startGame((Person)this.activePersons.get("bot4"));
        this.joinGame(game, (Person)this.activePersons.get("bot5"));
        this.joinGame(game, (Person)this.activePersons.get("bot6"));
        
        game = this.startGame((Person)this.activePersons.get("bot7"));
        this.joinGame(game, (Person)this.activePersons.get("bot8"));
        this.joinGame(game, (Person)this.activePersons.get("bot9"));
        
        game = this.startGame((Person)this.activePersons.get("bot10"));
        this.joinGame(game, (Person)this.activePersons.get("bot8"));
    }
}
