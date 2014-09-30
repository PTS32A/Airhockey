/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.airhockey;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/**
 *
 * @author Kargathia
 */
public class Game 
{

    @Getter private int sideLength;
    @Getter private List<String> gameInfo;
    @Getter private boolean isPaused;
    @Getter private String gameID;
    @Getter private int roundNo;
    @Getter private List<Player> myPlayers;

    /**
     *
     * @param message
     * @param from
     */
    public void addChatMessage(String message, Person from) 
    {
    }

    /**
     *
     * @param starter
     */
    public Game(Player starter) 
    {

    }

    /**
     *
     * @param player
     * @return
     */
    public boolean addPlayer(Player player) 
    {
    }

    /**
     *
     * @param spectator
     * @return
     */
    public boolean addSpectator(Spectator spectator) 
    {
    }

    /**
     *
     * @param spectator
     * @return
     */
    public boolean removeSpectator(Spectator spectator) 
    {
    }

    /**
     *
     * @return
     */
    public boolean beginGame() 
    {
    }

    /**
     *
     * @param puckSpeed
     * @return
     */
    public boolean adjustDifficulty(float puckSpeed) 
    {
    }

    /**
     *
     * @param isPaused
     * @return
     */
    public boolean pauseGame(boolean isPaused) 
    {
    }

    /**
     *
     */
    public void endGame() 
    {
    }

    /**
     *
     * @return
     */
    public Game update() 
    {
    }

    /**
     *
     */
    private void run() 
    {
    }

    /**
     *
     */
    private void startRound() 
    {
    }

}
