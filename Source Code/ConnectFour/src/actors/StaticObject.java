package actors;

import game.Vector2D;

import java.awt.image.BufferedImage;
/*
 * ASTAR PATHFINDING VISUALIZER
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     April 11, 2024
 * COURSE:   COMP452 - AI for Game Developers (Athabasca University)
 *
 * Static Object (Child of GameObject)
 * Description:
 * Class used to simplify code readability for Static Elements
 *
 * Future Updates/Refactor:
 * Kind of unnecessary but it made it easier to build the UI
 */

public class StaticObject extends GameObject{

    public StaticObject(BufferedImage sprite, Vector2D location){
        super(sprite);
        setPosition(location);
    }

    @Override
    public void update(double timeSinceUpdate) {

    }
}
