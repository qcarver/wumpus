/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.qcarver.wumpus;

import org.apache.commons.cli.Options;
import java.util.Set;

/**
 *
 * @author Quinn
 */
public class Text implements Playable {

    Configuration configuration = null;
    Agent agent = null;
    Cave cave = null;

    public Text() {
    }

    public void setOptions(Configuration configuration) {
        this.configuration = configuration;
    }

    public void play(Configuration configuration) {
        {
            configure(configuration);
            do {
                display(agent.currRoom, agent.getVisitedRooms());

                // Read the char
                try {
                    printOutput(agent.getPercepts());
                    agent.move();
                } catch (Death death) {
                    System.out.println(death.toString() + " your score: " + agent.getScore());
                };
            } while (agent.isAlive() && !agent.hasClimbedOut());

            //now where was that Wumpus?
            System.out.println("Score: " + agent.getScore() + "...and the reveal");
            display(agent.currRoom);
        }
    }

    void printOutput(Set<Percept> percepts) {
        System.out.print("facing " + agent.getOrientation() + ", ");
        System.out.println("["
                + "BREEZE" + (percepts.contains(Percept.BREEZE) ? "1" : "0") + ", "
                + "BUMP" + (percepts.contains(Percept.BUMP) ? "1" : "0") + ", "
                + "GLITTER" + (percepts.contains(Percept.GLITTER) ? "1" : "0") + ", "
                + "SCREAM" + (percepts.contains(Percept.SCREAM) ? "1" : "0") + ", "
                + "STENCH" + (percepts.contains(Percept.STENCH) ? "1" : "0") + "]");
    }
    
        /**
     * This display method ASCII draws the entire maze
     * @param playersRoom 
     */
    public void display(Room playersRoom) {
        int dimension = configuration.getDimension();
        int index = 0;
        for (Room room : cave.getRooms()) {
            //get the symbol for the room
            System.out.print(
//               ftw get the symbol for his room
                    getRoomSymbol(room));

            //draw row at a time then return
            if ((index++ % dimension) == dimension - 1) {
                System.out.print('\n');
            }
        }
    }

    /**
     * This display method ASCII draws only visited parts of the maze
     * Unvisited cells are marked with '?'
     * @param playersRoom
     * @param visitedRooms 
     */
    public void display(Room playersRoom, Set<Room> visitedRooms) {
        int dimension = configuration.getDimension();
        int index = 0;

        for (Room room : cave.getRooms()) {
            char symbol = '?';
            if (visitedRooms.contains(room)) {
                symbol = getRoomSymbol(room);
            }
            if ((room != null) && (room.equals(playersRoom))) {
                symbol = 'A';
            }
            System.out.print(symbol);

            //draw row at a time then return
            if ((index++ % dimension) == dimension - 1) {
                System.out.print('\n');
            }
        }
    }

    /**
     * This is a utility function used by the ASCII maze drawing functions
     * @param room
     * @return 
     */
    private char getRoomSymbol(Room room) {
        char symbol = 'E';
        if (room == null) {
            symbol = '#';
        } else {
            //the subtraction gives us unique symbols if more than 1 item in rm
            if (room.hasGold()) {
                symbol += 'G' - 'E';
            }
            if (room.hasPit()) {
                symbol += 'P' - 'E';
            }
            if (room.hasWumpus()) {
                if (room.getWumpus().isAlive()) {
                    symbol += 'W' - 'E';
                } else {
                    symbol += 'w' - 'E';
                }
            }
        }
        return symbol;
    }
    
    /**
     * Injects the configuration from main, and initializes some things
     * accordingly
     * @param configuration 
     */
    private void configure(Configuration configuration){
        this.configuration = configuration;
        
        cave = new Cave(configuration.getDimension(),
                configuration.getPossible(), configuration.getBumpers());
        System.err.println("cave dimension " + configuration.getDimension());
        agent = (configuration.getPlayMode()== Configuration.PlayMode.AUTOMATED)
                ? new AutomatedAgent(cave, configuration.getArrows())
                : new Agent(cave, configuration.getArrows());
    }
}
