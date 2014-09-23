/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package s32a.airhockey;

import java.util.List;
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
public class LobbyTest
{
    
    public LobbyTest()
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
    }
    
    @After
    public void tearDown()
    {
    }

    /**
     * Test of getSingle method, of class Lobby.
     */
    @Test
    public void testGetSingle()
    {
        System.out.println("getSingle");
        Lobby expResult = null;
        Lobby result = Lobby.getSingle();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addPlayer method, of class Lobby.
     */
    @Test
    public void testAddPlayer()
    {
        System.out.println("addPlayer");
        String playerName = "";
        String password = "";
        Lobby instance = new Lobby();
        boolean expResult = false;
        boolean result = instance.addPlayer(playerName, password);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of checkLogin method, of class Lobby.
     */
    @Test
    public void testCheckLogin()
    {
        System.out.println("checkLogin");
        String playerName = "";
        String password = "";
        Lobby instance = new Lobby();
        boolean expResult = false;
        boolean result = instance.checkLogin(playerName, password);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of startGame method, of class Lobby.
     */
    @Test
    public void testStartGame()
    {
        System.out.println("startGame");
        Lobby instance = new Lobby();
        boolean expResult = false;
        boolean result = instance.startGame();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of joinGame method, of class Lobby.
     */
    @Test
    public void testJoinGame()
    {
        System.out.println("joinGame");
        Game game = null;
        Lobby instance = new Lobby();
        boolean expResult = false;
        boolean result = instance.joinGame(game);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of spectateGame method, of class Lobby.
     */
    @Test
    public void testSpectateGame()
    {
        System.out.println("spectateGame");
        Game game = null;
        Lobby instance = new Lobby();
        boolean expResult = false;
        boolean result = instance.spectateGame(game);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addChatMessage method, of class Lobby.
     */
    @Test
    public void testAddChatMessage()
    {
        System.out.println("addChatMessage");
        String message = "";
        Person from = null;
        Lobby instance = new Lobby();
        boolean expResult = false;
        boolean result = instance.addChatMessage(message, from);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of endGame method, of class Lobby.
     */
    @Test
    public void testEndGame()
    {
        System.out.println("endGame");
        Game game = null;
        Player hasLeft = null;
        Lobby instance = new Lobby();
        boolean expResult = false;
        boolean result = instance.endGame(game, hasLeft);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of returnToLobby method, of class Lobby.
     */
    @Test
    public void testReturnToLobby()
    {
        System.out.println("returnToLobby");
        Person participant = null;
        Lobby instance = new Lobby();
        instance.returnToLobby(participant);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addGameChatMessage method, of class Lobby.
     */
    @Test
    public void testAddGameChatMessage()
    {
        System.out.println("addGameChatMessage");
        Game myGame = null;
        String message = "";
        Person from = null;
        Lobby instance = new Lobby();
        boolean expResult = false;
        boolean result = instance.addGameChatMessage(myGame, message, from);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMyGame method, of class Lobby.
     */
    @Test
    public void testGetMyGame()
    {
        System.out.println("getMyGame");
        String gameID = "";
        Lobby instance = new Lobby();
        Game expResult = null;
        Game result = instance.getMyGame(gameID);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMychatbox method, of class Lobby.
     */
    @Test
    public void testGetMychatbox()
    {
        System.out.println("getMychatbox");
        Lobby instance = new Lobby();
        Chatbox expResult = null;
        Chatbox result = instance.getMychatbox();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMyDatabaseControls method, of class Lobby.
     */
    @Test
    public void testGetMyDatabaseControls()
    {
        System.out.println("getMyDatabaseControls");
        Lobby instance = new Lobby();
        DatabaseControls expResult = null;
        DatabaseControls result = instance.getMyDatabaseControls();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCurrentPerson method, of class Lobby.
     */
    @Test
    public void testGetCurrentPerson()
    {
        System.out.println("getCurrentPerson");
        Lobby instance = new Lobby();
        Person expResult = null;
        Person result = instance.getCurrentPerson();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getActivePersons method, of class Lobby.
     */
    @Test
    public void testGetActivePersons()
    {
        System.out.println("getActivePersons");
        Lobby instance = new Lobby();
        List<Person> expResult = null;
        List<Person> result = instance.getActivePersons();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getActiveGames method, of class Lobby.
     */
    @Test
    public void testGetActiveGames()
    {
        System.out.println("getActiveGames");
        Lobby instance = new Lobby();
        List<Game> expResult = null;
        List<Game> result = instance.getActiveGames();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPlayedGame method, of class Lobby.
     */
    @Test
    public void testGetPlayedGame()
    {
        System.out.println("getPlayedGame");
        Lobby instance = new Lobby();
        Game expResult = null;
        Game result = instance.getPlayedGame();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSpectatedGames method, of class Lobby.
     */
    @Test
    public void testGetSpectatedGames()
    {
        System.out.println("getSpectatedGames");
        Lobby instance = new Lobby();
        List<Game> expResult = null;
        List<Game> result = instance.getSpectatedGames();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
