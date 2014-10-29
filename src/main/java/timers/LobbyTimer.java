/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timers;

import java.sql.SQLException;
import s32a.airhockey.gui.LobbyFX;
import javafx.animation.AnimationTimer;
import s32a.airhockey.Lobby;

/**
 *
 * @author Dennis
 */
public class LobbyTimer extends AnimationTimer
{
    
    private LobbyFX lobbyFX;
    
    private long refreshRate;
    private long prevUpdate = 0;
    private Lobby lobby;

    /**
     * Starts a new timer with the default refresh rate of 1 second
     *
     * @param lobby the lobbyFX class
     */
    public LobbyTimer(LobbyFX lobby)
    {
        this(lobby, 1000);
    }

    /**
     * Starts a new timer
     *
     * @param lobby the lobbyFX class
     * @param refreshInMs The refresh rate of the timer in milliseconds
     */
    public LobbyTimer(LobbyFX lobby, int refreshInMs)
    {
        this.refreshRate = refreshInMs * 1000;
        this.lobbyFX = lobby;
        this.lobby = Lobby.getSingle();
    }
    
    @Override
    public void handle(long now)
    {
        if (now - prevUpdate >= refreshRate)
        {
            prevUpdate = now;
            //change values
            lobbyFX.setActiveGames(lobby.getActiveGames());
            try
            {
                lobbyFX.setHighscore(lobby.getRankings());
            } catch (SQLException Ex)
            {
                System.out.print(Ex.getMessage());                
            }
            lobbyFX.setChatMessages(lobby.getMychatbox().getChat());
        }
    }
}
