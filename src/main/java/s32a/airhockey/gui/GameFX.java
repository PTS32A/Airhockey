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
import javafx.application.Platform;
import javafx.collections.FXCollections;
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
import javafx.stage.WindowEvent;
import lombok.Getter;
import lombok.Setter;
import s32a.airhockey.*;
import timers.GameTimer;

/**
 * Notes: 
 * - add Error handling - replace logging with showdialogs in all places
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
    @FXML Button btnStopSpec;
    @FXML ListView lvChatbox;
    @FXML TextField tfChatbox;
    @FXML Canvas canvas;
    
    private int width = 0;
    private int height = 0;
    private GraphicsContext graphics;
    private boolean gameEnded = false;
    private int sec = 0;
    private int min = 0;
    private @Getter boolean actionTaken = true;
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
            btnStopSpec.setVisible(false);
        }
        else
        {
            //Spectator
            myGame = Lobby.getSingle().getSpectatedGames().get(Lobby.getSingle()
                    .getSpectatedGames().size()-1);
            lblName.setText(myGame.getMyPlayers().get(0).getName());
            lblTime.setText("00:00");
            btnStart.setVisible(false);
            btnPause.setVisible(false);
            btnQuit.setVisible(false);
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
        height = (int)canvas.getHeight() - 1;
        graphics = canvas.getGraphicsContext2D();
        graphics.clearRect(0, 0, width, height);
        drawEdges();
    }
    
    public void updateScore()
    {
        if (!Lobby.getSingle().getPlayedGame().isPaused()) 
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
        lvChatbox.setItems(FXCollections.observableArrayList(Lobby.getSingle()
                .getPlayedGame().getMyChatbox().getChat()));
    }
    
    public void draw()
    {
        if (!Lobby.getSingle().getPlayedGame().isPaused()) 
        {
            graphics.clearRect(0, 0, width, height);
            Game myGame = Lobby.getSingle().getPlayedGame();
            myGame.getMyPlayers().get(0).draw(graphics, width, height);
            myGame.getMyPlayers().get(1).draw(graphics, width, height);
            myGame.getMyPlayers().get(2).draw(graphics, width, height);
            myGame.getMyPuck().draw(graphics);
        }
    }
    public void drawEdges()
    {
        // Left corner of triangle
        double aX = 0;
        double aY = height;
        // Top corner of triangle
        double bX = width/2;
        double bY = height - (width * Math.sin(Math.toRadians(60)));
        // Right corner of triangle
        double cX = width;
        double cY = height;
        
        graphics.strokeLine(aX, aY, bX, bY);
        graphics.strokeLine(bX, bY, cX, cY);
        graphics.strokeLine(cX, cY, aX, aY);
        Player p = (Player)Lobby.getSingle().getCurrentPerson();
        p.draw(graphics, width, height);
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
        actionTaken = true;
    }
    
    public void quitClick(Event evt)
    {
        if (gameTimer != null)
        {
            gameTimer.stop();
        }
        Lobby.getSingle().endGame(Lobby.getSingle().getPlayedGame(), 
                (Player)Lobby.getSingle().getCurrentPerson());
        getThisStage().close();
    }
    
    public void sendMessage(Event evt)
    {
        Lobby.getSingle().getPlayedGame().addChatMessage(tfChatbox.getText(), 
                Lobby.getSingle().getCurrentPerson());
        tfChatbox.setText("");
    }
    
    public void stopSpectating(Event evt)
    {
        getThisStage().close();
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
                        me.moveBat(-5);
                        actionTaken = true;
                    }
                    
                } 
                else if (keyEvent.getCode() == KeyCode.D 
                        || keyEvent.getCode() == KeyCode.RIGHT) 
                {
                    if (!Lobby.getSingle().getPlayedGame().isPaused()) 
                    {
                        me.moveBat(5);
                        actionTaken = true;
                    }
                }
            }
        };
        //Stop moving
        final EventHandler<KeyEvent> keyReleased = new EventHandler<KeyEvent>() 
        {
            public void handle(final KeyEvent keyEvent) 
            {
                if (!Lobby.getSingle().getPlayedGame().isPaused()) 
                {
                    me.moveBat(0);
                    actionTaken = false;
                }
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
