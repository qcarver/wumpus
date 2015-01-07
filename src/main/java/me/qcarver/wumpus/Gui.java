/*
 To add Processsing to this NetBeans Project I first had to get maven to
 put the processing jar in a local repository (the dependancy was not avialable 
 from a mirror). I did so by running this command.
 *
 C:\Users\qcarver>mvn install:install-file -Dfile=C:\processing-2.1.1-windows64\p
 ocessing-2.1.1\core\library\core.jar -DgroupId=org.processing -DartifactId=org.
 rocessing.core -Dversion=2.1.1 -Dpackaging=jar
 *
 * then in main I have main call this main: 
 Gui.main("me.qcarver.wumpus.Gui");
 *
 then I extended this class to extend PApplet and added the mandatory 
 public void setup() and public void draw() methods
 */
package me.qcarver.wumpus;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import processing.core.*;
import java.util.Set;
import javax.swing.Timer;

import static processing.core.PApplet.print;

/**
 *
 * @author qcarver
 */
public class Gui extends PApplet implements Playable {

    PShape s;
    Agent agent = null;
    Cave cave = null;
    public static Configuration configuration = null;
    PlayerInput playerInput = null;
    boolean wasDoubleClick = false;
    Timer timer = null;
    boolean exitOnNextInput = false;

    class PlayerInput {

        Deque<Action> actions = new ArrayDeque<Action>();

        public int getCellIndex() {
            return cellIndex;
        }

        public boolean isDoubleClick() {
            return doubleClick;
        }
        int cellIndex;
        boolean doubleClick = false;

        PlayerInput(int x, int y, boolean doubleClick) {
            this.doubleClick = doubleClick;
            this.cellIndex = ((y / getCellPixelWidth()) * configuration.getDimension())
                    + x / getCellPixelWidth();
            getActions(x, y);
        }

        public Deque<Action> getActions() {
            return actions;
        }

        private void getActions(int mouseX, int mouseY) {

            int currXCell = agent.currRoom.getIndex()
                    % configuration.getDimension() + 1;
            int currYCell = configuration.getDimension() - agent.currRoom.getIndex()
                    / configuration.getDimension();
            int mouseXCell = mouseX / getCellPixelWidth() + 1;
            int mouseYCell = configuration.getDimension() - mouseY / getCellPixelWidth();

//            print("currXCell " + currXCell + " ");
//            print("currYCell " + currYCell + " ");
//            print("mouseXCell " + mouseXCell + " ");
//            print("mouseYCell " + mouseYCell + "\n");
            Orientation whichWay = null;

            //doubleclick in same cell
            if (cellIndex == agent.currRoom.getIndex()) {
                if (doubleClick) {
                    //if we are in home room climb out
                    if (agent.currRoom == cave.getEntryRoom()) {
                        actions.add(Action.CLIMB);
                    } else {
                        //if we are not in home room, grab gold
                        actions.add(Action.GRAB);
                    }
                }
            } //vertical move
            else if ((mouseXCell == currXCell) && (mouseYCell != currYCell)) {
                whichWay = (mouseYCell > currYCell) ? Orientation.UP
                        : Orientation.DOWN;
            } //horizontal move
            else if ((mouseYCell == currYCell) && (mouseXCell != currXCell)) {
                whichWay = (mouseXCell > currXCell) ? Orientation.RIGHT
                        : Orientation.LEFT;
            }

            //if Orientation is not null orient that way
            if (whichWay != null) {
                switch (whichWay.getDelta(agent.getOrientation())) {
                    case UP:
                        break;
                    case RIGHT:
                        actions.add(Action.TURN_RIGHT);
                        break;
                    case DOWN:
                        actions.add(Action.TURN_LEFT); //no break
                    case LEFT:
                        actions.add(Action.TURN_LEFT);
                        break;
                }
                //if it was a doubleclick shoot that way otw walk that way
                actions.add(doubleClick ? Action.SHOOT : Action.GO_FORWARD);
            }
        }
    };

    public enum Contents {

        NOTHING {

                    @Override
                    protected void draw() {
                        _this.rect(xOffset, yOffset, width, height);
                    }
                },
        PIT {

                    @Override
                    protected void draw() {
                        _this.noStroke();
                        _this.fill(0, 0, 255);
                        _this.ellipse((float) (xOffset + width / 2),
                                (float) (yOffset + height / 2),
                                (float) (width * .9),
                                (float) (height * .9));
                    }
                },
        GOLD {

                    @Override
                    protected void draw() {
                        int gold = 0xFFFF00;
                        _this.noStroke();
                        _this.fill(255, 255, 0);
                        _this.rect((float) (xOffset + width / 10),
                                (float) (yOffset + height / 4),
                                (float) (width - width / 5),
                                (float) (height - height / 2));
                    }
                },
        WUMPUS {

                    @Override
                    protected void draw() {
                        _this.noStroke();
                        _this.fill(0, 255, 00);
                        _this.triangle((float) (xOffset + width * 0.1),
                                (float) (yOffset + height * 0.9),
                                (float) (xOffset + width * 0.5),
                                (float) (yOffset + height * 0.1),
                                (float) (xOffset + width * 0.9),
                                (float) (yOffset + height * 0.9));
                    }
                },
        DEAD_WUMPUS {

                    @Override
                    protected void draw() {
                        _this.noFill();
                        _this.stroke(0, 255, 0);
                        _this.triangle((float) (xOffset + width * 0.1),
                                (float) (yOffset + height * 0.9),
                                (float) (xOffset + width * 0.5),
                                (float) (yOffset + height * 0.1),
                                (float) (xOffset + width * 0.9),
                                (float) (yOffset + height * 0.9));
                    }
                },
        ARROW {

                    @Override
                    protected void draw() {
                        float x1 = x * height + height / 2.0f;
                        float y1 = yOffset + height / 2.0f;
                        if (orientation == Orientation.DOWN) {
                            _this.line(x1, y1, x1, y1 + height / 2.0f);
                        } else if (orientation == Orientation.LEFT) {
                            _this.line(x1, y1, x1 - width / 2.0f, y1);
                        } else if (orientation == Orientation.UP) {
                            _this.line(x1, y1, x1, y1 - height / 2.0f);
                        } else {//(orientation == Orientation.RIGHT){
                            _this.line(x1, y1, x1 + width / 2.0f, y1);
                        }
                    }
                },
        AGENT {

                    @Override
                    protected void draw() {
                        float startArc = 0.0f;
                        float endArc = PI;
                        if (orientation == Orientation.DOWN) {
                            startArc = 0.0f;
                            endArc = PI;
                        } else if (orientation == Orientation.LEFT) {
                            startArc = PI * 0.5f;
                            endArc = PI * 1.5f;
                        } else if (orientation == Orientation.UP) {
                            startArc = PI;
                            endArc = PI * 2;
                        } else {//(orientation == Orientation.RIGHT){
                            startArc = PI * 1.5f;
                            endArc = PI * 2.5f;
                        }
                        _this.noFill();
                        _this.arc(
                                (float) (xOffset + width / 2),
                                (float) (yOffset + height / 2),
                                (float) (width - width / 10),
                                (float) (height - height / 10),
                                startArc,
                                endArc,
                                CHORD);
                    }
                },
        BUBBLE {

                    @Override
                    protected void draw() {
                        _this.ellipse((float) (xOffset + width * .30),
                                (float) (yOffset + height * .5),
                                (float) (width * .55),
                                (float) (height * .8));
                        _this.ellipse((float) (xOffset + width * .70),
                                (float) (yOffset + height * .5),
                                (float) (width * .5),
                                (float) (height * .8));
                        _this.ellipse((float) (xOffset + width * .50),
                                (float) (yOffset + height * .25),
                                (float) (width * .33),
                                (float) (height * .33));
                        _this.ellipse((float) (xOffset + width * .50),
                                (float) (yOffset + height * .75),
                                (float) (width * .33),
                                (float) (height * .33));
                        _this.noStroke();
                        _this.fill(255, 255, 255);
                        _this.ellipse((float) (xOffset + width * .50),
                                (float) (yOffset + height * .50),
                                (float) (width * .75),
                                (float) (height * .75));
                    }
                },
        UNKNOWN {

                    @Override
                    protected void draw() {
                        //dont' draw the unknown sqaure;
                    }
                };
        Orientation orientation = Orientation.RIGHT;
        int dimension = configuration.getDimension();
        int width = 0;
        int height = 0;
        int x = 0;
        int y = 0;
        int xOffset = x * width;
        int yOffset = y * height;
        Gui _this;
        int scale = 1;

        public void draw(Gui _this, int x, int y) {
            width = (_this.getWidth() / dimension) / scale;
            height = (_this.getHeight() / dimension) / scale;
            this.xOffset = x * (_this.getWidth() / dimension);
            /*hack to get thought icons to center*/
            this.xOffset += (scale == 2) ? (_this.getWidth() / dimension) / 4 : 0;
            this.yOffset = y * (_this.getHeight() / dimension);
            /*hack to get thought icons to center*/
            this.yOffset += (scale == 2) ? (_this.getWidth() / dimension) / 4 : 0;
            this.x = x;
            this.y = y;
            this._this = _this;
            draw();
            //restore drawing tools to a known state
            scale = 1;
            _this.fill(255, 255, 255);
            _this.stroke(0x0);
        }

        /**
         * pretty much always overridden
         */
        protected void draw() {
            //prety much always overriden
        }

        public void drawSmall(Gui _this, int x, int y) {
            scale = 2;
            draw(_this, x, y);
        }

        public void setOrientation(Orientation orientation) {
            this.orientation = orientation;
        }

        /**
         * sets the height or width of the draw boundaries in pixels
         *
         * @param pixelDimension
         */
        public void setDimension(int pixelDimension) {
            this.dimension = pixelDimension;
        }
    };

    public void setup() {
        //set up the size of the canvas
        size(configuration.getGuiDimension(), configuration.getGuiDimension());
        if (configuration != null) {
            play(configuration);
        } else {
            System.err.println("Must initiatilize configuration statically "
                    + "before calling PApplet main");
            exit();
        }
    }

    public void drawGameTile(int x, int y) {
        int width = getWidth() / configuration.getDimension();
        int height = getHeight() / configuration.getDimension();
        rect(x * width, y * height, width, height);

    }

    /**
     * Processing calls draw() as the main loop iteratively
     */
    public void draw() {

        display(agent.currRoom, agent.getVisitedRooms());

        //show percepts
        if ((agent.isAlive()) && (!agent.hasClimbedOut())) {
            try {
                displayPercepts(agent.currRoom, agent.getPercepts());
                if (configuration.getPlayMode() == Configuration.PlayMode.AUTOMATED) {
                   delay(100);
                   agent.move();
                } else {
                    if (playerInput != null) {
                        for (Action action : playerInput.getActions()) {
                            agent.move(action);
                        }
                        playerInput = null;
                    }
                }
            } catch (Death death) {
                print("Thou art dead");
                playerInput = null;
            }
        } else {
            Set<Room> allRooms = new HashSet<Room>();
            allRooms.addAll(cave.getRooms());
            display(agent.currRoom, allRooms);
            showScore();
            exitOnNextInput = true;
        }
    }

    public void displayPercepts(Room currRoom, Set<Percept> percepts) {
        //creative liberty.. let's not worry about stench if Wumpus is dead
        if (percepts.contains(Percept.STENCH) && !cave.getWumpusAlive()) {
            percepts.remove(Percept.STENCH);
        }

        if (!percepts.isEmpty()) {
            if ((percepts.contains(Percept.BREEZE))
                    || (percepts.contains(Percept.STENCH))
                    || (percepts.contains(Percept.SCREAM))) {

                int dimension = configuration.getDimension();
                int x = (currRoom.getIndex() % dimension);
                int y = (currRoom.getIndex() / dimension);
                int width = getWidth() / dimension;
                int height = getHeight() / dimension;

                //can we paint the though bubble to the right
                if (cave.canMove(currRoom, Orientation.UP)) {
                    y--;
                    if (cave.canMove(currRoom, Orientation.RIGHT)) {
                        x++;
                        //draw the tail to the thought bubble pointing toward agent
                        ellipse((float) (x * width + width * .15),
                                (float) (y * height + height * .85),
                                (float) (width * .15),
                                (float) (height * .15));
                        ellipse((float) (x * width + width * .05),
                                (float) (y * height + height * .95),
                                (float) (width * .1),
                                (float) (height * .1));
                    } else {//have to paint the though bubble to the LEFT
                        x--;
                        //draw the tail to the thought bubble pointing toward agent
                        ellipse((float) (x * width + width * .85),
                                (float) (y * height + height * .85),
                                (float) (width * .15),
                                (float) (height * .15));
                        ellipse((float) (x * width + width * .95),
                                (float) (y * height + height * .95),
                                (float) (width * .1),
                                (float) (height * .1));
                    }
                } else {//have to paint the thought bubble DOWN
                    y++;
                    if (cave.canMove(currRoom, Orientation.RIGHT)) {
                        x++;
                        //draw the tail to the thought bubble pointing toward agent
                        ellipse((float) (x * width + width * .15),
                                (float) (y * height + height * .15),
                                (float) (width * .15),
                                (float) (height * .15));
                        ellipse((float) (x * width + width * .05),
                                (float) (y * height + height * .05),
                                (float) (width * .1),
                                (float) (height * .1));
                    } else {//have to paint the thought bubble to the LEFT
                        x--;
                        //draw the tail to the thought bubble pointing toward agent
                        ellipse((float) (x * width + width * .85),
                                (float) (y * height + height * .15),
                                (float) (width * .15),
                                (float) (height * .15));
                        ellipse((float) (x * width + width * .95),
                                (float) (y * height + height * .05),
                                (float) (width * .1),
                                (float) (height * .1));
                    }
                }
                Contents bubble = Contents.BUBBLE;
                Contents stench = Contents.WUMPUS;
                Contents scream = Contents.DEAD_WUMPUS;

                //draw teh thouhgt bubble
                bubble.draw(this, x, y);
                //draw icons in bubble...
                //shorthand for GUI.. only percept stench if it's relevant
                boolean drawStench = (percepts.contains(Percept.STENCH)
                        && cave.getWumpusAlive());
                //figure out how many and what go in bubble 
                if (percepts.contains(Percept.BREEZE)) {
                    Contents breeze = Contents.PIT;
                    //draw scream
                    if (percepts.contains(Percept.SCREAM)) {
                        scream.drawSmall(this, x, y);
                        //draw scream and breeze
                        if (percepts.contains(Percept.BREEZE)) {
                            breeze.drawSmall(this, x, y);
                            scream.drawSmall(this, x, y);

                        }
                    } //draw breeze and stench (Wumpus is alive)
                    else if (drawStench) {
                        breeze.drawSmall(this, x, y);
                        stench.drawSmall(this, x, y);
                    } //just draw breeze
                    else {
                        breeze.drawSmall(this, x, y);
                    }
                } //just draw stench
                else if (drawStench) {
                    stench.drawSmall(this, x, y);
                } else if (percepts.contains(Percept.STENCH)) {
                    if (cave.getWumpusAlive()) {
                        stench.drawSmall(this, x, y);
                    } else if (percepts.contains(Percept.SCREAM)) {
                        scream.drawSmall(this, x, y);
                    }
                } else if (percepts.contains(Percept.SCREAM)) {
                    scream.drawSmall(this, x, y);
                }
            }
        }
    }

    public void display(Room currRoom, Set<Room> visitedRooms) {
        int dimension = configuration.getDimension();

        //draw a big gray square
        noStroke();
        fill(153);
        rect(0, 0, getWidth(), getHeight());
        fill(255, 255, 255);
        stroke(0x0);

        for (Room room : cave.getRooms()) {
            Deque<Contents> contents = new ArrayDeque<Contents>();
            if (visitedRooms.contains(room)) {
                contents.add(Contents.NOTHING);
                if (room.hasPit()) {
                    contents.add(Contents.PIT);
                }
                if (room.hasGold()) {
                    contents.add(Contents.GOLD);
                }
                if (room.hasWumpus()) {
                    if (room.getWumpus().isAlive()) {
                        contents.add(Contents.WUMPUS);
                    } else {
                        contents.add(Contents.DEAD_WUMPUS);
                    }
                }
            }
            if (room.equals(currRoom) && (!agent.hasClimbedOut)) {
                contents.add(Contents.AGENT);
                if (agent.hasArrow()) {
                    contents.add(Contents.ARROW);
                }
            }
            int x = (room.getIndex() % dimension);
            int y = (room.getIndex() / dimension);
            for (Contents content : contents) {
                content.setOrientation(agent.getOrientation());
                content.draw(this, x, y);
            }
        }
    }

    public void display(Room currRoom) {
        //TODO:        
    }

    /**
     * This is a utility function which returns an enum for the contents of a
     * room
     *
     * @param room
     * @return
     */
    private Contents getRoomSymbol(Room room) {
        Contents content = Contents.UNKNOWN;
        //the subtraction gives us unique symbols if more than 1 item in rm
        if (room.hasGold()) {
            content = Contents.GOLD;
            //TODO: need to fix this method so that it returns a set.. can be more than one thing in a room
        }
        if (room.hasPit()) {
            content = Contents.PIT;
        }
        if (room.hasWumpus()) {
            if (room.getWumpus().isAlive()) {
                content = Contents.WUMPUS;
            } else {
                content = Contents.DEAD_WUMPUS;
            }
        }
        return content;
    }

    public void showScore() {
        PFont f;                          // STEP 2 Declare PFont variable
        f = createFont("Arial", 16, true); // STEP 3 Create Font
        textFont(f, 144);                 // STEP 4 Specify font to be used
        if (agent.getScore() > 0) {
            fill(0);                        // STEP 5 Specify font color 
            text(agent.getScore(), getWidth() / 4, getHeight() / 2 + getHeight() / 13);  // STEP 6 Display Text     
        } else {
            fill(255, 0, 0);
            text(agent.getScore(), getWidth() / 8, getHeight() / 2 + getHeight() / 13);  // STEP 6 Display Text
        }

    }

    public void showPercepts(Set<Percept> percepts) {
        //TODO:        
    }

    /**
     * The Processing instance of this class must be started from a static main
     * ..since we can't call methods for instances.. this method is not helpful
     *
     * @param configuration
     */
    public void play(Configuration configuration) {
        this.configuration = configuration;

        cave = new Cave(configuration.getDimension(),configuration.getPossible());
        agent = (configuration.getPlayMode() == Configuration.PlayMode.AUTOMATED)
                ? new AutomatedAgent(cave, configuration.getArrows())
                : new Agent(cave, configuration.getArrows());
    }

    private int getCellPixelWidth() {
        return (configuration.getGuiDimension() / configuration.getDimension());
    }

    /**
     * code which looks for a mouse click and then sets up a timer to
     * distinguish if it's a single or double click.. before handling the input
     */
    public void mousePressed() {
        //game over clicking from reveal?
        if (exitOnNextInput) {
            exit();
        }
        //still playing
        if (mouseEvent.getClickCount() == 2) {
            //single click
            wasDoubleClick = true;
            playerInput = new PlayerInput(mouseEvent.getX(), mouseEvent.getY(), true);
        } else {
            Integer timerinterval = (Integer) Toolkit.getDefaultToolkit().getDesktopProperty(
                    "awt.multiClickInterval");

            timer = new Timer(timerinterval.intValue(), new ActionListener() {

                public void actionPerformed(ActionEvent evt) {
                    if (wasDoubleClick) {
                        wasDoubleClick = false; // reset flag
                    } else {
                        //double click
                        playerInput = new PlayerInput(mouseEvent.getX(), mouseEvent.getY(), false);
                    }
                }
            });
            timer.setRepeats(false);

            timer.start();
        }

    }
}
