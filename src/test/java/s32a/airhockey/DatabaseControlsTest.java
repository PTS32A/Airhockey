/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package s32a.airhockey;

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
    }
    
    @After
    public void tearDown()
    {
    }

    /**
     * Test of checkLogin method, of class DatabaseControls.
     */
    @Test
    public void testCheckLogin()
    {
        System.out.println("checkLogin");
        String playerName = "";
        String password = "";
        DatabaseControls instance = new DatabaseControls();
        Person expResult = null;
        Person result = instance.checkLogin(playerName, password);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addPerson method, of class DatabaseControls.
     */
    @Test
    public void testAddPerson()
    {
        System.out.println("addPerson");
        String playerName = "";
        String password = "";
        DatabaseControls instance = new DatabaseControls();
        Person expResult = null;
        Person result = instance.addPerson(playerName, password);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
