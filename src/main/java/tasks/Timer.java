/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks;

import java.sql.SQLException;
import s32a.airhockey.gui.LobbyFX;
import javafx.animation.AnimationTimer;
import s32a.airhockey.Lobby;
import s32a.airhockey.Game;
import s32a.airhockey.Person;

/**
 *
 * @author Dennis
 */
public class Timer extends AnimationTimer
{

    private LobbyFX lobbyFX;

    private long refreshRate;
    private long prevUpdate = 0;
    private int position = 0;
    private Lobby lobby;

    /**
     * Starts a new timer with the default refresh rate of 1 second
     *
     * @param lobby the lobbyFX class
     */
    public Timer(LobbyFX lobby)
    {
        this(lobby, 1);
    }

    /**
     * Starts a new timer
     *
     * @param lobby the lobbyFX class
     * @param refreshinSec The refresh rate of the timer in seconds
     */
    public Timer(LobbyFX lobby, int refreshinSec)
    {
        this.refreshRate = refreshinSec * 100000000;
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
            for (Game activeGames : lobby.getActiveGames())
            {

            }
            try
            {
                for (Person Pranking : lobby.getRankings())
                {
                    
                }
            } catch (SQLException Ex)
            {
            }
            for(String chatMessage : lobby.getMychatbox().getChat())
            {
            }
        }
    }
}
