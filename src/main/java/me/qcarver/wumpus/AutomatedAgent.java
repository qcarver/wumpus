/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.qcarver.wumpus;

import java.util.HashSet;
import java.util.Set;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author Quinn
 */
public class AutomatedAgent extends Agent {
    //set to keep track of pitted rooms

    //set of rooms which do not have pits in them
    Set<Room> pitlessRooms = new HashSet<Room>();
    //set of rooms which are suspected to be pit rooms
    Set<Room> pittishRooms = new HashSet<Room>();
    //set to keep track of not wumpus rooms
    Set<Room> wumplessRooms = new HashSet<Room>();
    //set of rooms which are suspected to be WumpusRooms
    Set<Room> wumpishRooms = new HashSet<Room>();
    //set of safe rooms
    //Set<Room> safeRooms = new HashSet<Room>();
    //plans of action which are cached ahead to keep state
    Deque<Action> actions = new ArrayDeque<Action>();
    Room lastRoom = null;
    //wumpus is dead
    boolean isWumpusDead = false;
    //
    Random randomGenerator = new Random();
    //represents newMoves a direct path that is longer than possible
    final private int infinity = cave.rooms.size()*3+1;
    

    //keeps state of what the agent is doing
    private enum Plan {

        QUEST, ESCAPE, HUNT
    };
    Plan plan = Plan.QUEST;

    //hide
    private AutomatedAgent() {
    }

    public AutomatedAgent(Cave cave, int arrows) {
        super(cave, arrows);
    }

    public void move() throws Death {
        if (actions.isEmpty()) {
            switch (plan) {
                case QUEST:
                    pushQuestActions();
                    break;
                case ESCAPE:
                    pushEscapeActions();
                    break;
                case HUNT:
                    pushHuntActions();
                    break;
            }
        }
        System.out.println("mode: " + plan + " action: " + actions.peekFirst());
        lastRoom = currRoom;
        move(actions.removeFirst());
    }

    private void pushQuestActions() throws Death {
        getPercepts();
        markRooms();

        //Reasons to change plan.. we found gold or are getting nowehere
        if (percepts.contains(Percept.GLITTER)) {
            actions.clear();
            actions.push(Action.GRAB);
            plan = Plan.ESCAPE; //need to bail pushActionsForEntryRoom
        } else if (score < scoreForDeath / 2) {
            plan = Plan.ESCAPE;
            System.out.println("Can't get the gold, time to cut losses");
        } else {
            pushActionsToSafeRoom();
        }
    }

    private void pushActionsToSafeRoom() {
        Set<Room> safeUnvisitedRooms = getSafeRooms();
        safeUnvisitedRooms.removeAll(this.visitedRooms);

        Deque<Action> actionsToSafeRoom =
                getActionsForPath(safeUnvisitedRooms);
        actions.addAll(actionsToSafeRoom);

        //there are no safe unvisited rooms
        if (actions.isEmpty()) {
            //chicken out
            plan = Plan.ESCAPE;
            pushEscapeActions();
        }
    }

    private void pushActionsToEntryRoom() {
        //we just need a small set with one item for the getActionsToPath fxn
        Set<Room> entryRoom = new HashSet<Room>();
        entryRoom.add(cave.getEntryRoom());

        Deque<Action> actionsToEntryRoom =
                getActionsForPath(entryRoom);
        //todo..see how addAll adds to Deque
        actions.addAll(actionsToEntryRoom);
    }
   
    private Deque<Action> getActionsForPath(Set<Room> goalRooms) {  //todo fromRoom versus current room

        DijkstraPath path = null;
        path = new DijkstraPath(currRoom, orientation, goalRooms, cave, getSafeRooms());
        Deque<Action> leastActions = new ArrayDeque<Action>();
        List<Action> actions = path.getActions();
        //safeguard npe when solution has no moves, or there is no solution
        if (actions != null){
            leastActions.addAll(actions);
        }
        return leastActions;
    }

    private void markRooms() {
        wumplessRooms.add(currRoom);
        pitlessRooms.add(currRoom);
        Set<Room> rooms = getSurroundingRooms();
        for (Room room : rooms) {
            if (percepts.contains(Percept.STENCH)) {
                if (!wumplessRooms.contains(room)) {
                    wumpishRooms.add(room);
                }
            } else {
                wumplessRooms.add(room);
            }
            if (percepts.contains(Percept.BREEZE)) {
                if (!pitlessRooms.contains(room)) {
                    pittishRooms.add(room);
                }
            } else {
                pitlessRooms.add(room);
            }
        }
    }

    private void pushEscapeActions() {
        //we are in escape mode and have found the exit        
        pushActionsToEntryRoom();
        actions.addLast(Action.CLIMB);       
    }

    private void pushHuntActions() {
        //face Wumpus
        //shoot Wumpus
    }

    public Set<Room> getSurroundingRooms() {
        return cave.getSurroundingRooms(currRoom);
    }


    //A nextRoom is dangerous if it's suspected of  containing a wumpus or a pit
    public boolean isDangerous(Room room) {
        return ((wumpishRooms.contains(room)) || (pittishRooms.contains(room)));
    }

    public boolean isSafe(Room room) {
        return !isDangerous(room);
    }

    /**
     * returns a set of safe nextRooms, nextRooms w/o either pit or wumpus
     * @return 
     */
    private Set<Room> getSafeRooms() {
        Set<Room> safeRooms = new HashSet<Room>();
        safeRooms.addAll(wumplessRooms);
        safeRooms.retainAll(pitlessRooms);
        return safeRooms;
    }
}
