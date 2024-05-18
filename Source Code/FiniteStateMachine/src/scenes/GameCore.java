package scenes;

import actors.Ant;
import actors.Sprite;
import actors.StaticObject;
import actors.Tile;
import game.*;
import state_machine.Foraging;
import state_machine.HeadingHome;
import state_machine.Thirsty;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

/*
 * ANT COLONY SIMULATION
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     April 19, 2023
 * COURSE:   COMP452 - AI for Game Developers (Athabasca University)
 *
 * GameCore (Child of Scene)
 * Description:
 * Main game loop for the ant colony simulation.
 *
 * Future Updates/Refactor:
 * UI and overlays is still a bit of an issue and clutter in the game loop. Ideally UI elements should be moved out to
 * their own classes.
 */

public class GameCore extends Scene{
    //Game Objects
    private Tile[][] tileMap; //Map layout
    private ArrayList<Ant> ants = new ArrayList<>(); //Track all current ants
    private ArrayList<Ant> antsToRemove = new ArrayList<>(); //Track ants that need to be removed from array (died last cycle)
    private int antsToAdd = 0; //Tracks how many ants need to be added to the colony
    private Vector2D homeTile; //Stores the home location for spawning new ants

    //Image References
    private BufferedImage antSpriteSheet;
    private BufferedImage antSelectUI;
    private BufferedImage foodThought;
    private BufferedImage waterThought;
    private BufferedImage homeThought;

    //UI Info
    private StaticObject upButtonTens;
    private StaticObject downButtonTens;
    private StaticObject upButtonOnes;
    private StaticObject downButtonOnes;
    private StaticObject startButton;
    private StaticObject resetButton;
    //Tracks and display the information for starting ant size
    private int onesUnit = 1;
    private int tensUnit = 0;
    private int antsAtStart = 1;
    private String onesUnitAsString;
    private String tensUnitAsString;

    //Game State Variables
    private boolean setup = true; //Set to false when check mark is hit to confirm ant colony size
    private boolean endSimulation = false; //Set to true when the simulation is completed. Used to end the simulator

    //Class References
    private GameManager gameManager;

    public GameCore(GameManager gameManager){
        this.gameManager = gameManager;

        initializeGame();
    }

    //Initialization for the game elements.
    private void initializeGame(){
        BufferedImage grassSprite = ResourceLoader.loadImage(ResourceLoader.GRASS_TILE);
        BufferedImage foodSprite = ResourceLoader.loadImage(ResourceLoader.FOOD_TILE);
        BufferedImage waterSprite = ResourceLoader.loadImage(ResourceLoader.WATER_TILE);
        BufferedImage poisonSprite = ResourceLoader.loadImage(ResourceLoader.POISON_TILE);
        BufferedImage antHillSprite = ResourceLoader.loadImage(ResourceLoader.ANT_HILL);
        BufferedImage upArrow = ResourceLoader.loadImage(ResourceLoader.UP_ARROW);
        BufferedImage downArrow = ResourceLoader.loadImage(ResourceLoader.DOWN_ARROW);
        BufferedImage startSprite = ResourceLoader.loadImage(ResourceLoader.START);
        BufferedImage resetSprite = ResourceLoader.loadImage(ResourceLoader.RESET);
        foodThought = ResourceLoader.loadImage(ResourceLoader.FOOD_THOUGHT);
        waterThought = ResourceLoader.loadImage(ResourceLoader.WATER_THOUGHT);
        homeThought = ResourceLoader.loadImage(ResourceLoader.HOME_THOUGHT);

        antSelectUI = ResourceLoader.loadImage(ResourceLoader.UI);
        antSpriteSheet = ResourceLoader.loadImage(ResourceLoader.ANT);


        //Initialize the buttons and UI data
        Vector2D location = new Vector2D(350, 350);
        upButtonTens = new StaticObject(upArrow, location);
        location = new Vector2D(350, 450);
        downButtonTens = new StaticObject(downArrow, location);
        location = new Vector2D(400, 350);
        upButtonOnes = new StaticObject(upArrow, location);
        location = new Vector2D(400, 450);
        downButtonOnes = new StaticObject(downArrow, location);
        location = new Vector2D(450, 400 - startSprite.getHeight()/2);
        startButton = new StaticObject(startSprite, location);
        location = new Vector2D( 400 - resetSprite.getWidth()/2, 500);
        resetButton = new StaticObject(resetSprite, location);


        //Randomize values for poison, water, food and locations
        Random random = new Random();
        //Generate a start location for the ant colony
        int homeXLocation = random.nextInt(16);
        int homeYLocation = random.nextInt(16);
        int poisonCounter = 0;
        int waterCounter = 0;
        int foodCounter = 0;
        int amountPoison = random.nextInt(8)+3; //3-10
        int amountWater = random.nextInt(8)+3; //3-10
        int amountFood = random.nextInt(8)+3; //3-10

        //Start by setting all locations to grass tiles and the randomized home location
        tileMap = new Tile[16][16];
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){
                location = new Vector2D((i*grassSprite.getWidth()), j*grassSprite.getHeight());
                //Place the ant hill
                if(i == homeXLocation && j == homeYLocation){
                    tileMap[i][j] = new Tile(antHillSprite, location, TileType.HOME);
                    homeTile = location;
                    continue;
                }
                tileMap[i][j] = new Tile(grassSprite, location, TileType.GRASS);
            }
        }

        //LOOPS COULD IN THEORY LOOP FOREVER BUT IS INCREDIBLY UNLIKELY
        //Keep searching for a new location to spawn water (only if the random location grabbed is a grass tile
        while(waterCounter < amountWater){
            int randomizeX = random.nextInt(16);
            int randomizeY = random.nextInt(16);

            if(tileMap[randomizeX][randomizeY].getType() == TileType.GRASS){
                tileMap[randomizeX][randomizeY].changeType(TileType.WATER, waterSprite);
                waterCounter++;
            }
        }
        //Keep searching for a new location to spawn food (only if the random location grabbed is a grass tile
        while(foodCounter < amountFood){
            int randomizeX = random.nextInt(16);
            int randomizeY = random.nextInt(16);

            if(tileMap[randomizeX][randomizeY].getType() == TileType.GRASS){
                tileMap[randomizeX][randomizeY].changeType(TileType.FOOD, foodSprite);
                foodCounter++;
            }
        }

        //Keep searching for a new location to spawn poison (only if the random location grabbed is a grass tile
        while(poisonCounter < amountPoison){
            int randomizeX = random.nextInt(16);
            int randomizeY = random.nextInt(16);

            if(tileMap[randomizeX][randomizeY].getType() == TileType.GRASS){
                tileMap[randomizeX][randomizeY].changeType(TileType.POISON, poisonSprite);
                poisonCounter++;
            }
        }

        updateAntCount();
    }

    //Adds an ant  to the colony
    public void addAnt(){
        //Create a new sprite for each ant so they can have their own frame data
        Sprite antSprite = new Sprite(antSpriteSheet, 50, 50, 10);
        ants.add(new Ant(antSprite, homeTile, this));
    }

    //Food was added the colony. Updates ant counter to spawn more ants.
    public void addedFood(){
        antsToAdd++;
    }

    //Marks a specific ant for removal from the list
    public void markAntForRemoval(Ant ant){
        antsToRemove.add(ant);
    }

    //Draw each element of the game. Order matters. Lowest layer elements first (backgrounds), to highest layer (UI).
    @Override
    public void draw(Graphics g) {
        //Draw the tilemap
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){
                tileMap[i][j].draw(g);
            }
        }

        //If the simulation is over display the end screen content
        if(endSimulation){
            g.setFont(new Font("TimesRoman", Font.BOLD, 50));
            g.setColor(Color.WHITE);
            g.drawImage(antSelectUI, 400 - antSelectUI.getWidth()/2, 400 - antSelectUI.getHeight()/2, null);
            FontMetrics fm = g.getFontMetrics();
            //If the colony completely died off
            if(ants.size() == 0){
                String endMessage = "COLONY DIED OFF";
                int textXLocation = (800 - fm.stringWidth(endMessage)) / 2;
                g.drawString(endMessage, textXLocation, 400);
            }
            //Otherwise max colony size was hit
            else{
                String endMessage = "COLONY HIT MAX SIZE";
                int textXLocation = (800 - fm.stringWidth(endMessage)) / 2;
                g.drawString(endMessage, textXLocation, 400);
            }

            //Draw the reset button
            resetButton.draw(g);

            //Break outta the update
            return;
        }

        //If still in setup state, draw the UI information for setup
        if(setup){
            g.drawImage(antSelectUI, 400 - antSelectUI.getWidth()/2, 400 - antSelectUI.getHeight()/2, null);
            upButtonTens.draw(g);
            downButtonTens.draw(g);
            upButtonOnes.draw(g);
            downButtonOnes.draw(g);
            startButton.draw(g);
            g.setFont(new Font("TimesRoman", Font.BOLD, 50));
            g.setColor(Color.WHITE);
            g.drawString("SET COLONY SIZE", 175, 300);
            g.drawString(tensUnitAsString, 350, 425);
            g.drawString(onesUnitAsString, 400, 425);
        }
        //Otherwise draw the simulation
        else {
            //Draw all ants
            for (Ant ant : ants) {
                ant.draw(g);

                //Check what the current state of the ant is and add thought bubble
                if(ant.getCurrentState() instanceof Foraging){
                    g.drawImage(foodThought, (int)ant.getXLocation() + 25, (int)ant.getYLocation(), 25,25, null);
                }
                else if(ant.getCurrentState() instanceof HeadingHome){
                    g.drawImage(homeThought, (int)ant.getXLocation() + 25, (int)ant.getYLocation(), 25,25, null);
                }
                else if(ant.getCurrentState() instanceof Thirsty){
                    g.drawImage(waterThought, (int)ant.getXLocation() + 25, (int)ant.getYLocation(), 25,25, null);
                }
            }
            //Basic screen tracker for number of ants alive
            g.setFont(new Font("TimesRoman", Font.BOLD, 30));
            g.setColor(Color.WHITE);
            String antsAlive = "Ants: " + ants.size();
            g.drawString(antsAlive, 20, 40);
        }
    }

    @Override
    public void update(double deltaTime) {
        //Break update if simulation is over
        if(endSimulation){
            return;
        }
        //Update the display and ant values during setup
        if(setup){
            updateAntCount();
        }
        //Otherwise the simulation is running and update all the ants
        else {
            //Update all the ants
            for (Ant ant : ants) {
                ant.update(deltaTime);
            }

            //Remove any ants that died
            ants.removeAll(antsToRemove);
            antsToRemove.clear();

            //Add ants based on the amount of food returned
            for (int i = antsToAdd; i > 0; i--) {
                addAnt();
                antsToAdd--;
            }

            //Check for collisions
            collisionDetection();

            //If ant size ever hits 0 or is more then 70 then end simulation
            if(ants.size() > 70 || ants.size() == 0){
                endSimulation = true;
            }
        }
    }

    public void collisionDetection(){
        //Check each ant to see if it is idle/ready to check a new tile location
        for(Ant ant: ants){
            if(ant.isReady()){
                Tile collidingTile = tileMap[(int)ant.getXLocation()/50][(int)ant.getYLocation()/50];
                ant.resolveCollision(collidingTile);
            }
        }
    }

    //Updates the trackers for ant counters used during setup.
    private void updateAntCount(){
        tensUnitAsString = "" + tensUnit;
        onesUnitAsString = "" + onesUnit;
        antsAtStart = tensUnit*10 + onesUnit;
    }

    //Starts the simulation by adding ants and flagging setup as over
    private void start(){
        for(int i = antsAtStart; i > 0; i--){
            addAnt();
        }

        setup = false;
    }



    @Override
    //Checks for mouse clicks
    public void mousePressed(MouseEvent e) {
        //If during setup
        if(setup) {
            //If the up button on tens was hit increase and check for max of 5 (adjust ones unit appropriately)
            if (upButtonTens.getHitBox().contains(e.getPoint())) {
                tensUnit++;
                if (tensUnit >= 5) {
                    tensUnit = 5;
                    onesUnit = 0;
                }
            }
            //If the down button on tens was pressed decrease and check if at 0. If decreasing to 0 make sure ones is at least 1.
            if (downButtonTens.getHitBox().contains(e.getPoint())) {
                tensUnit--;
                if (tensUnit < 1) {
                    tensUnit = 0;
                    if (onesUnit < 1) {
                        onesUnit = 1;
                    }
                }
            }
            //If the up button on ones is hit check if tens is at 5 (max) and cap at 0. If cycling past 9 go back to 0 and increase tens.
            if (upButtonOnes.getHitBox().contains(e.getPoint())) {
                if (tensUnit >= 5) {
                    onesUnit = 0;
                    return;
                }

                onesUnit++;
                if (onesUnit > 9) {
                    onesUnit = 0;
                    tensUnit++;
                }
            }
            //If the down button ones is hit check if at minimum of 1. Check if decreasing from 0 to 9 and decrement tens.
            if (downButtonOnes.getHitBox().contains(e.getPoint())) {

                onesUnit--;
                if (tensUnit == 0 && onesUnit < 1) {
                    onesUnit = 1;
                    return;
                }

                if (onesUnit < 0) {
                    onesUnit = 9;

                    if (tensUnit >= 1) {
                        tensUnit--;
                    }
                }
            }
            //If the start button is hit, start the simulation
            if (startButton.getHitBox().contains(e.getPoint())) {
                start();
            }
        }

        //If the simulalation is over check for reset button press
        if(endSimulation){
            if(resetButton.getHitBox().contains(e.getPoint())){
                gameManager.changeState(GameState.GAME);
            }
        }
    }

    //NOT USED
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    //Store the mouse position.
    @Override
    public void mouseMoved(MouseEvent e) {
    }
    //Handle key presses for player movement
    @Override
    public void keyPressed(KeyEvent e) {
    }
    //Handle key releases for player movement
    @Override
    public void keyReleased(KeyEvent e) {
    }

}
