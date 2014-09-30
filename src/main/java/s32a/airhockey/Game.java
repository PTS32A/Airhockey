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

    @Getter
    private int sideLength;
    @Getter
    private List<String> gameInfo;
    @Getter
    private boolean isPaused;
    @Getter
    private String gameID;
    @Getter
    private int roundNo;

    /**
     *
     * @param message The message that is going to be sent to the chat
     * @param from The player that is sending the message
     */
    public void addChatMessage(String message, Person from)
    {
    }

    /**
     *
     * @param starter The player that starts the game initially
     */
    public Game(Player starter)
    {

    }

    /**
     *
     * @param player The player that's going to be added to the active game
     * @return returns true when the player was successfully added
     */
    public boolean addPlayer(Player player)
    {
    }

    /**
     *
     * @param spectator The spectator that's going to be added to the active
     * game
     * @return returns true when the spectator was successfully added.
     */
    public boolean addSpectator(Spectator spectator)
    {
    }

    /**
     *
     * @param spectator The spectator that needs to be removed from the active
     * game
     * @return returns true if the spectator was successfully removed
     */
    public boolean removeSpectator(Spectator spectator)
    {
    }

    /**
     *
     * @return returns true if the game was started
     */
    public boolean beginGame()
    {
    }

    /**
     *
     * @param puckSpeed The speed of the puck
     * @return returns true if the speed has been successfully adjusted.
     */
    public boolean adjustDifficulty(float puckSpeed)
    {
    }

    /**
     *
     * @param isPaused Set true if the game needs to be paused, false for
     * un-pausing
     * @return returns true if the pause change was successful.
     */
    public boolean pauseGame(boolean isPaused)
    {
    }

    /**
     * Method for ending the active game
     */
    public void endGame()
    {
    }

    /**
     *
     * @return Returns the game in an updated state
     */
    public Game update()
    {
    }

    /**
     *This method cycles to a new frame (puck position, bot position)
     * ToBeImplemented
     */
    private void run()
    {
    }

    /**
     *Starts a new round within the running game
     */
    private void startRound()
    {
    }

}
