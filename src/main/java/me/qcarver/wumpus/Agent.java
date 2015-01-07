package me.qcarver.wumpus;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Quinn
 */
public class Agent {
    //static finals are teh same as "const" in C/C++

    static final int scoreForMove = -1;
    static final int scorePerGold = 1000;
    static int scoreForDeath = -1000;
    static int scoreForUsingArrow = -10;
    boolean alive = true;
    int arrows = 1;
    boolean hasGold = false;
    Cave cave = null;
    Room currRoom = null;
    protected int score = 0;
    Orientation orientation = Orientation.RIGHT;
    boolean hasClimbedOut = false;
    Set<Percept> percepts = new HashSet<Percept>();
    Set<Room> visitedRooms = new HashSet<Room>();

    //hiding the default constructor ..we need the environment! 
    protected Agent() {
    }

    public int getScore() {
        return score;
    }
    
    public Agent(Cave cave, int arrows) {
        this.arrows = arrows;
        this.currRoom = cave.getEntryRoom();
        this.cave = cave;
        visitedRooms.add(currRoom);
    }
    
    public boolean hasArrow(){
        return arrows > 0;
    }

    public Set<Room> getVisitedRooms() {
        return visitedRooms;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    /**
     * adds percepts from cave (STENCH, BREEZE, GLITTER) to any percepts we may
     * have already accumulated in the last move (BUMP, SCREAM)
     *
     * @return
     * @throws Death
     */
    public Set<Percept> getPercepts() throws Death {
        //set of percepts may already include BUMP and SCREAM.. add to it here
        try {
            percepts.addAll(cave.getPercepts(currRoom));
        } catch (Death death) {
            //agent is dead no perceptions now, update agent
            score += scoreForDeath;
            alive = false;
            //rethrow the exception so the news bubbles up
            throw death;
        }

        return percepts;
    }

    public void move()  throws Death{
        System.out.println("Enter a character (l,r,f,g,s,c) followed by return: ");
        //we read in extra bytes.. mostly becasue enter is also a char
        byte input[] = new byte[255];
        try {
            System.in.read(input);
            char ch = (char) input[0];

            switch (ch) {
                case 'l':
                    move(Action.TURN_LEFT);
                    break;
                case 'r':
                    move(Action.TURN_RIGHT);
                    break;
                case 'f':
                    move(Action.GO_FORWARD);
                    break;
                case 'g':
                    move(Action.GRAB);
                    break;
                case 's':
                    move(Action.SHOOT);
                    break;
                case 'c':
                    move(Action.CLIMB);
                    break;
                default:
                    System.out.println("key not recognized, try again");
            }
        } catch (IOException e) {
            System.out.print("Couldn't get input");

        }
    }

    protected void move(Action action) throws Death{
        //we reinitialize perceptions with each move
        percepts = new HashSet<Percept>();

        //we are penalized for every move we make
        score += scoreForMove;

        switch (action) {
            case TURN_RIGHT:
                turnRight();
                break;
            case TURN_LEFT:
                turnLeft();
                break;
            case GO_FORWARD:
                try {
                    currRoom = cave.getNextRoom(currRoom, orientation);
                    //we entered a new currRoom add it to the list
                    visitedRooms.add(currRoom);
                } catch (Bump bumped) {
                    //only way we get a bump percept is by walking into it
                    percepts.add(Percept.BUMP);
                }
                break;
            case GRAB:
                grab();
                break;
            case CLIMB:
                climb();
                break;
            case SHOOT:
                if (shoot()) {
                    percepts.add(Percept.SCREAM);
                }
                break;
        }
    }

    private void turnLeft() {
        orientation = getLeft();
    }

    /**
     * returns the absolute orientation (wrt world) that corresponds to the the
     * agent's left facing orientation
     *
     * @return absolute orientation
     */
    protected Orientation getLeft() {
        Orientation absoluteOrientation = Orientation.LEFT;
        switch (orientation) {
            case UP:
                absoluteOrientation = Orientation.LEFT;
                break;
            case LEFT:
                absoluteOrientation = Orientation.DOWN;
                break;
            case DOWN:
                absoluteOrientation = Orientation.RIGHT;
                break;
            case RIGHT:
                absoluteOrientation = Orientation.UP;
                break;
        }
        return absoluteOrientation;
    }

    private void turnRight() {
        orientation = getRight();
    }

    /**
     * returns the absolute orientation (wrt world) that corresponds to the the
     * agent's right facing orientation
     *
     * @return absolute orientation
     */
    protected Orientation getRight() {
        Orientation absoluteOrientation = Orientation.RIGHT;
        switch (orientation) {
            case UP:
                absoluteOrientation = Orientation.RIGHT;
                break;
            case LEFT:
                absoluteOrientation = Orientation.UP;
                break;
            case DOWN:
                absoluteOrientation = Orientation.LEFT;
                break;
            case RIGHT:
                absoluteOrientation = Orientation.DOWN;
                break;
        }
        return absoluteOrientation;
    }

    private void grab() {
        if (currRoom.hasGold()) {
            currRoom.grabGold();
            hasGold = true;
            score += scorePerGold;
            System.out.println("WOOT! you got the gold!");
        }
    }

    private void climb() {
        if (currRoom.equals(cave.getEntryRoom())) {
            System.out.println("Climbed Out! your score: " + score);
            hasClimbedOut = true;
        } else {
            System.out.println("You can't climb out from this room.."
                    + "the Wumpus can smell your fear!");
        }
    }

    private boolean shoot() {
        boolean kill = false;
        if (arrows > 0) {
            score += scoreForUsingArrow;
            arrows --;
            kill = cave.shootFromRoom(currRoom, orientation);
        }
        return kill;
    }

    public boolean hasClimbedOut() {
        return this.hasClimbedOut;
    }

    public boolean isAlive() {
        return this.alive;
    }
}
