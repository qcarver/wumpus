/*
 * This is the java analog to the C++ Orientations.hh file
 */
package me.qcarver.wumpus;

/**
 *
 * @author Quinn
 */
public enum Orientation {
    UP("Up"){
        @Override
        public Orientation prev() {
            // lo bounds checking .. for modulus type behavior
            return values()[values().length - 1]; 
        }
    }, 
    RIGHT("Right"), 
    DOWN("Down"), 
    LEFT("Left"){
        @Override
        public Orientation next() {
            // hi bounds checking .. for modulus type behavior
            return values()[0];
        }
    };
    
    @Override
    public String toString() {
        return toString;
    }
    
    private String toString;

    private Orientation(String toString) {
        this.toString = toString;    
    } 
    
    public Orientation next() {
        // No bounds checking required here, because the last instance overrides
        return values()[ordinal() + 1];
    }
    
    public Orientation prev() {
        return values()[ordinal() - 1];
    }
    
    /**
     * gets a new relative vector when changing the relative vector by a direction
     * @param newDirection
     * @return 
     */
    public Orientation getNewDirection(Orientation newDirection){
        Orientation orientation = values()[ordinal()];
        switch(newDirection){
            case LEFT: orientation = orientation.prev(); break;
            case UP: break;
            case RIGHT: orientation = orientation.next(); break;
            case DOWN: orientation = orientation.next().next(); break;
        }
        return orientation;
    }
    
    /**
     * use when 'this' orientation is the next room relative to current room
     * and the orientation passed in is the direction the agent is pointing
     * @param agentOrientation
     * @return 
     */
    public Orientation getDelta(Orientation agentOrientation){
        int steps = 0;
        
        for (;this.compareTo(agentOrientation)!=0;agentOrientation = agentOrientation.next(),steps++);
        
        return values()[steps];
    }
}
