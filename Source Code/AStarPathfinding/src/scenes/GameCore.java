package scenes;

import actors.*;
import game.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
/*
 * ASTAR PATHFINDING VISUALIZER
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     April 11, 2023
 * COURSE:   COMP452 - AI for Game Developers (Athabasca University)
 *
 * GameCore (Child of Scene)
 * Description:
 * Main game play scene that has all the logic for the game loop.
 *
 * Future Updates/Refactor:
 * Contains a lot of UI logic. For this assignment it felt fine since it is less of a game, but the brush and buttons
 * should likely have been set up as a UI element. I am still not super happy with the way input and scenes are handled but
 * since it is working I chose to avoid an additional refactor
 */

public class GameCore extends Scene{
    //Game Objects
    private Ant ant;
    private Tile[][] tileMap;
    private StaticObject goal;

    //Sprite References
    private BufferedImage desertSprite;
    private BufferedImage grassSprite;
    private BufferedImage swampSprite;
    private BufferedImage barrierSprite;
    private BufferedImage uiBackground;
    private BufferedImage goalSprite;

    //Class References
    private GameManager gameManager;
    private AStar pathfinding;

    //UI Elements
    private final int TILE_OFFSET = 100;
    private Tile grassButton;
    private Tile desertButton;
    private Tile swampButton;
    private Tile barrierButton;
    private StaticObject goalButton;
    private Ant antButton;
    private StaticObject startButton;
    private StaticObject resetButton;
    private StaticObject noPathFound;

    //State variables
    private boolean goalSet = false; //Must be set to true to start
    private boolean antSet = false; //Must be set to true to start
    private boolean started = false; //Used to turn of UI once the pathfinding starts
    private boolean completed = false; //Used to stop updates and display end
    private boolean noPath = false; //Flag for if no path was found

    //Variables for the brush
    private Tile carriedTile;
    private BrushType brushType = BrushType.NONE;

    //Timers
    private long lastUpdatedTime = 0; //Track last update time
    private long timeBetweenUpdates = 1000; //Wait 1 second between updates

    //Mouse Location
    private Vector2D mouseLocation;

    public GameCore(GameManager gameManager){
        this.gameManager = gameManager;
        initializeGame();
    }

    //Initialization for the game elements.
    private void initializeGame(){
        //Load Sprites
        desertSprite = ResourceLoader.loadImage(ResourceLoader.DESERT_TILE);
        swampSprite = ResourceLoader.loadImage(ResourceLoader.SWAMP_TILE);
        grassSprite = ResourceLoader.loadImage(ResourceLoader.GRASS_TILE);
        barrierSprite = ResourceLoader.loadImage(ResourceLoader.BARRIER_TILE);
        uiBackground = ResourceLoader.loadImage(ResourceLoader.UI_BACKGROUND);
        goalSprite = ResourceLoader.loadImage(ResourceLoader.GOAL);

        //Construct buttons
        Vector2D buttonLocation = new Vector2D(25, 25);
        grassButton = new Tile(grassSprite, buttonLocation, TileType.GRASS);
        buttonLocation = new Vector2D(25, 100);
        desertButton = new Tile(desertSprite, buttonLocation, TileType.DESERT);
        buttonLocation = new Vector2D(25, 175);
        swampButton = new Tile(swampSprite, buttonLocation, TileType.SWAMP);
        buttonLocation = new Vector2D(25, 250);
        barrierButton = new Tile(barrierSprite, buttonLocation, TileType.BARRIER);
        buttonLocation = new Vector2D(25, 325);
        goalButton = new StaticObject(goalSprite, buttonLocation);

        //Initialize goal and set to same location as button
        goal = new StaticObject(goalSprite, buttonLocation);

        //Load start location button and actor
        BufferedImage spriteSheet = ResourceLoader.loadImage(ResourceLoader.ANT);
        Sprite antSprite = new Sprite(spriteSheet, 50, 50, 10);
        buttonLocation = new Vector2D(25, 400);
        ant = new Ant(antSprite, buttonLocation);
        antButton = new Ant(antSprite, buttonLocation);

        //Load Start button
        BufferedImage temp = ResourceLoader.loadImage(ResourceLoader.START);
        buttonLocation = new Vector2D(25, 700);
        startButton = new StaticObject(temp, buttonLocation);

        //Load Reset Button
        temp = ResourceLoader.loadImage(ResourceLoader.RESET);
        buttonLocation = new Vector2D(25, 625);
        resetButton = new StaticObject(temp, buttonLocation);

        //Load No Path Found Image
        temp = ResourceLoader.loadImage(ResourceLoader.NO_PATH);
        buttonLocation = new Vector2D((900/2)-(temp.getWidth()/2), (800/2)-(temp.getHeight()/2));
        noPathFound = new StaticObject(temp, buttonLocation);

        //Initialize map with grass tiles
        tileMap = new Tile[16][16];
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){
                Vector2D location = new Vector2D((i*grassSprite.getWidth()+100), j*grassSprite.getHeight());
                tileMap[i][j] = new Tile(grassSprite, location, TileType.GRASS);
            }
        }

        //Initialize brush and mouse variables
        mouseLocation = new Vector2D(0, 0);
        carriedTile = grassButton;
    }

    //Draw each element of the game. Order matters. Lowest layer elements first (backgrounds), to highest layer (UI).
    @Override
    public void draw(Graphics g) {
        //Draw the tiles
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){
                tileMap[i][j].draw(g);
            }
        }
        //Draw the UI and a backdrop for it
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 100, 800);
        g.drawImage(uiBackground, 0, 0, 100, 800, null);

        //Draw all the buttons
        grassButton.draw(g);
        desertButton.draw(g);
        swampButton.draw(g);
        barrierButton.draw(g);
        goalButton.draw(g);
        antButton.draw(g);

        //Draw the start and goal locations
        goal.draw(g);
        ant.draw(g);

        //If carrying a tile draw the carried tile (if carrying the goal or ant they will automatically follow the brush)
        if(brushType == BrushType.TILE){
            carriedTile.draw(g);
        }

        //If the ant and goal are set then draw the button. This logic should techinically be done outside draw and flag
        //a visability flag but this was simple for the purposes done here
        if(antSet && goalSet){
            startButton.draw(g);
        }

        //If started draw the pathfinding data
        if(started){
            pathfinding.draw(g);
        }

        //If completed draw the rest button and the no path found if necessary
        if(completed){
            resetButton.draw(g);
            if(noPath){
                noPathFound.draw(g);
            }
        }
    }

    //Once started run update of AStar every second to allow user to visualize the path
    @Override
    public void update(double deltaTime) {
        if(started && !completed) {
            if (System.currentTimeMillis() > lastUpdatedTime + timeBetweenUpdates) {
                lastUpdatedTime = System.currentTimeMillis();
                pathfinding.update();
            }
        }
    }

    //Starts the pathfinding using the set locations
    public void startPathfinding(){
        pathfinding = new AStar(this);

        //Get arrayLocation vectors for the goal and start
        Vector2D startLocation = new Vector2D((ant.getXLocation() - TILE_OFFSET) / grassSprite.getWidth(), ant.getYLocation() / grassSprite.getHeight());
        Vector2D goalLocation = new Vector2D((goal.getXLocation() - TILE_OFFSET) / grassSprite.getWidth(), goal.getYLocation() / grassSprite.getHeight());

        //Start the path
        pathfinding.findPath(tileMap, startLocation, goalLocation);
        started = true; //Update state for game
    }

    //Called once pathfinding is completed
    public void donePathfinding(boolean pathFound){
        //Set game state variables for results
        completed = true;
        if(!pathFound){
            noPath = true;
        }
    }

    //Store the mouse position.
    @Override
    public void mouseMoved(MouseEvent e) {
        mouseLocation.x = e.getX();
        mouseLocation.y = e.getY();

        //Update the location of objects if they are being carried
        if(brushType == BrushType.TILE){
            carriedTile.setPosition(mouseLocation);
        }
        else if(brushType == BrushType.GOAL){
            goal.setPosition(mouseLocation);
        }
        else if(brushType == BrushType.ANT){
            ant.setPosition(mouseLocation);
        }
    }

    //Check for when the mouse is pressed
    @Override
    public void mousePressed(MouseEvent e) {
        //Check if game is in completed state to allow for user to click on reset button
        if(completed){
            if(e.getX()<100){
                if(resetButton.getHitBox().contains(e.getPoint())){
                    gameManager.changeState(GameState.GAME);
                }
            }
        }

        //Once started prevent all other controls
        if(started){
            return;
        }

        //Track what the brush was before clicking to avoid bugs when holding goal and start objects
        BrushType preClickBrush = brushType;

        //First check to see if mouse is within the UI bounds to simplify checking
        //Checks which button is being pressed if any and then sets the brush type and object data
        if(e.getX()<100){
            //Check if clicking grass button
            if(grassButton.getHitBox().contains(e.getPoint())){
                carriedTile = new Tile(grassSprite, mouseLocation, TileType.GRASS);
                brushType = BrushType.TILE;
            }
            //Check if clicking desert button
            else if(desertButton.getHitBox().contains(e.getPoint())){
                carriedTile = new Tile(desertSprite, mouseLocation, TileType.DESERT);
                brushType = BrushType.TILE;
            }
            //Check if clicking swamp button
            else if(swampButton.getHitBox().contains(e.getPoint())){
                carriedTile = new Tile(swampSprite, mouseLocation, TileType.SWAMP);
                brushType = BrushType.TILE;
            }
            //Check if clicking wall button
            else if(barrierButton.getHitBox().contains(e.getPoint())){
                carriedTile = new Tile(barrierSprite, mouseLocation, TileType.BARRIER);
                brushType = BrushType.TILE;
            }
            //Check if pressing goal button
            else if(goalButton.getHitBox().contains(e.getPoint())){
                goal.setPosition(mouseLocation);
                goalSet = false;
                brushType = BrushType.GOAL;
            }
            //Check if pressing ant button
            else if(antButton.getHitBox().contains(e.getPoint())){
                ant.setPosition(mouseLocation);
                antSet = false;
                brushType = BrushType.ANT;
            }
            //Check if pressing start button
            else if(startButton.getHitBox().contains(e.getPoint())){
                //Make sure the goal and ant are set before initializing
                if(goalSet && antSet){
                    brushType = BrushType.NONE;
                    startPathfinding();
                }
            }
        }

        //If the user was carrying the ant or goal and switched to a different brush then reset the locations
        //so they do not hover in the last held location.
        if(preClickBrush == BrushType.ANT && brushType != preClickBrush){
            ant.setPosition(new Vector2D(antButton.getXLocation(), antButton.getYLocation()));
        }
        if(preClickBrush == BrushType.GOAL && brushType != preClickBrush){
            goal.setPosition(new Vector2D(goalButton.getXLocation(), goalButton.getYLocation()));
        }
    }

    //Check for when the mouse is released
    @Override
    public void mouseReleased(MouseEvent e) {
        //If the pathfinding has started then return to prevent controls
        if(started){
            return;
        }

        //If mouse release in UI area do nothing
        if (e.getX() < TILE_OFFSET) {
            return;
        }

        //EDGE CASE
        //If button pressed inside screen but released outside game screen do nothing
        Rectangle screenBounds = new Rectangle(0, 0, 900, 800);
        if (!screenBounds.contains(e.getPoint())) {
            return;
        }

        //Check the current BrushType and apply brush to the clicked tile location.
        if(brushType == BrushType.TILE) {
            //If placing a tile ontop of goal or start, make sure to reset state and objects
            if(goal.getHitBox().contains(e.getPoint())){
                //Rest Goal Position
                goal.setPosition(new Vector2D(goalButton.getXLocation(), goalButton.getYLocation()));
                goalSet = false;
            }
            else if(ant.getHitBox().contains(e.getPoint())){
                //Rest Ant Position
                ant.setPosition(new Vector2D(antButton.getXLocation(), antButton.getYLocation()));
                antSet = false;
            }

            //Update the tile at the clicked location to match the carried type
            Vector2D arrayLocation = new Vector2D((e.getX() - TILE_OFFSET) / grassSprite.getWidth(), e.getY() / grassSprite.getHeight());
            tileMap[(int) arrayLocation.x][(int) arrayLocation.y].changeType(carriedTile.getType(), carriedTile.getSprite());
        }
        //If using the goal brush
        else if(brushType == BrushType.GOAL){
            //Get the array location for the clicked tile to check if its a valid goal placement (Not a wall)
            Vector2D arrayLocation = new Vector2D((e.getX() - TILE_OFFSET) / grassSprite.getWidth(), e.getY() / grassSprite.getHeight());
            Tile goalTile = tileMap[(int) arrayLocation.x][(int) arrayLocation.y];
            if(goalTile.getType() == TileType.BARRIER){
                //Cannot place goal on barrier tile
                return;
            }

            //Set the location of the goal to the tile location (makes it snap to grid instead of using the click location)
            Vector2D newLocation = new Vector2D(goalTile.getXLocation(), goalTile.getYLocation());
            goal.setPosition(newLocation);

            //Check if the goal and ant are on the same tile and reset the ant if so
            if(goal.getHitBox().intersects(ant.getHitBox())){
                ant.setPosition(new Vector2D(antButton.getXLocation(), antButton.getYLocation()));
                antSet = false;
            }

            //Goal has been set
            goalSet = true;

            //Reset Brush
            brushType = BrushType.NONE;
        }
        //If using the start brush
        else if(brushType == BrushType.ANT){
            //Get the array location for the clicked tile to check if its a valid start placement (Not a wall)
            Vector2D arrayLocation = new Vector2D((e.getX() - TILE_OFFSET) / grassSprite.getWidth(), e.getY() / grassSprite.getHeight());
            Tile startTile = tileMap[(int) arrayLocation.x][(int) arrayLocation.y];
            if(startTile.getType() == TileType.BARRIER){
                //Cannot place start on barrier tile
                return;
            }

            //Set the location of the start to the tile location (makes it snap to grid instead of using the click location)
            Vector2D newLocation = new Vector2D(startTile.getXLocation(), startTile.getYLocation());
            ant.setPosition(newLocation);

            //Check if the goal and ant are on the same tile and reset the goal if so
            if(ant.getHitBox().intersects(goal.getHitBox())){
                goal.setPosition(new Vector2D(goalButton.getXLocation(), goalButton.getYLocation()));
                goalSet = false;
            }

            //Start has been set
            antSet = true;

            //Reset the brush
            brushType = BrushType.NONE;
        }
    }


    //NOT USED
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyPressed(KeyEvent e) {}
}
