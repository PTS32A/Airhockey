/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demoball;

import javafx.animation.AnimationTimer;
import javafx.scene.control.Label;

/**
 *
 * @author Kargathia
 */
public class AnimationBallTimer extends AnimationTimer
{
    Label myLabel;
    long prev;
    
    public AnimationBallTimer(Label label)
    {
        myLabel = label;
    }

    
    /**
     * Demonstrates use of an animationtimer, set to update once every 
     * 100 000 000 nanoseconds, or every 100 ms.
     * @param now 
     */
    @Override
    public void handle(long now)
    {
        if(now - prev > (long) 100000000)
        {
            myLabel.setText(String.valueOf(now));
            prev = now;
        }
    }
    
}
