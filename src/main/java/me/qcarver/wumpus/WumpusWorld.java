package me.qcarver.wumpus;

import java.io.IOException;
import java.util.Set;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Hello world! ---
 *
 */
public class WumpusWorld {

    private Cave cave = null;
    private Agent agent = null;

    /**
     * processes the command line arguments and starts a new WumpusWorld game
     *
     * @param args -h at command prompt will give a list of current args
     */
    public static void main(String[] args) {
        //Gui.main("me.qcarver.wumpus.Gui");
        Configuration configuration = new Configuration(args);

        if (!configuration.getHelp()) {
            WumpusWorld wumpusWorld =
                    new WumpusWorld(configuration);
        } else {
            HelpFormatter formatter = new HelpFormatter();
		    	formatter.printHelp( "JaxpTransformer", 
                                "wumpus [options]\n" +
                                        "A game for intelligent agents\n",
                                configuration.getOptions(), 
                                "", true );    	
        }
    }

    private WumpusWorld(Configuration configuration) {
        
        if (configuration.getUiMode()==Configuration.UiMode.TEXT){
            new Text().play(configuration);
        }
        else if (configuration.getUiMode()==Configuration.UiMode.GUI){
            Gui.configuration = configuration;
            Gui.main("me.qcarver.wumpus.Gui");
            //System.out.println("GUI mode Wumpus isn't out of Beta yet.. sorry");
        }
    }
}
