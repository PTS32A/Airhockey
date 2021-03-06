/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Shared;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Kargathia
 */
public interface IPerson extends Remote, Serializable {

    /**
     * Gets the name of the Person
     * @return Returns the name of the Person
     * @throws RemoteException 
     */
    public String getName()
            throws RemoteException;

    /**
     *
     * @return the person's rating
     * @throws RemoteException
     */
    public double getRating()
            throws RemoteException;
}
