/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Shared;

import com.badlogic.gdx.math.Vector2;
import java.rmi.RemoteException;
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

    /**
     * Gets the Color of the Player
     * @return Returns the Color of the Player
     * @throws RemoteException 
     */
    public Colors getColor()
            throws RemoteException;

    /**
     * Moves this Player's bat in one of two directions
     * For the Blue (left side) and Green (right side) Players, the bat moves
     * up with parameter amount set to 1 and down with parameter amount set
     * to -1. For the Red (down side) Player, the bat moves left and right
     * respectively.     * 
     * @param amount A float indication the direction of the bat to be moved
     * @return Returns a boolean indicating the success of moving the bat
     * @throws IllegalArgumentException
     * @throws RemoteException 
     */
    public boolean moveBat(float amount)
            throws IllegalArgumentException, RemoteException;
}
