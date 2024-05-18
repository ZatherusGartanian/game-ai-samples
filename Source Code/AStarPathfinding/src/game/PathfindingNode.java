package game;
/*
 * ASTAR PATHFINDING VISUALIZER
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     April 11, 2023
 * COURSE:   COMP452 - AI for Game Developers (Athabasca University)
 *
 * PathfindingNode
 * Description:
 * Representation of a node within the pathfinding graph
 *
 * Future Updates/Refactor:
 * This likely should have just been data that was held within the tile for simplicity. I had tried to make it reusuable
 * and it kind of is. See AStar for more details. For simplicity, I did not make elements private.
 */
public class PathfindingNode {

    Vector2D arrayLocation; //The location of the node within the array. Used for neighbour comparison and to grab associated tile
    float currentCost; //Actual cost of getting to the node so far
    float estimatedCost; //Hueristic cost of getting to goal from the node
    PathfindingNode previousTile; //Reference to the previous node in the path

    PathfindingNode(Vector2D arrayLocation, float currentCost, float estimatedCost, PathfindingNode previousTile){
        this.arrayLocation = arrayLocation;
        this.currentCost = currentCost;
        this.estimatedCost = estimatedCost;
        this.previousTile = previousTile;
    }
}
