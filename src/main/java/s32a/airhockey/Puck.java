/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package s32a.airhockey;

import com.badlogic.gdx.math.Vector2;
import java.util.List;
import java.util.Random;
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
    private float sideLength;
    private float middleLine;
    
    private Vector2 centre;
    
    private float goalLength;
    private float sideGoalMinY;
    private float sideGoalMaxY;
    private float bottomGoalMinX;
    private float bottomGoalMaxX;
    private float batWidth;
    
    private Game myGame;
    
    /**
     * initialises a game's puck
     * position is randomised, speed is a given
     * isMoving is initialised as false
     * @param speed 
     */
    public Puck(float speed, Game myGame)
    {
        this.speed = speed;
        
        this.sideLength = (float)Lobby.getSingle().getAirhockeySettings().get("Side Length");
        
        this.middleLine = (float)Math.sqrt(Math.pow(sideLength, 2) - Math.pow(sideLength / 2, 2));
        
        float centreX = (float)(0.5 * (double)sideLength);
        float centreY = (float)(Math.tan(30) * (0.5 * (double)sideLength));
        this.centre = new Vector2(centreX, centreY);
        
        this.goalLength = sideLength * 0.4f;
        
        this.sideGoalMinY = this.centre.y - (float)((0.5 * goalLength) * sideLength);
        this.sideGoalMaxY = this.centre.y + (float)((0.5 * goalLength) * sideLength);
        this.bottomGoalMinX = -(this.sideLength * 0.2f);
        this.bottomGoalMaxX = this.sideLength * 0.2f;
        
        this.batWidth = sideLength/100*8;
        
        this.size = sideLength * 0.04f;
              
        position = new Vector2((new Random().nextFloat() - 0.5f) * (sideLength * 0.1f), (new Random().nextFloat() - 0.5f) * (sideLength * 0.1f));
        direction = new Random().nextFloat() * 360;
        
        isMoving = false;
        
        this.myGame = myGame;
    }
    
    /**
     * Is continuously called by the Game class
     * Starts the process of updating the pucks position and detection of goal hits
     */
    public void run()
    {
        updatePosition(speed);
    }
    
    /**
     * updates the pucks position based on its speed, direction, and current position
     * does nothing if puck isn't moving
     * @param distance The distance for the puck to be traveled; based on either the current speed or the remaining distance the puck has to travel after a bounce
     */
    private void updatePosition(float distance)
    {
        if (isMoving)
        {
            float oldX = position.x;
            float oldY = position.y;
            float newX = oldX + (float)(Math.sin((double)direction) * distance);
            float newY = oldY + (float)(Math.cos((double)direction) * distance);
            
            Vector2 newPosition = new Vector2(newX, newY);
            
            Vector2 bouncePosition = isOutsideField(newPosition);
            
            if (bouncePosition == null)
            {
                //Inside field
                position = newPosition;
            }
            else
            {
                //Outside field or in collission with wall
                position = bouncePosition;
                               
                //Detect wheter a goal has been hit (includes detection of bat blocking the puck)
                int goalHitPlayerID = checkGoalHit(bouncePosition);
                
                if (goalHitPlayerID != -1)
                {
                    switch(goalHitPlayerID)
                    {
                        case 0:
                            //Player red goal hit
                            break;
                        case 1:
                            //Player blue goal hit
                            break;
                        case 2:
                            //Player green goal hit
                            break;
                        default:
                            //Default to player red goal hit
                            break;
                    }
                                               
                    //Round is over
                    
                    //Player who scored
                    Player whoScored = hitBy.get(hitBy.size() - 1);
                    whoScored.setScore(whoScored.getScore() + 1);
                    
                    //Player whose goal is hit
                    Player whoLostScore = myGame.getMyPlayers().get(goalHitPlayerID);
                    whoLostScore.setScore(whoScored.getScore() - 1);
                            
                    //End round
                    myGame.setContinueRun(false);
                }
                else
                {
                    //Position is set to bouncePosition and new direction has been calculated
                    //Repeat process with remaining distance to travel
                    distance = getDistance(bouncePosition, newPosition);
                    if (distance > 0)
                    {
                        updatePosition(distance);
                    }
                }
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
            //Left of field
            Vector2 linePos1 = new Vector2((float)(-(sideLength / 2)), 0);
            Vector2 linePos2 = new Vector2(0, (float)middleLine);
            
            updateDirection(90);
            return getIntersection(position, newPosition, linePos1, linePos2);
        }
        else if (outside == 1)
        {
            //Right of field
            Vector2 linePos1 = new Vector2((float)(sideLength / 2), 0);
            Vector2 linePos2 = new Vector2(0, (float)middleLine);
            
            updateDirection(-90);
            return getIntersection(position, newPosition, linePos1, linePos2);
        }
        else
        {
            if (y < 0)
            {
                //Underneath field
                Vector2 linePos1 = new Vector2((float)(-(sideLength / 2)), 0);
                Vector2 linePos2 = new Vector2((float)(sideLength / 2), 0);
            
                direction = 180;
                
                return getIntersection(position, newPosition, linePos1, linePos2);
            }
            else if (y > middleLine)
            {
                //Above field
                updateDirection(180);
                return new Vector2(0, (float)middleLine);
            }
        }
        
        //Inside field
        return null;
    }
    
    /**
     * Calculated lines between begin and end positions and calculated the crossing position of those lines
     * @param oldPos Puck start position
     * @param newPos Puck end position
     * @param linePos1 Field line start position
     * @param linePos2 Field line end position
     * @return Returns a Vector2 containing the position where the two lines generated by the given positions cross
     * Returns null if no crossing position is found
     */
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
        
        try
        {
            float x =(lineB-puckB)/(puckA -lineA);
            float y =puckA * x + puckB;
        
            return new Vector2(x, y);
        }
        catch (Exception ex)
        {
            return null;
        }
    }
    
    /**
     * Calculates the distance between two Vector2 positions
     * @param p1 Position one
     * @param p2 Position two
     * @return Returns the distance between two Vector2 positions
     */
    private float getDistance(Vector2 p1, Vector2 p2)
    {
        float distance = (float)Math.sqrt(Math.pow((double)Math.abs(p1.x - p2.x), 2) + Math.pow((double)Math.abs(p1.y - p2.y), 2));
        return distance;
    }
    
    /**
     * Updated the direction based on the wall it bounced off
     * @param adjustValue The value the direction has to be adjusted with to get the right bouncing position based on the direction of the wall
     */
    private void updateDirection(float adjustValue)
    {
        direction = -direction + adjustValue;
        
        while (direction > 360)
        {
            direction -= 360;
        }
        
        while (direction < 0)
        {
            direction += 360;
        }
    }
    
    /**
     * Calculate whether a Vector2 position is in a goal
     * @param pos
     * @return Return an int value to tell whose goal is hit where:
     * 1 refers to player Green
     * 2 refers to player Blue
     * 3 refers to player Red
     * Returns 0 if no goal has been hit
     */
    private int checkGoalHit(Vector2 pos)
    {      
        int playerID = -1;
        
        if (pos.y > sideGoalMinY && pos.y < sideGoalMaxY)
        {
            if (pos.x < 0)
            {
                //Green goal
                playerID = 3;
            }
            else
            {
                //Blue goal
                playerID = 2;
            }
        }
        else if (pos.x > bottomGoalMinX && pos.x < bottomGoalMaxX)
        {
            //Red goal
            playerID = 1;
        }
        
        if (playerID == -1)
        {
            //No goal hit
            return -1;
        }
        else
        {
            //Goal hit, but possible block by bat
            if (checkBatBlock(playerID, pos))
            {
                //Bat blocked the puck
                hitBy.add(myGame.getMyPlayers().get(playerID));
                
                //No goal hit
                return -1;
            }
            else
            {
                //Bat did not block the puck
                //Goal hit of player with playerID
                return playerID;
            }
        }
    }
    
    private boolean checkBatBlock(int playerID, Vector2 pos)
    {
        Vector2 batPos = myGame.getMyPlayers().get(playerID).getBatPos();
        
        if (playerID == 0)
        {
            //Player Red (bottom bat)
            float batMinX = batPos.x - (float)(0.5 * batWidth);
            float batMaxX = batPos.x + (float)(0.5 * batWidth);
            
            if (pos.x > batMinX && pos.x < batMaxX)
            {
                //Bat blocked the puck
                return true;
            }
            else
            {
                //Bat did not block the puck
                return false;
            }
        }
        else
        {
            //Player Blue or Green (side bat)
            float batMinY = batPos.y - (float)(Math.sin(30) * (0.5 * batWidth));
            float batMaxY = batPos.y + (float)(Math.sin(30) * (0.5 * batWidth));
            
            if (pos.y > batMinY && pos.y < batMaxY)
            {
                //Bat blocked the puck
                return true;
            }
            else
            {
                //Bat did not block the puck
                return false;
            }
        }
    }
}
