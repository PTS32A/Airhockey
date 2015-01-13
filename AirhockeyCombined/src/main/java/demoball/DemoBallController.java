/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demoball;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 *
 * @author Kargathia
 */
public class DemoBallController extends DemoBallMain implements Initializable
{

    Circle demoBall, demoBall2;
    Circle centerDemoBall;
    Timer timer;
    TimerTask ballTimer;
    ArrayList<Circle> ball;
    

    @FXML
    Label lblDemoBall;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        ball = new ArrayList<>();
        // initialises a new Circle (the ball), and sets its starting coordinates to be 100,100
        // default width is 2*radius
        demoBall = new Circle(100, 100, 20);
        // adds the circle to the window it should be displayed in
        ((AnchorPane) lblDemoBall.getParent()).getChildren().add(demoBall);
        demoBall2 = new Circle(200, 100, 20);
        // adds the circle to the window it should be displayed in
        ((AnchorPane) lblDemoBall.getParent()).getChildren().add(demoBall2);
        ball.add(demoBall);
        ball.add(demoBall2);
        // Adds a new (red) ball, to demonstrate the drawing origins of the balls
        // Because X and Y coords are bound to the first ball, this ball will move along
        centerDemoBall = new Circle(100, 100, 5, Color.RED);
        ((AnchorPane) lblDemoBall.getParent()).getChildren().add(centerDemoBall);
        centerDemoBall.toFront();
        centerDemoBall.centerXProperty().bind(demoBall.centerXProperty());
        centerDemoBall.centerYProperty().bind(demoBall.centerYProperty());
        

        // instantiates the timer, and adds functionality that whenever the ball is clicked for the first time,
        // the ball starts moving, and the label will start updating itself
        timer = new Timer();
        demoBall.setOnMouseClicked((MouseEvent e) ->
        {
            if (ballTimer == null)
            {
                // schedules a new BallTimer as timertask. BallTimer is responsible for movement of given ball.
                ballTimer = new BallTimer(ball);
                timer.scheduleAtFixedRate(ballTimer, 100, 50);
                lblDemoBall.setVisible(false);
            }
        });

    }
}
