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
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.Setter;
import s32a.timers.GameTimeTask;

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
    private IntegerProperty roundNo;

    @Getter
    @Setter
    private boolean continueRun;

    private int maxRounds;

    @Getter
    private boolean gameOver;

    private Timer puckTimer;
    
    @Getter
    private StringProperty gameTime;
    
    private boolean printMessages = false;

    /**
     * Sets gameTime - non threadsafe
     * @param input 
     */
    public void setGameTime(String input)
    {
        this.gameTime.set(input);
    }
    
    /**
     * threadsafe set of roundNo
     *
     * @param input
     */
    private void setRoundNo(int input)
    {
        Platform.runLater(() ->
        {
            this.roundNo.set(input);
        });
    }

    /**
     * getter for difficulty as a property
     *
     * @return
     */
    public StringProperty difficultyProperty()
    {
        return this.difficultyProp;
    }

    /**
     * Returns the current game status, formatted as StringProperty
     *
     * @return
     */
    public StringProperty statusProperty()
    {
        if (this.statusProp == null)
        {
            this.statusProp = new SimpleStringProperty("");
        }

        if (this.gameOver)
        {
            this.statusProp.set("Game Over");
        } else if (this.myPlayers.size() < 3)
        {
            this.statusProp.set("Setting Up");
        } else if (isPaused)
        {
            this.statusProp.set("Paused");
        } else if (!continueRun)
        {
            this.statusProp.set("Ready");
        } else
        {
            this.statusProp.set("Playing");
        }
        return this.statusProp;
    }

    /**
     * loads the name of player 1 in a StringProperty
     *
     * @return
     */
    public StringProperty player1NameProperty()
    {
        return this.playerNameProp(0);
    }

    /**
     * loads the name of player 2 in a StringProperty
     *
     * @return
     */
    public StringProperty player2NameProperty()
    {
        return this.playerNameProp(1);
    }

    /**
     * loads the name of Player 3 in a StringProperty
     *
     * @return
     */
    public StringProperty player3NameProperty()
    {
        return this.playerNameProp(2);
    }

    /**
     * returns playername property at given index - used by player<X>Property
     *
     * @param index
     * @return
     */
    private StringProperty playerNameProp(int index)
    {
        if (this.myPlayers.size() <= index)
        {
            return new SimpleStringProperty("--");
        } else
        {
            return this.myPlayers.get(index).nameProperty();
        }
    }

    /**
     * Constructor. Initialises sideLength, isPaused, gameID and roundNo to
     * default values gameID is a combination of starting player, and exact
     * start date/time (should be put in gameInfo)
     *
     * @param starter The player that starts the game initially
     */
    Game(Player starter)
    {
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

        this.roundNo = new SimpleIntegerProperty(0);
        float defaultSpeed = 15;
        this.myPuck = new Puck(defaultSpeed, this);
        this.adjustDifficulty();
        this.difficultyProp = new SimpleStringProperty("speed");
        this.difficultyProp.bind(myPuck.getSpeed().asString());
        this.maxRounds = 10;
        this.puckTimer = new Timer();
        this.gameTime = new SimpleStringProperty("00:00");
        
        this.continueRun = false;
        this.gameOver = false;
        this.myChatbox = new Chatbox();
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
            throw new IllegalArgumentException("sender or message is null");
        }
        if (message.trim().isEmpty())
        {
            throw new IllegalArgumentException("message is empty");
        }
        return myChatbox.addChatMessage(message, from);
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
    boolean addPlayer(Player player)
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
                    this.adjustDifficulty();

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

    /**
     * sets initial bat position for given player
     *
     * @param p
     * @param playerID
     */
    private void setBatPosition(Player p, int playerID)
    {
        float width = (float) Lobby.getSingle().getAirhockeySettings().get("Side Length");

        float x;
        float y;
        Vector2 batPos;

        if (playerID == 0)
        {
            //Player red
            batPos = new Vector2(0,0);
        } 
        else
        {
            // Left corner of triangle
            double aX = 0;
            double aY = 0;
            // Top corner of triangle
            double bX = width / 2;
            double bY = width * Math.sin(Math.toRadians(60));
            // Right corner of triangle
            double cX = width;
            double cY = 0;
            
            double bat = width / 100 * 8;
            
            if (playerID == 1) 
            {
                batPos = new Vector2((float) (aX + ((bX - aX) / 100 * 50)) + 3,
                    (float) ((aY + ((bY - aY) / 100 * 50))) + (float)bat/2);
            }
            else
            {
                batPos = new Vector2((float) (cX + ((bX - cX) / 100 * 50))
                         - 3, (float) ((cY + ((bY - cY) / 100 * 50))) + (float)bat/2);
            }
//            //Player blue or green
//            y = (float) (Math.tan(Math.toRadians(30)) * (0.5 * (double) sideLength));
//
//            float middleLine = (float) Math.sqrt(Math.pow(sideLength, 2) - Math.pow(sideLength / 2, 2));
//
//            Vector2 linePos1 = new Vector2(0, (float) middleLine);
//            Vector2 linePos2;
//
//            if (playerID == 1)
//            {
//                //Player blue
//                linePos2 = new Vector2((float) (sideLength / 2), 0);
//            } else
//            {
//                //Player green
//                linePos2 = new Vector2((float) (-(sideLength / 2)), 0);
//            }
//
//            float a = (linePos1.y - linePos2.y) / (linePos1.x - linePos2.x);
//            float b = linePos1.y - a * linePos1.x;
//
//            //y = a*x + b
//            //a*x = y - b
//            //x = (y - b) / a
//            x = (y - b) / a;
        }
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
    boolean addSpectator(Spectator spectator) throws IllegalArgumentException
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
    boolean removeSpectator(Spectator spectator)
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
            if (roundNo.get() == 0)
            {
                printMessage("BEGIN GAME");

                //Timer will keep going until game end
                long interval = 20; //10 ms for a max 50fps
                puckTimer.scheduleAtFixedRate(myPuck, 1000, interval);
                //Starts new Timer for gameTime
                puckTimer.scheduleAtFixedRate(new GameTimeTask(this), 1000, 1000);

                if (myPlayers.get(0).getName().equals("j") || myPlayers.get(1).getName().equals("j")
                        || myPlayers.get(2).getName().equals("j"))
                {
                    this.printMessages = true;
                    this.myPuck.setPrintMessages(true);
                }

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
        if (this.roundNo.get() == 0)
        {
            float min = 10;
            float max = 40;

            if (puckSpeed >= min && puckSpeed <= max)
            {
                myPuck.setSpeed(Math.round(puckSpeed));
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
     * lets the game decide what the difficulty should be for his players
     *
     * @return true if successfully set puckspeed
     */
    public boolean adjustDifficulty()
    {
        if (myPlayers.isEmpty())
        {
            return false;
        }
        double averageRating = 0;
        for (Player p : myPlayers)
        {
            averageRating += p.getRating();
        }
        averageRating = averageRating / myPlayers.size();
        return adjustDifficulty((float) averageRating);
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
    void run()
    {
        //Continue       
        if (!isPaused && myPuck != null)
        {
            //this.myPuck.resetPuck();      -- This is moved to endRound to allow customSetup to make changes to Puck for just round 1, which is used by PuckTest only.

            //BEGIN PUCK MOVEMENT
            printMessage("--BEGIN PUCK MOVEMENT");
            this.continueRun = true; //This allows Puck to be moved
            
            printMessage("--BEGIN BOT MOVEMENT");

            

            printMessage("--END BOT MOVEMENT");
        }
    }

    /**
     * Starts a new round within the running game rounds are ended automatically
     * within Game.run() whenever someone scores
     */
    private void startRound()
    {
        //Start new round
        this.setRoundNo(this.roundNo.get() + 1);
        printMessage("-ROUND " + roundNo.get());

        this.isPaused = false;

        this.run();
    }

    void endRound()
    {
        //END OF PUCK MOVEMENT
        this.continueRun = false;
        printMessage("--END PUCK MOVEMENT");

        this.myPuck.resetPuck();

        if (roundNo.get() < maxRounds)
        {
            startRound();
        } else
        {
            //End game
            printMessage("END GAME");
            printMessage("");
            this.gameOver = true;
        }
    }

    /**
     * gets the color the next player to join should be assigned
     *
     * @return the color the next player should have, cycling red, blue, green
     * returns null if game already has three players
     */
    Colors getNextColor()
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

        if (runCount > 0 && runCount < 5000)
        {
            this.myPuck.setRunCount(runCount);
        }

        if (maxRounds > 0 && maxRounds <= 10)
        {
            this.maxRounds = maxRounds;
        }
    }
    
    private void printMessage(String message)
    {
        if (printMessages)
        {
            System.out.println(message);
        }
    }
}
