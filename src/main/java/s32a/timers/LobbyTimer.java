/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.timers;

import java.sql.Timestamp;
import s32a.gui.LobbyFX;
import javafx.animation.AnimationTimer;
import s32a.airhockey.Lobby;

/**
 *
 * @author Dennis
 */
public class LobbyTimer extends AnimationTimer
{
    private LobbyFX lobbyFX;
    private Lobby lobby;

    private long refreshRate;
    private long prevUpdate;
    
    /**
     * Starts a new timer
     *
     * @param lobby the lobbyFX class
     * @param refreshInMs The refresh rate of the timer in milliseconds
     */
    public LobbyTimer(LobbyFX lobby, Integer refreshInMs)
    {
        this.refreshRate = refreshInMs.longValue() * 1000000;
        this.lobbyFX = lobby;
        this.lobby = Lobby.getSingle();
    }

    @Override
    public void handle(long now)
    {
        if (now - prevUpdate > refreshRate)
        {
            prevUpdate = now;
//            java.util.Date date= new java.util.Date();
//            System.out.println(new Timestamp(date.getTime()));
//            System.out.println("lobbytimer hit");
            try
            {
                //change values
                lobbyFX.setActiveGames(lobby.getActiveGames());
                lobbyFX.setHighscore(lobby.getRankings());
            } catch (Exception ex)
            {
                System.out.println("error in LobbyTimer: " + ex.getMessage());
            }
        }
    }
}
