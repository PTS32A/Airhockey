/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package s32a.airhockey;

import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
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
    @Getter @Setter private Vector2 position;
    @Getter @Setter private float speed;
    @Getter private List<Player> hitBy;
    @Getter @Setter private float direction;
    @Getter @Setter boolean isMoving;
    
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
    
    private int runCount;
    
    /**
     * initialises a game's puck
     * position is randomised, speed is a given
     * isMoving is initialised as false
     * @param speed 
     */
    public Puck(float speed, Game myGame)
    {
        this.speed = speed;
        this.hitBy = new ArrayList<Player>();
        
        this.sideLength = (float)Lobby.getSingle().getAirhockeySettings().get("Side Length");
        
        //Inner triangle for centre of Puck to bounce against so that the edges of the circle of the Puck will look like bouncing of the real triangle
        //TODO this.sideLength = less then sidelength (using puck width) to forn inner triangle
        
        this.middleLine = (float)Math.sqrt(Math.pow(sideLength, 2) - Math.pow(sideLength / 2, 2));
        
        float centreX = 0;
        float centreY = (float)(Math.tan(Math.toRadians(30)) * (0.5 * (double)sideLength));
        
        centreX = Math.round(centreX * 100) / 100;
        centreY = Math.round(centreY * 100) / 100;
               
        this.centre = new Vector2(centreX, centreY);
               
        this.goalLength = sideLength * 0.4f;
        
        this.sideGoalMinY = this.centre.y - (float)(Math.sin(Math.toRadians(0.5 * goalLength)));
        this.sideGoalMaxY = this.centre.y + sideGoalMinY;
        this.bottomGoalMinX = -(this.sideLength * 0.2f);
        this.bottomGoalMaxX = this.sideLength * 0.2f;
        
        //System.out.println("SIDEGOAL Y-RANGE: [" + sideGoalMinY + ", " + sideGoalMaxY + "]");
        //System.out.println("BOTTOMGOAL X-RANGE: [" + bottomGoalMinX + ", " + bottomGoalMaxX + "]");
        
        this.batWidth = sideLength/100*8;
                     
        this.position = centre;
        this.direction = new Random().nextFloat() * 360;
        
        this.isMoving = true;
        
        this.myGame = myGame;
        
        this.runCount = 0;
        
        Vector2 batPosition0 = myGame.getMyPlayers().get(0).getBatPos();
        Vector2 batPosition1 = myGame.getMyPlayers().get(1).getBatPos();
        Vector2 batPosition2 = myGame.getMyPlayers().get(2).getBatPos();
        
        System.out.println("Bat Red: " + batPosition0.x + ", " + batPosition0.y);
        System.out.println("Bat Blue: " + batPosition1.x + ", " + batPosition1.y);
        System.out.println("Bat Green: " + batPosition2.x + ", " + batPosition2.y);
    }
    
    public void resetPuck()
    {
        this.position = centre;
        this.direction = new Random().nextFloat() * 360;
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
            
            double radians = Math.toRadians((double)direction);
            
            float newX = oldX + (float)(Math.sin(radians) * (double)distance);
            float newY = oldY + (float)(Math.cos(radians) * (double)distance);
            
            newX = Math.round(newX * 100) / 100;
            newY = Math.round(newY * 100) / 100;
                       
            Vector2 newPosition = new Vector2(newX, newY);
            
            Vector2 bouncePosition = isOutsideField(newPosition);
            
            if (bouncePosition == null)
            {
                //Inside field
                position = newPosition;
                System.out.println("Position: " + newX + ", " + newY);
            }
            else
            {
                //Outside field or in collission with wall
                position = bouncePosition;
                
                System.out.println("Wanted Position: " + newX + ", " + newY);
                System.out.println("Bounce Position: " + bouncePosition.x + ", " + bouncePosition.y);
                               
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
                    Player whoScored = null;
                    if (hitBy.size() > 0)
                    {
                        whoScored = hitBy.get(hitBy.size() - 1);
                        whoScored.setScore(whoScored.getScore() + 1);
                    }
                    
                    //Player whose goal is hit
                    Player whoLostScore = myGame.getMyPlayers().get(goalHitPlayerID);
                    whoLostScore.setScore(whoLostScore.getScore() - 1);
                            
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
            
            System.out.println("OUTSIDE: Left of the field");
            
            Vector2 linePos1 = new Vector2((float)(-(sideLength / 2)), 0);
            Vector2 linePos2 = new Vector2(0, (float)middleLine);
            
            updateDirection(90);
            return getIntersection(position, newPosition, linePos1, linePos2);
        }
        else if (outside == 1)
        {
            //Right of field
            
            System.out.println("OUTSIDE: Right of the field");
            
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
                
                System.out.println("OUTSIDE: Underneath the field");
                
                Vector2 linePos1 = new Vector2((float)(-(sideLength / 2)), 0);
                Vector2 linePos2 = new Vector2((float)(sideLength / 2), 0);
            
                updateDirection(180);
                
                return getIntersection(position, newPosition, linePos1, linePos2);
            }
            else if (y > middleLine)
            {
                //Above field
                
                System.out.println("OUTSIDE: Above the field");
                
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
        //NEW CALCULATIONS
        
        /**
        * Line Formula:
        * y = a*x + b
        * a = (change in y) / (change in x)
        * b = y - a * x
        */
        
        //Line 1:
        float a1 = (oldPos.y - newPos.y) / (oldPos.x - newPos.x);
        float b1 = oldPos.y - a1 * oldPos.x;
        
        //Line 2:
        float a2 = (linePos1.y - linePos2.y) / (linePos1.x - linePos2.x);
        float b2 = linePos1.y - a2 * linePos1.x;
              
        float x;
        
        if (oldPos.x == newPos.x)
        {
            //Vertical line
            x = oldPos.x;
        }
        else
        {
            //Curved line
            
            //Equate the two lines:
            // a1*x + b1 = a2*x + b2
            // (a1 - a2) * x = b2 - b1
            // x = (b2 - b1) / (a1 - a2)
            x = (b2 - b1) / (a1 - a2);
        }
        
        //Find y:
        //y = a*x + b
        float y = a2 * x + b2;
        
        try
        {
            return new Vector2(x, y);
        }
        catch(Exception ex)
        {
            System.out.println("Exception: " + ex.getMessage());
            return null;
        }
        
        //OLD CALCULATIONS (PROBABLY WRONG)
        
        //float puckAX = oldPos.x;
        //float puckAY = oldPos.y;
        
        //float puckBX = newPos.x;
        //float puckBY = newPos.y;
        
        //float puckA = ((puckAX-puckBX)/(puckAY-puckBY));
        //float puckB = puckAY - (puckA*puckAX);
        
        
        //float lineAX = linePos1.x;
        //float lineAY = linePos1.y;
        
        //float lineBX = linePos2.x;
        //float lineBY = linePos2.y;
        
        //float lineA = ((lineAX-puckBX)/(lineAY-lineBY));
        //float lineB = lineAY - (lineA*lineAX);
        
        /** formula
        // y = puckA * x + puckB
        // y = lineA * x + lineB
        // puckA * x + puckB = lineA * x + lineB
        //(puckA -lineA)x = lineB-puckB
        //x = (lineB-puckB)/(puckA -lineA)
        */
        
        //try
        //{
        //    float x =(lineB-puckB)/(puckA -lineA);
        //    float y = puckA * x + puckB;
        //
        //    System.out.println("INTERSECTION: " + x + ", " + y);
        //    
        //    return new Vector2(x, y);
        //}
        //catch (Exception ex)
        //{
        //    return null;
        //}
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
                playerID = 2;
            }
            else
            {
                //Blue goal
                playerID = 1;
            }
        }
        else if (pos.x > bottomGoalMinX && pos.x < bottomGoalMaxX && pos.y == 0)
        {
            //Red goal
            playerID = 0;
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
                System.out.println("BAT BOUNCE AT PLAYER " + playerID);
                hitBy.add(myGame.getMyPlayers().get(playerID));
                
                //No goal hit
                return -1;
            }
            else
            {
                //Bat did not block the puck
                //Goal hit of player with playerID
                System.out.println("GOAL AT PLAYER " + playerID);
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
