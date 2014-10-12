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
    Puck puck;
    
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
        puck = new Puck(3);
    }
    
    @After
    public void tearDown()
    {
    }

    @Test
    public void testUpdatePosition()
    {
        Vector2 expResult = new Vector2(3,0);
        
        puck.run();
        
        Vector2 result = puck.getPosition();
        
        assertEquals("Pucks position is incorrect", expResult, result);
    }
}
