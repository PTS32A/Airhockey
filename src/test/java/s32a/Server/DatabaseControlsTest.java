/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Server;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import s32a.Client.ClientData.GameClient;
import s32a.Server.DatabaseControls;
import s32a.Server.Game;
import s32a.Server.Person;
import s32a.Server.Player;
import s32a.Shared.enums.Colors;
import s32a.Shared.enums.GameStatus;

/**
 *
 * @author Kargathia
 */
public class DatabaseControlsTest {

    private DatabaseControls mockDB;

    public DatabaseControlsTest() {
    }

    @BeforeClass
    public static void setUpClass() {

    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws RemoteException {
        this.mockDB = new DatabaseControls();
        try {
            this.mockDB.configure();
        }
        catch (IOException ex) {
            Logger.getLogger(DatabaseControlsTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("IOException on configure: " + ex.getMessage());
        }
    }

    @After
    public void tearDown() {
        
    }

    /*
    * Test of addPerson method.
    */
    @Test
    public void testAddPerson() {
        try {
            this.mockDB.clearDatabase();
            Person testey = (Person) this.mockDB.addPerson("testey", "testpass");
            assertEquals("testey name is wrong", "testey", testey.getName());
            assertEquals("testey rating is wrong", (double) 15, testey.getRating(), 0.1);
        }
        catch (SQLException ex) {
            Logger.getLogger(DatabaseControlsTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("SQLException on testAddPerson: " + ex.getMessage());
        }
        catch (RemoteException ex) {
            Logger.getLogger(DatabaseControlsTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on testAddPerson: " + ex.getMessage());
        }
    }
    
    /*
    * Test if adding a person twice is possible.
    * This shouldn't be possible.
    */
    @Test
    public void testAddDoublePerson() {
        try {
            this.mockDB.clearDatabase();
            Person testey = (Person) this.mockDB.addPerson("testey", "testpass");
            Person testey2 = (Person) this.mockDB.addPerson("testey", "testpass");
            assertEquals("testey is added twice to the database", null, testey2);
        }
        catch (SQLException ex) {
            Logger.getLogger(DatabaseControlsTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("SQLException on testAddDoublePerson: " + ex.getMessage());
        }
        catch (RemoteException ex) {
            Logger.getLogger(DatabaseControlsTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on testAddDoublePerson: " + ex.getMessage());
        }
    }

    /**
     * Test of checkLogin method.
     * Test if data correctly saved to database.
     */
    @Test
    public void testCheckLogin() {
        try {
            this.mockDB.clearDatabase();
            this.mockDB.addPerson("testey", "testpass");
            Person testey = (Person) this.mockDB.checkLogin("testey", "testpass");
            assertEquals("testey name is wrong", "testey", testey.getName());
            assertEquals("testey rating is wrong", (double) 15, testey.ratingProperty().get(), 0.1);
            assertNull("added testey twice", this.mockDB.addPerson("testey", "testpass"));

            assertNull("able to log in with wrong username",
                    this.mockDB.checkLogin("fakeTestey", "testpass"));
            assertNull("able to log in with wrong password",
                    this.mockDB.checkLogin("testey", "falsepass"));
        }
        catch (SQLException ex) {
            Logger.getLogger(DatabaseControlsTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("SQLException on testCheckLogin: " + ex.getMessage());
        }
        catch (RemoteException ex) {
            Logger.getLogger(DatabaseControlsTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on testCheckLogin: " + ex.getMessage());
        }
    }

    /*
    * Test on saving the game.
    * Test if rating is correctly saved to the database.
    */
    @Test
    public void testSaveGame(){
        try {
            this.mockDB.clearDatabase();
            GameClient client = new GameClient();
            
            Player test1 = new Player("test1", (double) 15, Colors.Red);
            Player test2 = new Player("test2", (double) 15, Colors.Blue);
            Player test3 = new Player("test3", (double) 15, Colors.Green);
                        
            this.mockDB.addPerson(test1.getName(), "test");
            this.mockDB.addPerson(test2.getName(), "test");
            this.mockDB.addPerson(test3.getName(), "test");
            
            Game mockGame;
            
            mockGame = new Game(test1);
            mockGame.startPublisher(test1, client);
            mockGame.addPlayer(test2, client);
            mockGame.addPlayer(test3, client);
            test1.setScore(20);
            test2.setScore(30);
            test3.setScore(25);
            assertTrue("game didn't start", mockGame.beginGame());
            this.mockDB.saveGame(mockGame);
            
            // checks ratings after 1 game in db
            assertEquals("rating game1 test1 incorrect",
                    (double) ((5 * 20 + 4 * 15 + 3 * 15 + 2 * 15 + 15) / 15),
                    this.mockDB.getNewRating(test1, null), 0.1);
            assertEquals("rating game1 test2 incorrect",
                    (double) ((5 * 30 + 4 * 15 + 3 * 15 + 2 * 15 + 15) / 15),
                    this.mockDB.getNewRating(test2, null), 0.1);
            assertEquals("rating game1 test3 incorrect",
                    (double) ((5 * 25 + 4 * 15 + 3 * 15 + 2 * 15 + 15) / 15),
                    this.mockDB.getNewRating(test3, null), 0.1);
            
            //sets score game 2
            mockGame = new Game(test3);
            mockGame.startPublisher(test3, client);
            mockGame.addPlayer(test1, client);
            mockGame.addPlayer(test2, client);
            test1.setScore(20);
            test2.setScore(30);
            test3.setScore(25);
            assertTrue("game didn't start", mockGame.beginGame());
            this.mockDB.saveGame(mockGame);

            // checks ratings after 2 games in db
            assertEquals("rating game2 test1 incorrect",
                    (double) ((5 * 20 + 4 * 20 + 3 * 15 + 2 * 15 + 15) / 15),
                    this.mockDB.getNewRating(test1, null), 0.1);
            assertEquals("rating game2 test2 incorrect",
                    (double) ((5 * 30 + 4 * 30 + 3 * 15 + 2 * 15 + 15) / 15),
                    this.mockDB.getNewRating(test2, null), 0.1);
            assertEquals("rating game2 test3 incorrect",
                    (double) ((5 * 25 + 4 * 25 + 3 * 15 + 2 * 15 + 15) / 15),
                    this.mockDB.getNewRating(test3, null), 0.1);

            //sets score game 3
            mockGame = new Game(test2);
            mockGame.startPublisher(test2, client);
            mockGame.addPlayer(test3, client);
            mockGame.addPlayer(test1, client);
            test1.setScore(20);
            test2.setScore(30);
            test3.setScore(25);
            assertTrue("game didn't start", mockGame.beginGame());
            this.mockDB.saveGame(mockGame);

            // checks ratings after 3 games in db
            assertEquals("rating game3 test1 incorrect",
                    (double) ((5 * 20 + 4 * 20 + 3 * 20 + 2 * 15 + 15) / 15),
                    this.mockDB.getNewRating(test1, null), 0.1);
            assertEquals("rating game3 test2 incorrect",
                    (double) ((5 * 30 + 4 * 30 + 3 * 30 + 2 * 15 + 15) / 15),
                    this.mockDB.getNewRating(test2, null), 0.1);
            assertEquals("rating game3 test3 incorrect",
                    (double) ((5 * 25 + 4 * 25 + 3 * 25 + 2 * 15 + 15) / 15),
                    this.mockDB.getNewRating(test3, null), 0.1);

            //sets score game 4
            mockGame = new Game(test1);
            mockGame.startPublisher(test1, client);
            mockGame.addPlayer(test2, client);
            mockGame.addPlayer(test3, client);
            test1.setScore(20);
            test2.setScore(30);
            test3.setScore(25);
            assertTrue("game didn't start", mockGame.beginGame());
            this.mockDB.saveGame(mockGame);

            // checks ratings after 4 games in db
            assertEquals("rating game4 test1 incorrect",
                    (double) ((5 * 20 + 4 * 20 + 3 * 20 + 2 * 20 + 15) / 15),
                    this.mockDB.getNewRating(test1, null), 0.1);
            assertEquals("rating game4 test2 incorrect",
                    (double) ((5 * 30 + 4 * 30 + 3 * 30 + 2 * 30 + 15) / 15),
                    this.mockDB.getNewRating(test2, null), 0.1);
            assertEquals("rating game4 test3 incorrect",
                    (double) ((5 * 25 + 4 * 25 + 3 * 25 + 2 * 25 + 15) / 15),
                    this.mockDB.getNewRating(test3, null), 0.1);

            //sets score game 5
            mockGame = new Game(test3);
            mockGame.startPublisher(test3, client);
            mockGame.addPlayer(test1, client);
            mockGame.addPlayer(test2, client);
            test1.setScore(20);
            test2.setScore(30);
            test3.setScore(25);
            assertTrue("game didn't start", mockGame.beginGame());
            this.mockDB.saveGame(mockGame);

            // checks ratings after 5 games in db
            assertEquals("rating game5 test1 incorrect",
                    (double) ((5 * 20 + 4 * 20 + 3 * 20 + 2 * 20 + 20) / 15),
                    this.mockDB.getNewRating(test1, null), 0.1);
            assertEquals("rating game5 test2 incorrect",
                    (double) ((5 * 30 + 4 * 30 + 3 * 30 + 2 * 30 + 30) / 15),
                    this.mockDB.getNewRating(test2, null), 0.1);
            assertEquals("rating game5 test3 incorrect",
                    (double) ((5 * 25 + 4 * 25 + 3 * 25 + 2 * 25 + 25) / 15),
                    this.mockDB.getNewRating(test3, null), 0.1);

            //sets score game 6
            mockGame = new Game(test1);
            mockGame.startPublisher(test1, client);
            mockGame.addPlayer(test2, client);
            mockGame.addPlayer(test3, client);
            test1.setScore(10); // goes down
            test2.setScore(40); // goes up
            test3.setScore(25); // stays even
            assertTrue("game didn't start", mockGame.beginGame());
            this.mockDB.saveGame(mockGame);

            // checks ratings after 6 games in db
            assertEquals("rating game6 test1 incorrect",
                    (double) ((5 * 10 + 4 * 20 + 3 * 20 + 2 * 20 + 20) / 15),
                    this.mockDB.getNewRating(test1, null), 0.1);
            assertEquals("rating game6 test2 incorrect",
                    (double) ((5 * 40 + 4 * 30 + 3 * 30 + 2 * 30 + 30) / 15),
                    this.mockDB.getNewRating(test2, null), 0.1);
            assertEquals("rating game6 test3 incorrect",
                    (double) ((5 * 25 + 4 * 25 + 3 * 25 + 2 * 25 + 25) / 15),
                    this.mockDB.getNewRating(test3, null), 0.1);

            //sets score game 7
            mockGame = new Game(test1);
            mockGame.startPublisher(test1, client);
            mockGame.addPlayer(test2, client);
            mockGame.addPlayer(test3, client);
            test1.setScore(0); // goes down
            test2.setScore(50); // goes up
            test3.setScore(25); // stays even
            assertTrue("game didn't start", mockGame.beginGame());
            this.mockDB.saveGame(mockGame);

            // checks how leavers impact scores (restrained)
            assertEquals("rating game7 leaver=test2 test1 incorrect",
                    (double) ((5 * 10 + 4 * 20 + 3 * 20 + 2 * 20 + 20) / 15),
                    this.mockDB.getNewRating(test1, test2), 0.1);
            assertEquals("rating game7 leaver=test2 test2 incorrect",
                    (double) ((5 * 40 + 4 * 30 + 3 * 30 + 2 * 30 + 30) / 15),
                    this.mockDB.getNewRating(test2, test2), 0.1);
            assertEquals("rating game7 test3 incorrect",
                    (double) ((5 * 25 + 4 * 25 + 3 * 25 + 2 * 25 + 25) / 15),
                    this.mockDB.getNewRating(test3, test2), 0.1);

            //sets score game 8
            mockGame = new Game(test1);
            mockGame.startPublisher(test1, client);
            mockGame.addPlayer(test2, client);
            mockGame.addPlayer(test3, client);
            test1.setScore(50); // goes up
            test2.setScore(0); // goes down
            test3.setScore(25); // stays even
            assertTrue("game didn't start", mockGame.beginGame());
            this.mockDB.saveGame(mockGame);

            // checks how leavers impact scores (unrestrained)
            assertEquals("rating game8 leaver=test2 test1 incorrect",
                    (double) ((5 * 50 + 4 * 0 + 3 * 10 + 2 * 20 + 20) / 15),
                    this.mockDB.getNewRating(test1, test2), 0.1);
            assertEquals("rating game8 leaver=test2 test2 incorrect",
                    (double) ((5 * 0 + 4 * 50 + 3 * 40 + 2 * 30 + 30) / 15),
                    this.mockDB.getNewRating(test2, test2), 0.1);
            assertEquals("rating game8 test3 incorrect",
                    (double) ((5 * 25 + 4 * 25 + 3 * 25 + 2 * 25 + 25) / 15),
                    this.mockDB.getNewRating(test3, test2), 0.1);
        }
        catch (RemoteException ex) {
            Logger.getLogger(DatabaseControlsTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on testSaveGame: " + ex.getMessage());
        }
        catch (SQLException ex) {
            Logger.getLogger(DatabaseControlsTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("SQLException on testSaveGame: " + ex.getMessage());
        }
        
    }

    /*
    * Test if it is possible to save a negative score.
    * This shouldn't be possible.
    */
    @Test(expected = IllegalArgumentException.class)
    public void saveGameNegativeScoreTest() throws RemoteException {
        try {
            Game mockGame;

            Player test1 = new Player("test1", (double) 15, Colors.Red);
            Player test2 = new Player("test2", (double) 15, Colors.Blue);
            Player test3 = new Player("test3", (double) 15, Colors.Green);

            GameClient client = new GameClient();

            this.mockDB.addPerson("test1", "test");
            this.mockDB.addPerson("test2", "test");
            this.mockDB.addPerson("test3", "test");

            //sets negative score
            mockGame = new Game(test1);
            mockGame.startPublisher(test1, client);
            mockGame.addPlayer(test2, client);
            mockGame.addPlayer(test3, client);
            test1.setScore(-20);
            test2.setScore(30);
            test3.setScore(25);
            assertTrue("game didn't start", mockGame.beginGame());
            this.mockDB.saveGame(mockGame);
        }
        catch (SQLException ex) {
            fail(ex.getMessage());
        }
        catch (IllegalStateException ex) {
            System.out.println("IllegalStateException on saveGameNegativeScoreTest(): " + ex.getMessage());
        }
    }

}
