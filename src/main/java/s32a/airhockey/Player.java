/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.airhockey;

import com.badlogic.gdx.math.Vector2;
import java.awt.Rectangle;
import java.util.Calendar;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.canvas.GraphicsContext;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Luke
 */
public class Player extends Person {

    @Getter
    private Colors color;
    @Getter
    @Setter
    private DoubleProperty posX,posY;
    /**
     * returns score as Integerproperty
     */
    @Getter
    private IntegerProperty score;
    @Getter
    @Setter
    private boolean isStarter;
    @Getter
    private int rotation;
    @Getter
    private Vector2 goalPos;
    @Getter
    private Calendar lastAction;
    @Getter
    @Setter
    private Game myGame;
    @Getter
    private Rectangle rec;
    @Getter
    private float sideLength;
    @Getter
    private int batWidth;

    /**
     * sets both int and property values
     *
     * @param input
     */
    public void setScore(int input) {
        Platform.runLater(() -> {
            score.setValue(input);
        });
    }

    /**
     *
     * @param name provided by Person
     * @param rating provided by Person
     * @param color player color - linked to them being player 1, 2 or 3
     * retrievable from game.getGameInfo.get("nextColor")
     */
    public Player(String name, double rating, Colors color) {
        super(name, rating);
        this.color = color;
        this.goalPos = (Vector2) Lobby.getSingle().getAirhockeySettings().get("Goal Default");
        sideLength = (float) Lobby.getSingle().getAirhockeySettings().get("Side Length");
        batWidth = (int) (sideLength / 100 * 8);
        this.posX = new SimpleDoubleProperty(.0);
        this.posY = new SimpleDoubleProperty(.0);
        rec = new Rectangle((int) posX.floatValue(), (int) posY.floatValue(), batWidth, batWidth);
        this.score = new SimpleIntegerProperty(20);
    }

    /**
     * Adjusts the bat position a given distance to left or right Bat is unable
     * to move if game is paused
     *
     * @param amount in a direction 1 for positive movement -1 for negative
     * movement
     * @return True if all went well False otherwise, including paused game
     * Eventually throws IllegalArgumentException if amount exceeds min or max
     * value
     */
    public boolean moveBat(float amount) throws IllegalArgumentException {
        double direction = 0;
        double x;
        double y;
        boolean out = false;

        // Left corner of triangle
        double aX = -sideLength / 2;
        double aY = 0;
        // Top corner of triangle
        double bX = 0;
        double bY = sideLength * Math.sin(Math.toRadians(60));
        // Right corner of triangle
        double cX = sideLength / 2;
        double cY = 0;

        if (!myGame.statusProperty().get().equals(GameStatus.Playing)) {
            return false;
        } else {
            // Will reimplement this later.
//            float check = this.batPos.x + amount;
//            if (check >= sideLength / 2 || check <= -(sideLength / 2))
//            {
//                throw new IllegalArgumentException();
//            }
            if (this.getColor() == Colors.Red) {
                // Bottom goal
                float aX1 = (float) (aX + ((cX - aX) / 100 * 30));
                float aX2 = (float) (aX + ((cX - aX) / 100 * 70));
                if (amount == 1) {
                    direction = 0;
                } else {
                    direction = 180;
                }
                if (posX.doubleValue() + Math.cos(Math.toRadians(direction)) * 5 < aX1
                        || posX.doubleValue() + Math.cos(Math.toRadians(direction)) * 5 > aX2) {
                    out = true;
                }
            }
            if (this.getColor() == Colors.Blue) {
                // Left goal
                float bY1 = (float) (aY + ((bY - aY) / 100 * 30));
                float bY2 = (float) (aY + ((bY - aY) / 100 * 70));

                if (amount == 1) {
                    direction = 240;
                } else {
                    direction = 60;
                }
                if (posY.doubleValue() + Math.sin(Math.toRadians(direction)) * 5 < bY1
                        || posY.doubleValue() + Math.sin(Math.toRadians(direction)) * 5 > bY2) {
                    out = true;
                }
            }
            if (this.getColor() == Colors.Green) {
                // Right goal
                float cY1 = (float) (cY + ((bY - cY) / 100 * 30));
                float cY2 = (float) (cY + ((bY - cY) / 100 * 70));
                if (amount == 1) {
                    direction = 300;
                } else {
                    direction = 120;
                }
                if (posY.doubleValue() + Math.sin(Math.toRadians(direction)) * 5 < cY1
                        || posY.doubleValue() + Math.sin(Math.toRadians(direction)) * 5 > cY2) {
                    out = true;
                }
            }
            if (out) {
                throw new IllegalArgumentException();
            } else {
                x = Math.cos(Math.toRadians(direction)) * 5;
                y = Math.sin(Math.toRadians(direction)) * 5;
            }
            this.posX.add(x);
            this.posY.add(y);
            this.rec.x = (int) posX.doubleValue();
            this.rec.y = (int) posY.doubleValue();
            return true;
        }

    }

    /**
     * Applies an owned PowerUp Could Have functionality - disregard for now
     *
     * @return True if everything went well False otherwise - including if no
     * PowerUp was in possession
     */
    public boolean applyPowerUp() {
        return false;
    }

    /**
     * Adds a PowerUp, readying it to be applied Could Have functionality -
     * disregard for now
     *
     * @param powerUp
     */
    public void addPowerUp(PowerUp powerUp) {
        //
    }
}
