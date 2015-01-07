/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.qcarver.wumpus;

import junit.framework.TestCase;

/**
 *
 * @author qcarver
 */
public class OrientationTest extends TestCase {
    
    /**
     * Test of next method, of class Orientation.
     */
    public void testNext() {
        System.out.println("next");
        Orientation o = Orientation.values()[0];
        for (int i = 0; i < 5; i++){
            o = o.next();
            System.out.println("Orientation next test, next value is " + o);
        }
        //this is a test to see if we throw some out of bounds exceptoin.
        assert(true); //apparently we didn't
    }

    /**
     * Test of prev method, of class Orientation.
     */
    public void testPrev() {
        System.out.println("prev");
        Orientation o = Orientation.values()[Orientation.values().length-1];
        for (int i = 5; i > 0; i--){
            o = o.prev();
            System.out.println("Orientation prev test, next value is " + o);
        }
        //this is a test to see if we throw some out of bounds exceptoin.
        assert(true); //apparently we didn't
    }

    /**
     * Test of getNewDirection method, of class Orientation.
     */
    public void testGetNewDirection() {
        System.out.println("testGetOffsetBy");
        
        Orientation right = Orientation.RIGHT;
        assertEquals(Orientation.UP, right.getNewDirection(Orientation.LEFT));
        
        Orientation down = Orientation.DOWN;
        assertEquals(Orientation.UP, down.getNewDirection(Orientation.DOWN));        
    }
    
    public void testGetDelta(){
        System.out.println("testGetDelta");
        
        Orientation left = Orientation.LEFT;
        assertEquals(Orientation.DOWN, left.getDelta(Orientation.RIGHT));
        
        Orientation right = Orientation.RIGHT;
        assertEquals(Orientation.DOWN, right.getDelta(Orientation.LEFT));
        
        assertEquals(Orientation.RIGHT, right.getDelta(Orientation.UP));
        
        assertEquals(Orientation.LEFT, left.getDelta(Orientation.UP));
        
        assertEquals(Orientation.RIGHT, left.getDelta(Orientation.DOWN));
        
        assertEquals(Orientation.LEFT, right.getDelta(Orientation.DOWN));
        
        Orientation up = Orientation.UP; //ROOM            //AGENT ORIENTATION
        assertEquals(Orientation.RIGHT, up.getDelta(Orientation.LEFT));
        
        assertEquals(Orientation.UP, left.getDelta(Orientation.LEFT));
        
        Orientation down = Orientation.DOWN;
        assertEquals(Orientation.UP, down.getDelta(Orientation.DOWN));
        
        assertEquals(Orientation.UP, left.getDelta(Orientation.LEFT));
    }
    
}
