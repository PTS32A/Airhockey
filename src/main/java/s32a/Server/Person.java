/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Server;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.Setter;
import s32a.Shared.IPerson;

/**
 *
 * @author Kargathia
 */
class Person implements IPerson{

    private StringProperty nameProp;
    private DoubleProperty ratingProp;

    @Getter
    private String name;
//    @Getter
//    private double rating;
    @Getter
    @Setter
    private boolean isBot = false;

    /**
     * Garden variety setter - also sets ratingProperty
     *
     * @param input
     */
    @Override
    public void setRating(double input) {
//        this.rating = input;
        this.ratingProp.set(input);
    }

    /**
     * Garden variety getter, not linked through lombok for naming reasons. Used
     * by JavaFX for display.
     *
     * @return
     */
    @Override
    public StringProperty nameProperty() {
        return this.nameProp;
    }

    /**
     * Garden variety getter, not linked through lombok for naming reasons. Used
     * by JavaFX for display.
     *
     * @return
     */
    @Override
    public DoubleProperty ratingProperty() {
        return this.ratingProp;
    }

    /**
     *
     * @param name
     * @param rating
     */
    public Person(String name, Double rating) {
        if (name == null || name.equals("") || rating == null || rating < 0) {
            throw new IllegalArgumentException();
        }
        this.name = name;
//        this.rating = rating;
        this.nameProp = new SimpleStringProperty(name);
        this.ratingProp = new SimpleDoubleProperty(rating);
    }
}
