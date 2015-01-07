/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.qcarver.wumpus;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An implementation of Dijkstra's iterative shortest path finding neighbor
 * w/in the Cave,Room,Orientation context of WumpusWorld
 * @author Quinn
 */
public class DijkstraPath {
    //The room that agent is starting in, paths are constructed from here
    private Room startRoom = null;
    //The set of destinations, any of which we would like to get to
    private Set<Room> goalRooms = null;
    //The direction the Agent is facing 
    private Orientation facing = null;
    //The cave Singelton keeps track of relationships between rooms
    private Cave cave = null;
    //Keeps track of the testRms needed to go from the start room to other rooms
    private Map<Room, List<Action>> movesToRoom = null;
    //From Dijkstra's, S is the outer loop container rooms we have cleared
    private Set<Room> S = null;
    //All the rooms we are to use from the cave (eg: use may be only safe rooms)
    private Set<Room> rooms = null;
    //an arbitrarily large number
    final private int infinity = 9999;
    
    //a way to return the room and the testRms to it at the same time
    class RoomMoveSet{
        Room room;
        List<Action> moves;
        RoomMoveSet(Room room, List<Action> moves){
            this.room = room;
            this.moves = moves;
        }
    }


    /**
     * DijkstraPath object constructor
     * @param startRoom the room where computing the path will start
     * @param facing the direction the agent is facing in the start room
     * @param goalRooms set containing destinations, any of which is acceptable
     * @param cave object which keeps track of the relationship between rooms
     * @param rooms the set of cave rooms are acceptable to traverse through
     */
    public DijkstraPath(Room startRoom, 
            Orientation facing, 
            Set<Room> goalRooms,
            Cave cave,
            Set<Room> rooms) {
        this.startRoom = startRoom;
        this.facing = facing;
        this.goalRooms = goalRooms;
        this.cave = cave;
        this.rooms = rooms;
    }    
    
     /**
     * function which returns the room with a minimal number of testRms
     *  between S and notS. Actions to room are also logged in map
     * @return 
     */
    private Room getLuMinimal() {
        RoomMoveSet rms = null;
        RoomMoveSet maxRms = null;
        if (S.isEmpty()) {
            S.add(startRoom);
            maxRms = new RoomMoveSet(startRoom, new ArrayList<Action>());
        } else {
            for (Room c : S) {
                rms = getNearestRoomInFrontier(c);
                if (rms == null) {
                    continue;
                }
                if ((maxRms == null) || (rms.moves.size() < maxRms.moves.size())) {
                    maxRms = rms;
                    //update the movesToRoom map ..concatenate moves to c + rms moves
                    movesToRoom.put(rms.room, getActionsToV(c, rms.moves));
                    //the minimal number of newMoves is 1, the first minimal is fine
                    if (maxRms.moves.size() == 1) {
                        break;
                    }
                }
            }
        }

        return (maxRms == null) ? null : maxRms.room;
    }
    
    private Set<Room> getSurroundingRooms(Room room){
        Set<Room> surroundingRooms = cave.getSurroundingRooms(room);
        surroundingRooms.retainAll(rooms);
        return surroundingRooms;
    }

    /**
     * Helper fxn for getDijkstrasPath 
     * @param room the room to find surrounding safe room from set
     * @param set of rooms which must be in the result
     * @return the nearest of the surrounding safe rooms
     */
    private RoomMoveSet getNearestRoomInFrontier(Room room){
        RoomMoveSet nearest = null;
        Set<Room> neighbor = getSurroundingRooms(room);
        neighbor.retainAll(rooms);
        neighbor.retainAll(notS());
        int max=infinity;
        //of the surrounding rooms, narrow down to closest one (newMoves wise)
        for(Room test : neighbor){
            RoomMoveSet testRms = getMovesToAdjacentRoom(room, test);
            if (testRms.moves.size() < max){
                max = testRms.moves.size();
                nearest = testRms;
                //can't do any better than one anyway
                if (testRms.moves.size()==1)break;
            }
        }
        return nearest;
    }
    
    private Orientation getOrientationIn(Room room){
        Orientation o = facing;
        List<Action> moves = movesToRoom.get(room);
        for (Action action: moves){
            if (action.equals(Action.TURN_LEFT)){  //TODO: add an Action equals comparator
                //basically decrement o, counterclockwise
                o = o.prev();
            }
            else if (action.equals(Action.TURN_RIGHT)){
                //basically increment o, clockwise
                o = o.next();
            }
        }
        return o;
    }
    
    /**
     * Given a start room, computes actions to move into an adjacent end room.
     * @param start the agents starting room
     * @param a room adjacent to the start room that the agent should end up in
     * @return a RoomMoveSet repeating the end room, and actions to get there,
     * actions will be empty if rooms are not adjacent.
     */
    private RoomMoveSet getMovesToAdjacentRoom(Room start, Room end){
        RoomMoveSet rms = new RoomMoveSet(end,new ArrayList<Action>());
        if (getSurroundingRooms(start).contains(end)){
                Orientation nextRoomsOrientation = cave.getOrientation(start,end);
                //convert nextRoom orient'n to an agent relative orientation
                switch (nextRoomsOrientation.getDelta(getOrientationIn(start))) {
                    //push newMoves which move the agent into the new nextRoom
                    case UP:
                        rms.moves.add(Action.GO_FORWARD);
                        break;
                    case LEFT:
                        rms.moves.add(Action.TURN_LEFT);
                        rms.moves.add(Action.GO_FORWARD);
                        break;
                    case RIGHT:
                        rms.moves.add(Action.TURN_RIGHT);
                        rms.moves.add(Action.GO_FORWARD);
                        break;
                    case DOWN:
                        rms.moves.add(Action.TURN_LEFT);
                        rms.moves.add(Action.TURN_LEFT);
                        rms.moves.add(Action.GO_FORWARD);
                        break;
                }
        }
        return rms;
    }
    
    /**
     * Determines whether sets of any generic type intersect
     * @param <T> the type of objects in the set
     * @param a the first set
     * @param b a second set
     * @return true if intersect
     */
    private <T> boolean setsIntersect(Set<T> a, Set<T> b){
        Set<T> test = new HashSet<T>(a);
        //if the set a changes as a result of removing b elements, it intersects
        return test.removeAll(b);
        
    }
    
    /**
     * computes the compliment of the set S
     * @return 
     */
    private Set<Room> notS(){
        Set<Room> notS = new HashSet<Room>(rooms);
        notS.removeAll(S);
        return notS;
        
    }
    
    /**
     * Given that there is a known path to u, and a path from u to v passed in
     * this method concatenates together the total path to v
     * @param u a room whose path is stored in the movesToRoom member variable
     * @param uToV the actions it takes to get from room u to room v
     */
    private List<Action> getActionsToV(Room u, List<Action> uToV){
        List<Action> concatenatedActions = 
                new ArrayList<Action>(movesToRoom.get(u));
        concatenatedActions.addAll(uToV);
        return concatenatedActions;                                  //TODO: test that these action are added in correct order eg: 1,2,3 + A,B = 1,2,3,A,B (not ending BA)
    }

    /**
     * This is the heart of the Dijkstra Path algorithm. All other methods are
     * basically just helpers.
     * @return 
     */
    public List<Action> getActions() {
        List<Action> rv = null;
        //initiazlize the map .. "movesToRoom" is analogous to 'L' in Dijkstra
        movesToRoom = new HashMap<Room, List<Action>>();
        //Our convention: empty list is 0 testRms, null list is infinity
        movesToRoom.put(startRoom, new ArrayList());
        //initialize S
        S = new HashSet<Room>();

        do {
            //newRoom u is a room in notS nearest to a room in S
            Room u = null;
            u = getLuMinimal();
            if (u == null) break;
            S.add(u);

            for (Room v: getSurroundingRooms(u))
            {
                //don't consider edges leading back to S
                if (S.contains(v))continue;
                //don't consider rooms which aren't allowed
                if (!rooms.contains(v)) continue;
                
                RoomMoveSet vRms = getMovesToAdjacentRoom(u,v);

                if ((movesToRoom.get(v) == null) || //if the distance is infinity 
                        ((movesToRoom.get(u).size() + vRms.moves.size())
                        < movesToRoom.get(v).size())) {  //of if we've found a shorter way
                    //update the map with the new shorter concatenated path
                    movesToRoom.put(v, getActionsToV(u, vRms.moves));
                }
                //if this is a goal room, we have a return value (shortest path)
                if (goalRooms.contains(v)){
                    rv = movesToRoom.get(v);
                }
            }

        } while (!setsIntersect(S, goalRooms));
        return rv;
    }
}
