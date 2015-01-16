To start the game in this directory type:
java -jar wumpus-1.0.jar

usage: wumpus-1.0.jar [-a] [-b <arg>] [-d <arg>] [-g] [-h] [-p] [-r <arg>]

[options]
 -a,--automated         automated agent
 -b,--bumpers <arg>     % chance a room is a bumper room, default is 0
 -d,--dimension <arg>   the x & y dimensions of the cave, default is 4
 -g,--graphic           graphic mode, default is false
 -h,--help              print this message
 -p,--possible          guarantee a safe path  to gold, default is false
 -r,--arrows <arg>      number of arrows in quiver, default is 1

The wumpus world is a cave consisting of rooms connected by passageways. Lurking somewhere in the cave 
is the terrible homicidal wmnpus. The wumpus can be shot by an agent, but the agent has only one 
arrow. Some rooms contain bottomless pits that will trap anyone who wanders into these rooms. The only 
reason to enter this bleak environment is the possibility of finding a heap of gold. The game ends 
either when the agent dies or when the agent climbs out of the cave (with or without the gold).

Performance measure: 
* 1000 for climbing out of the cave with the gold, 
* -1000 for falling into a pit or being eaten by the wumpus, 
* -1 for each action taken (including turning and moving)
* -10 for using the arrow. 

The environment is a 4 x 4 grid of rooms. The agent always starts in the square labeled [1,1], facing 
to the right. The locations of the gold and the wumpus are chosen randomly, with a uniform 
distribution, from the squares other than the start square. In addition, each square other than the 
start can he a pit, with probability 0.2.

The agent can move Forward, TurnLeft by 90°, or Turnftight by 90°. The agent dies a miserable death if 
it enters a square containing a pit or a live wumpus. (it is safe, albeit smelly, to enter a square 
with a dead wumpus.) If an agent tries to move forward and bumps into a wall, then the agent does not 
move. The action Grab can be used to pick up the gold if it is in the same square as the agent. The 
action Shoot can be used to fire an arrow in a straight line in the direction the agent is facing. The 
arrow continues until it either hits (and hence kills) the wumpus or hits a wall. The agent has only 
one arrow, so only the first Shoot action has any effect. Finally_ the action Climb can be used to 
climb out of the cave, but only from square

* Sensors: The agent has five sensors. each of which gives a single bit of information:
* In the square containing the wumpus and in the directly (not diagonally) adjacent squares, the agent 
will perceive a Stench.
* In the squares directly adjacent to a pit, the agent will perceive a Breeze.
* In the square where the gold is, the agent will perceive a Glitter.
* When an agent walks into a wall, it will perceive a Bump.
* When the wumpus is killed, it emits a woeful Scream that can be perceived any-where in the cave.
The percepts will be given to the agent program in the form of a list of five symbols; for example, if 
there is a stench and a breeze, but no glitter, bump, or scream, the agent program will get [Stench, 
Breeze, None, None, None].

Several options have been provided to change paramters from the original game format. These include 
possible mode, bumpers, dimension, number of arrows, automated and graphic mode. Possible, ensures 
that it is possible (although maybe not fair) to reach the gold. Bumpers, adds bumpers in place of 
rooms, you can bump into them but not enter (think walls). Dimension, allows players to make a bigger 
or smaller cave. Automated, creates an automated AI agent to play the game for you. Graphic mode 
creates a GUI version of the game which uses only the mouse for input.

In graphic mode, clicking once in a neighboring cell commands the agent to move into that room. Double 
clicking in anywhere outside the agents cell will cause the agent to shoot in that direction if 
possible (eg outside of cave or diagonally is not possible). Double clicking in the Agents cell will 
cause the agent to pick up gold if it is a room with gold OR climb out of the game if the agent is in 
the entry room. The percepts the agent experiences are shown in thought bubbles near the agents head.
