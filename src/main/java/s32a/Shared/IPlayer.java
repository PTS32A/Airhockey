/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Shared;

import com.badlogic.gdx.math.Vector2;
import java.util.Calendar;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import s32a.Shared.enums.Colors;

/**
 *
 * @author Kargathia
 */
public interface IPlayer extends IPerson {

    public Colors getColor();

    public DoubleProperty getPosX();

    public void setPosX(DoubleProperty input);

    public DoubleProperty getPosY();

    public void setPosY(DoubleProperty input);

    public IntegerProperty getScore();

    public boolean isStarter();

    public void setStarter(boolean input);

    public int getRotation();

    public Vector2 getGoalPos();

    public Calendar getLastAction();

    public void setMyGame(IGame game);

    public IGame getMyGame();

    public float getSideLength();

    public int getBatWidth();

    public void setScore(int input);

    public boolean moveBat(float amount)
            throws IllegalArgumentException;
}
