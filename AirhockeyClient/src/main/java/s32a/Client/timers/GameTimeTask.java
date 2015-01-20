/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package s32a.Client.timers;

import com.badlogic.gdx.Game;
import java.rmi.RemoteException;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import s32a.Client.ClientData.GameClient;
import s32a.Shared.enums.GameStatus;

/**
 *
 * @author Bart
 */
public class GameTimeTask extends TimerTask {

    private GameClient myGame;
    private int min;
    private int sec;

    public GameTimeTask(GameClient game) {
        this.myGame = game;
        this.min = 0;
        this.sec = 0;
    }

    @Override
    public void run() {
        if (myGame.getGameStatusProperty().get() == GameStatus.Playing) {
            if (sec >= 59) {
                sec = 0;
                min++;
            } else {
                sec++;
            }
            String second = Integer.toString(sec);
            if (sec < 10) {
                second = "0" + Integer.toString(sec);
            }
            String minute = Integer.toString(min);
            if (min < 10) {
                minute = "0" + Integer.toString(min);
            }
            final String output = minute + ":" + second;
            
            myGame.setGameTimeProperty(output);
        }
    }    
}
