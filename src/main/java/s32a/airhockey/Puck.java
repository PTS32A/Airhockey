/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package s32a.airhockey;

import com.badlogic.gdx.math.Vector2;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Kargathia
 */
public class Puck
{
    @Getter private Vector2 position;
    @Getter private float speed;
    @Getter private List<Player> hitBy;
    @Getter private float direction;
    @Getter @Setter boolean isMoving;
    
    
    /**
     * initialises a game's puck
     * position is randomised, speed is a given
     * isMoving is initialised as false
     * @param speed 
     */
    public Puck(float speed)
    {
        this.speed = speed;
        
        //TODO set position boundaries and randomise position
        position = new Vector2(0, 0);
        
        isMoving = false;
    }
    
    /**
     * updates the pucks position based on its speed, direction, and current position
     * does nothing if puck isn't moving
     */
    private void run()
    {
        if (isMoving)
        {
            float oldX = position.x;
            float oldY = position.y;
            float newX = oldX + (float)(Math.sin((double)direction) * speed);
            float newY = oldY + (float)(Math.cos((double)direction) * speed);
            
            position = new Vector2(newX, newY);
        }
    }
    
}
