/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.qcarver.wumpus;

/**
 *
 * @author Quinn
 */
public enum Percept {
    STENCH("a stench"), BREEZE("a breeze"), GLITTER(" a glitter"), BUMP("a bump"), SCREAM("a scream");

    @Override
    public String toString() {
        return toString;
    }
    
    private String toString;

    private Percept(String toString) {
        this.toString = toString;
    } 
}
