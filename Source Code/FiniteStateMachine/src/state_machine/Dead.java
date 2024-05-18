package state_machine;

import actors.Ant;
import actors.Tile;
/*
 * ANT COLONY SIMULATION
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     April 20, 2024
 * COURSE:   COMP452 - AI for Game Developers (Athabasca University)
 *
 * Dead State (Interface)
 * Represents the dead state for ants
 *
 * SAME NOTES AS ALL STATES
 * Future Updates/Refactor:
 * Could update this interface to include on exit checks. The constructor is currently used as an on start check.
 * Resolve collision and state swapping could also be done by a state machine class to switch and control the states
 * rather then having each state control the flow of state logic.
 */
public class Dead implements State{
    private Ant ant;
    private final long DEATH_TIMER = 3000;
    private long timeOfDeath = 0;

    //On spawn track the current time and switch to death sprite
    Dead(Ant ant){
        this.ant = ant;
        timeOfDeath = System.currentTimeMillis();
        ant.getSpriteSheet().setSpriteSheetRow(4);
    }

    @Override
    public void update(double timeSinceUpdate) {
        //If enough time has passed, remove the ant from array
        if(System.currentTimeMillis() > timeOfDeath + DEATH_TIMER){
            ant.removeAnt();
        }
    }

    @Override
    //Ignore collision checks
    public void resolveCollision(Tile tile) {

    }
}
