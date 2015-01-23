/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Server;

import s32a.Server.Player;
import com.badlogic.gdx.math.Vector2;
import com.sun.prism.paint.Color;
import java.awt.Rectangle;
import java.rmi.RemoteException;
import java.util.Calendar;
import lombok.Getter;
import lombok.Setter;
import s32a.Shared.enums.Colors;
import s32a.Shared.enums.GameStatus;

/**
 * The AI class for Iteration 1 - methods for automating bat movement should go
 * here - WIP
 *
 * @author Kargathia
 */
public class Bot extends Player {

    /**
     * constructor
     * @param name The name of the bot
     * @param rating The rating of the bot
     * @param color The color used by the bot in-game
     * @throws java.rmi.RemoteException
     */
    public Bot(String name, double rating, Colors color) throws RemoteException {
        super(name, rating, color);
    }

    /**
     * Moves the bot's bat around to defend it's goal from the puck
     * @throws RemoteException 
     */
    public void moveBot() throws RemoteException {
        if (((Game)getMyGame()).statusProperty().get().equals(GameStatus.Playing)) {
            if (this.getColor() == Colors.Red) {
                if (((Game)getMyGame()).getMyPuck().getPosition().get().x >= getPosX().doubleValue()) {
                    moveBat(-1);
                }
                if (((Game)getMyGame()).getMyPuck().getPosition().get().x <= getPosX().doubleValue()) {
                    moveBat(1);
                }
            } else {
                if (((Game)getMyGame()).getMyPuck().getPosition().get().y >= getPosY().doubleValue()) {
                    moveBat(-1);
                }
                if (((Game)getMyGame()).getMyPuck().getPosition().get().y <= getPosY().doubleValue()) {
                    moveBat(1);
                }
            }
        }
    }

    /**
     * Equals function
     * @param other The object this should be compared to
     * @return Returns whether the objects are equal based on this' parent equals method
     */
    @Override
    public boolean equals(Object other) {
        return super.equals(other);
    }
}
