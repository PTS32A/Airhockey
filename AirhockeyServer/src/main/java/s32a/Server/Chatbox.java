/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Server;

import s32a.Server.Person;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import s32a.Shared.IPerson;

/**
 *
 * @author Kargathia
 */
public class Chatbox {

    private ObservableList<String> chat;
    private final int chatLimit = 500;

    /**
     * Getter for the observable list used for chat
     * @return Return an observabhle list containing chat
     */
    public ObservableList<String> chatProperty() {
        return this.chat;
    }

    /**
     * constructor
     * The chat box element.
     */
    public Chatbox() {
        this.chat = FXCollections.observableArrayList(new ArrayList<String>());
    }

    /**
     * Message should be preformatted "<PlayerName>[HH:MM:SS]: message"
     *
     * @param message The message that is going to be sent to the chat box
     * @param sender The name of the person who sent the message
     * @return returns true if the message was sent successfully returns
     * IllegalArgumentException if message wasn't formatted correctly
     */
    public boolean addChatMessage(String message, String sender) {
        StringBuilder builder = new StringBuilder();
        String time = String.valueOf(Calendar.getInstance().getTime()).substring(11, 19);
        //Gets only the time of the day out of the calendar.

        builder.append("<").append(sender).append(">");
        builder.append("[").append(time).append("]");
        builder.append(": ").append(message);
        message = builder.toString();

//        System.out.println(message);
        if (message.startsWith("<") && message.contains(">[")
                && message.contains("]:")
                && message.regionMatches((message.indexOf("[") + 3), ":", 0, 1)
                && message.regionMatches((message.indexOf("[") + 6), ":", 0, 1)) {
            this.chat.add(message);
            
            // chat restricts itself to the last 500 messages
            while(this.chat.size() > chatLimit){
                this.chat.remove(0);
            }

            return true;
        } else {
            return false;
        }
    }
}
