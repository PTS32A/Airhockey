/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package s32a.airhockey;

import java.util.List;
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
        
    }
    
    /**
     * 
     * @param message The message that is going to be sent to the chat box
     * @return returns true if the message was sent successfully
     */
    public boolean addChatMessage(String message)
    {
        
    }
}
