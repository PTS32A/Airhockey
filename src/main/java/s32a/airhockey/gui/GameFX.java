/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.airhockey.gui;

import com.badlogic.gdx.math.Vector2;
import com.sun.prism.paint.Color;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point3D;
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
    
    private DoubleProperty width;
    private DoubleProperty height;
    private GraphicsContext graphics;
    private boolean gameEnded = false;
    private int sec = 0;
    private int min = 0;
    private @Getter boolean actionTaken = true;
    private GameTimer gameTimer;
    
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        this.width = new SimpleDoubleProperty(.0);
        this.height = new SimpleDoubleProperty(.0);
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
        this.width.bind(this.canvas.widthProperty());
        this.height.bind(Bindings.subtract(this.canvas.heightProperty(), 1));
        graphics = canvas.getGraphicsContext2D();
        graphics.clearRect(0, 0, width.doubleValue(), height.doubleValue());
        
        ChangeListener<Number> sizeChanged = new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
            {
                // REDRAW SHIT - maybe add listener to height 
            }
        };
        
        this.width.addListener(sizeChanged);
        //this.height.addListener(sizeChanged);
        
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
            graphics.clearRect(0, 0, width.doubleValue(), height.doubleValue());
            Game myGame = Lobby.getSingle().getPlayedGame();
            myGame.getMyPlayers().get(0).draw(graphics, width.doubleValue(), height.doubleValue());
            myGame.getMyPlayers().get(1).draw(graphics, width.doubleValue(), height.doubleValue());
            myGame.getMyPlayers().get(2).draw(graphics, width.doubleValue(), height.doubleValue());
            myGame.getMyPuck().draw(graphics);
        }
    }
    public void drawEdges()
    {
        // Left corner of triangle
        double aX = 0;
        double aY = height.doubleValue();
        // Top corner of triangle
        double bX = width.doubleValue()/2;
        double bY = height.doubleValue() - (width.doubleValue() * Math.sin(Math.toRadians(60)));
        // Right corner of triangle
        double cX = width.doubleValue();
        double cY = height.doubleValue();
        
        // Bottom goal
        Vector2 aXY1 = new Vector2((float)(aX + ((cX - aX)/100*30)), (float)(aY + ((cY - aY)/100*30)) - 1);
        Vector2 aXY2 = new Vector2((float)(aX + ((cX - aX)/100*70)), (float)(aY + ((cY - aY)/100*70)) - 1);
        // Left goal
        Vector2 bXY1 = new Vector2((float)(aX + ((bX - aX)/100*30)) + 1, (float)(aY + ((bY - aY)/100*30)));
        Vector2 bXY2 = new Vector2((float)(aX + ((bX - aX)/100*70)) + 1, (float)(aY + ((bY - aY)/100*70)));
        // Right goal
        Vector2 cXY1 = new Vector2((float)(cX + ((bX - cX)/100*30)) - 1, (float)(cY + ((bY - cY)/100*30)));
        Vector2 cXY2 = new Vector2((float)(cX + ((bX - cX)/100*70)) - 1, (float)(cY + ((bY - cY)/100*70)));
        
        graphics.strokeLine(aX, aY, bX, bY);
        graphics.strokeLine(bX, bY, cX, cY);
        graphics.strokeLine(cX, cY, aX, aY);
        graphics.setLineWidth(2);
        graphics.setStroke(Paint.valueOf("RED"));
        graphics.strokeLine(aXY1.x, aXY1.y, aXY2.x, aXY2.y);
        graphics.setStroke(Paint.valueOf("LIME"));
        graphics.strokeLine(bXY1.x, bXY1.y, bXY2.x, bXY2.y);
        graphics.setStroke(Paint.valueOf("BLUE"));
        graphics.strokeLine(cXY1.x, cXY1.y, cXY2.x, cXY2.y);
        graphics.setStroke(Paint.valueOf("BLACK"));
        Player p = (Player)Lobby.getSingle().getCurrentPerson();
        p.draw(graphics, width.doubleValue(), height.doubleValue());
        //double y = (height - (bX * Math.sin(Math.toRadians(30))));
        //Point3D point = new Point3D(0,0,1+y);
        //canvas.setRotationAxis(point);
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
        canvas.setRotate(120);
    }
    
    public void pauseClick(Event evt)
    {
        Lobby.getSingle().getPlayedGame().pauseGame(
                !Lobby.getSingle().getPlayedGame().isPaused());
        actionTaken = true;
        canvas.setRotate(-120);
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
        canvas.setRotate(0);
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
