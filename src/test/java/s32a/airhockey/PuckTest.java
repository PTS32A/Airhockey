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
    Player p1 = new Player("playerRed", (double)10, Colors.Red);
    Player p2 = new Player("playerBlue", (double)10, Colors.Blue);
    Player p3 = new Player("playerGreen", (double)10, Colors.Green);
    
    Game game;
    
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
        game = new Game(p1);
        
        game.addPlayer(p2);
        game.addPlayer(p3);
    }
    
    @After
    public void tearDown()
    {
    }

    @Test
    public void testUpdatePositionMovePuck()
    {
        Vector2 position = new Vector2(0, 10);
        float puckSpeed = 10;
        float direction = 90; //Move to the right
        int runCount = 5;
        
        game.customSetup(position, puckSpeed, direction, runCount);
        game.beginGame();
        Puck puck = game.getMyPuck();
        
        //No bounce
        float expX = runCount * puckSpeed;
        float expY = 10;
        
        Vector2 expResult = new Vector2(expX,expY);
        Vector2 result = puck.getPosition();
        
        assertEquals("Pucks position is incorrect", expResult, result);
    }
}
