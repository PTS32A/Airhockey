/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Shared;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;

/**
 *
 * @author Kargathia
 */
public interface IPuck {

    public DoubleProperty getXPos();

    public DoubleProperty getYPos();

    public FloatProperty getSpeed();

    public float getPuckSize();

}
