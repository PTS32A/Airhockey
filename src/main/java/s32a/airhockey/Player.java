/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package s32a.airhockey;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import java.awt.Rectangle;
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
    @Getter private Colors color;
    @Getter @Setter private int score;
    @Getter @Setter private boolean isStarter;
    @Getter private int rotation;
    @Getter private Vector2 goalPos;
    @Getter private Calendar lastAction;
    @Getter @Setter private Game myGame;
    @Getter private Rectangle rec;
    
    /**
     * 
     * @param name provided by Person
     * @param rating provided by Person
     * @param color player color - linked to them being player 1, 2 or 3
     * retrievable from game.getGameInfo.get("nextColor")
     */
    public Player(String name, int rating, Colors color)
    {
        super(name, rating);
        this.color = color;
        this.goalPos = (Vector2)Lobby.getSingle().getAirhockeySettings().get("Goal Default");
        float sideLength = (float)Lobby.getSingle().getAirhockeySettings().get("Side Length");
        int batWidth = (int)(sideLength/100*8);
        this.batPos = new Vector2(goalPos.x, goalPos.y + 5);
        rec = new Rectangle((int)batPos.x, (int)batPos.y, batWidth, batWidth);
        this.score = 20;
    }
    
    /**
     * Adjusts the bat position a given distance to left or right
     * Bat is unable to move if game is paused
     * @param amount on an X-scale - negative values are allowed
     * max and min values as of yet undetermined
     * @return True if all went well
     * False otherwise, including paused game
     * Eventually throws IllegalArgumentException if amount exceeds min or max value
     */
    public boolean moveBat(float amount) throws IllegalArgumentException
    {
        if (myGame.isPaused()) 
        {
            return false;
        }
        else
        {
            this.batPos.x += amount;
            this.rec.x = (int)batPos.x;
            return true;
        }
        
    }
    
    /**
     * Applies an owned PowerUp
     * Could Have functionality - disregard for now
     * @return True if everything went well
     * False otherwise - including if no PowerUp was in possession
     */
    public boolean applyPowerUp()
    {
        return false;
    }
    
    /**
     * Adds a PowerUp, readying it to be applied
     * Could Have functionality - disregard for now
     * @param powerUp 
     */
    public void addPowerUp(PowerUp powerUp)
    {
        //
    }
    
    /**
     * Tries to pause or unpause current game
     * @param isPaused the desired pausestate - false for unpause
     * @return True if pause status was changed
     * False if unable to change pause state, due to unexpected weirdness
     * or if isPaused == game.isPaused
     */
    public boolean pauseGame(boolean isPaused)
    {
        return myGame.pauseGame(isPaused);
    }
}
