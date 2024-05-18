package state_machine;

import actors.Ant;
import actors.Tile;
import game.AIManager;
/*
 * ANT COLONY SIMULATION
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     April 20, 2024
 * COURSE:   COMP452 - AI for Game Developers (Athabasca University)
 *
 * Foraging State (Interface)
 * Represents the foraging state for ants
 *
 * SAME NOTES AS ALL STATES
 * Future Updates/Refactor:
 * Could update this interface to include on exit checks. The constructor is currently used as an on start check.
 * Resolve collision and state swapping could also be done by a state machine class to switch and control the states
 * rather then having each state control the flow of state logic.
 */
public class Foraging implements State{

    Ant ant;

    public Foraging(Ant ant){
        this.ant = ant;
    }

    @Override
    public void update(double timeSinceUpdate) {
        //Move the ant and check for arrival
        ant.move(timeSinceUpdate);
        AIManager.arrive(ant, ant.getTargetLocation());
    }

    @Override
    public void resolveCollision(Tile tile) {
        //Check for interactions with current tile
        switch (tile.getType()){
            //Continue On
            case GRASS, WATER, HOME -> {
                AIManager.startWander(ant);
                ant.busy();
            }
            //Landed on poison and died
            case POISON -> {
                ant.changeState(new Dead(ant));
            }
            //Found food and can head home
            case FOOD -> {
                ant.changeState(new HeadingHome(ant));
            }
        }
    }
}
