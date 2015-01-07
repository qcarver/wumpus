/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.qcarver.wumpus;

/**
 *
 * @author Quinn
 */
public class Death extends Exception {

    public enum Cause {
        FELL("fell"), 
        EATEN("eaten");

    String why;    
    Cause(String why){
        this.why = why;
    }
    
    
}

    /**
     * Creates a new instance of <code>Death</code> without detail message.
     */
    public Death() {
    }

    /**
     * Constructs an instance of <code>Death</code> with the specified detail message.
     * @param msg the detail message.
     */
    public Death(String msg) {
        super(msg);
    }
}
