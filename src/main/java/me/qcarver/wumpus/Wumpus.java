
package me.qcarver.wumpus;

/**
 * Simple class to keep state of the Wumpus
 * @author Quinn
 */
public class Wumpus {
    //at initialization .. Wumpus is always alive
    private boolean alive = true;
    
    
    public void kill(){
        this.alive = false;
    }
    
    public boolean isAlive(){
        return alive;
    }
}
