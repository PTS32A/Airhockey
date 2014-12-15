/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genericTests;

import java.util.ArrayList;
import java.util.HashMap;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

/**
 *
 * @author Kargathia
 */
public class TestClass {

    public TestClass(){
        System.out.println("testey");
        HashMap<String, String> testMap = new HashMap<>();

        //objectproperty - does not work
        ObjectProperty<HashMap<String, String>> testProp = new SimpleObjectProperty<>(testMap);
        testProp.addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                System.out.println("testMap changed (objectProperty): " + testMap.get("testey"));
            }
        });

        //observable hashmap - works
        ObservableMap testOMap = FXCollections.observableMap(testMap);
        testOMap.addListener(new MapChangeListener() {

            @Override
            public void onChanged(MapChangeListener.Change change) {
                System.out.println("testMap changed (oMap): " + testMap.get("testey"));
            }
        });

        // second observable hashmap, pointer set at the first - works
        ObservableMap testOMapCopy = testOMap;
        testOMapCopy.addListener(new MapChangeListener() {

            @Override
            public void onChanged(MapChangeListener.Change change) {
                System.out.println("testMap changed (oMapCopy)");
            }
        });

        testMap.put("testey", "test");
        testOMap.put("testey", "testO");

        // ----------------------- testing firing of change event for observableCollection

        ObservableList<CollectionClass> collectionList = FXCollections.observableArrayList(new ArrayList<>());
        CollectionClass collClass = new CollectionClass();

        collectionList.add(collClass);

        collectionList.addListener(new InvalidationListener() {

            @Override
            public void invalidated(Observable observable) {
                System.out.println("invalidationlistener invalidated -> " + String.valueOf(collClass.getChange()));
            }
        });

        collectionList.addListener(new ListChangeListener() {

            @Override
            public void onChanged(ListChangeListener.Change c) {
                System.out.println("listchangelistener notified -> " + String.valueOf(collClass.getChange()));
            }
        });

        collClass.incrementChange();
        int index = collectionList.indexOf(collClass);
        collectionList.set(index, collClass);

    }

}
