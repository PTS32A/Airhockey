/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.timers;

import java.util.TimerTask;
import javafx.application.Platform;
import s32a.airhockey.Game;
import s32a.airhockey.Lobby;

/**
 *
 * @author Kargathia
 */
public class GameTimeTask extends TimerTask
{
    private Game myGame;
    private int min;
    private int sec;
    
    public GameTimeTask(Game game)
    {
        this.myGame = game;
        this.min = 0;
        this.sec = 0;
    }
    
    @Override
    public void run()
    {
        if (!myGame.isPaused())
        {
            sec++;
            if (sec > 59)
            {
                sec = 0;
                min++;
            }
            String second = Integer.toString(sec);
            if (sec < 10)
            {
                second = "0" + Integer.toString(sec);
            }
            String minute = Integer.toString(min);
            if (min < 10)
            {
                minute = "0" + Integer.toString(min);
            }
            final String output = minute + ":" + second;

            Platform.runLater(() ->
            {
                myGame.setGameTime(output);
            });
        }
    }  
}