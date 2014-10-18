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
import lombok.Setter;
import java.util.Timer;
import java.util.TimerTask;

//import static org.lwjgl.Sys.getTime;

/**
 * NOTES:
 * - should hashmaps be added for quick searching?
 * - probably chuck a few values out of gameInfo when it's finalised
 * @author Kargathia
 */
public class Game
{
    private Chatbox myChatbox;
    @Getter private Puck myPuck;
    @Getter private List<Spectator> mySpectators;
    @Getter private List<Player> myPlayers;
    
    /**
     * includes gameID, nextColor, sideLength, gameDate
     */
    @Getter private HashMap gameInfo;   
    @Getter private boolean isPaused;
    @Getter private int roundNo;
    
    @Getter @Setter private boolean continueRun;

    /**
     * Calls ChatBox.addMessage(string) with a pre-formatted message - 
     * this includes player name and timestamp appended to the message string
     * @param message The message that is going to be sent to the chat
     * @param from The player that is sending the message
     * @return True if everything went right, and chatbox.addchatmessage returned true
     */
    public boolean addChatMessage(String message, Person from)
    {
        if (message == null || from == null)
        {
            throw new IllegalArgumentException();
        }
        if (message.isEmpty())
        {
            throw new IllegalArgumentException();
        }
        return myChatbox.addChatMessage(message, from);
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
        starter.setMyGame(this);
        
        //TODO set default speed
        //this.myPuck = new Puck(10, this);
        
        this.gameInfo = new HashMap();
        this.gameInfo.put("gameID", starter.getName() 
                + String.valueOf(getInstance().get(Calendar.YEAR)) 
                + String.valueOf(getInstance().get(Calendar.WEEK_OF_YEAR))
                + String.valueOf(getInstance().get(Calendar.DAY_OF_WEEK))
                + String.valueOf(getInstance().get(Calendar.HOUR_OF_DAY))
                + String.valueOf(getInstance().get(Calendar.MINUTE)) 
                + String.valueOf(getInstance().get(Calendar.SECOND)));
        this.gameInfo.put("nextColor", "blue");
        
        this.roundNo = 0;
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
                    //TODO use enum for color
                    this.gameInfo.put("nextColor", getNextColor());
                
                    myPlayers.add(player);
                    player.setMyGame(this);
                    return true;
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
                mySpectators.add(spectator);
                return true;
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
                mySpectators.remove(spectator);
                return true;
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
            startRound();
        }
        
        return true;
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
        float min = -100;
        float max = 100;
        
        if (puckSpeed >= min && puckSpeed <= max)
        {
            myPuck.setSpeed(puckSpeed);
            return true;
        }
        else
        {
            throw new IllegalArgumentException(); 
        }
    }

    /**
     * Pauses or unpauses the game, based on input
     * a paused game does not update puck, bat, or score. chatbox remains enabled
     * @param isPaused Set true if the game needs to be paused, false for
     * un-pausing
     * @return returns true if the pause change was successful.
     * return false if desired pause state == Game.isPaused
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
     * Does not have to be called for every new frame or update Cycle - Game.run()
     * is responsible for that.
     * Merely returns an updated snapshot of the game, after checking whether all is as it should be
     * @return Returns the game in an updated state
     */
    public Game update()
    {
        return this;
    }

    /**
     *This method cycles to a new frame (puck position, bot position)
     * ToBeImplemented
     */
    private void run()
    {
        //Continue
        int count = 0;
        while (isPaused == false && continueRun == true && count < 25)
        {
            if (myPuck != null)
            {
                //System.out.println("RUN");
                myPuck.run();
            }
            count++;
        }
        
        //Start new round
        startRound();
    }

    /**
     *Starts a new round within the running game
     * rounds are ended automatically within Game.run() whenever someone scores
     */
    private void startRound()
    {
        if (roundNo < 1)
        {
            //Start new round
            this.roundNo++;
            System.out.println("ROUND " + roundNo);
            
            //Countdown
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException ex)
            {
                
            }
            
            this.myPuck = new Puck(10, this);
            this.continueRun = true;
            this.isPaused = false;
            this.run();
        }
        else
        {
            //End game
            System.out.println("END GAME");
        }
    }
    
    /**
     * gets the color the next player to join should be assigned
     * @return the color the next player should have, cycling red, blue, green
     * returns null if game already has three players
     */
    public Colors getNextColor()
    {
        switch (myPlayers.size())
        {
            case 0:
                return Colors.Red;
            case 1:
                return Colors.Blue;
            case 2:
                return Colors.Green;
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
