/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.airhockey;

import java.util.ArrayList;
import java.util.Calendar;
import static java.util.Calendar.getInstance;
import java.util.HashMap;
import java.util.List;
import lombok.Getter;
import static org.lwjgl.Sys.getTime;

/**
 * NOTES:
 * - should hashmaps be added for quick searching?
 * - probably chuck a few values out of gameInfo when it's finalised
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
    @Getter private List<Player> myPlayers;
    
    //includes gameID, nextColor, sideLength
    @Getter private HashMap gameInfo;   
    @Getter private boolean isPaused;
    @Getter private int roundNo;

    /**
     * Calls ChatBox.addMessage(string) with a pre-formatted message - 
     * this includes player name and timestamp appended to the message string
     * @param message The message that is going to be sent to the chat
     * @param from The player that is sending the message
     * @return True if everything went right, and chatbox.addchatmessage returned true
     */
    public boolean addChatMessage(String message, Person from)
    {
        String preMessage = "<" + from.getName() + ">[";
        
        //TODO add timesstamp to preMessage
        
        preMessage += "]: ";
        message = preMessage + message;
        return myChatbox.addChatMessage(message);
    }

    /**
     * Constructor. Initialises sideLength, isPaused, gameID and roundNo to default values
     * gameID is a combination of starting player, and exact start date/time 
     * (should be put in gameInfo)
     * @param starter The player that starts the game initially
     */
    public Game(Player starter)
    {
        this.myPlayers = new ArrayList<>();
        this.myPlayers.add(starter);
        
        this.gameInfo = new HashMap();
        this.gameInfo.put("gameID", starter.getName() + String.valueOf(getTime()));
        this.gameInfo.put("nextColor", "blue");
    }

    /**
     * Adds the provided player to the next open player slot.
     * If player is a bot, then implement it as bot (iteration 1)
     * sets nextColor in gameID to the next available color
     * @param player The player that's going to be added to the active game
     * player color can be retrieved from gameID.get("nextColor")
     * @return returns true when the player was successfully added
     * returns false when game is full, or player is already a participant
     * also returns false when anything wonky happens
     */
    public boolean addPlayer(Player player)
    {
        if (player != null)
        {
            if (!myPlayers.contains(player))
            {
                if (myPlayers.size() < 3)
                {
                    //TODO set nextColor to next available color
                
                    return myPlayers.add(player);
                }
            }
        }
        else
        {
            throw new IllegalArgumentException();
        }
        return false;
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
        if (spectator != null)
        {
            if (!mySpectators.contains(spectator))
            {
                return mySpectators.add(spectator);
            }
        }
        else
        {
            throw new IllegalArgumentException();
        }
        return false;
    }

    /**
     * removes given spectator from the list
     * @param spectator The spectator that needs to be removed from the active
     * game
     * @return returns true if the spectator was successfully removed
     */
    public boolean removeSpectator(Spectator spectator)
    {
        if (spectator != null)
        {
            if (mySpectators.contains(spectator))
            {
                return mySpectators.remove(spectator);
            }
        }
        else
        {
            throw new IllegalArgumentException();
        }
        return false;
    }

    /**
     * starts the entire game - startRound() is responsible for starting a new round
     * @return returns true if the game was started
     * returns false if the game was unable to start for any reason
     */
    public boolean beginGame()
    {
        if (myPlayers.size() == 3)
        {
            //TODO implement beginGame()
        }
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
        //TODO determine min and max values for puckSpeed an review the following code
        float min = 0;
        float max = 10;
        
        if (puckSpeed >= min && puckSpeed <= max)
        {
            return myPuck.setSpeed(puckSpeed);
        }
        else
        {
            throw new IllegalArgumentException(); 
        }
        
        return false;
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
        if (this.isPaused != isPaused)
        {
            this.isPaused = isPaused;
            return true;
        }
        else
        {
            //Return false because the pause state is already this way and is therefor not changed
            return false;
        }
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
        //TODO
    }

    /**
     * Does not have to be called for every new frame or update Cycle - Game.run()
     * is responsible for that.
     * Merely returns an updated snapshot of the game, after checking whether all is as it should be
     * @return Returns the game in an updated state
     */
    public Game update()
    {
        //TODO
        return this;
    }

    /**
     *This method cycles to a new frame (puck position, bot position)
     * ToBeImplemented
     */
    private void run()
    {
        //TODO
    }

    /**
     *Starts a new round within the running game
     * rounds are ended automatically within Game.run() whenever someone scores
     */
    private void startRound()
    {
        //TODO
    }
    
    /**
     * gets the color the next player to join should be assigned
     * @return the color the next player should have, cycling red, blue, green
     * returns null if game already has three players
     */
    public String getNextColor()
    {
        switch (myPlayers.size())
        {
            case 0:
                return "red";
            case 1:
                return "blue";
            case 2:
                return "green";
            default:
                return null;
        }
    }

    /**
     * 
     * @return gameID
     */
    @Override
    public String toString()
    {
        return (String)gameInfo.get("gameID");
    }
}
