/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.qcarver.wumpus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 *
 * @author Quinn
 */
public class DijkstraPathTest extends TestCase {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    public DijkstraPathTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testImpossible() {
        Set<Room> iSafeRooms = new HashSet<Room>();
        Set<Room> iGoalRooms = new HashSet<Room>();
        
        Cave impossibleCave = new Cave() {
            //make a simple cave with a pits surrounding gold in top right
            @Override
            protected void init(int dimension, int bumpers) {
                rooms = new ArrayList<Room>();
                for (int roomIndex = 0; roomIndex < dimension * dimension; roomIndex++) {
                    //make a room with three possible contents: wumpus, pit, gold
                    Room newRoom = new Room(
                            roomIndex,
                            false,
                            ((roomIndex==2)||(roomIndex==7))?true:false,
                            roomIndex==3?true:false);
                    //add the room to the rooms in the cave
                    rooms.add(newRoom);
                }
            }
        };
        
        for (Room room : impossibleCave.getRooms()) {
            int roomIndex = room.getIndex();
            //if room is a pit room, it is not safe for path making
            if ((roomIndex != 2) && (roomIndex != 7)) {
                iSafeRooms.add(room);
            }

            //add the gold room to the goals
            if ((roomIndex == 3)) {
                iGoalRooms.add(room);
            }
        }

        DijkstraPath impossiblePath = new DijkstraPath(
                impossibleCave.getEntryRoom(),
                Orientation.RIGHT, iGoalRooms, impossibleCave, iSafeRooms);
                String szPath = "";
                
        assertNull(impossiblePath.getActions());
    }
}
