/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genericTests;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import lombok.Getter;

/**
 *
 * @author Kargathia
 */
public class CollectionClass implements ObservableValue {

    @Getter
    private int change = 0;

    public CollectionClass(){

    }

    public void incrementChange(){
        this.change++;
    }

    @Override
    public void addListener(ChangeListener listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeListener(ChangeListener listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getValue() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addListener(InvalidationListener listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
