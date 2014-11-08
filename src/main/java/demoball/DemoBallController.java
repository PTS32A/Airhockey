/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demoball;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/**
 *
 * @author Kargathia
 */
public class DemoBallController extends DemoBallMain implements Initializable
{

    Circle demoBall;
    Timer timer;
    TimerTask ballTimer;
    AnimationTimer animTimer;

    @FXML
    Label lblDemoBall;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        // initialises a new Circle (the ball), and sets its starting coordinates to be 100,100
        // default width is 2*radius
        demoBall = new Circle();
        demoBall.setCenterX(100.0);
        demoBall.setCenterY(100.0);
        demoBall.setRadius(20);
        // adds the circle to the window it should be displayed in
        ((AnchorPane) lblDemoBall.getParent()).getChildren().add(demoBall);

        // instantiates the timer, and adds functionality that whenever the ball is clicked for the first time,
        // the ball starts moving, and the label will start updating itself
        timer = new Timer();
        demoBall.setOnMouseClicked((MouseEvent e) ->
        {
            if (ballTimer == null)
            {
                // schedules a new BallTimer as timertask. BallTimer is responsible for movement of given ball.
                ballTimer = new BallTimer(demoBall);
                timer.scheduleAtFixedRate(ballTimer, 100, 100);

                // starts the animation timer, which will continuously update the label
                animTimer = new AnimationBallTimer(lblDemoBall);
                animTimer.start();
            }
        });

    }
}
