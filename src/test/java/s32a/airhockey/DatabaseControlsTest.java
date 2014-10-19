/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package s32a.airhockey;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    public void setUp()
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
        /*
        try
        {
            this.mockDB.clearDatabase();
        } catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
        */
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
    public void testAddPersonCheckLogin()
    {
        try
        {
            this.mockDB.clearDatabase();
            this.mockDB.addPerson("testey", "testpass");
            Person testey = this.mockDB.checkLogin("testey", "testpass");
            assertEquals("testey name is wrong", "testey", testey.getName());
            assertEquals("testey rating is wrong", 15, testey.getRating());
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
    public void testSaveGameCheckRating()
    {
        try
        {
            this.mockDB.clearDatabase();
            this.mockDB.addPerson("testey", "testpass");
            Person testey = this.mockDB.checkLogin("testey", "testpass");
            
            assertEquals("initial rating incorrect", 15, this.mockDB.getNewRating(testey, null));
            
            Game mockGame;
            
            Player test1 = new Player("test1", 15, Colors.Red);
            Player test2 = new Player("test2", 15, Colors.Blue);
            Player test3 = new Player("test3", 15, Colors.Green);
            
            this.mockDB.addPerson("test1", "test");
            this.mockDB.addPerson("test2", "test");
            this.mockDB.addPerson("test3", "test");
            
            // initiates game
            mockGame = new Game(test1);
            mockGame.addPlayer(test2);
            mockGame.addPlayer(test3);
            
            //sets score game 1
            test1.setScore(20);
            test2.setScore(30);
            test3.setScore(25);
            assertTrue("game didn't start", mockGame.beginGame());
            this.mockDB.saveGame(mockGame);
            
            
            assertEquals("rating game1 test1 incorrect", 
                    ((5*20 + 4*15 + 3*15 + 2*15 + 15)/15), 
                    this.mockDB.getNewRating(test1, null));
            assertEquals("rating game1 test2 incorrect", 
                    ((5*30 + 4*15 + 3*15 + 2*15 + 15)/15), 
                    this.mockDB.getNewRating(test2, null));
            assertEquals("rating game1 test3 incorrect", 
                    ((5*25 + 4*15 + 3*15 + 2*15 + 15)/15), 
                    this.mockDB.getNewRating(test2, null));
            
            this.mockDB.getNewRating(test1, null);
            
            // TODO: test whether the database function handles 
                       
        } catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
    }

    
    
}
