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
import s32a.Client.ClientData.GameClient;
import s32a.Shared.enums.Colors;

/**
 *
 * @author Kargathia
 */
public interface IPlayer extends IPerson {

    public Colors getColor();

    public boolean moveBat(float amount)
            throws IllegalArgumentException;
}
