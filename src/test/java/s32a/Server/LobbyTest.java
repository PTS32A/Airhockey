/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Server;

import java.lang.*;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import s32a.Client.ClientData.GameClient;
import s32a.Client.ClientData.LobbyClient;
import s32a.Server.Game;
import s32a.Server.Lobby;
import s32a.Server.Person;
import s32a.Server.Player;
import s32a.Shared.IPerson;
import s32a.Shared.IPlayer;
import s32a.Shared.enums.Colors;

/**
 *
 * @author Kargathia
 */
public class LobbyTest {

    private Lobby mockLobby;
    private LobbyClient mockLobbyClient;
    private GameClient mockGameClient;

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
            this.mockLobbyClient = new LobbyClient(mockLobby);
            this.mockGameClient = new GameClient();
            
            mockLobby.startPublisher();
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
        try {
            Person testey = new Person("testey", (double) 15);

            assertTrue("addPerson was false", this.mockLobby.addPerson("testey", "testpass"));

            assertTrue("Person could not be logged in", this.mockLobby.checkLogin("testey", "testpass", mockLobbyClient));

            assertTrue("Person was not correctly initialised",
                    this.mockLobby.getActivePersons().get(testey.getName()) instanceof IPerson);

            assertFalse("wrong password logged in anyway",
                    this.mockLobby.checkLogin("testey", "falsepass", mockLobbyClient));
            assertFalse("wrong username logged in anyway",
                    this.mockLobby.checkLogin("falsetestey", "testpass", mockLobbyClient));
            assertEquals("Person not found in list",
                    ((Person) this.mockLobby.getActivePersons().get("testey")).getName(), testey.getName());
        }
        catch (IllegalArgumentException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("IllegalArgumentException on testPersons: " + ex.getMessage());
            fail("IllegalArgumentException on testPersons: " + ex.getMessage());
        }
        catch (SQLException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("SQLException on testPersons: " + ex.getMessage());
            fail("SQLException on testPersons: " + ex.getMessage());
        }
        catch (RemoteException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on testPersons: " + ex.getMessage());
            fail("RemoteException on testPersons: " + ex.getMessage());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddUserNullPlayerName() {
        try {
            this.mockLobby.addPerson(null, "testpass");
        }
        catch (SQLException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("SQLException on testAddUserNullPlayerName: " + ex.getMessage());
            fail("SQLException on testAddUserNullPlayerName: " + ex.getMessage());
        }
        catch (RemoteException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on testAddUserNullPlayerName: " + ex.getMessage());
            fail("RemoteException on testAddUserNullPlayerName: " + ex.getMessage());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddUserNullPassword() {
        try {
            this.mockLobby.addPerson("testey", null);
        }
        catch (SQLException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("SQLException on testAddUserNullPassword: " + ex.getMessage());
            fail("SQLException on testAddUserNullPassword: " + ex.getMessage());
        }
        catch (RemoteException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on testAddUserNullPassword: " + ex.getMessage());
            fail("RemoteException on testAddUserNullPassword: " + ex.getMessage());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddUserTrailingWhiteSpaceName() {
        try {
            this.mockLobby.addPerson("testey   ", "testpass");
        }
        catch (SQLException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("SQLException on testAddUserTrailingWhiteSpaceName: " + ex.getMessage());
            fail("SQLException on testAddUserTrailingWhiteSpaceName: " + ex.getMessage());
        }
        catch (RemoteException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on testAddUserTrailingWhiteSpaceName: " + ex.getMessage());
            fail("RemoteException on testAddUserTrailingWhiteSpaceName: " + ex.getMessage());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddUserLeadingWhiteSpacePassword() {
        try {
            this.mockLobby.addPerson("testey", "   testpass");
        }
        catch (SQLException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("SQLException on testAddUserLeadingWhiteSpacePassword: " + ex.getMessage());
            fail("SQLException on testAddUserLeadingWhiteSpacePassword: " + ex.getMessage());
        }
        catch (RemoteException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on testAddUserLeadingWhiteSpacePassword: " + ex.getMessage());
            fail("RemoteException on testAddUserLeadingWhiteSpacePassword: " + ex.getMessage());
        }
    }

    /**
     * Test of startGame method, of class Lobby.
     */
    @Test
    public void testStartJoinSpectateGame() {

        try {
            this.mockLobby.addPerson("testey", "testpass");
            this.mockLobby.checkLogin("testey", "testpass", mockLobbyClient);

            Game game;

            game = this.mockLobby.startGame(this.mockLobby.getMyPerson("testey"), mockGameClient);

            assertNotNull("startGame returned null", game);
            assertEquals("playedGame wasn't started right", game,
                    ((Player) this.mockLobby.getMyPerson("testey")).getMyGame());

            Player testey = (Player) this.mockLobby.getActivePersons().get("testey");
            assertEquals("currentPerson wasn't starting player",
                    ((Game) testey.getMyGame()).getMyPlayers().get(0), testey);
            assertEquals("color wasn't red", testey.getColor(), Colors.Red);
            assertEquals("starting score wasn't 20", testey.getScore(), 20);
            assertTrue("testey wasn't a starting player", testey.isStarter());
            assertNull("testey started a game while being a player",
                    this.mockLobby.startGame(this.mockLobby.getMyPerson("testey"), mockGameClient));

            this.mockLobby.addPerson("playey", "testpass");

            this.mockLobby.addPerson("spectey", "testpass");

            // playey
            this.mockLobby.checkLogin("playey", "testpass", mockLobbyClient);

            Person playey = (Person) this.mockLobby.getActivePersons().get("playey");
            assertEquals("playey was unable to join game",
                    this.mockLobby.joinGame(game,
                            (Person)this.mockLobby.getActivePersons().get("playey"), mockGameClient));
            assertTrue("playey is not a player", this.mockLobby.getActivePersons().get("playey") instanceof Player);
            playey = (Player) this.mockLobby.getActivePersons().get("playey");
            assertNull("playey was able to start game while in one",
                    this.mockLobby.startGame(playey, mockGameClient));
            assertNull("playey was able to join the same game twice",
                    this.mockLobby.joinGame(game,
                            (Person) this.mockLobby.getActivePersons().get("playey"), mockGameClient));

            // spectey
            this.mockLobby.checkLogin("spectey", "testpass", mockLobbyClient);

            Person spectey = (Person) this.mockLobby.getActivePersons().get("spectey");
            assertEquals("spectey didn't spectate the right game", game,
                    this.mockLobby.spectateGame(game, spectey, mockGameClient));
            assertNull("spectey was able to spectate the same game twice",
                    this.mockLobby.spectateGame(game, spectey, mockGameClient));
        }
        catch (IllegalArgumentException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("IllegalArgumentException on testStartJoinSpectateGame: " + ex.getMessage());
            fail("IllegalArgumentException on testStartJoinSpectateGame: " + ex.getMessage());
        }
        catch (SQLException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("SQLException on testStartJoinSpectateGame: " + ex.getMessage());
            fail("SQLException on testStartJoinSpectateGame: " + ex.getMessage());
        }
        catch (RemoteException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on testStartJoinSpectateGame: " + ex.getMessage());
            fail("RemoteException on testStartJoinSpectateGame: " + ex.getMessage());
        }
    }

    /**
     * Test of addChatMessage method, of class Lobby.
     */
    @Test
    public void testAddChatMessage() {
        try {
            this.mockLobby.addPerson("testey", "testpass");
            this.mockLobby.checkLogin("testey", "testpass", mockLobbyClient);

            assertTrue("testey wasn't able to post a chat message",
                    this.mockLobby.addChatMessage("testmessage", "testey"));
        }
        catch (IllegalArgumentException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("IllegalArgumentException on testAddChatMessage: " + ex.getMessage());
            fail("IllegalArgumentException on testAddChatMessage: " + ex.getMessage());
        }
        catch (SQLException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("SQLException on testAddChatMessage: " + ex.getMessage());
            fail("SQLException on testAddChatMessage: " + ex.getMessage());
        }
        catch (RemoteException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on testAddChatMessage: " + ex.getMessage());
            fail("RemoteException on testAddChatMessage: " + ex.getMessage());
        }

    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNullChatMessage() {
        try {
            this.mockLobby.addPerson("testey", "testpass");
            this.mockLobby.checkLogin("testey", "testpass", mockLobbyClient);

            this.mockLobby.addChatMessage(null,
                    ((Person) this.mockLobby.getActivePersons().get("testey")).getName());
        }
        catch (SQLException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("SQLException on testAddNullChatMessage: " + ex.getMessage());
            fail("SQLException on testAddNullChatMessage: " + ex.getMessage());
        }
        catch (RemoteException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on testAddNullChatMessage: " + ex.getMessage());
            fail("RemoteException on testAddNullChatMessage: " + ex.getMessage());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddFromNullChatMessage() {

        try {
            this.mockLobby.addPerson("testey", "testpass");
            this.mockLobby.checkLogin("testey", "testpass", mockLobbyClient);

            this.mockLobby.addChatMessage("anonpost", null);
        }
        catch (SQLException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("SQLException on testAddFromNullChatMessage: " + ex.getMessage());
            fail("SQLException on testAddFromNullChatMessage: " + ex.getMessage());
        }
        catch (RemoteException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on testAddFromNullChatMessage: " + ex.getMessage());
            fail("RemoteException on testAddFromNullChatMessage: " + ex.getMessage());
        }
    }

    /**
     * Test of endGame method, of class Lobby.
     */
    @Test
    public void testEndGame() {
        try {
            this.mockLobby.addPerson("testey", "testpass");
            this.mockLobby.checkLogin("testey", "testpass", mockLobbyClient);

            Game game = this.mockLobby.startGame(this.mockLobby.getMyPerson("testey"), mockGameClient);
            assertNotNull("game wasn't started properly", game);
            assertTrue("game didn't end as it should", this.mockLobby.endGame(game, null));
            assertFalse("successfully ended a previously ended game",
                    this.mockLobby.endGame(game, null));
            game = this.mockLobby.startGame(this.mockLobby.getMyPerson("testey"), mockGameClient);
            assertTrue("game didn't end as it should with leaver",
                    this.mockLobby.endGame(game,
                            (Player) this.mockLobby.getActivePersons().get("testey")));

            // TODO: check whether rating updates as it should
        }
        catch (IllegalArgumentException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("IllegalArgumentException on testEndGame: " + ex.getMessage());
            fail("IllegalArgumentException on testEndGame: " + ex.getMessage());
        }
        catch (SQLException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("SQLException on testEndGame: " + ex.getMessage());
            fail("SQLException on testEndGame: " + ex.getMessage());
        }
        catch (RemoteException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on testEndGame: " + ex.getMessage());
            fail("RemoteException on testEndGame: " + ex.getMessage());
        }
    }

    @Test
    public void testLogout1() {
        try {
            this.mockLobby.addPerson("testey", "testpass");
            this.mockLobby.checkLogin("testey", "testpass", mockLobbyClient);
            
            Game game = this.mockLobby.startGame(this.mockLobby.getMyPerson("testey"), mockGameClient);
            
            this.mockLobby.addPerson("testey1", "testpass");
            this.mockLobby.checkLogin("testey1", "testpass", mockLobbyClient);
            this.mockLobby.joinGame(game, this.mockLobby.getMyPerson("testey"), mockGameClient);
            
            this.mockLobby.addPerson("testey2", "testpass");
            //this.mockLobby.checkLogin("testey2", "testpass");
            this.mockLobby.joinGame(game, this.mockLobby.getMyPerson("testey"), mockGameClient);
            
            game.beginGame();
            
            assertTrue("Logout didn't succeed", this.mockLobby.logOut(this.mockLobby.getMyPerson("testey")));
        }
        catch (IllegalArgumentException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("IllegalArgumentException on testLogout1: " + ex.getMessage());
            fail("IllegalArgumentException on testLogout1: " + ex.getMessage());
        }
        catch (SQLException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("SQLException on testLogout1: " + ex.getMessage());
            fail("SQLException on testLogout1: " + ex.getMessage());
        }
        catch (RemoteException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on testLogout1: " + ex.getMessage());
            fail("RemoteException on testLogout1: " + ex.getMessage());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLogout2() {
        try {
            this.mockLobby.logOut(null);
        }
        catch (RemoteException ex) {
            Logger.getLogger(LobbyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on testLogout2: " + ex.getMessage());
            fail("RemoteException on testLogout2: " + ex.getMessage());
        }
    }

    //TODO check if user is already logged in
}
