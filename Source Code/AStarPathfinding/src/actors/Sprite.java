package actors;

import java.awt.image.BufferedImage;
/*
 * ASTAR PATHFINDING VISUALIZER
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     April 11, 2023
 * COURSE:   COMP452 - AI for Game Developers (Athabasca University)
 *
 * Sprite
 * Used to represent the visual of an object. Contains animation information for handling sprite sheet.
 *
 * Future Updates/Refactor:
 * Quick set up to get animations implemented in the engine. Classes will need to understand their spritesheets in
 * order to handle multiple rows. If one row is idle animation and another is for running, then the class will need to
 * swap rows when it changes its state. One other way to handle this would be to have unique sprites for each state of a
 * characer and do not use rows in spritesheets. I have usually done it the other way in the past so I decided to give
 * using rows a try here since it is common practice in 2D engines. One issue I have with using multi row sheets is that
 * it can make it difficult for one sprite to have different frame speeds. Sometimes it is nice to have a slow 3 frame
 * idle animation and then have a much faster smoother attacking or running speed. For this course though this will work
 * well.
 *
 * Looking at this again should this instead be using delta time? This version will work regardless of FPS but delta time
 * may be a cleaner way to represent framePeriod.
 */
public class Sprite {
    //Image file
    private BufferedImage spriteSheet;

    //Size of the sprite (Not the sheet)
    private int spriteWidth;
    private int spriteHeight;

    //Variables used for tracking animation frame
    private int frameCount;
    private int currentFrame;
    private int currentRow;
    private long lastFrameTime;
    private int framePeriod;

    public Sprite(BufferedImage spriteSheet, int spriteWidth, int spriteHeight, int animationFPS){
        this.spriteSheet = spriteSheet;
        this.spriteWidth = spriteWidth;
        this.spriteHeight = spriteHeight;

        //Initialize trackers
        frameCount = spriteSheet.getWidth()/spriteWidth;
        framePeriod = 1000/ animationFPS; //Sets number of milliseconds that each frame should last for
        lastFrameTime = 0l;
        currentFrame = 0;
        currentRow = 0;
    }

    //Gets the current sprite representation from within the image
    public BufferedImage getCurrentSprite(){
        //Update at the speed of the framePeriod
        if(System.currentTimeMillis() > lastFrameTime + framePeriod){
            lastFrameTime = System.currentTimeMillis();
            currentFrame++;
            if(currentFrame >= frameCount){
                currentFrame = 0;
            }
        }

        //Returns the subimage associated with the current frame data
        return spriteSheet.getSubimage(currentFrame*spriteWidth, currentRow*spriteHeight, spriteWidth, spriteHeight);
    }

    //Used to swap the row of the sheet.
    public void setSpriteSheetRow(int newRow){
        currentRow = newRow;
        currentFrame = 0;
    }

    //GETTERS
    public int getSpriteHeight(){
        return spriteHeight;
    }
    public int getSpriteWidth(){
        return spriteWidth;
    }
}
