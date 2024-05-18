package state_machine;

import actors.Tile;
/*
 * ANT COLONY SIMULATION
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     April 20, 2024
 * COURSE:   COMP452 - AI for Game Developers (Athabasca University)
 *
 * State (Interface)
 * Interface used to make custom states for ants.
 *
 * Future Updates/Refactor:
 * Could update this interface to include on exit checks. The constructor is currently used as an on start check.
 * Resolve collision and state swapping could also be done by a state machine class to switch and control the states
 * rather then having each state control the flow of state logic.
 */
public interface State {
    void update(double timeSinceUpdate);
    void resolveCollision(Tile tile);
}
