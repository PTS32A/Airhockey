/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timers;

import javafx.animation.AnimationTimer;
import s32a.airhockey.gui.GameFX;

/**
 *
 * @author Luke
 */
public class GameTimer extends AnimationTimer
{
    private final GameFX gameFX;
    private long refreshInMili;
    private long prevUpd;
    private long prevTime;
    private long lastAction;
    
    public GameTimer(GameFX gameFX)
    {
        this.gameFX = gameFX;
        this.refreshInMili = 20;
        this.prevUpd = 0;
        this.prevTime = 0;
        this.lastAction = 0;
    }

    @Override
    public void handle(long now) 
    {
        if (gameFX.isActionTaken()) 
        {
            lastAction = now;
        }
        if (now - lastAction > 60000000000L) 
        {
            gameFX.quitClick(null);
        }
        if (now - prevUpd > refreshInMili * 1000000)
        {
            gameFX.draw();
            gameFX.updateScore();
            if (now - prevTime > 1000000000)
            {
                gameFX.updateTime();
                prevTime = now;
            }
            prevUpd = now;
        }
    }
}
