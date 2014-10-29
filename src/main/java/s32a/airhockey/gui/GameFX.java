/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.airhockey.gui;

import com.sun.prism.paint.Color;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import s32a.airhockey.*;
import timers.GameTimer;

/**
 * Notes: 
 * ? check whether using initialize doesn't mess things up, as it occupies a weird spot in the call order
 * x Support for spectating games is lacking
 * x Lobby.startGame returns a game, use this
 * x Lobby.startGame should be called in LobbyFX, to check whether player is allowed to start a game at all
 * x if you're using a local variable for player, consistently use him --- its used what it is needed for.
 * x beyond that, Lobby already has playedGame.
 * - add Error handling - replace logging with showdialogs in all places
 * x what if game is null, or currentPerson? Handled in lobby
 * ? any timed function for redrawing the game is absent -- wanted to make observer/observable, will discuss
 * x why are player and puck responsible for drawing objects? ask them puck / bat position, 
 *          but domain classes should not import anything javafx related --- game engine orientated programming
 * x score always starts @ 20 - see URS
 * x check whether keyevents don't propagate outside game. --- stage.addEventFilter takes care of that
 * x currently keyevents do not support continuous movement --- this will be fixed in player class
 * x Game and puck are drawing an AWT element, not javaFX
 * 
 * - overall flow should be that game and its items update their stats, 
 *      and that GUI on a regular basis (every 10-20ms or so) redraws itself based on their info
 *      - preferably initial drawing on a second graphics element, which is not shown onscreen until it's finished drawing
 * @author Luke
 */
public class GameFX extends AirhockeyGUI implements Initializable
{
    @FXML Label lblName;
    @FXML Label lblDifficulty;
    @FXML Label lblScoreP1;
    @FXML Label lblScoreP2;
    @FXML Label lblScoreP3;
    @FXML Label lblRound;
    @FXML Label lblTime;
    @FXML Button btnStart;
    @FXML Button btnPause;
    @FXML Button btnQuit;
    @FXML Canvas canvas;
    
    private int width = 0;
    private int height = 0;
    private GraphicsContext graphics;
    private boolean gameEnded = false;
    private int sec = 0;
    private int min = 0;
    private GameTimer gameTimer;
    
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        setUp();
    }
    
    public void setUp()
    {
        Game myGame;
        Player me = null;
        if (Lobby.getSingle().getCurrentPerson() instanceof Player)
        {   
            myGame = Lobby.getSingle().getPlayedGame();
            me = (Player)Lobby.getSingle().getCurrentPerson();
            lblName.setText(Lobby.getSingle().getCurrentPerson().getName());
            lblTime.setText("00:00");
        }
        else
        {
            //Spectator
            myGame = Lobby.getSingle().getSpectatedGames().get(Lobby.getSingle()
                    .getSpectatedGames().size()-1);
            lblName.setText(myGame.getMyPlayers().get(0).getName());
            lblTime.setText("00:00");
        }
        if (me == null) 
        {
            lblScoreP1.setText(String.valueOf(myGame.getMyPlayers().get(0).getScore()));
            lblScoreP2.setText(String.valueOf(myGame.getMyPlayers().get(1).getScore()));
            lblScoreP3.setText(String.valueOf(myGame.getMyPlayers().get(2).getScore())); 
        }
        else
        {
            lblScoreP1.setText("20");
            lblScoreP2.setText("20");
            lblScoreP3.setText("20");
        }
        lblDifficulty.setText(Float.toString(myGame.getMyPuck().getSpeed()));
        width = (int)canvas.getWidth();
        height = (int)canvas.getHeight();
        graphics = canvas.getGraphicsContext2D();
        graphics.clearRect(0, 0, width, height);
        drawEdges();
    }
    
    public void updateScore()
    {
        List<Player> p = Lobby.getSingle().getPlayedGame().getMyPlayers();
        int score1 = p.get(0).getScore();
        int score2 = p.get(1).getScore();
        int score3 = p.get(2).getScore();
        lblScoreP1.setText(String.valueOf(score1));
        lblScoreP2.setText(String.valueOf(score2));
        lblScoreP3.setText(String.valueOf(score3));
        lblRound.setText(Integer.toString(Lobby.getSingle().getPlayedGame().getRoundNo()));
    }
    
    /**
     * updates the Time label to math the elapsed time since the game was started
     */
    public void updateTime()
    {   
        if (!Lobby.getSingle().getPlayedGame().isPaused()) 
        {
            sec++;
            if (sec > 59) 
            {
                sec = 0;
                min++;
            }
            String second = Integer.toString(sec);
            if (sec < 10) 
            {
               second = "0" + Integer.toString(sec); 
            }
            String minute = "0" + Integer.toString(min);
            lblTime.setText(minute + ":" + second); 
        }
    }
    
    public void draw()
    {
        Game myGame = Lobby.getSingle().getPlayedGame();
        myGame.getMyPlayers().get(0).draw(graphics);
        myGame.getMyPlayers().get(1).draw(graphics);
        myGame.getMyPlayers().get(2).draw(graphics);
        myGame.getMyPuck().draw(graphics);
    }
    public void drawEdges()
    {
        //todo
    }
    
    public void startClick(Event evt)
    {
        if (Lobby.getSingle().getPlayedGame().getMyPlayers().size() == 3) 
        {
            addKeyEvents();
            gameTimer = new GameTimer(this);
            gameTimer.start(); 
            Lobby.getSingle().getPlayedGame().run();
        }
        else
        {
            super.showDialog("Warning", "Not enough players to begin game.");
        }
    }
    
    public void pauseClick(Event evt)
    {
        Lobby.getSingle().getPlayedGame().pauseGame(
                !Lobby.getSingle().getPlayedGame().isPaused());
    }
    
    public void quitClick(Event evt)
    {
        Lobby.getSingle().endGame(Lobby.getSingle().getPlayedGame(), 
                (Player) Lobby.getSingle().getCurrentPerson());
    }
    
    private void addKeyEvents() 
    {
        //Moving left or right
        Player me = (Player)Lobby.getSingle().getCurrentPerson();
        final EventHandler<KeyEvent> keyPressed = new EventHandler<KeyEvent>() 
        {
            public void handle(final KeyEvent keyEvent) 
            {
                if (keyEvent.getCode() == KeyCode.A 
                        || keyEvent.getCode() == KeyCode.LEFT) 
                {
                    if (!Lobby.getSingle().getPlayedGame().isPaused()) 
                    {
                        me.moveBat(-1);
                        System.out.println("moving");
                    }
                    
                } 
                else if (keyEvent.getCode() == KeyCode.D 
                        || keyEvent.getCode() == KeyCode.RIGHT) 
                {
                    if (!Lobby.getSingle().getPlayedGame().isPaused()) 
                    {
                        me.moveBat(1);
                    }
                }
            }
        };
        //Stop moving
        final EventHandler<KeyEvent> keyReleased = new EventHandler<KeyEvent>() 
        {
            public void handle(final KeyEvent keyEvent) 
            {
                me.moveBat(0);
            }
        };
        
        getThisStage().addEventFilter(KeyEvent.KEY_PRESSED, keyPressed);
        getThisStage().addEventFilter(KeyEvent.KEY_RELEASED, keyReleased);
    }
    
    private Stage getThisStage() 
    {
        return (Stage) lblName.getScene().getWindow();
    }
    
    
}
