/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package s32a.airhockey;

import com.badlogic.gdx.math.Vector2;
import java.util.Calendar;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Kargathia
 */
public class Player extends Person
{
    @Getter private Vector2 batPos;
    @Getter private String color;
    @Getter private int score;
    @Getter @Setter private boolean starter = false;
    @Getter private int rotation;
    @Getter private Vector2 goalPos;
    @Getter private Calendar lastAction;
    @Getter private Game myGame;
    
    /**
     * 
     * @param name provided by Person
     * @param rating provided by Person
     * @param color player color - linked to them being player 1, 2 or 3
     * @param game 
     */
    public Player(String name, int rating, String color, Game game)
    {
        super(name, rating);
        this.color = color;
    }
    
    /**
     * Adjusts the bat position a given distance to left or right
     * @param amount on an X-scale - negative values are allowed
     * @return 
     */
    public boolean moveBat(float amount)
    {
        return false;
    }
    
    public boolean applyPowerUp()
    {
        return false;
    }
    
    public void addPowerUp(PowerUp powerUp)
    {
        //
    }
    
    public boolean pauseGame(boolean isPaused)
    {
        return false;
    }
}
