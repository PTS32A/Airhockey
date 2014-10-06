/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package s32a.airhockey;

import com.badlogic.gdx.math.Vector2;
import java.util.Calendar;
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
public class PlayerTest
{
    Player player;
    
    public PlayerTest()
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
        player = new Player("Test", 15, "Red");
    }
    
    @After
    public void tearDown()
    {
    }

    /**
     * Test of getBatPos method, of class Player.
     */
    @Test
    public void testGetBatPos()
    {
        System.out.println("getBatPos");
        Vector2 expResult = null;
        Vector2 result = player.getBatPos();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getColor method, of class Player.
     */
    @Test
    public void testGetColor()
    {
        System.out.println("getColor");
        Player instance = null;
        String expResult = "";
        String result = instance.getColor();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getScore method, of class Player.
     */
    @Test
    public void testGetScore()
    {
        System.out.println("getScore");
        Player instance = null;
        int expResult = 0;
        int result = instance.getScore();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isStarter method, of class Player.
     */
    @Test
    public void testIsStarter()
    {
        System.out.println("isStarter");
        Player instance = null;
        boolean expResult = false;
        boolean result = instance.isStarter();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isAI method, of class Player.
     */
    @Test
    public void testIsAI()
    {
        System.out.println("isAI");
        Player instance = null;
        boolean expResult = false;
        boolean result = instance.isAI();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRotation method, of class Player.
     */
    @Test
    public void testGetRotation()
    {
        System.out.println("getRotation");
        Player instance = null;
        int expResult = 0;
        int result = instance.getRotation();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getGoalPos method, of class Player.
     */
    @Test
    public void testGetGoalPos()
    {
        System.out.println("getGoalPos");
        Player instance = null;
        Vector2 expResult = null;
        Vector2 result = instance.getGoalPos();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLastAction method, of class Player.
     */
    @Test
    public void testGetLastAction()
    {
        System.out.println("getLastAction");
        Player instance = null;
        Calendar expResult = null;
        Calendar result = instance.getLastAction();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
