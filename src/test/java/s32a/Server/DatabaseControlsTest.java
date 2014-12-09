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

/**
 *
 * @author Kargathia
 */
public class DatabaseControlsTest
{
    private DatabaseControls mockDB;
    
    public DatabaseControlsTest()
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
    public void setUp() throws RemoteException
    {
        this.mockDB = new DatabaseControls();
        try
        {
            this.mockDB.configure();
        } catch (IOException ex)
        {
            Logger.getLogger(DatabaseControlsTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @After
    public void tearDown()
    {        
//        try
//        {
//            this.mockDB.clearDatabase();
//        } catch (SQLException ex)
//        {
//            fail(ex.getMessage());
//        }
    }

    @Test
    public void testConfigure()
    {
        try
        {
            this.mockDB.configure();
        } catch (IOException ex)
        {
            fail(ex.getMessage());
        }
    }
    
    /**
     * Test of addPerson method, of class DatabaseControls.
     */
    @Test
    public void testAddPersonCheckLogin() throws RemoteException
    {
        try
        {
            this.mockDB.clearDatabase();
            this.mockDB.addPerson("testey", "testpass");
            Person testey = (Person)this.mockDB.checkLogin("testey", "testpass");
            assertEquals("testey name is wrong", "testey", testey.getName());
            assertEquals("testey rating is wrong", (double)15, testey.ratingProperty().get(), 0.1);
            assertNull("added testey twice", this.mockDB.addPerson("testey", "testpass"));
            
            assertNull("able to log in with wrong username", 
                    this.mockDB.checkLogin("fakeTestey", "testpass"));
            assertNull("able to log in with wrong password",
                    this.mockDB.checkLogin("testey", "falsepass"));
        } catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
    }
    
    @Test
    public void testSaveGameCheckRating() throws RemoteException
    {
        try
        {
            this.mockDB.clearDatabase();
            this.mockDB.addPerson("testey", "testpass");
            Person testey = (Person)this.mockDB.checkLogin("testey", "testpass");
            
            assertEquals("initial rating incorrect", 
                    Double.doubleToLongBits((double)15), 
                    Double.doubleToLongBits(this.mockDB.getNewRating(testey, null)));
            
            Game mockGame;
            
            GameClient client = new GameClient();
            
            Player test1 = new Player("test1", (double)15, Colors.Red);
            Player test2 = new Player("test2", (double)15, Colors.Blue);
            Player test3 = new Player("test3", (double)15, Colors.Green);
            
            this.mockDB.addPerson("test1", "test");
            this.mockDB.addPerson("test2", "test");
            this.mockDB.addPerson("test3", "test");
            
            //sets score game 1
            mockGame = new Game(test1);
            mockGame.addPlayer(test2, client);
            mockGame.addPlayer(test3, client);
            test1.setScore(20);
            test2.setScore(30);
            test3.setScore(25);
            assertTrue("game didn't start", mockGame.beginGame());
            this.mockDB.saveGame(mockGame);
            
            // checks ratings after 1 game in db
            assertEquals("rating game1 test1 incorrect", 
                    (double)((5*20 + 4*15 + 3*15 + 2*15 + 15)/15), 
                    this.mockDB.getNewRating(test1, null), 0.1);
            assertEquals("rating game1 test2 incorrect", 
                    (double)((5*30 + 4*15 + 3*15 + 2*15 + 15)/15), 
                    this.mockDB.getNewRating(test2, null), 0.1);
            assertEquals("rating game1 test3 incorrect", 
                    (double)((5*25 + 4*15 + 3*15 + 2*15 + 15)/15), 
                    this.mockDB.getNewRating(test3, null), 0.1);
            
            //sets score game 2
            mockGame = new Game(test3);
            mockGame.addPlayer(test1, client);
            mockGame.addPlayer(test2, client);
            test1.setScore(20);
            test2.setScore(30);
            test3.setScore(25);
            assertTrue("game didn't start", mockGame.beginGame());
            this.mockDB.saveGame(mockGame);
            
            // checks ratings after 2 games in db
            assertEquals("rating game2 test1 incorrect", 
                    (double)((5*20 + 4*20 + 3*15 + 2*15 + 15)/15), 
                    this.mockDB.getNewRating(test1, null), 0.1);
            assertEquals("rating game2 test2 incorrect", 
                    (double)((5*30 + 4*30 + 3*15 + 2*15 + 15)/15), 
                    this.mockDB.getNewRating(test2, null), 0.1);
            assertEquals("rating game2 test3 incorrect", 
                    (double)((5*25 + 4*25 + 3*15 + 2*15 + 15)/15), 
                    this.mockDB.getNewRating(test3, null), 0.1);
            
            //sets score game 3
            mockGame = new Game(test2);
            mockGame.addPlayer(test3, client);
            mockGame.addPlayer(test1, client);
            test1.setScore(20);
            test2.setScore(30);
            test3.setScore(25);
            assertTrue("game didn't start", mockGame.beginGame());
            this.mockDB.saveGame(mockGame);
            
            // checks ratings after 3 games in db
            assertEquals("rating game3 test1 incorrect", 
                    (double)((5*20 + 4*20 + 3*20 + 2*15 + 15)/15), 
                    this.mockDB.getNewRating(test1, null), 0.1);
            assertEquals("rating game3 test2 incorrect", 
                    (double)((5*30 + 4*30 + 3*30 + 2*15 + 15)/15), 
                    this.mockDB.getNewRating(test2, null), 0.1);
            assertEquals("rating game3 test3 incorrect", 
                    (double)((5*25 + 4*25 + 3*25 + 2*15 + 15)/15), 
                    this.mockDB.getNewRating(test3, null), 0.1);
            
            //sets score game 4
            mockGame = new Game(test1);
            mockGame.addPlayer(test2, client);
            mockGame.addPlayer(test3, client);
            test1.setScore(20);
            test2.setScore(30);
            test3.setScore(25);
            assertTrue("game didn't start", mockGame.beginGame());
            this.mockDB.saveGame(mockGame);
            
            // checks ratings after 4 games in db
            assertEquals("rating game4 test1 incorrect", 
                    (double)((5*20 + 4*20 + 3*20 + 2*20 + 15)/15), 
                    this.mockDB.getNewRating(test1, null), 0.1);
            assertEquals("rating game4 test2 incorrect", 
                    (double)((5*30 + 4*30 + 3*30 + 2*30 + 15)/15), 
                    this.mockDB.getNewRating(test2, null), 0.1);
            assertEquals("rating game4 test3 incorrect", 
                    (double)((5*25 + 4*25 + 3*25 + 2*25 + 15)/15), 
                    this.mockDB.getNewRating(test3, null), 0.1);
            
            //sets score game 5
            mockGame = new Game(test3);
            mockGame.addPlayer(test1, client);
            mockGame.addPlayer(test2, client);
            test1.setScore(20);
            test2.setScore(30);
            test3.setScore(25);
            assertTrue("game didn't start", mockGame.beginGame());
            this.mockDB.saveGame(mockGame);
            
            // checks ratings after 5 games in db
            assertEquals("rating game5 test1 incorrect", 
                    (double)((5*20 + 4*20 + 3*20 + 2*20 + 20)/15), 
                    this.mockDB.getNewRating(test1, null), 0.1);
            assertEquals("rating game5 test2 incorrect", 
                    (double)((5*30 + 4*30 + 3*30 + 2*30 + 30)/15), 
                    this.mockDB.getNewRating(test2, null), 0.1);
            assertEquals("rating game5 test3 incorrect", 
                    (double)((5*25 + 4*25 + 3*25 + 2*25 + 25)/15), 
                    this.mockDB.getNewRating(test3, null), 0.1);
            
            //sets score game 6
            mockGame = new Game(test1);
            mockGame.addPlayer(test2, client);
            mockGame.addPlayer(test3, client);
            test1.setScore(10); // goes down
            test2.setScore(40); // goes up
            test3.setScore(25); // stays even
            assertTrue("game didn't start", mockGame.beginGame());
            this.mockDB.saveGame(mockGame);
            
            // checks ratings after 6 games in db
            assertEquals("rating game6 test1 incorrect", 
                    (double)((5*10 + 4*20 + 3*20 + 2*20 + 20)/15), 
                    this.mockDB.getNewRating(test1, null), 0.1);
            assertEquals("rating game6 test2 incorrect", 
                    (double)((5*40 + 4*30 + 3*30 + 2*30 + 30)/15), 
                    this.mockDB.getNewRating(test2, null), 0.1);
            assertEquals("rating game6 test3 incorrect", 
                    (double)((5*25 + 4*25 + 3*25 + 2*25 + 25)/15), 
                    this.mockDB.getNewRating(test3, null), 0.1);
            
            //sets score game 7
            mockGame = new Game(test1);
            mockGame.addPlayer(test2, client);
            mockGame.addPlayer(test3, client);
            test1.setScore(0); // goes down
            test2.setScore(50); // goes up
            test3.setScore(25); // stays even
            assertTrue("game didn't start", mockGame.beginGame());
            this.mockDB.saveGame(mockGame);
            
            // checks how leavers impact scores (restrained)
            assertEquals("rating game7 leaver=test2 test1 incorrect", 
                    (double)((5*10 + 4*20 + 3*20 + 2*20 + 20)/15), 
                    this.mockDB.getNewRating(test1, test2), 0.1);
            assertEquals("rating game7 leaver=test2 test2 incorrect", 
                    (double)((5*40 + 4*30 + 3*30 + 2*30 + 30)/15), 
                    this.mockDB.getNewRating(test2, test2), 0.1);
            assertEquals("rating game7 test3 incorrect", 
                    (double)((5*25 + 4*25 + 3*25 + 2*25 + 25)/15), 
                    this.mockDB.getNewRating(test3, test2), 0.1);
            
            //sets score game 8
            mockGame = new Game(test1);
            mockGame.addPlayer(test2, client);
            mockGame.addPlayer(test3, client);
            test1.setScore(50); // goes up
            test2.setScore(0); // goes down
            test3.setScore(25); // stays even
            assertTrue("game didn't start", mockGame.beginGame());
            this.mockDB.saveGame(mockGame);
            
            // checks how leavers impact scores (unrestrained)
            assertEquals("rating game8 leaver=test2 test1 incorrect", 
                    (double)((5*50 + 4*0 + 3*10 + 2*20 + 20)/15), 
                    this.mockDB.getNewRating(test1, test2), 0.1);
            assertEquals("rating game8 leaver=test2 test2 incorrect", 
                    (double)((5*0 + 4*50 + 3*40 + 2*30 + 30)/15), 
                    this.mockDB.getNewRating(test2, test2), 0.1);
            assertEquals("rating game8 test3 incorrect", 
                    (double)((5*25 + 4*25 + 3*25 + 2*25 + 25)/15), 
                    this.mockDB.getNewRating(test3, test2), 0.1);
            
                       
        } catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
    }
    
    @Test
    (expected = IllegalArgumentException.class)
    public void saveGameNegativeScoreTest() throws RemoteException
    {
        try
        {
            Game mockGame;
            
            Player test1 = new Player("test1", (double)15, Colors.Red);
            Player test2 = new Player("test2", (double)15, Colors.Blue);
            Player test3 = new Player("test3", (double)15, Colors.Green);
            
            GameClient client = new GameClient();
            
            this.mockDB.addPerson("test1", "test");
            this.mockDB.addPerson("test2", "test");
            this.mockDB.addPerson("test3", "test");
            
            //sets negative score
            mockGame = new Game(test1);
            mockGame.addPlayer(test2, client);
            mockGame.addPlayer(test3, client);
            test1.setScore(-20);
            test2.setScore(30);
            test3.setScore(25);
            assertTrue("game didn't start", mockGame.beginGame());
            this.mockDB.saveGame(mockGame);
        } catch (SQLException ex)
        {
            fail(ex.getMessage());
        } catch (IllegalArgumentException ex)
        {
            
        }
    }

    
    
}
