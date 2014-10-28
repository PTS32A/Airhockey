/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.airhockey.gui;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import lombok.Setter;
import s32a.airhockey.*;

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
public class GameFX extends AirhockeyGUI implements Initializable, Observer
{
    @FXML Label lblName;
    @FXML Label lblDifficulty;
    @FXML Label lblScoreP1;
    @FXML Label lblScoreP2;
    @FXML Label lblScoreP3;
    @FXML Label lblTime;
    @FXML Button btnStart;
    @FXML Button btnPause;
    @FXML Button btnQuit;
    @FXML Canvas canvas;
    
    private int width = 0;
    private int height = 0;
    private GraphicsContext graphics;
    private Game myGame;
    private Player me;
    private Calendar start;
    
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        setUp();
    }
    
    public void setUp()
    {
        if (Lobby.getSingle().getCurrentPerson() instanceof Player)
        {
            this.myGame = Lobby.getSingle().getPlayedGame();
            this.me = (Player)Lobby.getSingle().getCurrentPerson();
            lblName.setText(Lobby.getSingle().getCurrentPerson().getName());
            lblTime.setText("00:00");
        }
        else
        {
            //Spectator
            lblName.setText(myGame.getMyPlayers().get(0).getName());
            lblTime.setText("00:00");
        }
        lblScoreP1.setText(String.valueOf(myGame.getMyPlayers().get(0).getScore()));
        //lblScoreP2.setText(String.valueOf(myGame.getMyPlayers().get(1).getScore()));
        //lblScoreP3.setText(String.valueOf(myGame.getMyPlayers().get(2).getScore()));
        lblDifficulty.setText(Float.toString(myGame.getMyPuck().getSpeed()));
        width = (int)canvas.getWidth();
        height = (int)canvas.getHeight();
        graphics = canvas.getGraphicsContext2D();
        graphics.clearRect(0, 0, width, height);
        drawEdges();
        
        Platform.runLater(new Runnable() 
        {
            @Override
            public void run() 
            {
                draw();
            }
        });
    }
    
    public void updateScore()
    {
        int score = Integer.parseInt(lblScoreP1.getText());
        score++;
        lblScoreP1.setText(String.valueOf(score));
    }
    
    /**
     * updates the Time label to math the elapsed time since the game was started
     */
    public void updateTime()
    {
        long elapsed = Calendar.getInstance().getTimeInMillis() - start.getTimeInMillis();
        TimeUnit tU = TimeUnit.MINUTES;
        long minute = tU.convert(elapsed, TimeUnit.MILLISECONDS);
        tU = TimeUnit.SECONDS;
        long second = tU.convert(elapsed, TimeUnit.MILLISECONDS);
        lblTime.setText(minute + ":" + second);
    }
    
    public void draw()
    {
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
        addKeyEvents();
        start = Calendar.getInstance();
        //myGame.run();
        updateTime();
    }
    
    public void pauseClick(Event evt)
    {
        myGame.pauseGame(!myGame.isPaused());
    }
    
    public void quitClick(Event evt)
    {
        //Handle someone leaving here
        updateTime();
    }
    
    private void addKeyEvents() 
    {
        //Moving left or right
        final EventHandler<KeyEvent> keyPressed = new EventHandler<KeyEvent>() 
        {
            public void handle(final KeyEvent keyEvent) 
            {
                if (keyEvent.getCode() == KeyCode.A || keyEvent.getCode() == KeyCode.LEFT) 
                {
                    me.moveBat(-1);
                } 
                else if (keyEvent.getCode() == KeyCode.D || keyEvent.getCode() == KeyCode.RIGHT) 
                {
                    me.moveBat(1);
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

    @Override
    public void update(Observable o, Object arg) 
    {
        //to be updated
    }
    
    
}
