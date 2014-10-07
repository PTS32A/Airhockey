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
    @Getter @Setter private float speed;
    @Getter private List<Player> hitBy;
    @Getter private float direction;
    @Getter @Setter boolean isMoving;
    
    private float size;
    private double sideLength;
    private double middleLine;
    
    /**
     * initialises a game's puck
     * position is randomised, speed is a given
     * isMoving is initialised as false
     * @param speed 
     */
    public Puck(float speed)
    {
        this.speed = speed;
        
        this.sideLength = Lobby.getSingle().getSideLength();
        
        this.middleLine = Math.sqrt(Math.pow(sideLength, 2) - Math.pow(sideLength / 2, 2));
        
        this.size = (float)(sideLength * 0.04);
        
        //TODO set position boundaries and randomise position
        position = new Vector2(0, 0);
        
        isMoving = false;
    }
    
    /**
     * updates the pucks position based on its speed, direction, and current position
     * does nothing if puck isn't moving
     */
    public void run()
    {
        if (isMoving)
        {
            float oldX = position.x;
            float oldY = position.y;
            float newX = oldX + (float)(Math.sin((double)direction) * speed);
            float newY = oldY + (float)(Math.cos((double)direction) * speed);
            
            Vector2 newPosition = new Vector2(newX, newY);
            
            Vector2 bouncePosition = isOutsideField(newPosition);
            
            if (bouncePosition == null)
            {
                //No bounce
                position = newPosition;
            }
            else
            {
                //Bounce
                float distance = getDistance(bouncePosition, newPosition);
                
                //TODO update direction and new position
            }
        }
    }
    
    /**
     * @return the wall position that the puck bounces off
     * returns null if the puck is not in collision with any walls
     */
    private Vector2 isOutsideField(Vector2 newPosition)
    {       
        float x = newPosition.x;
        float y = newPosition.y;

        int outside = 0;
        
        
        if (x < 0)
        {
            //Check outside field left
            if (y > (middleLine / (sideLength / 2)) * x + middleLine)
            {
                outside = -1;
            }
        }
        else if (x > 0)
        {
            //Check outside field right
            if (y > -(middleLine / (sideLength / 2)) * x + middleLine)
            {
                outside = +1;
            }
        }
        
        
        if (outside == -1)
        {
            Vector2 linePos1 = new Vector2((float)(-(sideLength / 2)), 0);
            Vector2 linePos2 = new Vector2(0, (float)middleLine);
            
            return getIntersection(position, newPosition, linePos1, linePos2);
        }
        else if (outside == 1)
        {
            Vector2 linePos1 = new Vector2((float)(sideLength / 2), 0);
            Vector2 linePos2 = new Vector2(0, (float)middleLine);
            
            return getIntersection(position, newPosition, linePos1, linePos2);
        }
        else
        {
            if (y < 0)
            {
                //Underneath field
                Vector2 linePos1 = new Vector2((float)(-(sideLength / 2)), 0);
                Vector2 linePos2 = new Vector2((float)(sideLength / 2), 0);
            
                return getIntersection(position, newPosition, linePos1, linePos2);
            }
            else if (y > middleLine)
            {
                return new Vector2(0, (float)middleLine);
            }
        }
        
        return null;
    }
    
    private Vector2 getIntersection(Vector2 oldPos, Vector2 newPos, Vector2 linePos1, Vector2 linePos2)
    {
        float puckAX = oldPos.x;
        float puckAY = oldPos.y;
        
        float puckBX = newPos.x;
        float puckBY = newPos.y;
        
        float puckA = ((puckAX-puckBX)/(puckAY-puckBY));
        float puckB = puckAY - (puckA*puckAX);
        
        
        float lineAX = linePos1.x;
        float lineAY = linePos1.y;
        
        float lineBX = linePos2.x;
        float lineBY = linePos2.y;
        
        float lineA = ((lineAX-puckBX)/(lineAY-lineBY));
        float lineB = lineAY - (lineA*lineAX);
        
        // y = puckA * x + puckB
        // y = lineA * x + lineB
        // puckA * x + puckB = lineA * x + lineB
        //(puckA -lineA)x = lineB-puckB
        //x = (lineB-puckB)/(puckA -lineA)
        
        float x =(lineB-puckB)/(puckA -lineA);
        float y =puckA * x + puckB;
        
        return new Vector2(x, y);
    }
    
    private float getDistance(Vector2 p1, Vector2 p2)
    {
        float distance = (float)Math.sqrt(Math.pow((double)Math.abs(p1.x - p2.x), 2) + Math.pow((double)Math.abs(p1.y - p2.y), 2));
        return distance;
    }
}
