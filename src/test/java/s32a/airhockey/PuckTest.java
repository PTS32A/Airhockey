/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package s32a.airhockey;

import com.badlogic.gdx.math.Vector2;
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
    
    Vector2 position;
    float puckSpeed;
    float direction;
    int runCount;
    
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
        position = new Vector2(0, 10);
        puckSpeed = 10;
        direction = 90; //Move to the right
        runCount = 5;
        
        game.customSetup(position, puckSpeed, direction, runCount, 1);
        game.beginGame();

        //No bounce
        //Exspected X is the ammount of times moving (runCount) * the speed (distance traveling) of the puck (runSpeed)
        float expX = runCount * puckSpeed;
        float expY = 10;
        
        Vector2 expResult = new Vector2(expX,expY);
        Vector2 result = game.getMyPuck().getEndPosition();
        
        assertEquals("Pucks position is incorrect", expResult, result);
    }
    
    @Test
    public void testUpdatePositionBounce()
    {
        position = null; //Keep default start position
        puckSpeed = 10;
        direction = 90; //Move to the right
        runCount = 10;
        
        game.customSetup(position, puckSpeed, direction, runCount, 1);
        game.beginGame();

        //Bounce off the rightside wall >>> resulting in the direction going towards the right (90) changing to going towards the bottom (180).
        int expResult = 180;
        int result = (int)game.getMyPuck().getEndDirection();
        
        assertEquals("Pucks position is incorrect", expResult, result);
    }
    
    @Test
    public void testUpdatePositionGoalHit()
    {
        position = new Vector2(0, 95);
        puckSpeed = 10;
        direction = 90; //Move to the right
        runCount = 10;
        
        game.customSetup(position, puckSpeed, direction, runCount, 1);
        game.beginGame();

        Player expResult = game.getMyPlayers().get(1); //Player blue
        Player result = game.getMyPuck().getEndGoalHit();
        
        assertEquals("Pucks position is incorrect", expResult, result);
    }
    
    @Test
    public void testUpdatePositionBatHit()
    {
        position = null; //Keep default start position
        puckSpeed = 10;
        direction = 90; //Move towards the bottom
        runCount = 10;
        
        game.customSetup(position, puckSpeed, direction, runCount, 1);
        game.beginGame();

        Player expResult = game.getMyPlayers().get(1); //Player blue
        Player result = game.getMyPuck().getEndBatHit();
        
        assertEquals("Pucks position is incorrect", expResult, result);
    }
    
    @Test
    (expected = IllegalArgumentException.class)
    public void testPuckWrongSpeed1()
    {
        Puck p = new Puck(0, game);
        fail("Puckspeed must positive");
    }
    
    @Test
    (expected = IllegalArgumentException.class)
    public void testPuckWrongSpeed2()
    {
        Puck p = new Puck(-1, game);
        fail("Puckspeed must positive");
    }
    
    @Test
    (expected = IllegalArgumentException.class)
    public void testPuckWrongGame()
    {
        Puck p = new Puck(1, null);
        fail("Game can't be null");
    }
}
