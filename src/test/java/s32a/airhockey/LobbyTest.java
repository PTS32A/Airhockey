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
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    }
    
    @AfterClass
    public static void tearDownClass()
    {       

    }
    
    @Before
    public void setUp()
    {
        this.mockLobby = new Lobby();
        try
        {
            this.mockLobby.getMyDatabaseControls().clearDatabase();
        } catch (SQLException ex)
        {
            System.out.println("failed to clear database");
        }
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
        try
        {
            assertTrue("addPerson was false", this.mockLobby.addPerson("testey", "testpass"));
        } catch (IllegalArgumentException ex)
        {
            fail("Illegal argument on addPerson: " + ex.getMessage());
        } catch (SQLException ex)
        {
            fail("SQL exception on addPerson: " + ex.getMessage());
        }
        try
        {
            assertTrue("Person could not be logged in", this.mockLobby.checkLogin("testey", "testpass"));
        } catch (IllegalArgumentException ex)
        {
            fail("Illegal argument on checkLogin: " + ex.getMessage());
        } catch (SQLException ex)
        {
            fail("SQL exception on checkLogin: " + ex.getMessage());
        }
        assertEquals("Person was not correctly initialised", 
                this.mockLobby.getCurrentPerson().getName(), testey.getName());
        try
        {
            assertFalse("wrong password logged in anyway",
                    this.mockLobby.checkLogin("testey", "falsepass"));
        } catch (IllegalArgumentException ex)
        {
            fail("Illegal argument on wrong password: " + ex.getMessage());
        } catch (SQLException ex)
        {
            fail("SQL exception on wrong password: " + ex.getMessage());
        }
        try
        {
            assertFalse("wrong username logged in anyway",
                    this.mockLobby.checkLogin("falsetestey", "testpass"));
        } catch (IllegalArgumentException ex)
        {
            fail("Illegal argument on wrong username: " + ex.getMessage());
        } catch (SQLException ex)
        {
            fail("SQL exception on wrong username: " + ex.getMessage());
        }
        assertEquals("Person not found in list", 
                ((Person)this.mockLobby.getActivePersons().get("testey")).getName(), testey.getName());
    }
    
    @Test
    (expected = IllegalArgumentException.class)
    public void testAddUserNullPlayerName()
    {
        try
        {
            this.mockLobby.addPerson(null, "testpass");
        } catch (SQLException ex)
        {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    (expected = IllegalArgumentException.class)
    public void testAddUserNullPassword()
    {
        try
        {
            this.mockLobby.addPerson("testey", null);
        } catch (SQLException ex)
        {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    (expected = IllegalArgumentException.class)
    public void testAddUserTrailingWhiteSpaceName()
    {
        try
        {
            this.mockLobby.addPerson("testey   ", "testpass");
        } catch (SQLException ex)
        {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    (expected = IllegalArgumentException.class)
    public void testAddUserLeadingWhiteSpacePassword()
    {
        try
        {
            this.mockLobby.addPerson("testey", "   testpass");
        } catch (SQLException ex)
        {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        
        try
        {
            this.mockLobby.addPerson("playey", "testpass");
        } catch (IllegalArgumentException ex)
        {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex)
        {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        try
        {
            this.mockLobby.addPerson("spectey", "testpass");
        } catch (IllegalArgumentException ex)
        {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex)
        {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try
        {
            // playey
            this.mockLobby.checkLogin("playey", "testpass");
        } catch (IllegalArgumentException ex)
        {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex)
        {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        
        try
        {
            // spectey
            this.mockLobby.checkLogin("spectey", "testpass");
        } catch (IllegalArgumentException ex)
        {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex)
        {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
        }
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
    
    //TODO check if user is already logged in
}
