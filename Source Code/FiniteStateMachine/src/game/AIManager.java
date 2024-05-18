package game;
import actors.*;
import java.util.List;
/*
 * ANT COLONY SIMULATION
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     April 20, 2024
 * COURSE:   COMP452 - AI for Game Developers (Athabasca University)
 *
 * AIManager
 * Description:
 * Contains methods used for solving movement of AI. Contains a custom version of seek with a randomized wander and arrival
 * check.
 *
 * Future Updates/Refactor:
 * Fairly focused use with Ant class. Could replace ant Game Object though and have these methods work for tile based movement.
 * The seek by tile is also a bit inefficient as the math is rechecked every tile move instead of creating a path once and
 * following it. This version worked well though with how the ants state is checked every tile moved.
 *
 */

public class AIManager {

    public static void arrive(Ant ant, Vector2D targetLocation){
        if(Math.abs(targetLocation.x - ant.getXLocation()) < 0.5 && Math.abs(targetLocation.y - ant.getYLocation()) < 0.5){
            ant.setPosition(targetLocation);
            ant.setVelocity(new Vector2D(0, 0));
            ant.ready();
        }
    }

    public static void startWander(Ant ant){
        Direction newDirection = Direction.getRandomDirection();
        Vector2D movementVector = Direction.getDirectionalVector(newDirection);

        //Update Target Location
        float newX = ant.getXLocation() + movementVector.x*50;
        float newY = ant.getYLocation() + movementVector.y*50;

        if(newX < 0 || newX > 799 || newY < 0 || newY > 799){
            if(newDirection == Direction.UP){
                newDirection = Direction.DOWN;
            }
            else if(newDirection == Direction.DOWN){
                newDirection = Direction.UP;
            }
            else if(newDirection == Direction.LEFT){
                newDirection = Direction.RIGHT;
            }
            else if(newDirection == Direction.RIGHT){
                newDirection = Direction.LEFT;
            }
            movementVector = Direction.getDirectionalVector(newDirection);

            //Update Target Location
            newX = ant.getXLocation() + movementVector.x*50;
            newY = ant.getYLocation() + movementVector.y*50;
        }

        //Update Facing
        ant.updateFacing(newDirection);
        ant.setTargetLocation(new Vector2D(newX, newY));

        //Set velocity
        movementVector.scalarMultiply(ant.getMaxSpeed());
        ant.setVelocity(movementVector);
    }

    public static void seekByTile(Ant ant, Vector2D targetLocation){
        //Calculate the vector towards the target
        Vector2D distance = Vector2D.getVectorBetweenPoints(ant.getXLocation(), ant.getYLocation(), targetLocation.x, targetLocation.y);
        Vector2D movementVector;
        Direction direction;
        if(Math.abs(distance.x) >= Math.abs(distance.y)){
            if(distance.x < 0){
                direction = Direction.LEFT;
            }
            else {
                direction = Direction.RIGHT;
            }
        }
        else{
            if(distance.y < 0){
                direction = Direction.UP;
            }
            else {
                direction = Direction.DOWN;
            }
        }

        ant.updateFacing(direction);
        movementVector = Direction.getDirectionalVector(direction);

        //Update Target Location
        float newX = ant.getXLocation() + movementVector.x*50;
        float newY = ant.getYLocation() + movementVector.y*50;

        ant.setTargetLocation(new Vector2D(newX, newY));

        //Set velocity
        movementVector.scalarMultiply(ant.getMaxSpeed());
        ant.setVelocity(movementVector);
    }
}
