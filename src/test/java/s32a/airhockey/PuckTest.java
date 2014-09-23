/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package s32a.airhockey;

import com.badlogic.gdx.math.Vector2;
import java.util.List;
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
public class PuckTest
{
    
    public PuckTest()
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
     * Test of start method, of class Puck.
     */
    @Test
    public void testStart()
    {
        System.out.println("start");
        Puck instance = null;
        boolean expResult = false;
        boolean result = instance.start();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of stop method, of class Puck.
     */
    @Test
    public void testStop()
    {
        System.out.println("stop");
        Puck instance = null;
        boolean expResult = false;
        boolean result = instance.stop();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPosition method, of class Puck.
     */
    @Test
    public void testGetPosition()
    {
        System.out.println("getPosition");
        Puck instance = null;
        Vector2 expResult = null;
        Vector2 result = instance.getPosition();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSpeed method, of class Puck.
     */
    @Test
    public void testGetSpeed()
    {
        System.out.println("getSpeed");
        Puck instance = null;
        float expResult = 0.0F;
        float result = instance.getSpeed();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getHitBy method, of class Puck.
     */
    @Test
    public void testGetHitBy()
    {
        System.out.println("getHitBy");
        Puck instance = null;
        List<Player> expResult = null;
        List<Player> result = instance.getHitBy();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDirection method, of class Puck.
     */
    @Test
    public void testGetDirection()
    {
        System.out.println("getDirection");
        Puck instance = null;
        float expResult = 0.0F;
        float result = instance.getDirection();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
