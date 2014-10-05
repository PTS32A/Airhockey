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

//Test line added by Julius to test syncing.

/**
 *
 * @author Kargathia
 */
public class InternetConnectionTest
{
    
    public InternetConnectionTest()
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
     * Test of populatePersons method, of class InternetConnection.
     */
    @Test
    public void testPopulatePersons()
    {
        System.out.println("populatePersons");
        InternetConnection instance = new InternetConnection();
        List<Person> expResult = null;
        List<Person> result = instance.populatePersons();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of populateGames method, of class InternetConnection.
     */
    @Test
    public void testPopulateGames()
    {
        System.out.println("populateGames");
        InternetConnection instance = new InternetConnection();
        List<Game> expResult = null;
        List<Game> result = instance.populateGames();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
