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
import java.util.TimerTask;
import javafx.scene.canvas.GraphicsContext;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Kargathia
 */
public class Puck extends TimerTask
{

    @Getter
    @Setter
    private Vector2 position;
    @Getter
    @Setter
    private float speed;
    @Getter
    private List<Player> hitBy;
    @Getter
    @Setter
    private float direction;
    @Getter
    @Setter
    boolean isMoving;

    @Getter
    private float sideLength;
    private float middleLine;

    @Getter
    private Vector2 centre;

    @Getter
    private float goalLength;
    private float sideGoalMinY;
    private float sideGoalMaxY;
    private float bottomGoalMinX;
    private float bottomGoalMaxX;
    @Getter
    private float batWidth;
    private float puckSize;

    private Game myGame;

    @Getter
    private Vector2 endPosition;
    @Getter
    private float endDirection;
    @Getter
    private Player endGoalHit;
    @Getter
    private Player endBatHit;
    @Getter
    @Setter
    private int runCount;
    @Getter
    @Setter
    private int defaultRunCount;

    @Getter
    @Setter
    private boolean printMessages = true;

    /**
     * initialises a game's puck position is randomised, speed is a given
     * isMoving is initialised as false
     *
     * @param speed
     * @param myGame
     */
    public Puck(float speed, Game myGame)
    {
        if (speed <= 0)
        {
            throw new IllegalArgumentException("Speed can't be negative");
        }
        if (myGame == null)
        {
            throw new IllegalArgumentException("myGame parameter was null");
        }

        this.speed = speed;
        this.hitBy = new ArrayList<>();

        this.sideLength = (float) Lobby.getSingle()
                .getAirhockeySettings().get("Side Length");
        this.goalLength = sideLength * 0.4f;
        this.batWidth = sideLength / 100 * 8;

        //Inner triangle for centre of Puck to bounce against so that 
        // the edges of the circle of the Puck will look like bouncing of the real triangle
        this.puckSize = (float) (this.sideLength * 0.04);
        this.sideLength = this.sideLength - (float) (puckSize * Math.sqrt(3));

        this.middleLine = (float) Math.sqrt(Math.pow(sideLength, 2) 
                - Math.pow(sideLength / 2, 2));

        float centreX = 0;
        float centreY = (float) (Math.tan(Math.toRadians(30)) 
                * (0.5 * (double) sideLength));

        centreX = Math.round(centreX * 100) / 100;
        centreY = Math.round(centreY * 100) / 100;

        this.centre = new Vector2(centreX, centreY);

        this.sideGoalMinY = this.centre.y - 
                (float) (Math.sin(Math.toRadians(0.5 * goalLength)));
        this.sideGoalMaxY = this.centre.y + sideGoalMinY;
        this.bottomGoalMinX = -(this.sideLength * 0.2f);
        this.bottomGoalMaxX = this.sideLength * 0.2f;

        //System.out.println("SIDEGOAL Y-RANGE: [" + sideGoalMinY + ", " + sideGoalMaxY + "]");
        //System.out.println("BOTTOMGOAL X-RANGE: [" + bottomGoalMinX + ", " + bottomGoalMaxX + "]");
        this.isMoving = true;

        this.myGame = myGame;


        this.defaultRunCount = -1;
        
        resetPuck();
        clearEndData();
    }

    public void resetPuck()
    {
        setEndData();
        
        this.position = centre;
        this.direction = new Random().nextFloat() * 360;
        this.runCount = defaultRunCount;
    }

    /**
     * Sets the EndData variables at the end of each round.
     * These variables contain the last position and last direction of the Puck,
     * the player whose Bat last touched the Puck.
     */
    private void setEndData()
    {
        //DATA for after a round; used by unittests.
        this.endPosition = position;
        this.endDirection = direction;

        if (hitBy.size() > 0)
        {
            this.endBatHit = hitBy.get(hitBy.size() - 1);
        }
        else
        {
            this.endBatHit = null;
        }
    }

    /**
     * Clears the EndData variables at the start of each round.
     */
    private void clearEndData()
    {
        //DATA for after a round; used by unittests.
        this.endPosition = null;
        this.endDirection = 0;
        this.endGoalHit = null;
        this.endBatHit = null;
        hitBy.clear();
    }

    /**
     * Is continuously called by the Game class Starts the process of updating
     * the pucks position and detection of goal hits
     */
    @Override
    public void run()
    {
        // idle state between rounds
        if (myGame.isContinueRun() == false)
        {
            return;
        }
        
        // Game is paused
        if (myGame.isPaused() == true)
        {
            return;
        }
               
        if (this.runCount <= -1)
        {
            //runCount is not used (Default setting for the actual product)
            //Round will end only end when a goal has been scored
            updatePosition(speed / 10);
        }
        else
        {
            //runCount is used (used for unittesting only)
            //Round will end when a goal has been scored OR when runCount reaches 0
            if (this.runCount == 0)
            {
                myGame.endRound();
            }
            else
            {
                updatePosition(speed / 10);
                this.runCount--;
            }
        }
    }

    /**
     * updates the pucks position based on its speed, direction, and current
     * position does nothing if puck isn't moving
     *
     * @param distance The distance for the puck to be traveled; based on either
     * the current speed or the remaining distance the puck has to travel after
     * a bounce
     */
    private void updatePosition(float distance)
    {
        if (isMoving)
        {
            float oldX = position.x;
            float oldY = position.y;

            double radians = Math.toRadians((double) direction);

            float newX = oldX + (float) (Math.sin(radians) * (double) distance);
            float newY = oldY + (float) (Math.cos(radians) * (double) distance);
           
            Vector2 newPosition = new Vector2(newX, newY);
           
            //Check whether the puck is outside the field
            Vector2 bouncePosition = isOutsideField(newPosition);

            if (bouncePosition == null)
            {
                //Inside field
                //Check whether the puck is bouncing of a bat
                
                Vector2 batBouncePosition = checkBatBounce(newPosition);
                
                if(batBouncePosition == null)
                {
                    //No bat bounce
                    position = newPosition;
//                  printMessage("Position: " + roundPosition(newPosition));
                }
                else
                {
                    //Bat bounce
                    position = batBouncePosition;
                    
                    //Continue the position with the remaining distance, calculated using batBouncePosition and newPosition
                    continueUpdatePosition(batBouncePosition, newPosition);
                }

                    
            } else
            {
                //Outside field or in collission with wall
                position = bouncePosition;

//                printMessage("  Wanted Position: " + roundPosition(newPosition));
//                printMessage("  Bounce Position: " + roundPosition(bouncePosition));

                //Detect wheter a goal has been hit 
                //(includes detection of bat blocking the puck)
                int goalHitPlayerID = checkGoalHit(bouncePosition);

                if (goalHitPlayerID != -1)
                {
                    //Round is over

                    //Player who scored
                    Player whoScored = null;
                    if (hitBy.size() > 0)
                    {
                        whoScored = hitBy.get(hitBy.size() - 1);
                        whoScored.setScore(whoScored.getScore().get() + 1);
                    }

                    //Player whose goal is hit
                    Player whoLostScore = myGame.getMyPlayers().get(goalHitPlayerID);
                    whoLostScore.setScore(whoLostScore.getScore().get() - 1);
                    this.endGoalHit = whoLostScore;

                    //End round
                    myGame.endRound();
                } else
                {
                    //Continue the position with the remaining distance, calculated using bouncePosition and newPosition
                    continueUpdatePosition(bouncePosition, newPosition);
                }
            }
        }
    }
    
    private void continueUpdatePosition(Vector2 bouncePosition, Vector2 newPosition)
    {
        //Position is set to bouncePosition and new direction has been calculated
        //Repeat process with remaining distance to travel
        float distance = getDistance(bouncePosition, newPosition);
        distance = Math.round(distance);
        if (distance > 0)
        {
            updatePosition(distance);
        }
    }

    /**
     * @return the wall position that the puck bounces off returns null if the
     * puck is not in collision with any walls
     */
    public Vector2 isOutsideField(Vector2 newPosition)
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
        } else if (x > 0)
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

            printMessage("OUTSIDE: Left of the field");

            Vector2 linePos1 = new Vector2((float) (-(sideLength / 2)), 0);
            Vector2 linePos2 = new Vector2(0, (float) middleLine);

            updateDirection(90);
            return getIntersection(position, newPosition, linePos1, linePos2);
        } else if (outside == 1)
        {
            //Right of field

            printMessage("OUTSIDE: Right of the field");

            Vector2 linePos1 = new Vector2((float) (sideLength / 2), 0);
            Vector2 linePos2 = new Vector2(0, (float) middleLine);

            updateDirection(-90);
            return getIntersection(position, newPosition, linePos1, linePos2);
        } else
        {
            if (y < 0)
            {
                //Underneath field

                printMessage("OUTSIDE: Underneath the field");

                Vector2 linePos1 = new Vector2((float) (-(sideLength / 2)), 0);
                Vector2 linePos2 = new Vector2((float) (sideLength / 2), 0);

                updateDirection(180);

                return getIntersection(position, newPosition, linePos1, linePos2);
            } else if (y > middleLine)
            {
                //Above field

                printMessage("OUTSIDE: Above the field");

                updateDirection(180);
                return new Vector2(0, (float) middleLine);
            }
        }

        //Inside field
        return null;
    }

    /**
     * Calculated lines between begin and end positions and calculated the
     * crossing position of those lines
     *
     * @param oldPos Puck start position
     * @param newPos Puck end position
     * @param linePos1 Field line start position
     * @param linePos2 Field line end position
     * @return Returns a Vector2 containing the position where the two lines
     * generated by the given positions cross Returns null if no crossing
     * position is found
     */
    private Vector2 getIntersection(Vector2 oldPos, Vector2 newPos, 
            Vector2 linePos1, Vector2 linePos2)
    {
        /**
         * Line Formula: y = a*x + b
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
        } else
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
        } catch (Exception ex)
        {
            System.out.println("Exception: " + ex.getMessage());
            return null;
        }
    }

    /**
     * Calculates the distance between two Vector2 positions
     *
     * @param p1 Position one
     * @param p2 Position two
     * @return Returns the distance between two Vector2 positions
     */
    private float getDistance(Vector2 p1, Vector2 p2)
    {
        float distance = (float) Math.sqrt(
                Math.pow((double) Math.abs(p1.x - p2.x), 2) 
                        + Math.pow((double) Math.abs(p1.y - p2.y), 2));
        return distance;
    }

    /**
     * Updated the direction based on the wall it bounced off
     *
     * @param adjustValue The value the direction has to be adjusted with to get
     * the right bouncing position based on the direction of the wall
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
     *
     * @param pos
     * @return Return an int value to tell whose goal is hit where: 0 refers to
     * player Red, 1 refers to player Blue and 2 refers to player Green.
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
            } else
            {
                //Blue goal
                playerID = 1;
            }
        } else if (pos.x > bottomGoalMinX && pos.x < bottomGoalMaxX && pos.y == 0)
        {
            //Red goal
            playerID = 0;
        }

        if (playerID == -1)
        {
            //No goal hit
            return -1;
        } else
        {
            //Goal hit, but possible block by bat
            if (checkBatBlock(playerID, pos))
            {
                //Bat blocked the puck
                printMessage("BAT BOUNCE AT PLAYER " + getColorName(playerID));
                hitBy.add(myGame.getMyPlayers().get(playerID));

                //No goal hit
                return -1;
            } else
            {
                //Bat did not block the puck
                //Goal hit of player with playerID
                printMessage("GOAL AT PLAYER " + getColorName(playerID));
                return playerID;
            }
        }
    }

    /**
     * USED FOR Bat AS LINE WHICH IS A PART OF THE CORRESPONDING GOAL LINE.
     * FOR Bat AS ROUND OBJECT GO TO checkBatBounce()
     * Checks whether a position on the goalline is blocked by the corresponding Bat.
     * @param playerID The ID of the player whose Bat should be checked
     * @param pos The position on the goalline to be checked
     * @return Returns a boolean containing whether a Bat has blocked the Puck or not
     */
    private boolean checkBatBlock(int playerID, Vector2 pos)
    {
        Vector2 batPos = myGame.getMyPlayers().get(playerID).getBatPos();

        if (playerID == 0)
        {
            //Player Red (bottom bat)
            float batMinX = batPos.x - (float) (0.5 * batWidth);
            float batMaxX = batPos.x + (float) (0.5 * batWidth);

            if (pos.x > batMinX && pos.x < batMaxX)
            {
                //Bat blocked the puck
                return true;
            } else
            {
                //Bat did not block the puck
                return false;
            }
        } else
        {
            //Player Blue or Green (side bat)
            //Correct batPosition because of inner triangle:
            float batY = batPos.y - (float) (0.5 * this.puckSize * Math.sqrt(3));

            float batMinY = batY - 
                    (float) (Math.sin(Math.toRadians(30)) * (0.5 * batWidth));
            float batMaxY = batY + 
                    (float) (Math.sin(Math.toRadians(30)) * (0.5 * batWidth));

            if (pos.y > batMinY && pos.y < batMaxY)
            {
                //Bat blocked the puck
                return true;
            } else
            {
                //Bat did not block the puck
                return false;
            }
        }
    }
    
    /**
     * USED FOR Bat AS ROUND OBJECT.
     * FOR Bat AS LINE GO TO checkBatBlock()
     * @param pos
     * @return Returns a the player whose bat the position is on.
     * Return null if the position is not on any bats.
     */
    private Vector2 checkBatBounce(Vector2 pos)
    {
        /**
         * Formulas:
         * A point (x, y) is on or within a circle if (x - Xcentre)^2 + y-Ycentre)^2 <= radius^2
         * Where Xcentre and Ycentre make up the centre of the circle and
         * radius is the radius of the circle
         */
        Vector2 batCentre;
        
        for (Player p : myGame.getMyPlayers())
        {
            batCentre = p.getBatPos();
            
            if (Math.pow(pos.x - batCentre.x, 2) + Math.pow(pos.y - batCentre.y, 2) <= Math.pow(batWidth, 2))
            {
                //Vector2 pos is on or within the circle
                
                Vector2 batBouncePosition = getIntersection(position, pos, batCentre, batWidth);
                
                if (batBouncePosition == null)
                {
                    return null;
                }
            }
        }
        return null;
    }
    
    private Vector2 getIntersectionWithCircle(Vector2 lineA, Vector2 lineB, Vector2 circleCentre, float radius)
    {
        /**
         * Formulas
         * 
         * Point on Line: y = a*x + b
         * a = (change in y) / (change in x)
         * b = y - a * x
         * 
         * Point on Circle: (x - Xcentre)^2 + y-Ycentre)^2 == radius^2
         * (y-Ycentre)^2 == radius^2 - (x-Xcentre)^2
         * y-Ycentre == SQRT(radius^2 - (x-Xcentre)^2)
         * y = SQRT(radius^2 - (x-Xcentre)^2) + ycentre
         */
        
        float a = (lineA.y - lineB.y) / (lineA.x - lineB.x);
        float b = lineA.y - a * lineA.x;
        
        float Xcentre = circleCentre.x;
        float Ycentre = circleCentre.y;
        
        /**
         * Equate the formulas:
         * 
         * y = a*x + b
         * y = SQRT(radius^2 - (x-Xcentre)^2) + ycentre
         * 
         * a*x + b = SQRT(radius^2 - (x-Xcentre)^2) + ycentre
         * a*x + (b - Ycentre) = SQRT(radius^2 - (x-Xcentre)^2)
         * a^2*x^2 + 2*(b-Ycentre)*a*x + (b - Ycentre)^2 = radius^2 - (x-Xcentre)^2
         * 
         * TODO continue maths
         */
        
        float x = 0;
        float y = 0;
        
        return null;
    }

    /**
     * Gets the corresponding color of the player with id playerID, where 0 is Red,
     * 1 is Blue and 2 is Green.
     * @param playerID The id of the player
     * @return Return a string containing the name of the color of the player whose id is playerID
     * returns "unknown" if the playerID can't be matched with a player.
     */
    private String getColorName(int playerID)
    {
//        switch (playerID)
//        {
//            case 0:
//                return "Red";
//            case 1:
//                return "Blue";
//            case 2:
//                return "Green";
//            default:
//                return "Unknown";
//        }
        String output = "Unknown";
        try
        {
            output = this.myGame.getMyPlayers().get(playerID).getColor().toString();
        }
        catch (NullPointerException ex)
        {
            System.out.println("Null pointer thrown in Puck.getColorName(): " 
                    + ex.getMessage());
            output = "Unknown";
        }
        return output;
    }

    /**
     * A private method used to print messages about the status and events of the Puck.
     * Checks whether a local boolean (printMessages) is true to print the messages.
     * Used for testing only.
     * @param message 
     */
    private void printMessage(String message)
    {
        try
        {
            if (printMessages)
            {
                System.out.println("   " + message);
            }
        }
        catch (StackOverflowError ex)
        {
            
        }
    }
    
    private String roundPosition(Vector2 pos)
    {
        int xRounded = Math.round(pos.x * 100) / 100;
        int yRounded = Math.round(pos.y * 100) / 100;
        String output = xRounded + ", " + yRounded;
        return output;
    }
    
    public void draw(GraphicsContext graphics, double width, double height)
    {
        int radius = (int)(puckSize/2);
        int x = (int)position.x + (int)width/2 - radius;
        int y = (int)height - (int)(position.y + puckSize / 2) - radius;
        graphics.fillOval(x, y, (int)puckSize, (int)puckSize);
    }
}
