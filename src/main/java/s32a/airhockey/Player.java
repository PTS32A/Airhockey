/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.airhockey;

import com.badlogic.gdx.math.Vector2;
import java.awt.Rectangle;
import java.util.Calendar;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.canvas.GraphicsContext;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Luke
 */
public class Player extends Person
{

    @Getter
    @Setter
    private Vector2 batPos;
    @Getter
    private Colors color;
    /**
     * returns score as Integerproperty
     */
    @Getter
    private IntegerProperty score;
    @Getter
    @Setter
    private boolean isStarter;
    @Getter
    private int rotation;
    @Getter
    private Vector2 goalPos;
    @Getter
    private Calendar lastAction;
    @Getter
    @Setter
    private Game myGame;
    @Getter
    private Rectangle rec;
    @Getter
    private float sideLength;
    @Getter
    private int batWidth;

    /**
     * sets both int and property values
     *
     * @param input
     */
    public void setScore(int input)
    {
        Platform.runLater(() ->
        {
            score.setValue(input);
        });
    }

    /**
     *
     * @param name provided by Person
     * @param rating provided by Person
     * @param color player color - linked to them being player 1, 2 or 3
     * retrievable from game.getGameInfo.get("nextColor")
     */
    public Player(String name, double rating, Colors color)
    {
        super(name, rating);
        this.color = color;
        this.goalPos = (Vector2) Lobby.getSingle().getAirhockeySettings().get("Goal Default");
        sideLength = (float) Lobby.getSingle().getAirhockeySettings().get("Side Length");
        batWidth = (int) (sideLength / 100 * 8);
        this.batPos = new Vector2(goalPos.x, goalPos.y);
        rec = new Rectangle((int) batPos.x, (int) batPos.y, batWidth, batWidth);
        this.score = new SimpleIntegerProperty(20);
    }

    /**
     * Adjusts the bat position a given distance to left or right Bat is unable
     * to move if game is paused
     *
     * @param amount in a direction 1 for positive movement -1 for negative
     * movement
     * @return True if all went well False otherwise, including paused game
     * Eventually throws IllegalArgumentException if amount exceeds min or max
     * value
     */
    public boolean moveBat(float amount) throws IllegalArgumentException
    {
        double direction = 0;
        double x;
        double y;
                
        // Left corner of triangle
        double aX = 0;
        double aY = 0;
        // Top corner of triangle
        double bX = sideLength / 2;
        double bY = sideLength * Math.sin(Math.toRadians(60));
        // Right corner of triangle
        double cX = sideLength;
        double cY = 0;
        
        if (myGame.isPaused())
        {
            return false;
        } 
        else
        {
            // Will reimplement this later.
//            float check = this.batPos.x + amount;
//            if (check >= sideLength / 2 || check <= -(sideLength / 2))
//            {
//                throw new IllegalArgumentException();
//            }
            if (this.getColor() == Colors.Red)
            {
                // Bottom goal
                float aX1 = (float)(aX + ((cX - aX) / 100 * 30));
                float aX2 = (float)(aX + ((cX - aX) / 100 * 70));
                
//                if (batPos.x < aX1 || batPos.x > aX2) 
//                {
//                    return false;
//                }
                if (amount == 1)
                {
                    direction = 0;
                } else
                {
                    direction = 180;
                }
            }
            if (this.getColor() == Colors.Blue)
            {
                // Left goal
                float bY1 = (float)(aY + ((bY - aY) / 100 * 30));
                float bY2 = (float)(aY + ((bY - aY) / 100 * 70));
//                if (batPos.y < bY1 || batPos.y > bY2) 
//                {
//                    return false;
//                }
                if (amount == 1)
                {
                    direction = 240;
                } else
                {
                    direction = 60;
                }
            }
            if (this.getColor() == Colors.Green)
            {
                // Right goal
                float cY1 = (float)(cY + ((bY - cY) / 100 * 30));
                float cY2 = (float)(cY + ((bY - cY) / 100 * 70));
//                if (batPos.y < cY1 || batPos.y > cY2) 
//                {
//                    return false;
//                }
                if (amount == 1)
                {
                    direction = 300;
                } else
                {
                    direction = 120;
                }
            }
            x = Math.cos(Math.toRadians(direction)) * 5;
            y = Math.sin(Math.toRadians(direction)) * 5;
            this.batPos.x += x;
            this.batPos.y += y;
            this.rec.x = (int) batPos.x;
            this.rec.y = (int) batPos.y;
            return true;
        }

    }

    /**
     * Applies an owned PowerUp Could Have functionality - disregard for now
     *
     * @return True if everything went well False otherwise - including if no
     * PowerUp was in possession
     */
    public boolean applyPowerUp()
    {
        return false;
    }

    /**
     * Adds a PowerUp, readying it to be applied Could Have functionality -
     * disregard for now
     *
     * @param powerUp
     */
    public void addPowerUp(PowerUp powerUp)
    {
        //
    }

    /**
     * Tries to pause or unpause current game
     *
     * @param isPaused the desired pausestate - false for unpause
     * @return True if pause status was changed False if unable to change pause
     * state, due to unexpected weirdness or if isPaused == game.isPaused
     */
    public boolean pauseGame(boolean isPaused)
    {
        return myGame.pauseGame(isPaused);
    }
}
