/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package s32a.Server;

import s32a.Server.Chatbox;
import s32a.Server.Person;
import java.util.Calendar;
import java.util.List;
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
        Person piet = new Person("piet", (double)100);
        String mes = "Ik ben piet";
        instance.addChatMessage(mes, piet);
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
        System.out.println("addChatMessage");
        String message = "Ik ben jan";
        boolean expResult = true;
        Person from = new Person("jan", (double)50);
        boolean result = instance.addChatMessage(message, from);
        assertEquals(expResult, result);
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
