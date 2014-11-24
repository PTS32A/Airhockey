/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package s32a.Server;

import s32a.Shared.enums.GameStatus;
import s32a.Shared.enums.Colors;
import s32a.Server.Player;
import s32a.Server.Puck;
import s32a.Server.Game;
import com.badlogic.gdx.math.Vector2;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import s32a.Shared.IPlayer;

/**
 *
 * @author Kargathia
 */
@Ignore
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
    float distance;
    
    float sideLength;
    
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
        
        sideLength = ((Puck)game.getMyPuck()).getSideLength();
    }
    
    @After
    public void tearDown()
    {
    }

    @Test
    public void testUpdatePositionMovePuck()
    {
        System.out.println("!!!MOVE PUCK TEST!!!");
        position = ((Puck)game.getMyPuck()).getCentre(); //Set position to centre
        puckSpeed = 10;
        direction = 90; //Move to the right
        
        //Small distance, no bounce
        distance = sideLength / 4;
        
        runCount = calculateNeededRunCount();
        
        float expX = position.x + distance;
        float expY = position.y;
        
        game.customSetup(position, puckSpeed, direction, runCount, 1);
        game.beginGame();

        waitForPuck();
        
        Vector2 expResult = new Vector2(expX,expY);
        Vector2 result = ((Puck)game.getMyPuck()).getEndPosition();
        
        assertTrue("Pucks position is incorrect", checkEqualPositions(expResult, result));
    }
    
    @Test
    public void testUpdatePositionBounceRight()
    {
        position = ((Puck)game.getMyPuck()).getCentre(); //Set position to centre
        puckSpeed = 10;
        direction = 90; //Move to the right
        
        //Half the sideLength which is a little bit more then the distance from centre to the right wall, so a wall bounce can occur.
        distance = sideLength / 2;
        
        runCount = calculateNeededRunCount();
        
        game.customSetup(position, puckSpeed, direction, runCount, 1);
        game.beginGame();

        waitForPuck();
        
        //Bounce off the rightside wall >>> resulting in the direction going towards the right (90) changing to going towards the bottom (180).
        int expResult = 180;
        int result = (int)((Puck)game.getMyPuck()).getEndDirection();
        
        assertEquals("Pucks position is incorrect", expResult, result);
    }
    
    @Test
    public void testUpdatePositionBounceLeft()
    {
        position = ((Puck)game.getMyPuck()).getCentre(); //Set position to centre
        puckSpeed = 10;
        direction = 270; //Move to the left
        
        //Half the sideLength which is a little bit more then the distance from centre to the left wall, so a wall bounce can occur.
        distance = sideLength / 2;
        
        runCount = calculateNeededRunCount();
        
        game.customSetup(position, puckSpeed, direction, runCount, 1);
        game.beginGame();

        waitForPuck();
        
        //Bounce off the leftside wall >>> resulting in the direction going towards the left (90) changing to going towards the bottom (180).
        int expResult = 180;
        int result = (int)((Puck)game.getMyPuck()).getEndDirection();
        
        assertEquals("Pucks position is incorrect", expResult, result);
    }
    
    @Test
    public void testUpdatePositionBounceUp()
    {
        position = ((Puck)game.getMyPuck()).getCentre(); //Set position to centre
        puckSpeed = 10;
        direction = 0; //Move up
        
        //75% the sideLength which is a little bit more then the distance from centre to the left wall, so a wall bounce can occur.
        distance = sideLength * 0.75f;
        
        runCount = calculateNeededRunCount();
        
        game.customSetup(position, puckSpeed, direction, runCount, 1);
        game.beginGame();

        waitForPuck();
        
        //Bounce off the top corner >>> resulting in the direction going up (0) changing to going towards the bottom (180).
        int expResult = 180;
        int result = (int)((Puck)game.getMyPuck()).getEndDirection();
        
        assertEquals("Pucks position is incorrect", expResult, result);
    }
    
    @Test
    public void testUpdatePositionBounceDown()
    {
        position = ((Puck)game.getMyPuck()).getCentre(); //Set position to centre
        puckSpeed = 10;
        direction = 180; //Move down
        
        //Half the sideLength which is a little bit more then the distance from centre to the bottom wall, so a wall bounce can occur.
        distance = sideLength / 2;
        
        runCount = calculateNeededRunCount();
        
        game.customSetup(position, puckSpeed, direction, runCount, 1);
        game.beginGame();

        waitForPuck();
        
        //Bounce off the top corner >>> resulting in the direction going down (180) changing to going up (0).
        int expResult = 0;
        int result = (int)((Puck)game.getMyPuck()).getEndDirection();
        
        assertEquals("Pucks position is incorrect", expResult, result);
    }
    
    @Test
    public void testUpdatePositionBounceDiagonalTowardsBottomWall()
    {
        position = ((Puck)game.getMyPuck()).getCentre(); //Set position to centre
        puckSpeed = 10;
        direction = 160; //Move diagonally down towards the bottom wall
        
        //Half the sideLength which is a little bit more then the distance from centre diagonally to the bottom wall, so a wall bounce can occur.
        distance = sideLength / 2;
        
        runCount = calculateNeededRunCount();
        
        game.customSetup(position, puckSpeed, direction, runCount, 1);
        game.beginGame();

        waitForPuck();
        
        //Bounce off the top corner >>> resulting in the direction going down (180) changing to going up (0).
        int expResult = 20;
        int result = (int)((Puck)game.getMyPuck()).getEndDirection();
        
        assertEquals("Pucks position is incorrect", expResult, result);
    }
    
    public void testUpdatePositionBounceDiagonalTowardsLeftWall()
    {
        position = ((Puck)game.getMyPuck()).getCentre(); //Set position to centre
        puckSpeed = 10;
        direction = 290; //Move diagonally down towards the bottom wall
        
        //Half the sideLength which is a little bit more then the distance from centre diagonally to the left wall, so a wall bounce can occur.
        distance = sideLength / 2;
        
        runCount = calculateNeededRunCount();
        
        game.customSetup(position, puckSpeed, direction, runCount, 1);
        game.beginGame();

        waitForPuck();
        
        //Bounce off the top corner >>> resulting in the direction going down (180) changing to going up (0).
        int expResult = 130;
        int result = (int)((Puck)game.getMyPuck()).getEndDirection();
        
        assertEquals("Pucks position is incorrect", expResult, result);
    }
    
    @Test
    public void testUpdatePositionGoalHit()
    {
        position = ((Puck)game.getMyPuck()).getCentre(); //Set position to centre
        position.x += ((Puck)game.getMyPuck()).getBatWidth() * 0.75f;
        puckSpeed = 10;
        direction = 180; //Move towards the bottom
        
        //Half the sideLength which is a little bit more then the distance from centre to the right wall, so a wall bounce can occur.
        distance = sideLength / 2;
        
        runCount = calculateNeededRunCount();
        
        game.customSetup(position, puckSpeed, direction, runCount, 1);
        game.beginGame();

        waitForPuck();
        
        IPlayer expResult = game.getMyPlayers().get(0); //Player Red
        IPlayer result = ((Puck)game.getMyPuck()).getEndGoalHit();
        
        assertEquals("Pucks position is incorrect", expResult, result);
    }
    
    @Test
    public void testUpdatePositionBatHit()
    {
        position = ((Puck)game.getMyPuck()).getCentre(); //Set position to centre
        puckSpeed = 10;
        direction = 90; //Move towards the right
        
        //Half the sideLength which is a little bit more then the distance from centre diagonally to the left wall, so a wall bounce can occur.
        distance = sideLength / 2;
        
        runCount = calculateNeededRunCount();
        
        game.customSetup(position, puckSpeed, direction, runCount, 1);
        game.beginGame();

        waitForPuck();
        
        IPlayer expResult = game.getMyPlayers().get(1); //Player blue
        IPlayer result = ((Puck)game.getMyPuck()).getEndBatHit();
        
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
    
    private void waitForPuck()
    {       
        while (!game.statusProperty().get().equals(GameStatus.GameOver))
        {
            //Wait indefinitly for Puck to finish
            System.out.print("");
        }
    }
    
    private int calculateNeededRunCount()
    {
        int runCount = 0;
        
        runCount = (int)(distance / (puckSpeed / 10));
        
        System.out.print("Needed runcount: " + runCount);
        return runCount;
    }
    
    private boolean checkEqualPositions(Vector2 p1, Vector2 p2)
    {
        if (p1.x < p2.x + puckSpeed && p1.x > p2.x - puckSpeed)
        {
            if (p1.y < p2.y + puckSpeed && p1.y > p2.y - puckSpeed)
            {
                return true;
            }
        }
        return false;
    }
}
