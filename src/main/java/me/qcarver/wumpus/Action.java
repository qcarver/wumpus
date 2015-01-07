/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.qcarver.wumpus;

import java.util.List;

/**
 *
 * @author Quinn
 */
public enum Action {
    TURN_RIGHT("Turn_Right"), TURN_LEFT("Turn_Left"), GO_FORWARD("Go_Forward"), 
    GRAB("Grab"), SHOOT("Shoot"), CLIMB("Climb");

    @Override
    public String toString() {
        return toString;
    }
    
    private String toString;

    private Action(String toString) {
        this.toString = toString;
    }     
    
    /**
     * Utility method for debugging output.. not needed in release.
     * @param actions 
     */
    public static final void printActions(List<Action> actions){
        if (actions != null) {
            String szActions = null;
            for (Action action : actions) {
                if (szActions == null) {
                    szActions = action.toString();
                } else {
                    szActions += ", ";
                    szActions += action;
                }
            }
            System.out.println("Actions are: " + szActions);
        } else {
            System.out.println("There are no actions");
        }
    }
}
