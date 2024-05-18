package game;
import scenes.*;
import input.InputController;

import java.awt.*;
/*
 * WEREWOLF SURVIVORS GAME
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     March 27
 * COURSE:   COMP452 - AI for Game Developers (Athabasca University)
 *
 * GameManager
 * Description:
 * Handles the frame rate and game looping. Controls the flow of control for the system.
 *
 * Future Updates/Refactor:
 * Pretty happy with this class. Could attempt to add multithreading. The use of "GameState" feels awkward and changing
 * how "Scenes" function may change in future builds. Input Control feels the most awkward as one scene should have a
 * cleaner solution for handling multiple input states.
 */

public class GameManager implements Runnable{
    //Variables used for framerate tracking (Debugging only)
    private final int MAX_FPS = 120;
    private int frameCount;
    public int fps;

    //Game Object Referencing
    private GameWindow gameWindow;
    private InputController inputController;
    private GameCore gameCore;
    private Scene currentScene;

    //Thread Variables
    private boolean isRunning = false;
    private Thread gameThread;

    GameManager(){
        //Start with the mainMenu
        gameCore = new GameCore(this);
        currentScene = gameCore;

        //Build the input Controller and window
        inputController = new InputController(this);
        gameWindow = new GameWindow(this, inputController);

        //Start the looping
        startGameLoop();
    }

    //Stats and restarts the game on the thread
    public void startGameLoop() {
        if(!isRunning) {
            isRunning = true;
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    //Stops the game by ending the current thread
    public void stopGameLoop() {
        isRunning = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Main game loop
    @Override
    public void run() {
        //Variables used for tracking timing and frame speed
        long targetTime = 1000000000 / MAX_FPS; // Calculate the target max frame time in nanoseconds
        long lastFpsTime = System.currentTimeMillis(); //Tracks the last time the FPS was calculated
        long priorFrameStartTime = System.nanoTime(); //Tracks the start of the prior frame
        long extraSleepTime = 0; //Tracks how much extra time the thread slept for

        while (isRunning) {
            long startFrameTime = System.nanoTime();//Stores the time at the start of the frame
            long elapsedTime = startFrameTime - priorFrameStartTime; //Finds how long the prior frame took
            priorFrameStartTime = startFrameTime; //Reset the priorFrameStartTime for next check

            // Convert elapsed time to seconds (Allows game values to be per second values and not per nanosecond)
            double deltaTime = (double) elapsedTime / 1000000000.0;

            // Perform game logic update with delta time and then draw
            update(deltaTime);
            gameWindow.getViewPort().repaint();

            //Used to prevent the system from overlooping and using excessive CPU


            //Calculate remaining time until the next frame based on targetTime per frame.
            long timeBeforeSleep = System.nanoTime(); //Stores the time before the sleep starts
            long remainingTime = targetTime - (timeBeforeSleep-startFrameTime) - extraSleepTime;

            //If the remaining time is more then a millisecond put the thread to sleep for the remaining time in the frame
            if (remainingTime > 1000000) {
                try {
                    // Sleep if there's remaining time until the next frame
                    Thread.sleep(remainingTime/1000000); // Convert nanoseconds to milliseconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //Calculate the additional time the thread was asleep for to remove that time off the next frames sleep
                extraSleepTime = System.nanoTime() - timeBeforeSleep - remainingTime;
            }
            //If the time remaining is less the a millisecond then busy wait loop until the target time.
            else {
                while (System.nanoTime() - startFrameTime < targetTime) {
                    // Busy wait
                }
                extraSleepTime = 0; //No additional time to recover on the next frame
            }

            //Calculate FPS
            frameCount++;
            //If a second has passed since the last check, set the fps and reset the frameCount.
            if (System.currentTimeMillis() - lastFpsTime >= 1000) {
                fps = frameCount;
                frameCount = 0;
                lastFpsTime = System.currentTimeMillis();
            }
        }
    }

    //Sends the update to the current scene. Is a method in case the game manager wants to be used to also do some updates.
    public void update(double deltaTime){
        currentScene.update(deltaTime);
    }

    //Is called by the call to repaint to the JPanel. Takes the drawing surface and passes it to the current scene.
    public void draw(Graphics g){

        currentScene.draw(g);

        /*FRAME RATE DEBUGGING. REMOVE COMMENTS TO SEE FRAMERATE
        // Draw FPS
        String fpsText = "FPS: " + fps;
        g.setColor(Color.RED);
        g.drawString(fpsText, 10, 565);
         */
    }

    //Changes the current scene based on the game state. Either creates a new object of that scene or swaps to the stored
    //scene. MainMenu doesnt need to be stored and is currently wasted memory once the game launches.
    public void changeState(GameState newState){

        switch(newState) {
            case MENU:
                break;
            case GAME:
                gameCore = new GameCore(this);
                currentScene = gameCore;
                break;
            case GAMEOVER:
            default:
                break;
        }
    }

    //Getter for the current scene
    public Scene getCurrentScene(){return currentScene;}
}