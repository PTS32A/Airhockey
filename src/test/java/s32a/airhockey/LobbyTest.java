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
        Lobby.getSingle().removePerson("playey");
        Lobby.getSingle().removePerson("spectey");
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
        assertEquals("Person was not correctly initialised", 
                this.mockLobby.getCurrentPerson(), testey);
        assertFalse("wrong password logged in anyway", 
                this.mockLobby.checkLogin("testey", "falsepass"));
        assertFalse("wrong username logged in anyway", 
                this.mockLobby.checkLogin("falsetestey", "testpass"));
        assertEquals("Person not found in list", 
                (Person)this.mockLobby.getActivePersons().get("testey"), testey);
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
        //testey
        Game game = this.mockLobby.startGame(this.mockLobby.getCurrentPerson());
        assertNotNull("startGame returned null", game);
        assertEquals("playedGame wasn't started right", this.mockLobby.getPlayedGame(), game);
        
        Player testey = (Player)this.mockLobby.getCurrentPerson();
        assertEquals("currentPerson wasn't starting player", 
                this.mockLobby.getPlayedGame().getMyPlayers().get(0), testey);
        assertEquals("color wasn't red", testey.getColor(), "red");
        assertEquals("starting score wasn't 20", testey.getScore(), 20);
        assertTrue("testey wasn't a starting player", testey.isStarter());       
        assertNull("testey started a game while being a player", 
                this.mockLobby.startGame(this.mockLobby.getCurrentPerson()));
        
        this.mockLobby.addPerson("playey", "testpass");
        this.mockLobby.addPerson("spectey", "testpass");
        
        // playey
        this.mockLobby.checkLogin("playey", "testpass");
        Person playey = (Person)this.mockLobby.getActivePersons().get("playey");
        assertNull("playedGame wasn't cleared", 
                this.mockLobby.joinGame(this.mockLobby.getPlayedGame(), playey));
        assertEquals("playey was unable to join game", 
                this.mockLobby.joinGame(game,
                        (Person)this.mockLobby.getActivePersons().get("playey")), game);
        assertNull("playey was able to start game while in one", 
                this.mockLobby.startGame(playey));
        assertNull("playey was able to join the same game twice", 
                this.mockLobby.joinGame(game,
                        (Person)this.mockLobby.getActivePersons().get("playey")));
        
        // spectey
        this.mockLobby.checkLogin("spectey", "testpass");
        Person spectey = (Person)this.mockLobby.getActivePersons().get("spectey");
        assertEquals("spectey didn't spectate the right game", 
                this.mockLobby.spectateGame(game, spectey));
        assertNull("spectey was able to spectate the same game twice", 
                this.mockLobby.spectateGame(game, spectey));
    }

    /**
     * Test of addChatMessage method, of class Lobby.
     */
    @Test
    public void testAddChatMessage()
    {
        assertTrue("testey wasn't able to post a chat message", 
                this.mockLobby.addChatMessage("testmessage", 
                this.mockLobby.getCurrentPerson()));
        assertFalse("testey was able to post a null content message", 
                this.mockLobby.addChatMessage(null, 
                        (Person)this.mockLobby.getActivePersons().get("testey")));
        assertFalse("anon managed to post",
                this.mockLobby.addChatMessage("anonpost", null));
    }

    /**
     * Test of endGame method, of class Lobby.
     */
    @Test
    public void testEndGame()
    {
        Game game = this.mockLobby.startGame(this.mockLobby.getCurrentPerson());
        assertTrue("game didn't end as it should", this.mockLobby.endGame(game, null));
        assertFalse("successfully ended a previously ended game", 
                this.mockLobby.endGame(game, null));
        game = this.mockLobby.startGame(this.mockLobby.getCurrentPerson());
        assertTrue("game didn't end as it should with leaver", 
                this.mockLobby.endGame(game, 
                        (Player)this.mockLobby.getActivePersons().get("testey")));
    }
}
