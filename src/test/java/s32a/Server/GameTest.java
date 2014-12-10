/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Server;

import com.badlogic.gdx.math.Vector2;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.DoubleProperty;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import s32a.Client.ClientData.GameClient;
import s32a.Server.Game;
import s32a.Server.Player;
import s32a.Server.Spectator;
import s32a.Shared.enums.Colors;

/**
 *
 * @author Kargathia
 */
public class GameTest {

    Game game;
    GameClient client;
    Player starter;
    Spectator spec;
    String message;

    public GameTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        try {
            //Player starter
            client = new GameClient();
            starter = new Player("testPlayer", (double) 20, Colors.Red);
            game = new Game(starter);
            spec = new Spectator("testSpectator", (double) 0);
            
            game.startPublisher(starter, client);
            game.addSpectator(spec, client);
        }
        catch (RemoteException ex) {
            Logger.getLogger(GameTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on setUp: " + ex.getMessage());
        }
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of addChatMessage method, of class Game.
     * Test to confirm it isn't possible to send no(null) message.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddChatMessageNullMessage() {
        try {
            game.addChatMessage(null, starter.getName());
            fail("ChatMessage can't be null");
        }
        catch (RemoteException ex) {
            Logger.getLogger(GameTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on testAddChatMessageNullMessage: " + ex.getMessage());
        }
    }

    /**
    * Test of addChatMessage method, of class Game
    * Test to confirm it isn't possible to send a chatmessage with no sender.
    */
    @Test(expected = IllegalArgumentException.class)
    public void testAddChatMessageNullPlayer() {
        try {
            message = "This is a test message.";
            game.addChatMessage(message, null);
            fail("Given player can't be null");
        }
        catch (RemoteException ex) {
            Logger.getLogger(GameTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on testAddChatMessageNullPlayer: " + ex.getMessage());
        }
    }

    /**
    * Test of addChatMessage method, of class Game
    * Test to confirm it isn't possible to send an empty message.
    */
    @Test(expected = IllegalArgumentException.class)
    public void testAddChatMessageEmpty() {
        try {
            message = "";
            game.addChatMessage(message, starter.getName());
            fail("ChatMessage must contain characters");
        }
        catch (RemoteException ex) {
            Logger.getLogger(GameTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on testAddChatMessageEmpty: " + ex.getMessage());
        }
    }

    /**
    * Test of addChatMessage method, of class Game
    * Test to confirm it isn't possible to send a message containing only white spaces.
    */
    @Test(expected = IllegalArgumentException.class)
    public void testAddChatMessageWhiteSpaceMessage() {
        try {
            message = "   ";
            game.addChatMessage(message, starter.getName());
            fail("ChatMessage must contain characters other than white space");
        }
        catch (RemoteException ex) {
            Logger.getLogger(GameTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on testAddChatMessageWhiteSpaceMessage: " + ex.getMessage());
        }
    }

    /**
    * Test of addPlayer method, of class Game.
    * Test to confirm it isn't possible to create a game with no starterplayer.
    */
    @Test(expected = IllegalArgumentException.class)
    public void testAddPlayerNullPlayer() {
        try {
            game.addPlayer(null, client);
            fail("Given player can't be null");
        }
        catch (RemoteException ex) {
            Logger.getLogger(GameTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on testAddPlayerNullPlayer: " + ex.getMessage());
        }
    }
    
    /**
    * Test of addPlayer method, of class Game.
    * Test to confirm it isn't possible to create a game with no gameclient.
    */
    @Test(expected = IllegalArgumentException.class)
    public void testAddPlayerNullClient() {
        try {
            game.addPlayer(starter, null);
            fail("Given GameClient can't be null");
        }
        catch (RemoteException ex) {
            Logger.getLogger(GameTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on testAddPlayerNullClient: " + ex.getMessage());
        }
    }

    /**
     * Test of addPlayer method, of class Game.
     * Test to confirm it isn't possible to join a game multiple times.
     */
    @Test
    public void testAddPlayerAlreadyAParticipant() {
        try {
            Boolean expResult = false;
            Boolean result = game.addPlayer(starter, client);
            assertEquals("A player can't be added if he is already a participant", expResult, result);
        }
        catch (RemoteException ex) {
            Logger.getLogger(GameTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on testAddPlayerAlreadyAParticipant: " + ex.getMessage());
        }
    }

    /**
     * Test of addPlayer method, of class Game.
     * Test to confirm it isn't possible to join a full game.
     */
    @Test
    public void testAddPlayerGameFull() {
        try {
            Player p2 = new Player("testPlayer2", (double) 20, Colors.Blue);
            Player p3 = new Player("testPlayer3", (double) 20, Colors.Green);
            Player p4 = new Player("testPlayer4", (double) 20, Colors.Green);

            game.addPlayer(p2, client);
            game.addPlayer(p3, client);
            Boolean expResult = false;
            Boolean result = game.addPlayer(p4, client);

            assertEquals("A player can't be added if the game is full", expResult, result);
        }
        catch (RemoteException ex) {
            Logger.getLogger(GameTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on testAddPlayerGameFull: " + ex.getMessage());
        }
    }

    /**
     * Test of addSpectator method, of class Game.
     * Test to confirm it isn't possible to spectate a game as no(null) spectator.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddSpectatorNullSpectator() {
        try {
            game.addSpectator(null, client);
            fail("Given spectator can't be null");
        }
        catch (RemoteException ex) {
            Logger.getLogger(GameTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on testAddSpectatorNullSpectator: " + ex.getMessage());
        }
    }
    
    /**
     * Test of addSpectator method, of class Game.
     * Test to confirm it isn't possible to spectate a game without a client.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddSpectatorNullClient() {
        try {
            game.addSpectator(spec, null);
            fail("Given GameClient can't be null");
        }
        catch (RemoteException ex) {
            Logger.getLogger(GameTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on testAddSpectatorNullClient: " + ex.getMessage());
        }
    }

    /**
     * Test of addSpectator method, of class Game.
     * Test to confirm it isn't possible to spectate a game multiple times.
     */
    @Test
    public void testAddSpectatorAllreadyAParticipant() {
        try {
            Boolean expResult = false;
            Boolean result = game.addSpectator(spec, client);
            
            assertEquals("A spectator can't be added if he is already a participant", expResult, result);
        }
        catch (RemoteException ex) {
            Logger.getLogger(GameTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on testAddSpectatorAllreadyAParticipant: " + ex.getMessage());
        }
    }

    /**
     * Test of removeSpectator method, of class Game.
     * Test to confirm it isn't possible to remove no(null) spectator.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testRemoveSpectatorNullSpectator() {
        try {
            game.removeSpectator(null);
            fail("Given spectator can't be null");
        }
        catch (RemoteException ex) {
            Logger.getLogger(GameTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on testRemoveSpectatorNullSpectator: " + ex.getMessage());
        }
    }

    /**
     * Test of removeSpectator method, of class Game.
     * Test to confirm it isn't possible to remove a spectator who isn't spectating the game.
     */
    @Test
    public void testRemoveSpectatorNotAParticipant() {
        try {
            Spectator spec2 = new Spectator("testSpectator2", (double) 0);

            Boolean expResult = false;
            Boolean result = game.removeSpectator(spec2);

            assertEquals("A spectator can't be removed if he is not a participant", expResult, result);
        }
        catch (RemoteException ex) {
            Logger.getLogger(GameTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on testRemoveSpectatorNotAParticipant: " + ex.getMessage());
        }
    }

    /**
     * Test of beginGame method, of class Game.
     * Test to confirm it isn't possible to start a game with less than 3 players.
     */
    @Test
    public void testBeginGameNotEnoughPlayers() {
        try {
            Boolean expResult = false;
            Boolean result = game.beginGame();

            assertEquals("Game can't begin without 3 players", expResult, result);
        }
        catch (RemoteException ex) {
            Logger.getLogger(GameTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on testBeginGameNotEnoughPlayers: " + ex.getMessage());
        }
    }

    /**
     * Test of beginGame method, of class Game.
     * Test to confirm it isn't possible to start an already started game.
     */
    @Test
    public void testBeginGameAlreadyBegun() {
        try {
            Player p2 = new Player("testPlayer2", (double) 20, Colors.Blue);
            Player p3 = new Player("testPlayer3", (double) 20, Colors.Green);

            game.addPlayer(p2, client);
            game.addPlayer(p3, client);
            game.beginGame();

            Boolean expResult = false;
            Boolean result = game.beginGame();

            assertEquals("Game has already begon", expResult, result);
        }
        catch (RemoteException ex) {
            Logger.getLogger(GameTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on testBeginGameAlreadyBegun: " + ex.getMessage());
        }
    }

    /**
     * Test of adjustDifficulty method, of class Game.
     * Test to confirm it isn't possible to adjust the difficulty of an already started game.
     */
    @Test
    public void testAdjustDifficultyGameAlreadyBegun() {
        try {
            Player p2 = new Player("testPlayer2", (double) 0, Colors.Blue);
            Player p3 = new Player("testPlayer3", (double) 0, Colors.Green);

            game.addPlayer(p2, client);
            game.addPlayer(p3, client);
            game.beginGame();

            Boolean expResult = false;
            Boolean result = game.adjustDifficulty(6);

            assertEquals("Diffuclty can't be adjusted if game is already begon", expResult, result);
        }
        catch (RemoteException ex) {
            Logger.getLogger(GameTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on testAdjustDifficultyGameAlreadyBegun: " + ex.getMessage());
        }
    }

    /**
     * Test of adjustDifficulty method, of class Game.
     * Test to confirm it isn't possible to adjust the difficulty to less than the minimum
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAdjustDifficultyMinLimit() {
        try {
            Boolean expResult = false;
            Boolean result = game.adjustDifficulty(-1);

            fail("Speed must be greater than the minimal limit (0)");
        }
        catch (RemoteException ex) {
            Logger.getLogger(GameTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on testAdjustDifficultyMinLimit: " + ex.getMessage());
        }
    }

    /**
     * Test of adjustDifficulty method, of class Game.
     * Test to confirm it isn't possible to adjust the difficulty to more than the maximum
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAdjustDifficultyMaxLimit() {
        try {
            Boolean expResult = false;
            Boolean result = game.adjustDifficulty(101);

            fail("Speed must be smaller than the maximal limit (101)");
        }
        catch (RemoteException ex) {
            Logger.getLogger(GameTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on testAdjustDifficultyMaxLimit: " + ex.getMessage());
        }
    }

    /**
     * Test of pauseGame method, of class Game.
     * Test to confirm it isn't possible to pause an already paused game.
     */
    @Test
    public void testPauseGameAlreadyPaused() {
        try {
            game.beginGame();

            game.pauseGame(true);

            Boolean expResult = false;
            Boolean result = game.pauseGame(true);

            assertEquals("Game can't be paused if it is already paused", expResult, result);
        }
        catch (RemoteException ex) {
            Logger.getLogger(GameTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on testPauseGameAleadyPaused: " + ex.getMessage());
        }
    }

    /**
     * Test of pauseGame method, of class Game.
     * Test to confirm it isn't possible to unpause an already unpaused game.
     */
    @Test
    public void testPauseGameAlreadyUnPaused() {
        try {
            game.beginGame();

            Boolean expResult = false;
            Boolean result = game.pauseGame(false);

            assertEquals("Game can't be unpaused if it is already unpaused", expResult, result);
        }
        catch (RemoteException ex) {
            Logger.getLogger(GameTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException in GameTest: " + ex.getMessage());
        }
    }

    /**
     * Test of pauseGame method, of class Game.
     */
    @Test
    public void testPauseGame() {
        try {
            game.beginGame();
            game.pauseGame(true);

            Vector2 expResult = game.getMyPuck().getPosition().get();
//            DoubleProperty expResultX = game.getMyPuck().getXPos();
//            DoubleProperty expResultY = game.getMyPuck().getYPos();

            try {
                Thread.sleep(1000);
            }
            catch (Exception ex) {
                System.out.print("Exception: " + ex.getMessage());
            }

            Vector2 result = game.getMyPuck().getPosition().get();
//            DoubleProperty resultX = game.getMyPuck().getXPos();
//            DoubleProperty resultY = game.getMyPuck().getYPos();

            game.pauseGame(false);

//        System.out.print("ExpResult: " + expResult.toString());
//        System.out.print("Result: " + result.toString());
            assertEquals("Puck position can't change during pause", expResult, result);
        }
        catch (RemoteException ex) {
            Logger.getLogger(GameTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException in GameTest: " + ex.getMessage());
        }
    }

    /**
     * Test of getNextColor method, of class Game.
     * Test to confirm that the next colour after Red is Blue.
     */
    @Test
    public void testGetNextColorBlue() {
        Colors expResult = Colors.Blue;
        Colors result = game.getNextColor();

        assertEquals("Next color must be blue", expResult, result);
    }

    /**
     * Test of getNextColor method, of class Game.
     * Test to confirm that the next colour after Blue is Green.
     */
    @Test
    public void testGetNextColorGreen() {
        try {
            game.startPublisher(starter, client);
            game.addPlayer(new Player("testPlayer2", (double)20, Colors.Blue), client);
            
            Colors expResult = Colors.Green;
            Colors result = game.getNextColor();
            
            assertEquals("Next color must be green", expResult, result);
        }
        catch (RemoteException ex) {
            Logger.getLogger(GameTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCustomSetup() {
        game.customSetup(new Vector2(300, 300), 1, 1, 1, 1);
        fail("Custom position is outside of field");
    }
}
