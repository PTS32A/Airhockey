/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genericTests;

import java.util.HashMap;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

/**
 *
 * @author Kargathia
 */
public class TestClass {

    public TestClass(){
        System.out.println("testey");
        HashMap<String, String> testMap = new HashMap<>();

        //objectproperty
        ObjectProperty<HashMap<String, String>> testProp = new SimpleObjectProperty<>(testMap);
        testProp.addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                System.out.println("testMap changed (objectProperty): " + testMap.get("testey"));
            }
        });

        //observable hashmap
        ObservableMap testOMap = FXCollections.observableMap(testMap);
        testOMap.addListener(new MapChangeListener() {

            @Override
            public void onChanged(MapChangeListener.Change change) {
                System.out.println("testMap changed (oMap): " + testMap.get("testey"));
            }
        });

        testMap.put("testey", "test");
        testOMap.put("testey", "testO");
    }

}
