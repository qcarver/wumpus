package me.qcarver.wumpus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * The cave is an array of rooms, in order to leverage a typical java array
 * each row of rooms is modeled as 'dimension' rooms across
 * for example:
 *  in a cave with 16 rooms, rows are {{0,1,2,3,4},{5,6,7,8}...}
 * @author Quinn
 */
public class Cave {

    /**
     * the Room objects which the cave is composed of
     */
    ArrayList<Room> rooms = null;
    /**
     * both the vertical and horizontal dimension of this square cave
     */
    int dimension;
    /**
     * the Room that has the Wumpus
     */
    Room wumpusRoom = null;
    /**
     * the Room that has the gold
     */
    private Room goldRoom = null;
    /**
     * Is there a guaranteed path to getting the gold?
     */
    private boolean possible = false;
    /**
     * Percentage likelihood that a room will instantiate as a bumper room
     */
    private int bumper;

    /**
     * the default cave constructor
     */
    public Cave() {
        //invokes the dimensional constructor with the argument 4
        this(4);
    }

    /**
     * constructor for building a cave of a custom dimension
     * @param dimension the vertical and horizontal dimension of the cave
     */
    public Cave(int dimension) {
        this(dimension, false);
    }
    
    /**
     * constructs a new cave with parameters passed
     * @param dimension the vertical and horizontal dimension of the cave
     * @param possible is there a guaranteed path to getting the gold?
     */
    public Cave(int dimension, boolean possible){
        this(dimension, possible, 0);
    }
    
    /**
     * constructs a new cave with parameters passed
     * @param dimension the vertical and horizontal dimension of the cave
     * @param possible is there a guaranteed path to getting the gold?
     * @param bumpers the % likelihood that a room will instantiate as a bumper
     */
    public Cave(int dimension, boolean possible, int bumper){
        //the grid will be square so we need dimension * dimension rooms
        this.dimension = dimension;
        this.possible = possible;
        this.bumper = bumper;
        do{
            init(dimension, bumper);
        }while(!meetsExpectedPossibility());
    }
    
    /**
     * Computes the set of Rooms which do not contain pits
     * @return the Rooms of this cave which do not contain pits
     */
    private Set<Room> getPitlessRooms(){
        //make a set of rooms that are just pits
            Set<Room> pitlessRooms = new HashSet<Room>();
            for (Room room : rooms){
                if ((room != null) && (!room.hasPit()))
                    pitlessRooms.add(room);
            }
        return pitlessRooms;
    }
    
    /**
     * verify a cave guaranteed to have a path to gold does in fact have one
     * @return true if no guarantee was made, true or false if it was
     */
    private boolean meetsExpectedPossibility(){
        List<Action> actions = null;
        boolean meetsExpected = true;
        if (possible == true){
            //the goal is the set of one element, the gold room
            Set<Room> goldRoomSet = new HashSet<Room>();
            goldRoomSet.add(goldRoom);
            
            DijkstraPath goldPath = new DijkstraPath(
            getEntryRoom(), 
            Orientation.RIGHT, //arbitrary
            goldRoomSet,
            this,
            getPitlessRooms());
        
            actions = goldPath.getActions();
            meetsExpected = (actions != null);
        }   
        return meetsExpected;
    }
    


    public ArrayList<Room> getRooms() {
        return rooms;
    }

    protected void init(int dimension, int bumper) {
        //as init may be called multiple times, clear member vars each time
        rooms = new ArrayList<Room>();
        this.wumpusRoom = null;
        goldRoom = null;

        //The next 3 lines pick a random room in the cave for gold & wumpus
        Random randomGenerator = new Random();
        int wumpusRoom = 0;
        int goldRoom = 0;
        int entryRoom = dimension * dimension - dimension;
        boolean hasPit = false;
        boolean hasBumper = false;
        
        //randomly pick a wumpusRoom which is not the entryRoom
        do{
         wumpusRoom = randomGenerator.nextInt(dimension * dimension - 1);
        }while (wumpusRoom == entryRoom);
        //randomly pick a goldRoom which is not the entryRoom
        do{
         goldRoom = randomGenerator.nextInt(dimension * dimension - 1);
        } while (goldRoom == entryRoom);

        for (int roomIndex = 0; roomIndex < dimension * dimension; roomIndex++) {         
            //for these rooms there is 'bumper' % chance the room is a bumper
            if (    (bumper != 0) 
                    && (roomIndex != goldRoom) 
                    && (roomIndex != wumpusRoom) 
                    && (randomGenerator.nextInt(100)<bumper)){
                rooms.add(null);
                continue;
            }
            //for most rooms theres a 20% chance, the room has a pit 
            hasPit = (randomGenerator.nextInt(5) == 3) ? true : false;
 
            //make a room with three possible contents: wumpus, pit, gold
            Room newRoom = new Room(
                    roomIndex,
                    wumpusRoom == roomIndex,
                    (roomIndex == entryRoom)?false:hasPit,
                    goldRoom == roomIndex);

            //add the room to the rooms in the cave
            rooms.add(newRoom);
            
            //mark the Wumpus room (Gui likes to know)
            if (newRoom.hasWumpus()) this.wumpusRoom = newRoom;
            
            //mark the gold room, for private uses
            if (newRoom.hasGold()) this.goldRoom = newRoom;
        }
    }



    /**
     * Given a room and a direction to step toward, method returns the next Room
     * @param room the room we are in
     * @param steppingToward the way we are heading
     * @return
     * @throws Bump throws Bump if it's impossible to step into next room
     */
    public Room getNextRoom(Room room, Orientation steppingToward) throws Bump {
        Room newRoom;
        
        try {
            newRoom = rooms.get(getIndexOfNextRoom(room, steppingToward));
            if (newRoom == null){
                throw new Bump(steppingToward);
            }
        } catch (IndexOutOfBoundsException e) {
            newRoom = room;
            throw new Bump(steppingToward);
        }
        return newRoom;
    }
    
    /**
     * Hint: Rooms are arranged in an array from which is parsed into rows by
     * dimension, starting with the top row and going to the bottom. 
     * @param room
     * @param steppingToward
     * @return 
     */
    private int getIndexOfNextRoom(Room room, Orientation steppingToward){
        int currIndex = room.getIndex();
        int newIndex = currIndex;

        switch (steppingToward) {
            case UP:
                newIndex -= dimension;
                break;
            case LEFT:
                newIndex = (newIndex % dimension == 0) ? -1 : --newIndex;
                break;
            case DOWN:
                newIndex += dimension;
                break;
            case RIGHT:
                newIndex = (newIndex % dimension == dimension - 1) ? -1 : ++newIndex;
                break;
        }
        return newIndex;
    }
    
    /**
     * tests if the next room will bump us into a wall
     * @param room
     * @param steppingToward
     * @return 
     */
    public boolean canMove(Room room, Orientation steppingToward){
        //get the index in the array that the room represents
        int newIndex = getIndexOfNextRoom(room, steppingToward);       
        //test to see if such a room index is legal for the world
        return ((newIndex >= 0 ) && (newIndex < dimension * dimension));
    }

    /**
     * Given a room and a direction in which the arrow is shot, returns success
     * @param room the room where the archer is
     * @param shootingToward the direction in which the archer is facing
     * @return did the shot kill the Wumpus?
     */
    public boolean shootFromRoom(Room room, Orientation shootingToward) {
        //we have failed unless we hit the Wumpus
        boolean success = true;
        //arrow starts in the same room as the archer
        Room arrowsRoom = room;

        //getNextRoom in the path of the arrow.. if arrow Bumps wall we missed!
        try {
            do {
                arrowsRoom = getNextRoom(arrowsRoom, shootingToward);
            } //if the room didn't have a wumpus keep going
            while (!arrowsRoom.hasWumpus());

        } catch (Bump bump) {
            System.out.println(("Bump!"));
            //arrow hit a wall ..oh no we missed!
            success = false;
        };
        
        if (success){
            arrowsRoom.getWumpus().kill();
        }
        return success;
    }

    /**
     * Returns the room in the lower left corner of the grid
     * @return 
     */
    public Room getEntryRoom() {
        return rooms.get(rooms.size() - dimension);
    }

    /**
     * Returns a set of percepts with 
     * BREEZE or STENCH if a surrounding cell has a pit or wumpus, and
     * GLITTER if we are standing on gold .. doesn't return bump.
     * 
     * @param room we are perceiving from
     * @return a set of percepts, but not BUMP
     */
    Set<Percept> getPercepts(Room room) throws Death {
        if (room.hasPit()) {
            throw new Death("Aaaaa! you fell in a pit!");
        }
        if (room.hasWumpus() && room.getWumpus().isAlive()) {
            throw new Death("Nom nom noms! you were eaten by the Wumpus");
        }

        Set<Percept> percepts = new HashSet<Percept>();
        if (room.hasGold()) {
            percepts.add(Percept.GLITTER);
        }
        for (Orientation orientation : Orientation.values()) {
            try {
                Room neighbor = getNextRoom(room, orientation);
                if (neighbor.hasPit()) {
                    percepts.add(Percept.BREEZE);
                }
                if (neighbor.hasWumpus()) {
                    percepts.add(Percept.STENCH);
                }
                if (room.hasWumpus()) {
                    percepts.add(Percept.STENCH);
                }
            } catch (Bump noRoomAndThusNoPerceptsThisWay) {
            };
        }
        return percepts;
    }
    
    boolean getWumpusAlive(){
        return wumpusRoom.getWumpus().isAlive();
    }
    
    public Set<Room> getSurroundingRooms(Room room) {
        Set<Room> rooms = new HashSet<Room>();
        for (Orientation facing : Orientation.values()) {
            try {
                rooms.add(getNextRoom(room, facing));
            } catch (Bump b) {
                //there is no nextRoom in this direction.. we would just bump
            }
        }
        return rooms;
    }
    
    
    public Map<Orientation, Room> getRoomsByOrientation(Room room) {
        Map<Orientation, Room> rooms = new HashMap<Orientation, Room>();
        for (Orientation facing : Orientation.values()) {
            try {
                rooms.put(facing, getNextRoom(room, facing));
            } catch (Bump b) {
                //there is no nextRoom in this direction.. we would just bump
            }
        }
        return rooms;
    } 
    
    /**
     * returns absolute orientation of a neighbor end room wrt a start room. 
     * @param start starting point of orientation vector
     * @param end ending point of orientaiton vector
     * @return an orientation value if rooms are neighbors else null
     */
    public Orientation getOrientation(Room start,Room end){
        Orientation orientation = null;        
        Map<Orientation, Room> neighbors = getRoomsByOrientation(start);
        for(Orientation o : neighbors.keySet()){
            if (neighbors.get(o).equals(end)){
                orientation = o;
            }
        }
        return orientation;
    }
}
