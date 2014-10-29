/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.airhockey;

import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.Calendar;
import static java.util.Calendar.getInstance;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.Setter;

//import static org.lwjgl.Sys.getTime;
/**
 * @author Kargathia
 */
public class Game
{    
    private StringProperty difficultyProp, statusProp;
    
    @Getter
    private Chatbox myChatbox;
    @Getter
    private Puck myPuck;
    @Getter
    private List<Spectator> mySpectators;
    @Getter
    private List<Player> myPlayers;

    /**
     * includes gameID, nextColor, sideLength, gameDate
     */
    @Getter
    private HashMap gameInfo;
    @Getter
    private boolean isPaused;
    @Getter
    private int roundNo;

    @Getter
    @Setter
    private boolean continueRun;

    private int maxRounds;
    
    @Getter
    private boolean gameOver;
    
    private Timer puckTimer;
    
    /**
     * getter for difficulty as a property
     * @return 
     */
    public StringProperty difficultyProperty()
    {
        if(this.difficultyProp == null)
        {
            this.difficultyProp = new SimpleStringProperty(String.valueOf(this.myPuck.getSpeed()));
        }
        return this.difficultyProp;
    }
    
    /**
     * Returns the current game status, formatted as StringProperty
     * @return 
     */
    public StringProperty statusProperty()
    {
        if(this.statusProp == null)
        {
            this.statusProp = new SimpleStringProperty("");
        }
        
        if(this.myPlayers.size() < 3)
        {
            this.statusProp.set("Setting Up");
            return this.statusProp;
        }      
        else if (isPaused)
        {
            this.statusProp.set("Paused");
            return this.statusProp;
        }
        else if(!continueRun && !gameOver)
        {
            this.statusProp.set("Ready");
            return this.statusProp;
        }
        else
        {
            this.statusProp.set("Playing");
            return this.statusProp;
        }
    }
    
    /**
     * loads the name of player 1 in a StringProperty
     * @return 
     */
    public StringProperty player1NameProperty()
    {
        return this.playerNameProp(0);
    }
    
    /**
     * loads the name of player 2 in a StringProperty
     * @return 
     */
    public StringProperty player2NameProperty()
    {
        return this.playerNameProp(1);
    }
    
    /**
     * loads the name of Player 3 in a StringProperty
     * @return 
     */
    public StringProperty player3NameProperty()
    {
        return this.playerNameProp(2);
    }
    
    /**
     * returns playername property at given index - used by player<X>Property
     * @param index
     * @return 
     */
    private StringProperty playerNameProp(int index)
    {
        if(this.myPlayers.size() <= index)
        {
            return new SimpleStringProperty("--");
        }
        else
        {
            return this.myPlayers.get(index).nameProperty();
        }
    }

    /**
     * Calls ChatBox.addMessage(string) with a pre-formatted message - this
     * includes player name and timestamp appended to the message string
     *
     * @param message The message that is going to be sent to the chat
     * @param from The player that is sending the message
     * @return True if everything went right, and chatbox.addchatmessage
     * returned true
     */
    public boolean addChatMessage(String message, Person from)
    {
        if (message == null || from == null)
        {
            throw new IllegalArgumentException();
        }
        if (message.trim().isEmpty())
        {
            throw new IllegalArgumentException();
        }
        return myChatbox.addChatMessage(message, from);
    }

    /**
     * Constructor. Initialises sideLength, isPaused, gameID and roundNo to
     * default values gameID is a combination of starting player, and exact
     * start date/time (should be put in gameInfo)
     *
     * @param starter The player that starts the game initially
     */
    public Game(Player starter)
    {
        float defaultSpeed = 10;
        
        this.myPlayers = new ArrayList<>();
        this.mySpectators = new ArrayList<>();

        this.myPlayers.add(starter);
        setBatPosition(starter, 0);

        starter.setMyGame(this);
        starter.setStarter(true);

        this.gameInfo = new HashMap();
        this.gameInfo.put("gameID", starter.getName()
                + String.valueOf(getInstance().get(Calendar.YEAR))
                + String.valueOf(getInstance().get(Calendar.WEEK_OF_YEAR))
                + String.valueOf(getInstance().get(Calendar.DAY_OF_WEEK))
                + String.valueOf(getInstance().get(Calendar.HOUR_OF_DAY))
                + String.valueOf(getInstance().get(Calendar.MINUTE))
                + String.valueOf(getInstance().get(Calendar.SECOND)));
        this.gameInfo.put("nextColor", this.getNextColor());

        this.roundNo = 0;

        this.myPuck = new Puck(defaultSpeed, this);

        this.maxRounds = 10;
        
        this.puckTimer = new Timer();
        
        this.gameOver = false;
    }

    /**
     * Adds the provided player to the next open player slot. If player is a
     * bot, then implement it as bot (iteration 1) sets nextColor in gameID to
     * the next available color
     *
     * @param player The player that's going to be added to the active game
     * player color can be retrieved from gameID.get("nextColor")
     * @return returns true when the player was successfully added returns false
     * when game is full, or player is already a participant also returns false
     * when anything wonky happens
     */
    public boolean addPlayer(Player player)
    {
        if (player != null)
        {
            if (!myPlayers.contains(player))
            {
                if (myPlayers.size() < 3)
                {
                    this.gameInfo.put("nextColor", getNextColor());

                    myPlayers.add(player);
                    player.setMyGame(this);

                    setBatPosition(player, myPlayers.size() - 1);

                    return true;
                }
            }
        } else
        {
            throw new IllegalArgumentException();
        }
        return false;
    }

    private void setBatPosition(Player p, int playerID)
    {
        float sideLength = (float) Lobby.getSingle().getAirhockeySettings().get("Side Length");

        float x;
        float y;

        if (playerID == 0)
        {
            //Player red
            x = 0;
            y = 0;
        } else
        {
            //Player blue or green
            y = (float) (Math.tan(Math.toRadians(30)) * (0.5 * (double) sideLength));

            float middleLine = (float) Math.sqrt(Math.pow(sideLength, 2) - Math.pow(sideLength / 2, 2));

            Vector2 linePos1 = new Vector2(0, (float) middleLine);
            Vector2 linePos2;

            if (playerID == 1)
            {
                //Player blue
                linePos2 = new Vector2((float) (sideLength / 2), 0);
            } else
            {
                //Player green
                linePos2 = new Vector2((float) (-(sideLength / 2)), 0);
            }

            float a = (linePos1.y - linePos2.y) / (linePos1.x - linePos2.x);
            float b = linePos1.y - a * linePos1.x;

            //y = a*x + b
            //a*x = y - b
            //x = (y - b) / a
            x = (y - b) / a;
        }

        Vector2 batPos = new Vector2(x, y);
        p.setBatPos(batPos);
    }

    /**
     * Adds the provided player to the next
     *
     * @param spectator The spectator that's going to be added to the active
     * game
     * @return returns true when the spectator was successfully added. false
     * when the spectator was already associated with this game also false if
     * the method failed to add for any other reason
     */
    public boolean addSpectator(Spectator spectator) throws IllegalArgumentException
    {
        if (spectator != null)
        {
            for (Spectator spect : this.mySpectators)
            {
                if (spect.getName().equals(spectator.getName()))
                {
                    return false;
                }
            }
            mySpectators.add(spectator);
            return true;
        } else
        {
            throw new IllegalArgumentException("spectator is null");
        }
    }

    /**
     * removes given spectator from the list
     *
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
        } else
        {
            throw new IllegalArgumentException();
        }
        return false;
    }

    /**
     * starts the entire game - startRound() is responsible for starting a new
     * round
     *
     * @return returns true if the game was started returns false if the game
     * was unable to start for any reason
     */
    public boolean beginGame()
    {
        if (myPlayers.size() == 3)
        {
            if (roundNo == 0)
            {
                System.out.println("BEGIN GAME");
                this.startRound();
                return true;
            }
        }

        return false;
    }

    /**
     * Puckspeed functions as difficulty lever - min and max values to be
     * determined Can only be called if the game has not yet begun
     *
     * @param puckSpeed The speed of the puck
     * @return returns true if the speed has been successfully adjusted. returns
     * false if it was unable to adjust puck speed throws
     * IllegalArgumentException when given puckspeed was outside min/max values
     */
    public boolean adjustDifficulty(float puckSpeed)
    {
        if (this.roundNo == 0)
        {
            float min = 0;
            float max = 101;

            if (puckSpeed > min && puckSpeed < max)
            {
                myPuck.setSpeed(puckSpeed);
                return true;
            } else
            {
                throw new IllegalArgumentException();
            }
        } else
        {
            //Can't adjust difficulty if game has already begun
            return false;
        }
    }

    /**
     * Pauses or unpauses the game, based on input a paused game does not update
     * puck, bat, or score. chatbox remains enabled
     *
     * @param isPaused Set true if the game needs to be paused, false for
     * un-pausing
     * @return returns true if the pause change was successful. return false if
     * desired pause state == Game.isPaused
     */
    public boolean pauseGame(boolean isPaused)
    {
        if (this.isPaused != isPaused)
        {
            this.isPaused = isPaused;
            return true;
        } else
        {
            //Return false because the pause state is already this way and is therefor not changed
            return false;
        }
    }

    /**
     * Does not have to be called for every new frame or update Cycle -
     * Game.run() is responsible for that. Merely returns an updated snapshot of
     * the game, after checking whether all is as it should be
     *
     * @return Returns the game in an updated state
     */
    public Game update()
    {
        return this;
    }

    /**
     * This method cycles to a new frame (puck position, bot position)
     * ToBeImplemented
     */
    public void run()
    {
        //BEGIN PUCK MOVEMENT
        System.out.println("--BEGIN PUCK MOVEMENT");

        myPuck.clearEndData();
        
        //Continue       
        if (!isPaused && myPuck != null)
        {
            //OLD:
            //myPuck.run();
                                           
            //Timer will keep going until someone scored
            long interval = 10; //10 ms for a max 100fps
            puckTimer.scheduleAtFixedRate(myPuck, 0, interval);
        }
        
        System.out.println("--BEGIN BOT MOVEMENT");
        
        for(Player p : myPlayers)
        {
            if(p instanceof Bot)
            {
                ((Bot)p).moveBat();
            }
        }
        
        System.out.println("--END BOT MOVEMENT");
    } 

    /**
     * Starts a new round within the running game rounds are ended automatically
     * within Game.run() whenever someone scores
     */
    private void startRound()
    {
        //Start new round
        this.roundNo++;
        System.out.println("-ROUND " + roundNo);

//            //Countdown
//            try
//            {
//                //SHOULD BE IMPLEMENTED IN JAVAFX
//                Thread.sleep(1000);
//            }
//            catch (InterruptedException ex)
//            {
//                
//            }
        this.continueRun = true;
        this.isPaused = false;
            
        this.run();
    }
    
    public void endRound()
    {
        puckTimer.cancel();
        
         //END OF PUCK MOVEMENT
        System.out.println("--END PUCK MOVEMENT");

        //Start new round
        this.myPuck.resetPuck();
        
        if (roundNo < maxRounds)
        {
            startRound();
        } else
        {
            //End game
            System.out.println("END GAME");
            System.out.println("");
            this.gameOver = true;
        }
    }

    /**
     * gets the color the next player to join should be assigned
     *
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
        return (String) gameInfo.get("gameID");
    }

    /**
     * Used to set properties of Puck for customization of unit tests
     *
     * @param position the start position (Vector2) of the Puck
     * @param puckSpeed the speed of the Puck
     * @param direction the start direction of the Puck
     * @param runCount the number of times the run() method of Puck should be
     * called
     * @param maxRounds
     */
    public void customSetup(Vector2 position, float puckSpeed, 
            float direction, int runCount, int maxRounds)
    {
        //Caution: puck position and direction are reset to default after the first round has ended

        if (position != null)
        {
            if (this.myPuck.isOutsideField(position) == null)
            {
                //Inside of field
                this.myPuck.setPosition(position);
            } else
            {
                //Outside of field
                throw new IllegalArgumentException();
            }
        }

        if (puckSpeed > 0 && puckSpeed < 100)
        {
            this.myPuck.setSpeed(puckSpeed);
        }

        if (direction >= 0 && direction < 360)
        {
            this.myPuck.setDirection(direction);
        }

        if (runCount > 0 && runCount < 100)
        {
            this.myPuck.setRunCount(runCount);
        }

        if (maxRounds > 0 && maxRounds <= 10)
        {
            this.maxRounds = maxRounds;
        }
    }
}
