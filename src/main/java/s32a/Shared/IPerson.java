/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Shared;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Kargathia
 */
public interface IPerson {

    public String getName();

    public boolean isBot();

    public void setBot(boolean newState);

    public void setRating(double input);

    public StringProperty nameProperty();

    public DoubleProperty ratingProperty();
}
