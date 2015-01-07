/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.qcarver.wumpus;

/**
 *
 * @author Quinn
 */
public class Bump extends Exception {

    /**
     * Creates a new instance of <code>eBump</code> without detail message.
     */
    public Bump() {
    }

    /**
     * Constructs an instance of <code>eBump</code> with the specified detail message.
     * @param msg the detail message.
     */
    public Bump(String msg) {
        super(msg);
    }
}
