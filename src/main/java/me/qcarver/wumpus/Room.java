/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.qcarver.wumpus;

/**
 *
 * @author Quinn
 */
public class Room {
    private Wumpus wumpus = null;
    private boolean pit;
    private boolean gold;
    private int index;
    private boolean bumper = false;
    
    
    //in Java, this hides the default constructor..(not using it)
    private Room(){};

    public Room(int roomIndex, boolean hasaWumpus, boolean pit, boolean gold) {
        this.index = roomIndex;
        //if there is to be a Wumpus make a wumpus object for this room
        this.wumpus = (hasaWumpus)?new Wumpus():null;
        this.pit = pit;
        this.gold = gold;
    }

    public boolean hasGold() {
        return gold;
    }
    
    /**
     * no error checking.. but if there was gold, it has been taken now
     */
    public void grabGold(){
        gold = false;
    }

    public boolean hasPit() {
        return pit;
    }

    public boolean hasWumpus() {
        return this.wumpus != null;
    }
    
    public Wumpus getWumpus(){
        return this.wumpus;
    }
    
    public int getIndex(){
        return index;
    }


    /**
     * typical comparison to see if rooms are the same.. compares indexes
     * @param obj
     * @return 
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Room other = (Room) obj;
        if (this.index != other.index) {
            return false;
        }
        return true;
    }    
}
