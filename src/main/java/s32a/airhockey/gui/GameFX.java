/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.airhockey.gui;

import java.awt.Canvas;
import java.awt.Graphics;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Setter;
import s32a.airhockey.Game;
import s32a.airhockey.Lobby;
import s32a.airhockey.Player;

/**
 *
 * @author Luke
 */
public class GameFX extends AirhockeyGUI implements Initializable
{
    @FXML Label lblName;
    @FXML Label lblDifficulty;
    @FXML Label lblScore;
    @FXML Label lblTime;
    @FXML Button btnStart;
    @FXML Button btnPause;
    @FXML Button btnQuit;
    
    private int width = 500;
    private int height = 500;
    private Game myGame;
    private Player me;
    private Canvas canvas;
    private Calendar start;
    
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        setUp();
    }
    
    public void setUp()
    {
        Lobby.getSingle().startGame(Lobby.getSingle().getCurrentPerson());
        Player current = (Player)Lobby.getSingle().getCurrentPerson();
        lblName.setText(Lobby.getSingle().getCurrentPerson().getName());
        
        lblScore.setText("0");
        lblTime.setText("00:00");
        this.myGame = new Game(current);
        lblDifficulty.setText(Float.toString(myGame.getMyPuck().getSpeed()));
        this.me = current;
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
        int score = Integer.parseInt(lblScore.getText());
        score++;
        lblScore.setText(String.valueOf(score));
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
        canvas = new Canvas();
        canvas.setSize(width, height);
        Graphics graphics = canvas.getGraphics();
        graphics.clearRect(0, 0, width, height);
        myGame.getMyPlayers().get(0).draw(graphics);
        //myGame.getMyPlayers().get(1).draw(graphics);
        //myGame.getMyPlayers().get(2).draw(graphics);
        myGame.getMyPuck().draw(graphics);
        // Need to draw edges still
    }
    
    public void startClick(Event evt)
    {
        start = Calendar.getInstance();
        myGame.run();
        Platform.runLater(new Runnable() 
        {
            @Override
            public void run() 
            {
                updateTime();
            }
        });
    }
    
    public void pauseClick(Event evt)
    {
        myGame.pauseGame(!myGame.isPaused());
    }
    
    public void quitClick(Event evt)
    {
        //Handle someone leaving here
    }
    
    private void addKeyEvents() 
    {
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
        getThisStage().addEventFilter(KeyEvent.KEY_PRESSED, keyPressed);
    }
    
    private Stage getThisStage() 
    {
        return (Stage) btnStart.getScene().getWindow();
    }
    
    
}
