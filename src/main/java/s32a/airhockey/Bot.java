/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.airhockey;

import com.badlogic.gdx.math.Vector2;
import com.sun.prism.paint.Color;
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
            if(this.getColor() == Colors.Red)
            {
                if(getMyGame().getMyPuck().getPosition().x >= getBatPos().x)
                {
                    moveBat(-1);
                } 
                if(getMyGame().getMyPuck().getPosition().x <= getBatPos().x)
                {
                    moveBat(1);
                }
            }
            else
            {
                if(getMyGame().getMyPuck().getPosition().y >= getBatPos().y)
                {
                    moveBat(-1);
                } 
                if(getMyGame().getMyPuck().getPosition().y <= getBatPos().y)
                {
                    moveBat(1);
                } 
            }
        }
    }
}
