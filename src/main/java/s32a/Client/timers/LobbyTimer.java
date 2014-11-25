/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Client.timers;

import s32a.Client.GUI.LobbyFX;
import javafx.animation.AnimationTimer;
import s32a.Shared.ILobby;

/**
 *
 * @author Dennis
 */
public class LobbyTimer extends AnimationTimer {

    private LobbyFX lobbyFX;
    private ILobby lobby;

    private long refreshRate;
    private long prevUpdate;

    /**
     * Starts a new timer
     *
     * @param lobbyfx the lobbyFX class
     * @param lobby
     * @param refreshInMs The refresh rate of the timer in milliseconds
     */
    public LobbyTimer(LobbyFX lobbyfx, ILobby lobby, Integer refreshInMs) {
        this.refreshRate = refreshInMs.longValue() * 1000000;
        this.lobbyFX = lobbyfx;
        this.lobby = lobby;
    }

    @Override
    public void handle(long now) {
        if (now - prevUpdate > refreshRate) {
            prevUpdate = now;
            try {
                //change values
                this.lobbyFX.setGames(lobby.getActiveGames());
                this.lobbyFX.setHighScores(lobby.getRankings());
                this.lobbyFX.updatePlayerInfo();
            } catch (Exception ex) {
                System.out.println("error in LobbyTimer: " + ex.getMessage());
            }
        }
    }
}
