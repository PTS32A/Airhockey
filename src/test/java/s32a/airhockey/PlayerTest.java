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
    Game game;
    
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
        game = new Game(player);
        player.setMyGame(game);
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
        Vector2 expResult = new Vector2(0,0);
        Vector2 result = player.getBatPos();
        assertEquals("Expected result does not match given result",expResult, result);
    }

    /**
     * Test of getColor method, of class Player.
     */
    @Test
    public void testGetColor()
    {
        System.out.println("getColor");
        String expResult = "Red";
        String result = player.getColor();
        assertEquals("Colors do not match", expResult, result);
    }

    /**
     * Test of getScore method, of class Player.
     */
    @Test
    public void testGetScore()
    {
        System.out.println("getScore");
        int expResult = 0;
        int result = player.getScore();
        assertEquals("Score incorrect",expResult, result);
    }
    
    @Test
    public void testMoveBat()
    {
        System.out.println("moveBat");
        Vector2 expResult = new Vector2(5,0);
        player.moveBat(5f);
        Vector2 result = player.getBatPos();
        assertEquals("Bat not moved properly",expResult, result);
    }
    
    @Test
    public void testPause()
    {
        System.out.println("Pause");
        boolean expResult = true;
        player.pauseGame(true);
        boolean result = game.isPaused();
        assertEquals("Game was not paused",expResult, result);
    }
}
