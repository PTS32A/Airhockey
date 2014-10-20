/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.airhockey.gui;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import s32a.airhockey.Game;

/**
 *
 * @author Kargathia
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
    
    private Game myGame;
    private Calendar start;
    
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {

    }
    
    public void setUp(String name, String difficulty, Game game)
    {
        lblName.setText(name);
        lblDifficulty.setText(difficulty);
        lblScore.setText("0");
        lblTime.setText("00:00");
        this.myGame = game;
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
        //
    }
    
    public void startClick(Event evt)
    {
        start = Calendar.getInstance();
        myGame.run();
    }
    
    public void pauseClick(Event evt)
    {
        myGame.pauseGame(!myGame.isPaused());
    }
    
    public void quitClick(Event evt)
    {
        //Handle someone leaving here
    }
    
    private Stage getThisStage() 
    {
        return (Stage) btnStart.getScene().getWindow();
    }
    
    
}
