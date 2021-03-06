/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Client.timers;

import java.util.TimerTask;
import s32a.Client.GUI.GameFX;

/**
 *
 * @author Luke
 */
public class AFKTimerTask extends TimerTask {

    private final GameFX gameFX;
    private long lastAction, prevTimeUpdate;

    /**
     * constructor
     * @param gameFX 
     */
    public AFKTimerTask(GameFX gameFX) {
        this.gameFX = gameFX;
        this.lastAction = 0;
    }

    /**
    * runs the timer
    */
    @Override
    public void run() {
        long now = System.currentTimeMillis();
        if (gameFX.isActionTaken()) {
            lastAction = now;
        }
        if (now - lastAction > 600000L) {
            gameFX.quitClick(null);
        }
    }
}
