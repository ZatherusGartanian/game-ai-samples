package game;

import actors.Tile;
import scenes.GameCore;

import java.awt.*;
import java.util.*;
import java.util.List;
/*
 * ASTAR PATHFINDING VISUALIZER
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     April 11, 2023
 * COURSE:   COMP452 - AI for Game Developers (Athabasca University)
 *
 * AStar
 * Description:
 * Class used to perform the AStar Pathfinding on the provided tile map.
 *
 * Future Updates/Refactor:
 * I am not super certain on the hueristic I decided to use. I also tried to map nodes seperately from the tile itself as
 * I wanted to make it more reusable but it requires alot of knowledge of how the tile map is set up anyways. I do like
 * that the pathfinding nodes are only instantiated and that the logic is separated from the tile but I was trying to
 * avoid building a second array but discovered difficulties with assessing equality and neighbours were being added multiple
 * times. As a result I chose to just build the array of null references so the location was mapped to the tiles.
 */
public class AStar {
    //Variables used to maintain state during steps. Not needed if trying to simply get the path.
    private PathfindingNode[][] pathfindingMap;
    private Set<PathfindingNode> closedSet;
    private Set<PathfindingNode> openSet;
    private PathfindingNode goalNode;
    private PathfindingNode startNode;
    private Tile[][] tileMap;
    private PathfindingNode currentNode;

    //Used for attempts with alternate heuristic
    private float averageTileCost;

    //Game Reference
    private GameCore gameCore;

    public AStar(GameCore gameCore){
        this.gameCore = gameCore;
    }

    //Start the process of finding a path through the provided tileMap
    public void findPath(Tile[][] tileMap, Vector2D startLocation, Vector2D goalLocation){
        this.tileMap = tileMap;
        //Create a pathfindingnode map the same size as the tile map
        pathfindingMap = new PathfindingNode[tileMap.length][tileMap[0].length];

        //Initialize the open and closed sets
        closedSet = new HashSet<>();
        openSet = new HashSet<>();

        //Initialize the startnode and goalNodes
        startNode = new PathfindingNode(startLocation,0, heuristicCostEstimate(startLocation, goalLocation), null);
        goalNode = new PathfindingNode(goalLocation, Float.POSITIVE_INFINITY, 0, null);

        //Update map with the start and goal nodes
        pathfindingMap[(int)startLocation.x][(int)startLocation.y] = startNode;
        pathfindingMap[(int)goalLocation.x][(int)goalLocation.y] = goalNode;

        //Add the startNode to the open set
        openSet.add(startNode);

        //ATTEMPT AT ALTERNATE HUERISTIC. USING AVERAGE TILE COST of the map.
        int totalTileCost = 0;
        int numberOfTiles = 0;
        for(Tile[] mapRow : tileMap){
            for(Tile tile : mapRow){
                if(tile.getCost() == 0){
                    continue; //Ignore walls
                }
                totalTileCost += tile.getCost();
                numberOfTiles++;
            }
        }
        averageTileCost = (float)totalTileCost/numberOfTiles;

        //Take the first step
        nextStep();
    }

    //Takes the next step through the pathfinding.
    private void nextStep(){
        //Replace with while in order to execute all at once.
        //Check to see if there is a node in the openSet
        if(!openSet.isEmpty()){
            //Get the current cheapest node and remove from the open set
            currentNode = cheapestEstimate(openSet);
            openSet.remove(currentNode);

            //If the current node is the goal a path has been found and can stop
            if(currentNode == goalNode){
                gameCore.donePathfinding(true);
            }

            //Gather all neighbouring nodes
            List<PathfindingNode> neighbourNodes = neighbouringNodes(currentNode);
            for(PathfindingNode neighbour : neighbourNodes){
                //Get the tile at the node location
                Tile neighbourTile = tileMap[(int)neighbour.arrayLocation.x][(int)neighbour.arrayLocation.y];

                //Ignore the tile if it is a barrier type
                if(neighbourTile.getCost() == 0){
                    continue;
                }

                //Calculate the cost of moving to the tile by adding currentCost to the tiles type cost
                float neighbourCost = currentNode.currentCost + neighbourTile.getCost();

                //Check if the node is in the closed set in case a cheaper/new path has been found to that node
                //If so, remove it from the closed set so it can be updated.
                if(closedSet.contains(neighbour)) {
                    if(neighbour.currentCost > neighbourCost) {
                        closedSet.remove(neighbour);
                    }
                    else {
                        //If the node is already in the closed set and does not have a cheaper path check the next neighbour
                        continue;
                    }
                }

                //If the node is already in the open set and has a cheaper cost then assess the next neighbour
                if(openSet.contains(neighbour) && neighbour.currentCost < neighbourCost){
                    continue;
                }

                //Add the updated cost to move to the neighbour and update the previousNode
                neighbour.currentCost = neighbourCost;
                neighbour.previousTile = currentNode;

                //Since using Sets we can try and add and if the node already exists it will not be added
                openSet.add(neighbour);
            }

            //Add the currentNode to the closedSet as it has now been assessed
            closedSet.add(currentNode);
        }
        else{
            //No path was found
            gameCore.donePathfinding(false);
        }
    }

    //Using the Manhattan Distance heuristic to estimate the distance to goal. Multiplies by 4 as the max tile cost
    //is 4. This was necessary for an entire map of swamp tiles. However, I am sure a more efficient heuristic could
    //exist as there are situations where this is an overestimate. For example, a map that has no swamps used or lots
    //of green tiles this estimate will be high. See supporting documentation for further thoughts.
    private float heuristicCostEstimate(Vector2D currentLocation, Vector2D goalLocation){
        float xDistance = Math.abs(currentLocation.x - goalLocation.x);
        float yDistance = Math.abs(currentLocation.y - goalLocation.y);
        return ((xDistance + yDistance) * averageTileCost); //Replace with *4 for max tile movement heuristic
    }

    //Find the next cheapest node. Uses cheapest estimatedCost to break ties to prioritize nodes closer to the goal since
    //the supported hueristic is the manhattan distance. See supporting documentation for further thoughts
    private PathfindingNode cheapestEstimate(Set<PathfindingNode> openSet){

        //Cheapest node reference and value
        PathfindingNode currentCheapest = null;
        float lowestEstimateCost = Float.POSITIVE_INFINITY;

        //Cycle through all the nodes comparing to current cheapest
        for(PathfindingNode node : openSet){
            //If the goalNode is in the openSet then a prior path existed but the step onto the final location may be more
            //expensive (stepping onto a swap goal tile, but that cost will always exist regardless of which tile you come from
            //so you may as well stop
            if(node == goalNode){
                return node;
            }
            if(node.estimatedCost + node.currentCost < lowestEstimateCost) {
                currentCheapest = node;
                lowestEstimateCost = node.estimatedCost + node.currentCost;
            }
            //If estimated total cost is the same then use node with lower heuristic cost
            else if(node.estimatedCost + node.currentCost == lowestEstimateCost){

                if(node.estimatedCost < currentCheapest.estimatedCost){
                    currentCheapest = node;
                }
            }
        }

        return currentCheapest;
    }

    //Get Nodes for neighbours of the provided node
    public List<PathfindingNode> neighbouringNodes(PathfindingNode currentNode){
        List<PathfindingNode> neighbouringNodes = new ArrayList<>();
        Vector2D currentLocation = currentNode.arrayLocation;

        //Variables for neighbour creation
        Vector2D neighbourLocation;
        PathfindingNode neighbour;

        //Add Left Neighbour
        neighbourLocation = new Vector2D(currentLocation.x - 1, currentLocation.y);
        //Make sure the neighbour is not outside the ArraySize
        if(neighbourLocation.x >= 0){
            //Check if the node for that location has been created, if not, create it
            if(pathfindingMap[(int)neighbourLocation.x][(int)neighbourLocation.y] == null){
                neighbour = new PathfindingNode(neighbourLocation, Float.POSITIVE_INFINITY, heuristicCostEstimate(neighbourLocation, goalNode.arrayLocation), currentNode);
                pathfindingMap[(int)neighbourLocation.x][(int)neighbourLocation.y] = neighbour;
            }
            //Otherwise, get the already created node
            else{
                neighbour = pathfindingMap[(int)neighbourLocation.x][(int)neighbourLocation.y];
            }

            //Add the neighbour to the list
            neighbouringNodes.add(neighbour);
        }

        //Add Above Neighbour
        neighbourLocation = new Vector2D(currentLocation.x, currentLocation.y - 1);
        if(neighbourLocation.y >= 0){
            if(pathfindingMap[(int)neighbourLocation.x][(int)neighbourLocation.y] == null){
                neighbour = new PathfindingNode(neighbourLocation, Float.POSITIVE_INFINITY, heuristicCostEstimate(neighbourLocation, goalNode.arrayLocation), currentNode);
                pathfindingMap[(int)neighbourLocation.x][(int)neighbourLocation.y] = neighbour;
            }
            else{
                neighbour = pathfindingMap[(int)neighbourLocation.x][(int)neighbourLocation.y];
            }

            neighbouringNodes.add(neighbour);
        }

        //Add Below Neighbour
        neighbourLocation = new Vector2D(currentLocation.x, currentLocation.y + 1);
        if(neighbourLocation.y <= (tileMap[0].length-1)){
            if(pathfindingMap[(int)neighbourLocation.x][(int)neighbourLocation.y] == null){
                neighbour = new PathfindingNode(neighbourLocation, Float.POSITIVE_INFINITY, heuristicCostEstimate(neighbourLocation, goalNode.arrayLocation), currentNode);
                pathfindingMap[(int)neighbourLocation.x][(int)neighbourLocation.y] = neighbour;
            }
            else{
                neighbour = pathfindingMap[(int)neighbourLocation.x][(int)neighbourLocation.y];
            }

            neighbouringNodes.add(neighbour);
        }

        //Add Right Neighbour
        neighbourLocation = new Vector2D(currentLocation.x + 1, currentLocation.y);
        if(neighbourLocation.x <= (tileMap.length-1)){
            if(pathfindingMap[(int)neighbourLocation.x][(int)neighbourLocation.y] == null){
                neighbour = new PathfindingNode(neighbourLocation, Float.POSITIVE_INFINITY, heuristicCostEstimate(neighbourLocation, goalNode.arrayLocation), currentNode);
                pathfindingMap[(int)neighbourLocation.x][(int)neighbourLocation.y] = neighbour;
            }
            else{
                neighbour = pathfindingMap[(int)neighbourLocation.x][(int)neighbourLocation.y];
            }

            neighbouringNodes.add(neighbour);
        }

        return neighbouringNodes;
    }

    //Update used for game control
    public void update(){
        nextStep();
    }

    //Visualize the current state of the algorithm
    public void draw(Graphics g){
        //Color values to provide opacity fill
        final Color TRANSPARENT_RED = new Color(255, 0, 0, 128);
        final Color TRANSPARENT_BLUE = new Color(0, 0, 255, 128);
        final Color TRANSPARENT_GREEN = new Color(0, 255, 0, 128);

        //Variables used to reference cost and current tile values
        Tile currentTile;
        String costString;
        float totalCost;

        //Color in all the closed nodes
        for(PathfindingNode closedNode : closedSet){
            currentTile = tileMap[(int)closedNode.arrayLocation.x][(int)closedNode.arrayLocation.y];

            g.setColor(TRANSPARENT_RED);
            g.fillRect((int)currentTile.getXLocation(), (int)currentTile.getYLocation(), 50, 50);

            totalCost = closedNode.currentCost + closedNode.estimatedCost;
            g.setColor(Color.BLACK);
            costString = String.format("%.1f", totalCost);
            g.drawString(costString, (int)currentTile.getCenter().x, (int)currentTile.getCenter().y);
        }

        //Color in all the open nodes
        for(PathfindingNode openNode : openSet){
            currentTile = tileMap[(int)openNode.arrayLocation.x][(int)openNode.arrayLocation.y];
            g.setColor(TRANSPARENT_BLUE);
            g.fillRect((int)currentTile.getXLocation(), (int)currentTile.getYLocation(), 50, 50);

            totalCost = openNode.currentCost + openNode.estimatedCost;
            g.setColor(Color.BLACK);
            costString = String.format("%.1f", totalCost);
            g.drawString(costString, (int)currentTile.getCenter().x, (int)currentTile.getCenter().y);
        }

        //Color the current tile
        currentTile = tileMap[(int)currentNode.arrayLocation.x][(int)currentNode.arrayLocation.y];
        g.setColor(TRANSPARENT_GREEN);
        g.fillRect((int)currentTile.getXLocation(), (int)currentTile.getYLocation(), 50, 50);


        //Draw a line for the path to the current tile.
        g.setColor(Color.GREEN);
        PathfindingNode drawingPath = currentNode;
        Tile previousTile;
        //Step backwards through the currentNode path and draw a line between each node to display the path
        while(drawingPath.previousTile != null){
            currentTile = tileMap[(int)drawingPath.arrayLocation.x][(int)drawingPath.arrayLocation.y];
            previousTile = tileMap[(int)drawingPath.previousTile.arrayLocation.x][(int)drawingPath.previousTile.arrayLocation.y];

            g.drawLine((int)currentTile.getCenter().x, (int)currentTile.getCenter().y, (int)previousTile.getCenter().x, (int)previousTile.getCenter().y);

            drawingPath = drawingPath.previousTile;
        }
    }

    //Built to provide path. Not used as path is drawn backwards to screen but felt worth including. For example,
    //this could provided to the ant in order to move the ant to the goal.
    private List<PathfindingNode> reconstructPath(PathfindingNode currentNode){
        List<PathfindingNode> path = new ArrayList<>();

        //Work backwards through the nodes and add them to List
        while(currentNode != null){
            path.add(currentNode);
            currentNode = currentNode.previousTile;
        }

        //Reverse the list to start at the start and end at the goal
        Collections.reverse(path);
        return path;
    }
}
