/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package s32a.airhockey;

import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import lombok.Getter;

/**
 *
 * @author Kargathia
 */
public class Chatbox
{
    @Getter private List<String> chat;
    
    /**
     * The chat box element.
     */
    public Chatbox()
    {
        this.chat = new ArrayList<>();
    }
    
    /**
     * Message should be preformatted "<PlayerName>[HH:MM:SS]: message"
     * @param message The message that is going to be sent to the chat box
     * @return returns true if the message was sent successfully
     * returns IllegalArgumentException if message wasn't formatted correctly
     */
    public boolean addChatMessage(String message, Person from)
    {
        StringBuilder builder = new StringBuilder();
        String time = "";
        time = String.valueOf(Calendar.getInstance().getTime()).substring(11, 19); //Gets only the time of the day out of the calendar.
        
        builder.append("<").append(from.getName()).append(">");
        builder.append("[").append(time).append("]");
        builder.append(": ").append(message);
        message = builder.toString();
        
        System.out.println(message);
        if(message.startsWith("<") && message.contains(">[") && message.contains("]:") && message.regionMatches((message.indexOf("[") + 3), ":", 0, 1) && message.regionMatches((message.indexOf("[") + 6), ":", 0, 1))
        {
            chat.add(message);
            return true;
        }
        else
        {
            return false;
        }
    }
}
