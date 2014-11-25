/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Shared;

import javafx.collections.ObservableList;

/**
 *
 * @author Kargathia
 */
public interface IChatbox {

    public ObservableList<String> chatProperty();

    public boolean addChatMessage(String message, String sender);

}
