/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Objects;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import lombok.Getter;
import lombok.Setter;
import s32a.Shared.IPerson;

/**
 *
 * @author Kargathia
 */
public class Person extends UnicastRemoteObject implements IPerson {

    private transient StringProperty nameProp;
    private transient DoubleProperty ratingProp;

    @Getter
    private final String name;
    @Getter
    private double rating;
    @Getter
    @Setter
    private boolean isBot = false;

    /**
     * Garden variety setter - also sets ratingProperty
     *
     * @param input The new rating
     */
    public void setRating(double input) {

        if (input < 0) {
            throw new IllegalArgumentException();
        }

        this.rating = input;
        this.ratingProp.set(input);
    }

    /**
     * Garden variety getter, not linked through lombok for naming reasons. Used
     * by JavaFX for display.
     *
     * @return Returns the name of the person as a stringproperty
     */
    public StringProperty nameProperty() {
        return this.nameProp;
    }

    /**
     * Garden variety getter, not linked through lombok for naming reasons. Used
     * by JavaFX for display.
     *
     * @return Returns the rating of the person as a ratingproperty
     */
    public DoubleProperty ratingProperty() {
        return this.ratingProp;
    }

    /**
     * constructor
     * @param name The name of the person
     * @param rating The rating of the person
     * @throws java.rmi.RemoteException
     */
    public Person(String name, Double rating) throws RemoteException {
        if (name == null || name.equals("") || rating == null || rating < 0) {
            throw new IllegalArgumentException();
        }
        this.name = name;
        this.rating = rating;
        this.nameProp = new SimpleStringProperty(name);
        this.ratingProp = new SimpleDoubleProperty(rating);
    }

    /**
     * JVM method used for deSerializing this class - instantiates transient
     * fields.
     *
     * @param is The objectinputstream to be read
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException {
        is.defaultReadObject();
        this.nameProp = new SimpleStringProperty(this.name);
        this.ratingProp = new SimpleDoubleProperty(this.rating);
    }

    /**
     * Sets .equals to compare two instances on Name, instead of all variables.
     * NOTE: All subclasses will call this. A Player named "testey" will be
     * considered equal to a spectator, or a person named "testey".
     *
     * @param other The other object to be compared to this
     * @return
     */
    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (!(other instanceof Person)) {
            return false;
        }
        Person otherMyClass = (Person) other;
        return this.name.equals(otherMyClass.getName());
    }

    /**
     * Gets the hashcode from this name
     * @return 
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.name);
        return hash;
    }

    /**
     * toString method
     * @return Returns this name as String
     */
    @Override
    public String toString(){
        return this.name;
    }

}
