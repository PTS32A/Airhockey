/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.airhockey;

import com.badlogic.gdx.math.Vector2;
import java.awt.Rectangle;
import java.util.Calendar;
import lombok.Getter;
import lombok.Setter;

/**
 * The AI class for Iteration 1 - methods for automating bat movement should go
 * here - WIP
 *
 * @author Kargathia
 */
public class Bot extends Player
{

    /**
     *
     * @param name
     * @param rating
     * @param color
     */
    public Bot(String name, double rating, Colors color)
    {
        super(name, rating, color);
    }
    
    public void moveBot()
    {
        if(!getMyGame().isPaused())
        {
            if(getMyGame().getMyPuck().getPosition().y >= getBatPos().y)
            {
                moveBat(1);
            } 
            if(getMyGame().getMyPuck().getPosition().y <= getBatPos().y)
            {
                moveBat(-1);
            } 
        }
    }
}
