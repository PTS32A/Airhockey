/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package s32a.Server;

import java.rmi.RemoteException;
import s32a.Server.Chatbox;
import s32a.Server.Person;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Kargathia
 */
public class ChatboxTest
{
    Chatbox instance;
    
    public ChatboxTest()
    {
        instance = new Chatbox();
    }
    
    @BeforeClass
    public static void setUpClass()
    {
    }
    
    @AfterClass
    public static void tearDownClass()
    {
    }
    
    @Before
    public void setUp()
    {
        try {
            Person piet = new Person("piet", (double)100);
            String mes = "Ik ben piet";
            instance.addChatMessage(mes, piet.getName());
        }
        catch (RemoteException ex) {
            Logger.getLogger(ChatboxTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on setUp: " + ex.getMessage());
        }
    }
    
    @After
    public void tearDown()
    {
    }

    /**
     * Test of addChatMessage method, of class Chatbox.
     */
    @Test
    public void testAddChatMessage()
    {
        try {
            System.out.println("addChatMessage");
            String message = "Ik ben jan";
            boolean expResult = true;
            Person from = new Person("jan", (double)50);
            boolean result = instance.addChatMessage(message, from.getName());
            assertEquals(expResult, result);
        }
        catch (RemoteException ex) {
            Logger.getLogger(ChatboxTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on testAddChatMessage: " + ex.getMessage());
        }
    }

    /**
     * Test of getChat method, of class Chatbox.
     */
    @Test
    public void testGetChat()
    {
        System.out.println("getChat");

        String expResult = "<piet>[" + String.valueOf(Calendar.getInstance().getTime()).substring(11, 19) + "]: Ik ben piet";
        String result = instance.chatProperty().get(0);
        assertEquals(expResult, result);
    }
    
}
