/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demoball;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/**
 *
 * @author Kargathia
 */
public class BallTimer extends TimerTask
{

    private DoubleProperty xPos,xPos2, yPos,yPos2, radius, sceneWidth, sceneHeight;
    private double xMovement, yMovement, xMovement2, yMovement2;

    /**
     * Instantiates a new BallTimer, with three properties to govern the given
     * javaFX circle shape (ball). Currently yPos is unused,
     *
     * @param ball
     */
    public BallTimer(ArrayList<Circle> ball)
    {
        // initialises the properties controlling the x/y coordinates of the ball on the ball's coordinates
        xPos = new SimpleDoubleProperty(ball.get(0).getCenterX());
        yPos = new SimpleDoubleProperty(ball.get(0).getCenterY());
        xPos2 = new SimpleDoubleProperty(ball.get(1).getCenterX());
        yPos2 = new SimpleDoubleProperty(ball.get(1).getCenterY());
        // does the same for the ball's radius
        radius = new SimpleDoubleProperty(ball.get(0).getRadius());

        // binds the actual ball's settings to the instance properties
        // whenever the instance properties (xPos, yPos, radius) are changed, 
        // the ball's properties will automatically change themselves to the new value
        ball.get(0).centerXProperty().bind(xPos);
        ball.get(0).centerYProperty().bind(yPos);
        ball.get(0).radiusProperty().bind(radius);
        ball.get(1).centerXProperty().bind(xPos2);
        ball.get(1).centerYProperty().bind(yPos2);

        // binds sceneWidth and sceneHeight property to actual width and height
        // this prevents having to refresh or hardcode screen size properties
        this.sceneHeight = new SimpleDoubleProperty();
        this.sceneHeight.bind(ball.get(0).getScene().heightProperty());
        this.sceneWidth = new SimpleDoubleProperty();
        this.sceneWidth.bind(ball.get(0).getScene().widthProperty());
        

        // sets the default x/y distance the ball will change every time run() is called
        // this value will flip between -10 and +10, depending on whether the ball should go right/down or left/up
        xMovement = 5;
        yMovement = 5;
        xMovement2 = -4;
        yMovement2 = -5;
    }

    @Override
    public void run()
    {
        // reverse x bounce movement whenever the ball would cross the right border on its current course
        if (xMovement > 0 && xPos.get() + xMovement + radius.get() > sceneWidth.get())
        {
            xMovement *= -1;
        }
        // reverses x bounce movement if ball would cross left border
        if (xMovement < 0 && xPos.get() + xMovement - radius.get() < 0)
        {
            xMovement *= -1;
        }
        // reverses y bounce movement if ball would cross bottom border
        if( yMovement > 0 && yPos.get() + yMovement + radius.get() > sceneHeight.get())
        {
            yMovement *= -1;
        }
        // reverses y bounce movement if ball would cross top border
        if (yMovement <0 && yPos.get() + yMovement - radius.get() < 0)
        {
            yMovement *= -1;
        }
        
        // reverse x bounce movement whenever the ball would cross the right border on its current course
        if (xMovement2 > 0 && xPos2.get() + xMovement2 + radius.get() > sceneWidth.get())
        {
            xMovement2 *= -1;
        }
        // reverses x bounce movement if ball would cross left border
        if (xMovement2 < 0 && xPos2.get() + xMovement2 - radius.get() < 0)
        {
            xMovement2 *= -1;
        }
        // reverses y bounce movement if ball would cross bottom border
        if( yMovement2 > 0 && yPos2.get() + yMovement2 + radius.get() > sceneHeight.get())
        {
            yMovement2 *= -1;
        }
        // reverses y bounce movement if ball would cross top border
        if (yMovement2 <0 && yPos2.get() + yMovement2 - radius.get() < 0)
        {
            yMovement2 *= -1;
        }

        // Increments x/y coordinates with (positive or negative) movement.
        // Do so in Platform.runLater in order to avoid changing a javaFX object while not on application thread
        Platform.runLater(() ->
        {
            xPos.set(xPos.get() + xMovement);
            yPos.set(yPos.get() + yMovement);
            //xPos2.set(xPos2.get() + xMovement2);
            //yPos2.set(yPos2.get() + yMovement2);
            checkCollision();
        });
    }
    
    public void checkCollision()
    {
        //Pythagoras
        double deltaX = Math.abs(xPos.get() - xPos2.get());
        double deltaY = Math.abs(yPos.get() - yPos2.get());
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        
        //radius is same for both for this example
        if (distance < (radius.get()) + (radius.get())) {
            
            //4 is "mass" both are equal
            //8 is combination of both masses
//            double xSpeed = (xMovement * (4 - 4) + (2 * 4 * xMovement2)) / 8;
//
//            double xSpeed2 = (xMovement2 * (4 - 4) + (2 * 4 * xMovement)) / 8;
//
//            double ySpeed = (yMovement * (4 - 4) + (2 * 4 * yMovement2)) / 8;
//
//            double ySpeed2 = (yMovement2 * (4 - 4) + (2 * 4 * yMovement)) / 8;
//
//            xMovement = xSpeed;
//            yMovement = ySpeed;
//            xMovement2 = xSpeed2;
//            yMovement2 = ySpeed2;

            double degrees = Math.toDegrees(Math.atan2
                (yPos2.get() - yPos.get(), xPos2.get() - xPos.get()));

            xMovement = Math.cos(Math.toRadians((int) (180+degrees))) * 10;
            yMovement = Math.sin(Math.toRadians((int) (180+degrees))) * 10;
            
//            double degrees2 = Math.toDegrees(Math.atan2
//                (yPos.get() - yPos2.get(), xPos.get() - xPos2.get()));
//
//            xMovement2 = Math.cos(Math.toRadians((int) (180+degrees2))) * 10;
//            yMovement2 = Math.sin(Math.toRadians((int) (180+degrees2))) * 10;
        }
    }
}
