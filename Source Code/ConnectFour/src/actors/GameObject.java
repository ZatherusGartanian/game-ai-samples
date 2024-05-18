package actors;
import game.Vector2D;

import java.awt.*;
import java.awt.image.BufferedImage;
/*
 * WEREWOLF SURVIVORS GAME
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     March 27, 2024
 * UPDATED:  April 11, 2024
 * COURSE:   COMP452 - AI for Game Developers (Athabasca University)
 *
 * GameObject Astract Class
 * Description:
 * Defines the structure for objects that will exist within the game.
 *
 * Future Updates/Refactor:
 * Velocity and movement also need to be refactored to use Vector2D and solve for max speed here instead of outside the class.
 * This will be important if ever acceleration is used instead of one speed. Would also be nice to allow for custom hitboxs
 * instead of the sprite size.
 *
 * UPDATE NOTE:
 * - Added sprite class to handle animations.
 *
 * - Currently uses a hack solution to determine if using a spritesheet or sprite but future updates should remove the
 *   basic sprite image and all should run a sprite sheet. The spritesheet class would then handle 1 image no animation
 *   sprites. The GameObject doesnt care.
 *
 */
public abstract class GameObject {
    protected Vector2D location; //Location to draw the sprite from
    protected BufferedImage sprite; //Visual of object
    protected Rectangle hitBox; //Hitbox for the object

    protected Sprite spriteSheet; //Animation sprite sheet
    private boolean usingSpriteSheet = false; //Temporary hack solution to allow for both sprite sheet and sprite image use.

    protected Vector2D velocity;
    protected int maxSpeed; //Maximum Speed of the object. (Pixels per second)

    protected GameObject(BufferedImage sprite){
        this.sprite = sprite;
        location = new Vector2D(300, 300);
        velocity = new Vector2D(0, 0);

        hitBox = new Rectangle((int)location.x, (int)location.y, sprite.getWidth(), sprite.getHeight());
    }

    protected GameObject(Sprite spriteSheet){
        this.spriteSheet = spriteSheet;
        location = new Vector2D(300, 300);
        velocity = new Vector2D(0, 0);

        hitBox = new Rectangle((int)location.x, (int)location.y, spriteSheet.getSpriteWidth(), spriteSheet.getSpriteHeight());
        usingSpriteSheet = true;
    }

    //All objects should have a way of updating themselves
    public abstract void update(double timeSinceUpdate);

    //Move the object using the provided velocity and delta time.
    public void move(double timeSinceUpdate){
        location.x += (float)(velocity.x * timeSinceUpdate);
        location.y += (float)(velocity.y * timeSinceUpdate);

        hitBox.setLocation((int)location.x, (int)location.y);
    }

    public void setVelocity(Vector2D newVelocity){
        velocity.x = newVelocity.x;
        velocity.y = newVelocity.y;
    }

    //Draws the visual for the object. Includes visual for hitbox drawing with removal of comments.
    public void draw(Graphics g){
        //HITBOX TESTING
        //g.setColor(Color.PINK);
        //g.fillRect(hitBox.x, hitBox.y, hitBox.width, hitBox.height);

        //Hack check to draw from the sprite sheet or just the basic sprite.
        if(usingSpriteSheet){
            g.drawImage(spriteSheet.getCurrentSprite(), (int)location.x, (int)location.y, null);
        }

        g.drawImage(sprite, (int)location.x, (int)location.y, null);
    }

    //Sets a new position for the object
    public void setPosition(Vector2D newLocation) {
        location.x = newLocation.x;
        location.y = newLocation.y;
        hitBox.setLocation((int)location.x, (int)location.y);
    }

    //GETTERS
    public Rectangle getHitBox(){return hitBox;}
    public float getXLocation(){return location.x;}
    public float getYLocation(){return location.y;}
    public int getMaxSpeed() {return maxSpeed;}
    public BufferedImage getSprite(){return sprite;}
    public Sprite getSpriteSheet(){
        return spriteSheet;
    }

    //Future use to return the center of the object. Would be used for seeking and axe spawning instead of the top left
    //draw coordinates.
    //WILL NOT WORK WITH SPRITE SHEET RIGHT NOW
    public Vector2D getCenter(){
        return new Vector2D(location.x + (float)(sprite.getWidth()/2), location.y + (float)(sprite.getHeight()/2));
    }
}