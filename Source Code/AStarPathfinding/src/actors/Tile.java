package actors;

import game.TileType;
import game.Vector2D;

import java.awt.*;
import java.awt.image.BufferedImage;
/*
 * ASTAR PATHFINDING VISUALIZER
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     April 11, 2023
 * COURSE:   COMP452 - AI for Game Developers (Athabasca University)
 *
 * Tile (Child of GameObject)
 * Description:
 * Class representing the grid based tiles within the game. Contains enum for the type of tile it is.
 *
 * Future Updates/Refactor:
 * In theory, I likely should have just used this class for the purposes of pathfinding instead of overlaying nodes
 * ontop of the tiles (See other notes). No real notes otherwise as this is a fairly simple object. One thing that could
 * maybe be added is having a method that uses a tileType to assign the associated visual. This would protect and avoid
 * an accident of changing the type but not changing the visual. This was never an issue but it is worth noting.
 */
public class Tile extends GameObject {

    //Reference to the type of tile.
    private TileType tileType;

    public Tile(BufferedImage sprite, Vector2D location, TileType type) {
        super(sprite);
        tileType = type;
        setPosition(location);
    }

    //Custom Draw method to add a black border to tiles to enhance visualization
    public void draw(Graphics g) {
        g.drawImage(sprite, (int) location.x, (int) location.y, null);
        g.setColor(Color.BLACK);
        g.drawRect(hitBox.x, hitBox.y, hitBox.width, hitBox.height);
    }

    //Sets the tileType and visual to the provided values
    public void changeType(TileType newType, BufferedImage newVisual) {
        sprite = newVisual;
        tileType = newType;
    }

    public TileType getType() {
        return tileType;
    }

    @Override
    public void update(double timeSinceUpdate) {

    }

    //Returns the associated movement cost for the tile type
    public int getCost() {
        switch (tileType) {
            case GRASS:
                return 1;
            case DESERT:
                return 3;
            case SWAMP:
                return 4;
        }

        //Barriers and tiles without a type will not be moveable
        return 0;
    }
}
