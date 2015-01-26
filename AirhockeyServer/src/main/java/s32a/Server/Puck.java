/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Server;

import com.badlogic.gdx.math.Vector2;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.shape.Line;
import lombok.Getter;
import lombok.Setter;
import s32a.Shared.IPlayer;
import s32a.Shared.enums.Colors;
import s32a.Shared.enums.GameStatus;
import s32a.Shared.enums.LobbySetting;

/**
 *
 * @author Kargathia
 */
public class Puck extends TimerTask {

    @Getter
    private ObjectProperty<Vector2> position; 
    @Getter
    private FloatProperty speed;
    @Getter
    private DoubleProperty xPosBat, yPosBat; 
    @Getter
    private List<IPlayer> hitBy;
    @Getter
    @Setter
    private float direction;
    @Getter
    @Setter
    boolean isMoving;
    boolean stuck = false;
    long stuckBegin = 0;
    @Getter
    private float sideLength;
    private float middleLine;
    @Getter
    private Vector2 centre;
    @Getter
    private float goalLength,
            sideGoalMinY,
            sideGoalMaxY,
            bottomGoalMinX,
            bottomGoalMaxX,
            batWidth,
            puckSize;
    @Getter
    private Line bottomBounceLine, 
            leftBounceLine,
            rightBounceLine;

    private Game myGame;

    @Getter
    private Vector2 endPosition;
    @Getter
    private float endDirection;
    @Getter
    private IPlayer endGoalHit, endBatHit;
    @Getter
    @Setter
    private int runCount, defaultRunCount;

    private Vector2 leftCorner, upperCorner, rightCorner;

    /**
     * @param input
     */
    void setSpeed(float input) {
        speed.set(input);
    }

    /**
     * initialises a game's puck position is randomised, speed is a given
     * isMoving is initialised as false
     *
     * @param speed The speed of the puck
     * @param myGame The parent game of the puck
     */
    public Puck(float speed, Game myGame) {
        Lobby lobby = null;
        try {
            lobby = Lobby.getSingle();
        } catch (RemoteException ex) {
            System.out.println("RemoteExceptoin in getSingle: " + ex.getMessage());
            Logger.getLogger(Puck.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (speed <= 0) {
            throw new IllegalArgumentException("Speed can't be negative");
        }
        if (myGame == null) {
            throw new IllegalArgumentException("myGame parameter was null");
        }

        this.speed = new SimpleFloatProperty(0f);
        this.xPosBat = new SimpleDoubleProperty(0);
        this.yPosBat = new SimpleDoubleProperty(0);
        this.speed.set(speed);
        this.hitBy = new ArrayList<>();

        this.position = new SimpleObjectProperty(new Vector2(0f, 0f));
        this.sideLength = (float) lobby.getAirhockeySettings().get(LobbySetting.SideLength);
        this.goalLength = sideLength * 0.4f;
        this.batWidth = sideLength / 100 * 8;

        float centreX = 0;
        float centreY = (float) (Math.tan(Math.toRadians(30))
                * (0.5 * (double) sideLength));

        centreX = Math.round(centreX * 100) / 100;
        centreY = Math.round(centreY * 100) / 100;

        this.centre = new Vector2(centreX, centreY);

        //Inner triangle for centre of Puck to bounce against so that 
        // the edges of the circle of the Puck will look like bouncing of the real triangle
        this.puckSize = (float) (this.sideLength * 0.04);
        this.sideLength = this.sideLength - (float) (puckSize * Math.sqrt(3));

        this.middleLine = (float) Math.sqrt(Math.pow(sideLength, 2)
                - Math.pow(sideLength / 2, 2));

        this.sideGoalMinY = (float) (this.middleLine * 0.5
                - (Math.sin(Math.toRadians(60)) * 0.5 * goalLength));
        this.sideGoalMaxY = (float) (this.middleLine * 0.5
                + (Math.sin(Math.toRadians(60)) * 0.5 * goalLength));
        this.bottomGoalMinX = (float) (-goalLength * 0.5);
        this.bottomGoalMaxX = (float) (goalLength * 0.5);

        this.bottomBounceLine = new Line(-(sideLength / 2), this.puckSize / 2,
                sideLength / 2, this.puckSize / 2);
        this.rightBounceLine = new Line(sideLength / 2, this.puckSize / 2,
                0, middleLine + this.puckSize / 2);
        this.leftBounceLine = new Line(-(sideLength / 2), this.puckSize / 2,
                0, middleLine + this.puckSize / 2);

        //System.out.println("SIDEGOAL Y-RANGE: [" + sideGoalMinY + ", " + sideGoalMaxY + "]");
        //System.out.println("BOTTOMGOAL X-RANGE: [" + bottomGoalMinX + ", " + bottomGoalMaxX + "]");
        this.isMoving = true;

        this.myGame = myGame;

        this.defaultRunCount = -1;
        this.runCount = defaultRunCount;

        this.leftCorner = new Vector2((float) (-(sideLength / 2)), this.puckSize / 2);
        this.upperCorner = new Vector2(0, (float) (middleLine + this.puckSize / 2));
        this.rightCorner = new Vector2((float) (sideLength / 2), this.puckSize / 2);

        resetPuck();
        clearEndData();
    }

    /**
     * Resets the puck position to the centre of the field and randomizes the
     * puck's direction
     */
    public void resetPuck() {
        setEndData();
        this.position.set(centre);
        this.direction = new Random().nextFloat() * 360;
    }

    /**
     * Sets the EndData variables at the end of each round. These variables
     * contain the last position and last direction of the Puck, the player
     * whose Bat last touched the Puck.
     */
    private void setEndData() {
        //DATA for after a round; used by unittests.
        this.endPosition = position.get();
        this.endDirection = direction;

        if (hitBy.size() > 0) {
            this.endBatHit = hitBy.get(hitBy.size() - 1);
        } else {
            this.endBatHit = null;
        }
    }

    /**
     * Clears the EndData variables at the start of each round.
     */
    private void clearEndData() {
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
    public void run() {
        // idle state between rounds
        if (myGame.continueRun == false) {
            return;
        }

        // Game is paused
        if (myGame.getStatusProp().get().equals(GameStatus.Paused) == true) {
            return;
        }

        if (this.runCount <= -1) {
            //runCount is not used (Default setting for the actual product)
            //Round will end only end when a goal has been scored

            //Check wheter puck is being pushed.
            updatePosition(speed.get() / 10);
        } else {
            //runCount is used (used for unittesting only)
            //Round will end when a goal has been scored OR when runCount reaches 0
            if (this.runCount == 0) {
                try {
                    myGame.endRound();
                } catch (RemoteException ex) {
                    System.out.println("remoteException in myGame.endRound(): " + ex.getMessage());
                    Logger.getLogger(Puck.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                updatePosition(speed.get() / 10);
                this.runCount--;
                System.out.println("Runcount: " + runCount);
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
    private void updatePosition(float distance) {
        if (isMoving && !stuck) {
            float oldX = position.get().x;
            float oldY = position.get().y;

            double radians = Math.toRadians((double) direction);

            float newX = oldX + (float) (Math.sin(radians) * (double) distance);
            float newY = oldY + (float) (Math.cos(radians) * (double) distance);

            Vector2 newPosition = new Vector2(newX, newY);

            //Check whether the puck is outside the field
            Vector2 bouncePosition = isOutsideField(newPosition);

            if (bouncePosition == null) {
                //Inside field

                //Check whether the puck is bouncing of a bat
                Vector2 batBouncePosition = checkBatBounce(newPosition);

                if (batBouncePosition == null) {
                    //No bat bounce
                    position.set(newPosition);
                } else {
                    //Bat bounce
                    position.set(batBouncePosition);
                }

            } else {
                try {
                    //Outside field or in collission with wall
                    position.set(bouncePosition);

                    //Detect whether a goal has been hit
                    int goalHitPlayerID = checkGoalHit(bouncePosition);

                    if (goalHitPlayerID != -1) {
                        //Player who scored
                        Player whoScored = null;
                        if (hitBy.size() > 0) {
                            whoScored = (Player) hitBy.get(hitBy.size() - 1);
                            whoScored.setScore(whoScored.getScore().get() + 1);
                            this.myGame.addChatMessage(whoScored.getName() + " Scored", "GAME");
                        }

                        //Player whose goal is hit
                        Player whoLostScore = (Player) myGame.getMyPlayers().get(goalHitPlayerID);
                        whoLostScore.setScore(whoLostScore.getScore().get() - 1);
                        this.endGoalHit = whoLostScore;

                        //End round
                        myGame.endRound();
                    } else {
                        //Continue the position with the remaining distance, calculated using bouncePosition and newPosition
                        continueUpdatePosition(bouncePosition, newPosition);
                    }
                } catch (RemoteException ex) {
                    System.out.println("RemoteException in Puck.updatePosition: " + ex.getMessage());
                }
            }
        } else if (stuck) {
            stuckRandomDirection();
        }
    }

    /**
     * Sets a random direction when the puck is stuck
     */
    public void stuckRandomDirection() {
        if (System.currentTimeMillis() - stuckBegin > 1000) {
            Random r = new Random();
            direction += r.nextInt(180);
            stuck = false;
        } else {
            position.set(new Vector2((float) xPosBat.get(), (float) yPosBat.get()));
        }
    }

    /**
     * Continues to update position after a change
     * @param bouncePosition The bounce position calculated for the next frame
     * @param newPosition  The new position calculated for the next frame
     */
    private void continueUpdatePosition(Vector2 bouncePosition, Vector2 newPosition) {
        //Position is set to bouncePosition and new direction has been calculated
        //Repeat process with remaining distance to travel
        float distance = getDistance(bouncePosition, newPosition);
        distance = Math.round(distance) - 1;
        if (distance > 0) {
            updatePosition(distance);
        }
    }

    /**
     * @param newPosition The position to be checked
     * @return the wall position that the puck bounces off returns null if the
     * puck is not in collision with any walls
     */
    public Vector2 isOutsideField(Vector2 newPosition) {
        float x = newPosition.x;
        float y = newPosition.y;

        int outside = 0;

        if (x < 0) {
            //Check outside field left
            if (y > (middleLine / (sideLength / 2)) * x + middleLine + puckSize / 2) {
                outside = -1;
            }
        } else if (x > 0) {
            //Check outside field right
            if (y > -(middleLine / (sideLength / 2)) * x + middleLine + puckSize / 2) {
                outside = 1;
            }
        }
        Random r = new Random();
        if (outside == -1) {
            //Left of field
            updateDirection(60 + r.nextInt(15));

            return getIntersection(position.get(), newPosition, leftCorner, upperCorner);
        } else if (outside == 1) {
            //Right of field

            updateDirection(-60 + r.nextInt(15));

            return getIntersection(position.get(),
                    newPosition,
                    rightCorner,
                    upperCorner);
        } else {
            if (y < (puckSize / 2)) {
                //Underneath field
                updateDirection(180 + r.nextInt(10));
                return getIntersection(position.get(),
                        newPosition, leftCorner, rightCorner);
            } else if (y > middleLine + puckSize / 2) {
                //Above field
                updateDirection(180 + r.nextInt(10));
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
            Vector2 linePos1, Vector2 linePos2) {
        /**
         * Line Formula: y = a*x + b a = (change in y) / (change in x) b = y - a
         * * x
         */
        //Line 1:
        float tempY = (oldPos.y - newPos.y);
        float tempX = (oldPos.x - newPos.x);

        float a1 = tempY / tempX;
        float b1 = oldPos.y - a1 * oldPos.x;

        //Line 2:
        tempY = (linePos1.y - linePos2.y);
        tempX = (linePos1.x - linePos2.x);

        float a2 = tempY / tempX;
        float b2 = linePos1.y - a2 * linePos1.x;

        float x;

        if (oldPos.x == newPos.x) {
            //Vertical line
            x = oldPos.x;
        } else {
            //Equate the two lines:
            x = (b2 - b1) / (a1 - a2);
        }

        //Find y:
        //y = a*x + b
        float y = a2 * x + b2;

        try {
            return new Vector2(x, y);
        } catch (Exception ex) {
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
    private float getDistance(Vector2 p1, Vector2 p2) {
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
    private void updateDirection(float adjustValue) {
        direction = -direction + adjustValue;
        correctDirection();
    }

    /**
     * Sets direction to be within 0 and 359
     */
    private void correctDirection() {
        while (direction > 359) {
            direction -= 360;
        }

        while (direction < 0) {
            direction += 360;
        }
    }

    /**
     * Calculate whether a Vector2 position is in a goal
     *
     * @param pos The position to be checked
     * @return Return an int value to tell whose goal is hit where: 0 refers to
     * player Red, 1 refers to player Blue and 2 refers to player Green. Returns
     * 0 if no goal has been hit
     */
    private int checkGoalHit(Vector2 pos) {
        int playerID = -1;

        if (pos.y >= sideGoalMinY && pos.y <= sideGoalMaxY) {
            if (pos.x < 0) {
                //Blue goal
                playerID = 1;
            } else {
                //Green goal
                playerID = 2;
            }
        } else if (pos.x >= bottomGoalMinX && pos.x <= bottomGoalMaxX & pos.y < centre.y) {
            //Red goal
            playerID = 0;
        }

        return playerID;
    }

    /**
     * USED FOR Bat AS ROUND OBJECT. FOR Bat AS LINE GO TO checkBatBlock()
     *
     * @param pos The position to be checked
     * @return Returns a the player whose bat the position is on. Return null if
     * the position is not on any bats.
     */
    private Vector2 checkBatBounce(Vector2 pos) {
        /**
         * Formulas: A point (x, y) is on or within a circle if (x - Xcentre)^2
         * + y-Ycentre)^2 <= radius^2 Where Xcentre and Ycentre make up the
         * centre of the circle and radius is the radius of the circle
         */
        Vector2 batCentre;
        double radius = batWidth / 2 + puckSize / 2;

        for (IPlayer Ip : myGame.getMyPlayers()) {
            Player p = (Player) Ip;
            batCentre = new Vector2(p.getPosX().floatValue(), p.getPosY().floatValue());

            Vector2 batBouncePosition = null;
            //Pythagoras
            double deltaX = Math.abs(position.get().x - batCentre.x);
            double deltaY = Math.abs(position.get().y - batCentre.y);
            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
            Vector2 result = null;

            if (distance < (puckSize / 2) + (p.getBatWidth() / 2)) {
                
                if (p.getColor() == Colors.Green) {
                    direction = 120;
//                    result.y = (float)p.getPosY().get() + (int)Math.round(radius * Math.sin(Math.toRadians(330)));
//                    result.x = (float)p.getPosX().get() + (int)Math.round(radius * Math.cos(Math.toRadians(330)));
                    xPosBat.bind(Bindings.add(p.getPosX(), (int)Math.round(radius * Math.sin(Math.toRadians(330)))));
                    yPosBat.bind(Bindings.add(p.getPosY(), (int)Math.round(radius * Math.cos(Math.toRadians(330)))));
                    batBouncePosition = new Vector2((float)xPosBat.get(), (float)yPosBat.get());
                } else if (p.getColor() == Colors.Blue) {
                    direction = 60;
//                    result.y = (float)p.getPosY().get() + (int)Math.round(radius * Math.sin(Math.toRadians(210)));
//                    result.x = (float)p.getPosX().get() + (int)Math.round(radius * Math.cos(Math.toRadians(210)));
                    xPosBat.bind(Bindings.add(p.getPosX(), (int)Math.round(radius * Math.sin(Math.toRadians(210)))));
                    yPosBat.bind(Bindings.add(p.getPosY(), (int)Math.round(radius * Math.cos(Math.toRadians(210)))));
                    batBouncePosition = new Vector2((float)xPosBat.get(), (float)yPosBat.get());
                } else {
                    direction = 0;
                    batBouncePosition = new Vector2((float) (p.getPosX().get()), (float) (p.getPosY().get() + radius));
                    xPosBat.bind(p.getPosX());
                    yPosBat.bind(Bindings.add(p.getPosY(), radius));
                }
                stuckBegin = System.currentTimeMillis();
                stuck = true;
            }

            if (batBouncePosition != null) {

                //Bat bounce by player p
                this.hitBy.add(p);
                return batBouncePosition;
            }
        }

        return null;
    }

    /**
     * Returns X-coord, Y-coord, and pucksize of puck
     *
     * @return Returns the puck's position as an array of float
     */
    public float[] getPuckLocation() {
        return new float[]{position.get().x, position.get().y, puckSize};
    }
}
