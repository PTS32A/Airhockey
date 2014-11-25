/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package s32a.Server;

import s32a.Shared.enums.Colors;
import s32a.Server.Player;
import s32a.Server.Game;
import s32a.Server.Lobby;
import com.badlogic.gdx.math.Vector2;
import java.util.Calendar;
import javafx.beans.property.SimpleDoubleProperty;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Luke
 */
public class PlayerTest
{
    Player player;
    float sideLength;
    Game g;
    
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
        player = new Player("Test", (double)15, Colors.Red);
        sideLength = (float)Lobby.getSingle().getAirhockeySettings().get("Side Length");
        //g = new Game(player);
    }
    
    @After
    public void tearDown()
    {
    }
    
    @Test
    public void testBatOutOfBounds()
    {
        player.setPosX(new SimpleDoubleProperty(sideLength/2));
        boolean result = player.moveBat(1);
        assertFalse("Bat moved past right side of goal", result);
        player.setPosX(new SimpleDoubleProperty(-sideLength/2));
        result = player.moveBat(-1);
        assertFalse("Moved out of field to the left", result);
    }
    
    /**
     * Test of getBatPos method, of class Player.
     */
    @Test
    public void testGetBatPos()
    {
        System.out.println("getBatPos");
        Vector2 expResult = new Vector2(0,0);
        Vector2 result = new Vector2(player.getPosX().floatValue(), player.getPosY().floatValue());
        assertEquals("Expected result does not match given result",expResult, result);
    }

    /**
     * Test of getColor method, of class Player.
     */
    @Test
    public void testGetColor()
    {
        System.out.println("getColor");
        Colors expResult = Colors.Red;
        Colors result = player.getColor();
        assertEquals("Colors do not match", expResult, result);
    }

    /**
     * Test of getScore method, of class Player.
     */
    @Test
    public void testGetScore()
    {
        System.out.println("getScore");
        int expResult = 20;
        int result = player.getScore().get();
        assertEquals("Score incorrect",expResult, result);
    }
    
    @Test
    public void testMoveBat()
    {
        System.out.println("moveBat");
        Vector2 expResult = new Vector2(5,0);
        player.moveBat(1);
        Vector2 result = new Vector2(player.getPosX().floatValue(), player.getPosY().floatValue());
        assertEquals("Bat not moved properly",expResult, result);
        expResult = new Vector2(0,0);
        player.moveBat(-1);
        result = new Vector2(player.getPosX().floatValue(), player.getPosY().floatValue());
        assertEquals("Bat not moved properly",expResult, result);
    }
}
