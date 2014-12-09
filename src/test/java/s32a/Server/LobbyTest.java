/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Server;

import s32a.Shared.enums.Colors;
import s32a.Server.Player;
import s32a.Server.Person;
import s32a.Server.Game;
import s32a.Server.Lobby;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.lang.*;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import s32a.Shared.IPerson;
import s32a.Shared.IPlayer;

/**
 *
 * @author Kargathia
 */
public class LobbyTest {

    private Lobby mockLobby;

    public LobbyTest() {
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
            this.mockLobby = new Lobby();
            this.mockLobby.clearDatabase();
        }
        catch (RemoteException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException in LobbyTest: " + ex.getMessage());
        }

    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getSingle method, of class Lobby.
     */
    @Test
    public void testGetSingle() {
        try {
            assertNotNull(Lobby.getSingle());
        }
        catch (RemoteException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException in LobbyTest: " + ex.getMessage());
        }
    }

    /**
     * Test of addPlayer method, of class Lobby.
     */
    @Test
    public void testPersons() {
        Person testey = new Person("testey", (double) 15);
        try {
            assertTrue("addPerson was false", this.mockLobby.addPerson("testey", "testpass"));
        }
        catch (IllegalArgumentException ex) {
            fail("Illegal argument on addPerson: " + ex.getMessage());
        }
        catch (SQLException ex) {
            fail("SQL exception on addPerson: " + ex.getMessage());
        }
        catch (RemoteException ex) {
            System.out.println("RemoteException in LobbyTest: " + ex.getMessage());
        }
        try {
            //assertTrue("Person could not be logged in", this.mockLobby.checkLogin("testey", "testpass"));
        }
        catch (IllegalArgumentException ex) {
            fail("Illegal argument on checkLogin: " + ex.getMessage());
        }
//        catch (SQLException ex) {
//            fail("SQL exception on checkLogin: " + ex.getMessage());
//        }
        assertTrue("Person was not correctly initialised",
                this.mockLobby.getMyPerson(testey.getName()) instanceof IPerson);
        try {
//            assertFalse("wrong password logged in anyway",
//                    this.mockLobby.checkLogin("testey", "falsepass"));
        }
        catch (IllegalArgumentException ex) {
            fail("Illegal argument on wrong password: " + ex.getMessage());
        }
//        catch (SQLException ex) {
//            fail("SQL exception on wrong password: " + ex.getMessage());
//        }
        try {
//            assertFalse("wrong username logged in anyway",
//                    this.mockLobby.checkLogin("falsetestey", "testpass"));
        }
        catch (IllegalArgumentException ex) {
            fail("Illegal argument on wrong username: " + ex.getMessage());
        }
//        catch (SQLException ex) {
//            fail("SQL exception on wrong username: " + ex.getMessage());
//        }
        assertEquals("Person not found in list",
                ((Person) this.mockLobby.getActivePersons().get("testey")).getName(), testey.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddUserNullPlayerName() {
        try {
            this.mockLobby.addPerson(null, "testpass");
        }
        catch (SQLException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (RemoteException ex) {
            System.out.println("RemoteException in LobbyTest: " + ex.getMessage());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddUserNullPassword() {
        try {
            this.mockLobby.addPerson("testey", null);
        }
        catch (SQLException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (RemoteException ex) {
            System.out.println("RemoteException in LobbyTest: " + ex.getMessage());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddUserTrailingWhiteSpaceName() {
        try {
            this.mockLobby.addPerson("testey   ", "testpass");
        }
        catch (SQLException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (RemoteException ex) {
            System.out.println("RemoteException in LobbyTest: " + ex.getMessage());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddUserLeadingWhiteSpacePassword() {
        try {
            this.mockLobby.addPerson("testey", "   testpass");
        }
        catch (SQLException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (RemoteException ex) {
            System.out.println("RemoteException in LobbyTest: " + ex.getMessage());
        }
    }

    /**
     * Test of startGame method, of class Lobby.
     */
    @Test
    public void testStartJoinSpectateGame() {
        try {
            this.mockLobby.addPerson("testey", "testpass");
            //this.mockLobby.checkLogin("testey", "testpass");
        }
        catch (IllegalArgumentException | SQLException ex) {
            fail("unable to add or log in testey" + ex.getMessage());
        }
        catch (RemoteException ex) {
            System.out.println("RemoteException in LobbyTest: " + ex.getMessage());
        }

        //testey
        //Game game = this.mockLobby.startGame(this.mockLobby.getMyPerson("testey"));
        //assertNotNull("startGame returned null", game);
        //assertEquals("playedGame wasn't started right", game, 
        //        ((Player)this.mockLobby.getMyPerson("testey")).getMyGame());

        Player testey = (Player) this.mockLobby.getMyPerson("testey");
        assertEquals("currentPerson wasn't starting player",
                ((Game)testey.getMyGame()).getMyPlayers().get(0), testey);
        assertEquals("color wasn't red", testey.getColor(), Colors.Red);
        assertEquals("starting score wasn't 20", testey.getScore(), 20);
        assertTrue("testey wasn't a starting player", testey.isStarter());
        //assertNull("testey started a game while being a player",
        //        this.mockLobby.startGame(this.mockLobby.getMyPerson("testey")));

        try {
            this.mockLobby.addPerson("playey", "testpass");
        }
        catch (IllegalArgumentException | SQLException ex) {
            fail("Unable to add playey: " + ex.getMessage());
        }
        catch (RemoteException ex) {
            System.out.println("RemoteException in LobbyTest: " + ex.getMessage());
        }
        try {
            this.mockLobby.addPerson("spectey", "testpass");
        }
        catch (IllegalArgumentException | SQLException ex) {
            fail("unable to add spectey: " + ex.getMessage());
        }
        catch (RemoteException ex) {
            System.out.println("RemoteException in LobbyTest: " + ex.getMessage());
        }

//        try {
//            // playey
//            this.mockLobby.checkLogin("playey", "testpass");
//        }
//        catch (IllegalArgumentException | SQLException ex) {
//            fail("unable to log in playey" + ex.getMessage());
//        }
//        Person playey = (Person) this.mockLobby.getActivePersons().get("playey");
//        assertEquals("playey was unable to join game",
//                this.mockLobby.joinGame(game,
//                        (Person) this.mockLobby.getActivePersons().get("playey")), game);
//        assertTrue("playey is not a player", this.mockLobby.getActivePersons().get("playey") instanceof Player);
//        playey = (Player) this.mockLobby.getActivePersons().get("playey");
//        assertNull("playey was able to start game while in one",
//                this.mockLobby.startGame(playey));
//        assertNull("playey was able to join the same game twice",
//                this.mockLobby.joinGame(game,
//                        (Person) this.mockLobby.getActivePersons().get("playey")));
//
//        try {
//            // spectey
//            this.mockLobby.checkLogin("spectey", "testpass");
//        }
//        catch (IllegalArgumentException | SQLException ex) {
//            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        Person spectey = (Person) this.mockLobby.getActivePersons().get("spectey");
//        assertEquals("spectey didn't spectate the right game", game,
//                this.mockLobby.spectateGame(game, spectey));
//        assertNull("spectey was able to spectate the same game twice",
//                this.mockLobby.spectateGame(game, spectey));
    }

    /**
     * Test of addChatMessage method, of class Lobby.
     */
    @Test
    public void testAddChatMessage() {
        try {
            this.mockLobby.addPerson("testey", "testpass");
            //this.mockLobby.checkLogin("testey", "testpass");
        }
        catch (IllegalArgumentException | SQLException ex) {
            fail("unable to add or log in testey" + ex.getMessage());
        }
        catch (RemoteException ex) {
            System.out.println("RemoteException in LobbyTest: " + ex.getMessage());
        }
        
        try {
            assertTrue("testey wasn't able to post a chat message",
                    this.mockLobby.addChatMessage("testmessage", "testey"));
        }
        catch (IllegalArgumentException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (RemoteException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException in LobbyTest: " + ex.getMessage());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNullChatMessage() {
        try {
            this.mockLobby.addPerson("testey", "testpass");
            //this.mockLobby.checkLogin("testey", "testpass");
        }
        catch (IllegalArgumentException | SQLException ex) {
            fail("unable to add or log in testey" + ex.getMessage());
        }
        catch (RemoteException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException in LobbyTest: " + ex.getMessage());
        }
        
        try {
            this.mockLobby.addChatMessage(null,
                    ((Person) this.mockLobby.getActivePersons().get("testey")).getName());
        }
        catch (IllegalArgumentException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (RemoteException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException in LobbyTest: " + ex.getMessage());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddFromNullChatMessage() {
        try {
            this.mockLobby.addPerson("testey", "testpass");
            //this.mockLobby.checkLogin("testey", "testpass");
        }
        catch (IllegalArgumentException | SQLException ex) {
            fail("unable to add or log in testey" + ex.getMessage());
        }
        catch (RemoteException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException in LobbyTest: " + ex.getMessage());
        }
        
        try {
            this.mockLobby.addChatMessage("anonpost", null);
        }
        catch (IllegalArgumentException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (RemoteException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException in LobbyTest: " + ex.getMessage());
        }
    }

    /**
     * Test of endGame method, of class Lobby.
     */
    @Test
    public void testEndGame() {
        try {
            this.mockLobby.addPerson("testey", "testpass");
            //this.mockLobby.checkLogin("testey", "testpass");
        }
        catch (IllegalArgumentException | SQLException ex) {
            fail("unable to add or log in testey" + ex.getMessage());
        }
        catch (RemoteException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException in LobbyTest: " + ex.getMessage());
        }

//        Game game = this.mockLobby.startGame(this.mockLobby.getMyPerson("testey"));
//        assertNotNull("game wasn't started properly", game);
//        assertTrue("game didn't end as it should", this.mockLobby.endGame(game, null));
//        assertFalse("successfully ended a previously ended game",
//                this.mockLobby.endGame(game, null));
//        game = this.mockLobby.startGame(this.mockLobby.getMyPerson("testey"));
//        assertTrue("game didn't end as it should with leaver",
//                this.mockLobby.endGame(game,
//                        (Player) this.mockLobby.getActivePersons().get("testey")));

        // TODO: check whether rating updates as it should
    }

    @Test
    public void testLogout1() {
        try {
            this.mockLobby.addPerson("testey", "testpass");
            //this.mockLobby.checkLogin("testey", "testpass");
        }
        catch (IllegalArgumentException | SQLException ex) {
            fail("unable to add or log in testey" + ex.getMessage());
        }
        catch (RemoteException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException in LobbyTest: " + ex.getMessage());
        }

        //Game game = this.mockLobby.startGame(this.mockLobby.getMyPerson("testey"));

        try {
            this.mockLobby.addPerson("testey1", "testpass");
            //this.mockLobby.checkLogin("testey1", "testpass");
        }
        catch (IllegalArgumentException | SQLException ex) {
            fail("unable to add or log in testey1" + ex.getMessage());
        }
        catch (RemoteException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException in LobbyTest: " + ex.getMessage());
        }
        //this.mockLobby.joinGame(game, this.mockLobby.getMyPerson("testey"));

        try {
            this.mockLobby.addPerson("testey2", "testpass");
            //this.mockLobby.checkLogin("testey2", "testpass");
        }
        catch (IllegalArgumentException | SQLException ex) {
            fail("unable to add or log in testey2" + ex.getMessage());
        }
        catch (RemoteException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException in LobbyTest: " + ex.getMessage());
        }
        //this.mockLobby.joinGame(game, this.mockLobby.getMyPerson("testey"));

        //game.beginGame();

        //assertTrue("Logout didn't succeed", this.mockLobby.logOut(this.mockLobby.getMyPerson("testey")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLogout2() {
        try {
            this.mockLobby.logOut(null);
        }
        catch (RemoteException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException in LobbyTest: " + ex.getMessage());
        }
    }

    //TODO check if user is already logged in
}
