/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package s32a.airhockey;

import java.io.IOException;
import java.sql.SQLException;
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
        try
        {
            this.mockDB.clearDatabase();
        } catch (SQLException ex)
        {
            fail(ex.getMessage());
        }
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
    

    
    
}
