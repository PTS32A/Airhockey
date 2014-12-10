/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Server;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import s32a.Server.Person;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Luke
 */
public class PersonTest 
{
    Person person;
    public PersonTest()
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
        try {
            person = new Person("Test", (double)15);
        }
        catch (RemoteException ex) {
            Logger.getLogger(PersonTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on initializing new Person: " + ex.getMessage());
        }
    }
    
    @After
    public void tearDown()
    {
    }

    @Test
    (expected = IllegalArgumentException.class)
    public void testNameEmptyEx()
    {
        try {
            person = new Person("", (double)15);
        }
        catch (RemoteException ex) {
            Logger.getLogger(PersonTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on initializing new Person: " + ex.getMessage());
        }
    }
    
    @Test
    (expected = IllegalArgumentException.class)
    public void testNameNullEx()
    {
        try {
            person = new Person(null, (double)15);
        }
        catch (RemoteException ex) {
            Logger.getLogger(PersonTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on initializing new Person: " + ex.getMessage());
        }
    }
    
    @Test
    (expected = IllegalArgumentException.class)
    public void testRatingNegativeEx()
    {
        try {
            person = new Person("Test", (double)-1);
        }
        catch (RemoteException ex) {
            Logger.getLogger(PersonTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on initializing new Person: " + ex.getMessage());
        }
    }
    
    @Test
    (expected = IllegalArgumentException.class)
    public void testRatingNullEx()
    {
        try {
            person = new Person("Test", null);
        }
        catch (RemoteException ex) {
            Logger.getLogger(PersonTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RemoteException on initializing new Person: " + ex.getMessage());
        }
    }
    
    @Test
    (expected = IllegalArgumentException.class)
    public void testSetRatingNegativeEx()
    {
        person.setRating((double)-1);
    }
    
    @Test
    public void testProperties()
    {
        Assert.assertTrue("Name is incorrect", person.getName().equals("Test"));
        Assert.assertTrue("Rating is incorrect", person.getRating() == 15);
        Assert.assertTrue("NameProperty is incorrect", person.getName() == person.nameProperty().getValue());
        Assert.assertTrue("RatingProperty is incorrect", person.getRating() == person.ratingProperty().getValue());
    }
    
    @Test
    public void testSetRating()
    {
        person.setRating((double)16);
        
        Assert.assertTrue("Rating not set properly", person.getRating() == 16);
        Assert.assertTrue("RatingProp not set properly", person.ratingProperty().getValue() == (double)16);
    }
}
