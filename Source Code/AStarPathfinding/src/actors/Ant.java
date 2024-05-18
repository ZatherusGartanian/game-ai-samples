package actors;

import game.Vector2D;

import java.awt.image.BufferedImage;
/*
 * ASTAR PATHFINDING VISUALIZER
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     April 11, 2023
 * COURSE:   COMP452 - AI for Game Developers (Athabasca University)
 *
 * Ant (Child of GameObject)
 * Ant class used to represent the start location of the game.
 *
 * Future Updates/Refactor:
 * Built preemptively for the next part of the assignment. Realized after the fact that this object had no
 * custom code.
 */

public class Ant extends GameObject{

    public Ant(Sprite sprite, Vector2D location){
        super(sprite);
        setPosition(location);
    }

    @Override
    public void update(double timeSinceUpdate) {

    }
}
