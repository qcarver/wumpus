/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.qcarver.wumpus;

import java.util.Random;

/**
 *
 * @author Quinn
 */
public class TestCave extends Cave {
    //override init function to make a safe cave
    @Override
    protected void init(int dimension) {
        //The next 3 lines pick a random room in the cave for gold & wumpus
        Random randomGenerator = new Random();
        int wumpusRoom = 0;
        int goldRoom = 0;
        int entryRoom = dimension * dimension - dimension;
        boolean hasPit = false;       

        for (int roomIndex = 0; roomIndex < dimension * dimension; roomIndex++) { 
            //make a room with three possible contents: wumpus, pit, gold
            Room newRoom = new Room(
                    roomIndex,
                    false,
                    false,
                    false);
            //add the room to the rooms in the cave
            rooms.add(newRoom);
        }
    }
    

}
