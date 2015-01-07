/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.qcarver.wumpus;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author qcarver
 */
public class Configuration {

    public enum PlayMode {

        HUMAN, AUTOMATED
    };

    public enum UiMode {

        TEXT, GUI
    };

    private PlayMode playMode = PlayMode.HUMAN;
    private UiMode uiMode = UiMode.TEXT;
    private int gridDimension = 4;
    private boolean help = false;
    private int guiDimension = 500;
    private boolean possible = false;
    private Options options = null;
    private int arrows = 1;

    /**
     * returns the mode of play
     *
     * @return human or automated?
     */
    public PlayMode getPlayMode() {
        return playMode;
    }

    /**
     * returns the type of user interface
     *
     * @return text or gui?
     */
    public UiMode getUiMode() {
        return uiMode;
    }

    /**
     * returns the x or y dimension of the square Environment
     *
     * @return
     */
    public int getDimension() {
        return gridDimension;
    }

    /**
     * returns the x or y dimension of the environment in pixels;
     *
     * @return
     */
    public int getGuiDimension() {
        return guiDimension;
    }
    
    /**
     * returns existence of guaranteed path to gold
     * 
     * @return cave is solvable
     */
    public boolean getPossible() {
        return possible;
    }
    
    /**
     * returns the number of arrows to start the game with
     * 
     * @return number of arrows 
     */
    public int getArrows(){
        return arrows;
    }

    /**
     * was help requested (or required)
     *
     * @return true indicates help was invoked and the game should not continue
     */
    public boolean getHelp() {
        return help;
    }

    Configuration(String[] args) {
        parse(args);
    }

    /**
     * Parses command line arguments into an object. After invoking this method
     * Use getters in this class to query the values of command line arguments.
     *
     * @param args
     */
    void parse(String[] args) {
        this.options = new Options();
        options.addOption("a", "automated", false, "automated agent");
        options.addOption("r", "arrows", true, "number of arrows in quiver, "
                + "default is " + arrows);
        options.addOption("g", "graphic", false, "graphic mode, default is "
                + ((uiMode == UiMode.TEXT)?"false":"true"));
        options.addOption("d", "dimension", true, "the x & y dimensions of "
                + "the cave, default is " + gridDimension);
        options.addOption("p", "possible", false, "guarantee a safe path "
                + " to gold, default is "  + (possible?"true":"false"));
        options.addOption("h", "help", false, "print this message");

        CommandLine cmd = null;
        Agent agent = null;
        WumpusWorld wumpusWorld = null;

        CommandLineParser parser = new BasicParser();
        try {
            cmd = parser.parse(options, args);
            help = (cmd.hasOption("h")) ? true : false;
        } catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Wumpus World",
                    WumpusWorld.class.getSimpleName() + " [options]",
                    options,
                    "", true);
        }
        //if isn't true by necesity.. did user request?
        if (!help) {
            uiMode = (cmd.hasOption("g") ? UiMode.GUI : UiMode.TEXT);
        }
        playMode = (cmd.hasOption("a") ? PlayMode.AUTOMATED : PlayMode.HUMAN);
        try {
            gridDimension = (cmd.hasOption("d") ? Integer.parseInt(
                    cmd.getOptionValue("dimension")) : gridDimension);
        } catch (NumberFormatException e) {
            System.err.println("Couln't parse guiDimension parameter, going "+
                    "with default " + gridDimension);
        }
        try {
            arrows = (cmd.hasOption("r") ? Integer.parseInt(
                    cmd.getOptionValue("arrows")) : arrows);
        } catch (NumberFormatException e) {
            System.err.println("Couln't parse arrows parameter, going "+
                    "with default " + arrows);
        }
        possible = (cmd.hasOption("p")? true:false);
    }

    Options getOptions() {
        return options;
    }
}
