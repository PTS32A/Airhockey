/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package s32a.airhockey;

import java.util.Calendar;

import com.badlogic.gdx.math.Vector2;

import lombok.Getter;

/**
 *
 * @author Kargathia
 */
public class Player extends Person
{
    @Getter private Vector2 batPos;
    @Getter private String color;
    @Getter private int score;
    @Getter private boolean starter;
    @Getter private boolean AI;
    @Getter private int rotation;
    @Getter private Vector2 goalPos;
    @Getter private Calendar lastAction;
    /**
     * 
     * @param name
     * @param rating
     * @param batPos
     * @param color
     * @param score
     * @param starter
     * @param AI
     * @param rotation
     * @param goalPos 
     */
    public Player(String name, int rating, Vector2 batPos, String color, int score,
            boolean starter, boolean AI, int rotation, Vector2 goalPos)
    {
        super(name, rating);
        this.batPos = batPos;
        this.color = color;
        this.score = score;
        this.starter = starter;
        this.AI = AI;
        this.rotation = rotation;
        this.goalPos = goalPos;
    }
    
    public boolean moveBall(float amount)
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
