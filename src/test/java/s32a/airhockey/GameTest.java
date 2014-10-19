/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package s32a.airhockey;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Kargathia
 */
public class GameTest
{
    Game game;
    Player starter;
    Spectator spec;
    String message;
    
    public GameTest()
    {
    }
    
    @BeforeClass
    public static void setUpClass()
    {
    }
    
    @AfterClass
    public static void tearDownClass()
    {
    }
    
    @Before
    public void setUp()
    {
        //Player starter
        starter = new Player("testPlayer", (double)0, Colors.Red);
        game = new Game(starter);
        spec = new Spectator("testSpectator", (double)0, game);
        game.addSpectator(spec);
    }
    
    @After
    public void tearDown()
    {
    }

    /**
     * Test of addChatMessage method, of class Game.
     */
    @Test
    (expected = IllegalArgumentException.class)
    public void testAddChatMessageNullMessage()
    {      
        game.addChatMessage(null, starter);
    }
    
    @Test
    (expected = IllegalArgumentException.class)
    public void testAddChatMessageNullPlayer()
    {
        message = "This is a test message.";
        game.addChatMessage(message, null);
    }
    
    @Test
    (expected = IllegalArgumentException.class)
    public void testAddChatMessageWhiteSpaceMessage()
    {      
        message = "";
        game.addChatMessage(message, starter);
        
    }
    
    @Test
    (expected = IllegalArgumentException.class)
    public void testAddPlayerNullPlayer()
    {
        game.addPlayer(null);
    }
    
    /**
     * Test of addPlayer method, of class Game.
     */
    @Test
    public void testAddPlayerAlreadyAParticipant()
    {
        Boolean expResult = false;
        Boolean result = game.addPlayer(starter);
        assertEquals("A player can't be added if he is already a participant", expResult, result);
    }
    
    @Test
    public void testAddPlayerGameFull()
    {
        Player p2 = new Player("testPlayer2", (double)0, Colors.Blue);
        Player p3 = new Player("testPlayer3", (double)0, Colors.Green);
        Player p4 = new Player("testPlayer4", (double)0, Colors.Green);
        
        game.addPlayer(p2);
        game.addPlayer(p3);
        
        Boolean expResult = false;
        Boolean result = game.addPlayer(p4);
        
        assertEquals("A player can't be added if the game is full", expResult, result);
    }
    
    /**
     * Test of addSpectator method, of class Game.
     */
    @Test
    (expected = IllegalArgumentException.class)
    public void testAddSpectatorNullSpectator()
    {
        game.addSpectator(null);
    }
    
    @Test
    public void testAddSpectatorAllreadyAParticipant()
    {
        Boolean expResult = false;
        Boolean result = game.addSpectator(spec);
        
        assertEquals("A spectator can't be added if he is already a participant", expResult, result);
    }
    
    /**
     * Test of removeSpectator method, of class Game.
     */
    @Test
    (expected = IllegalArgumentException.class)
    public void testRemoveSpectatorNullSpectator()
    {
        game.removeSpectator(null);
    }
    
    @Test
    public void testRemoveSpectatorNotAParticipant()
    {
        Spectator spec2 = new Spectator("testSpectator2", (double)0, game);
        
        Boolean expResult = false;
        Boolean result = game.removeSpectator(spec2);
        
        assertEquals("A spectator can't be removed if he is not a participant", expResult, result);
    }
    
    /**
     * Test of beginGame method, of class Game.
     */
    @Test
    public void testBeginGameNotEnoughPlayers()
    {      
        Boolean expResult = false;
        Boolean result = game.beginGame();
        
        assertEquals("Game can't begin without 3 players", expResult, result);
    }
    
    @Test
    public void testBeginGameAlreadyBegon()
    {
        Player p2 = new Player("testPlayer2", (double)0, Colors.Blue);
        Player p3 = new Player("testPlayer3", (double)0, Colors.Green);
        
        game.addPlayer(p2);
        game.addPlayer(p3);
        
        game.beginGame();
        
        Boolean expResult = false;
        Boolean result = game.beginGame();
        
        assertEquals("Game has already begon", expResult, result);
    }
       
    /**
     * Test of adjustDifficulty method, of class Game.
     */
    @Test
    public void testAdjustDifficultyGameAlreadyBegon()
    {   
        game.beginGame();
        
        Boolean expResult = false;
        Boolean result = game.adjustDifficulty(6);
        
        assertEquals("Diffuclty can't be adjusted if game is already begon", expResult, result);
    }
    
    @Test
    (expected = IllegalArgumentException.class)
    public void testAdjustDifficultyMinLimit()
    {
        Boolean result = game.adjustDifficulty(-200);
        
        //Todo review the code and add a minimum difficulty property to Game class.
    }
    
    @Test
    (expected = IllegalArgumentException.class)
    public void testAdjustDifficultyMaxLimit()
    {
        Boolean expResult = false;
        Boolean result = game.adjustDifficulty(200);
        
        //Todo review the code and add a maximum difficulty property to Game class.
    }
    
    @Test
    public void testPauseGameAlreadyPaused()
    {
        game.beginGame();
        
        game.pauseGame(true);
        
        Boolean expResult = false;
        Boolean result = game.pauseGame(true);
        
        assertEquals("Game can't be paused if it is already paused", expResult, result);
    }
    
    @Test
    public void testPauseGameAlreadyUnPaused()
    {
        game.beginGame();
              
        Boolean expResult = false;
        Boolean result = game.pauseGame(false);
        
        assertEquals("Game can't be unpaused if it is already unpaused", expResult, result);
    }
    
    /**
     * Test of getNextColor method, of class Game.
     */
    @Test
    public void testGetNextColorBlue()
    {
        Colors expResult = Colors.Blue;
        Colors result = game.getNextColor();
        
        assertEquals("Next color must be blue", expResult, result);
    }
    
    @Test
    public void testGetNextColorGreen()
    {
        game.addPlayer(new Player("testPlayer2", (double)0, Colors.Blue));
        
        Colors expResult = Colors.Green;
        Colors result = game.getNextColor();
        
        assertEquals("Next color must be green", expResult, result);
    }
}
