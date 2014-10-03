/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package s32a.airhockey;

import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.lang.*;

/**
 *
 * @author Kargathia
 */
public class LobbyTest
{
    private Lobby mockLobby;
    public LobbyTest()
    {
    }
    
    @BeforeClass
    public static void setUpClass()
    {
        Lobby.getSingle().addPerson("testey", "testpass");
    }
    
    @AfterClass
    public static void tearDownClass()
    {       
        Lobby.getSingle().removePerson("testey");
    }
    
    @Before
    public void setUp()
    {
        this.mockLobby = new Lobby();
        this.mockLobby.checkLogin("testey", "testpass");
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
        assertNotNull(Lobby.getSingle());
    }

    /**
     * Test of addPlayer method, of class Lobby.
     */
    @Test
    public void testPersons()
    {
        Person testey = new Person("testey", 15);
        //assertTrue("addPerson was false", this.mockLobby.addPerson("testey", "testpass"));
        //assertTrue("Person was not logged in", this.mockLobby.checkLogin("testey", "testpass"));
        assertEquals("Person was not correctly initialised", this.mockLobby.getCurrentPerson(), testey);
        assertFalse("wrong password logged in anyway", this.mockLobby.checkLogin("testey", "falsepass"));
        assertFalse("wrong username logged in anyway", this.mockLobby.checkLogin("falsetestey", "testpass"));
        assertEquals("Person not found in list", (Person)this.mockLobby.getActivePersons().get("testey"), testey);
    }
    
    @Test
    (expected = IllegalArgumentException.class)
    public void testAddUserNullPlayerName()
    {
        this.mockLobby.addPerson(null, "testpass");
    }
    
    @Test
    (expected = IllegalArgumentException.class)
    public void testAddUserNullPassword()
    {
        this.mockLobby.addPerson("testey", null);
    }
    
    @Test
    (expected = IllegalArgumentException.class)
    public void testAddUserTrailingWhiteSpaceName()
    {
        this.mockLobby.addPerson("testey   ", "testpass");
    }
    
    @Test
    (expected = IllegalArgumentException.class)
    public void testAddUserLeadingWhiteSpacePassword()
    {
        this.mockLobby.addPerson("testey", "   testpass");
    }

    /**
     * Test of startGame method, of class Lobby.
     */
    @Test
    public void testStartJoinSpectateGame()
    {
        assertTrue("startGame returned false", this.mockLobby.startGame());
        assertNotNull("playedGame was null", this.mockLobby.getPlayedGame());
        
        Player testey = (Player)this.mockLobby.getCurrentPerson();
        assertEquals("currentPerson wasn't starting player", 
                this.mockLobby.getPlayedGame().getMyPlayers().get(0), testey);
        assertEquals("color wasn't red", testey.getColor(), "red");
        assertEquals("starting score wasn't 20", testey.getScore(), 20);
        assertTrue("testey wasn't a starting player", testey.isStarter());       
        assertFalse("testey started a game while being a player", this.mockLobby.startGame());
        
        this.mockLobby.addPerson("playey", "testpass");
        this.mockLobby.addPerson("spectey", "testpass");
        
        this.mockLobby.checkLogin("playey", "testpass");
        //this.mockLobby.checkLogin("spectey", "testpass");
        assertTrue("playey was unable to join game", this.mockLobby.joinGame(this.mockLobby.))
    }

    /**
     * Test of joinGame method, of class Lobby.
     */
    @Test
    public void testJoinGame()
    {
        
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
