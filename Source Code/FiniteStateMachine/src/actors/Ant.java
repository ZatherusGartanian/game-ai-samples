package actors;

import game.Direction;
import game.Vector2D;
import scenes.GameCore;
import state_machine.*;

/*
 * ANT COLONY SIMULATION
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     April 20, 2024
 * COURSE:   COMP452 - AI for Game Developers (Athabasca University)
 *
 * Ant (Child of GameObject)
 * Ant class used to represent a single ant within the colony
 *
 * Future Updates/Refactor:
 * Nothing major to note.
 */

public class Ant extends GameObject{
    private Direction facing = Direction.RIGHT; //Tracks facing direction for updating sprite sheet
    private Sprite sprite; //Reference to sprite sheet for updating row
    private Vector2D targetLocation; //Current target movement
    private boolean ready = true; //Used to track when idle and busy
    private State currentState; //Tracks current state
    private GameCore gameCore; //Reference to game class
    private Vector2D homeLocation; //Home location for pathfinding back home

    public Ant(Sprite sprite, Vector2D location, GameCore gameCore){
        super(sprite);
        setPosition(location);

        maxSpeed = 50;
        homeLocation = new Vector2D(location.x, location.y);
        targetLocation = new Vector2D(location.x, location.y);
        currentState = new Foraging(this);

        this.sprite = sprite;
        this.gameCore = gameCore;
    }

    @Override
    //On update let whatever is current state make the decision
    public void update(double timeSinceUpdate) {
        currentState.update(timeSinceUpdate);
    }

    //Resovle the current collision based on the current state
    public void resolveCollision(Tile tile){
        currentState.resolveCollision(tile);
    }

    //Change state to the provided state
    public void changeState(State newState){
        currentState = newState;
    }

    //Updates the spritesheet to the correct row for animation use
    public void updateFacing(Direction direction){
        facing = direction;
        switch (facing) {
            case UP -> sprite.setSpriteSheetRow(3);
            case RIGHT -> sprite.setSpriteSheetRow(0);
            case DOWN -> sprite.setSpriteSheetRow(2);
            case LEFT -> sprite.setSpriteSheetRow(1);
        }
    }

    //When arriving home let the game know to add food to the colony
    public void arrivedHome(){
        gameCore.addedFood();
    }

    //When dead update the game to remove object
    public void removeAnt(){
        gameCore.markAntForRemoval(this);
    }

    //Set the target location for movement
    public void setTargetLocation(Vector2D newTarget){
        targetLocation.x = newTarget.x;
        targetLocation.y = newTarget.y;
    }

    //Setters for ready boolean
    public void ready(){ready = true;}
    public void busy(){ready = false;}

    //GETTERS
    public State getCurrentState(){return currentState;}
    public Vector2D getTargetLocation(){
        return targetLocation;
    }
    public boolean isReady(){return ready;}
    public Vector2D getHomeLocation(){return homeLocation;}
}


