/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package s32a.airhockey;

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
    
    public ChatboxTest()
    {
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
        String message = "";
        Chatbox instance = new Chatbox();
        boolean expResult = false;
        boolean result = instance.addChatMessage(message);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getChat method, of class Chatbox.
     */
    @Test
    public void testGetChat()
    {
        System.out.println("getChat");
        Chatbox instance = new Chatbox();
        List<String> expResult = null;
        List<String> result = instance.getChat();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
