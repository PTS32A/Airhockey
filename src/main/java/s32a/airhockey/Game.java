/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.airhockey;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import lombok.Getter;

/**
 * NOTES:
 * - should hashmaps be added for quick searching?
 * @author Kargathia
 */
public class Game
{
    private Chatbox myChatbox;
    private Puck myPuck;
    @Getter private List<Spectator> mySpectators;
    
    /**
     * these are externally retrievable through gameInfo
     */
    @Getter private Player myPlayer1, myPlayer2, myPlayer3;
    
    //unclear whether this is useful
    @Getter private HashMap gameInfo;
    
    @Getter private int sideLength;
    @Getter private boolean isPaused;
    @Getter private final String gameID;
    @Getter private int roundNo;

    /**
     * Calls ChatBox.addMessage(string) with a pre-formatted message - 
     * this includes player name and timestamp appended to the message string
     * @param message The message that is going to be sent to the chat
     * @param from The player that is sending the message
     */
    public void addChatMessage(String message, Person from)
    {
    }

    /**
     * Constructor. Initialises sideLength, isPaused, gameID and roundNo to default values
     * gameID is a combination of starting player, and exact start date/time
     * @param starter The player that starts the game initially
     */
    public Game(Player starter)
    {
        this.myPlayer1 = starter;
        this.myPlayer2 = null;
        this.myPlayer3 = null;
        
        gameInfo = new HashMap();
    }

    /**
     * Adds the provided player to the next open player slot.
     * If player is a bot, then implement it as bot (iteration 1)
     * @param player The player that's going to be added to the active game
     * @return returns true when the player was successfully added
     * returns false when game is full, or player is already a participant
     * also returns false when anything wonky happens
     */
    public boolean addPlayer(Player player)
    {
        
    }

    /**
     * Adds the provided player to the next 
     * @param spectator The spectator that's going to be added to the active
     * game
     * @return returns true when the spectator was successfully added.
     * false when the spectator was already associated with this game
     * also false if the method failed to add for any other reason
     */ 
    public boolean addSpectator(Spectator spectator)
    {
    }

    /**
     * removes given spectator from the list
     * @param spectator The spectator that needs to be removed from the active
     * game
     * @return returns true if the spectator was successfully removed
     */
    public boolean removeSpectator(Spectator spectator)
    {
    }

    /**
     * starts the entire game - startRound() is responsible for starting a new round
     * @return returns true if the game was started
     * returns false if the game was unable to start for any reason
     */
    public boolean beginGame()
    {
    }

    /**
     * Puckspeed functions as difficulty lever - min and max values to be determined
     * Can only be called if the game has not yet begun
     * @param puckSpeed The speed of the puck
     * @return returns true if the speed has been successfully adjusted.
     * returns false if it was unable to adjust puck speed
     * throws IllegalArgumentException when given puckspeed was outside min/max values
     */
    public boolean adjustDifficulty(float puckSpeed)
    {
    }

    /**
     * Pauses or unpauses the game, based on input
     * a paused game does not update puck, bat, or score. chatbox remains enabled
     * @param isPaused Set true if the game needs to be paused, false for
     * un-pausing
     * @return returns true if the pause change was successful.
     */
    public boolean pauseGame(boolean isPaused)
    {
    }

    /**
     * dumpsters currently active game, after cleaning up internally score
     * can be called internally on player timeout, by Player when leaving, or on natural game end
     * calls Lobby.endGame(this, hasLeft)
     * Lobby.endGame(game, player) is responsible for returning players and spectators to lobby
     * Lobby.endGame() can be called directly to forcibly end game, at the cost of ending neatly
     * @param hasLeft player responsible for the game ending prematurely
     * can be null
     */
    public void endGame(Player hasLeft)
    {
    }

    /**
     * Does not have to be called for every new frame or update Cycle - Game.run()
     * is responsible for that.
     * Merely returns an updated snapshot of the game, after checking whether all is as it should be
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
     * rounds are ended automatically within Game.run() whenever someone scores
     */
    private void startRound()
    {
    }

}
