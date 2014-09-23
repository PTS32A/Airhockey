/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package s32a.airhockey;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/**
 *
 * @author Kargathia
 */
public class Lobby
{
    private static Lobby _singleton;
    @Getter private Chatbox mychatbox;
    @Getter private DatabaseControls myDatabaseControls;
    @Getter private Person currentPerson;
    @Getter private List<Person> activePersons;
    @Getter private List<Game> activeGames;
    @Getter private Game playedGame;
    @Getter private List<Game> spectatedGames;
    
    /**
     * 
     */
    public Lobby()
    {
        this.mychatbox = new Chatbox();
        this.myDatabaseControls = new DatabaseControls();
        this.currentPerson = null;
        this.activePersons = new ArrayList<>();
        this.activeGames = new ArrayList<>();
        this.playedGame = null;
        this.spectatedGames = new ArrayList<>();
    }
    
    /**
     * 
     * @return instance of lobby
     */
    public static Lobby getSingle()
    {
        
    }
    
    /**
     * 
     * @param playerName
     * @param password
     * @return 
     */
    public boolean addPlayer(String playerName, String password)
    {
        
    }
    
    /**
     * 
     * @param playerName
     * @param password
     * @return 
     */
    public boolean checkLogin(String playerName, String password)
    {
        
    }
    
    /**
     * 
     * @return 
     */
    public boolean startGame()
    {
        
    }
    
    /**
     * 
     * @param game
     * @return 
     */
    public boolean joinGame(Game game)
    {
        
    }
    
    /**
     * 
     * @param game
     * @return 
     */
    public boolean spectateGame(Game game)
    {
        
    }
    
    /**
     * 
     * @param message
     * @param from
     * @return 
     */
    public boolean addChatMessage(String message, Person from)
    {
        
    }
    
    /**
     * 
     * @param game
     * @param hasLeft
     * @return 
     */
    public boolean endGame(Game game, Player hasLeft)
    {
        
    }
    
    /**
     * 
     * @param participant 
     */
    public void returnToLobby(Person participant)
    {
        
    }
    
    /**
     * 
     * @param myGame
     * @param message
     * @param from
     * @return 
     */
    public boolean addGameChatMessage(Game myGame, String message, Person from)
    {
        
    }
    
    /**
     * 
     * @param gameID
     * @return 
     */
    public Game getMyGame(String gameID)
    {
        
    }
}
