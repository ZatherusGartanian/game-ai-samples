package scenes;

import actors.Board;
import actors.StaticObject;
import game.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/*
 * CONNECT FOUR
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     April 28, 2024
 * COURSE:   COMP452 - AI for Game Developers (Athabasca University)
 *
 * GameCore (Child of Scene)
 * Description:
 * Main game loop for the game of Connect Four.
 *
 * Future Updates/Refactor:
 * UI and overlays is still a bit of an issue an adds clutter in the game loop. Ideally UI elements should be moved out to
 * their own classes. One day perhaps I will update this functionality in the engine.
 */

public class GameCore extends Scene{
    //Representation of the current board
    private Board currentBoard;

    //Visual representation for where the token will place
    private StaticObject selectedTile;

    //Images
    private BufferedImage redToken;
    private BufferedImage yellowToken;
    private BufferedImage tileFrame;
    private BufferedImage endGameUI;

    //Buttons
    private StaticObject forfeitButton;
    private StaticObject nextMatchButton;

    //Game State Trackers
    private boolean playerTurn = true;
    private boolean gameOver = false;
    private int playerWinCount = 0;
    private int playerLoseCount = 0;
    private int turnCount = 0;

    //Mouse Location
    private Vector2D mouseLocation;

    //Game References
    private GameManager gameManager;

    public GameCore(GameManager gameManager){
        this.gameManager = gameManager;

        initializeGame();
    }

    public void initializeGame(){
        //Load images
        redToken = ResourceLoader.loadImage(ResourceLoader.RED_TOKEN);
        yellowToken = ResourceLoader.loadImage(ResourceLoader.YELLOW_TOKEN);
        tileFrame = ResourceLoader.loadImage(ResourceLoader.TILE_FRAME);
        endGameUI = ResourceLoader.loadImage(ResourceLoader.UI_BACKGROUND);

        //Initialize the board states to empty
        TileType[][] boardStatus = new TileType[7][6];
        for(int i = 0; i < 7; i++){
            for(int j = 0; j < 6; j++){
                boardStatus[i][j] = TileType.EMPTY;
            }
        }

        //Set the current board to the starting board state
        currentBoard = new Board(boardStatus);

        //Initialize Tile Selector for Visual Aid
        BufferedImage temp = ResourceLoader.loadImage(ResourceLoader.GREEN_TOKEN);
        selectedTile = new StaticObject(temp, new Vector2D(0,550));

        //Initialize buttons
        temp = ResourceLoader.loadImage(ResourceLoader.FORFEIT_BUTTON);
        forfeitButton = new StaticObject(temp, new Vector2D(640, 0));
        temp = ResourceLoader.loadImage(ResourceLoader.NEXT_BUTTON);
        nextMatchButton = new StaticObject(temp, new Vector2D((700-temp.getWidth())/2, 400));

        //Initialize mouse location vector
        mouseLocation = new Vector2D(0,0);
    }

    @Override
    public void draw(Graphics g) {
        //Draw top UI
        g.setColor(Color.BLACK);
        g.fillRect(0,0,700, 50);

        //Text Display of score tally
        g.setFont(new Font("TimesRoman", Font.BOLD, 20));
        g.setColor(Color.WHITE);
        String scoreTotal = "Score: " + playerWinCount + " - " + playerLoseCount;
        g.drawString(scoreTotal, 10, 30);

        //Draw the forfeit button
        forfeitButton.draw(g);

        //If its the players turn draw the tile they are selecting
        if(playerTurn){
            selectedTile.draw(g);
        }

        //Draw the board. Draw tokens underneath then the tile image overtop
        for(int i = 0; i < 7; i++){
            for(int j = 0; j < 6; j++){
                switch(currentBoard.getBoard()[i][j]){
                    case RED -> g.drawImage(redToken, i*100, (j*100) + 50, null);
                    case YELLOW -> g.drawImage(yellowToken, i*100, (j*100) + 50, null);
                }
                g.drawImage(tileFrame, i*100, (j*100) + 50, null);
            }
        }
        //If the game is over, also overlay with end screen
        if(gameOver){
            g.setFont(new Font("TimesRoman", Font.BOLD, 50));
            g.setColor(Color.BLACK);
            FontMetrics fm = g.getFontMetrics();

            //DRAW A LINE THROUGH WINNING 4
            Vector2D endPoint1 = currentBoard.getEdgeOne();
            Vector2D endPoint2 = currentBoard.getEdgeTwo();
            //Stroke setting only exists in graphics 2D so have to cast to adjust line width
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(5));
            g2.drawLine((int)(endPoint1.x*100)+50, (int)(endPoint1.y*100)+100, (int)(endPoint2.x*100)+50, (int)(endPoint2.y*100)+100);

            //Draw the UI panel
            g.drawImage(endGameUI, (700 - endGameUI.getWidth())/2, (650 - endGameUI.getHeight())/2, null);
            g.setColor(Color.WHITE);

            //Set the message based on how the game ended
            String endMessage;
            if(playerTurn) {
                if(turnCount >= 21){
                    endMessage = "ITS A DRAW";
                }
                else{
                    endMessage = "YOU WON!";
                }
            }
            else{
                endMessage = "YOU LOSE!";
            }

            //Display the final message and button to move on to the next match
            int textXLocation = (700 - fm.stringWidth(endMessage)) / 2;
            g.drawString(endMessage, textXLocation, 650/2);
            nextMatchButton.draw(g);
        }
    }

    //Resets the board for a new match
    public void resetBoard(){
        TileType[][] boardStatus = new TileType[7][6];

        //Initialize the board states to empty
        for(int i = 0; i < 7; i++){
            for(int j = 0; j < 6; j++){
                boardStatus[i][j] = TileType.EMPTY;
            }
        }

        //Create a new board with the empty states
        currentBoard = new Board(boardStatus);

        //Reset game state variables
        gameOver = false;
        playerTurn = true;
        turnCount = 0;
    }

    @Override
    public void update(double deltaTime) {
        //If the game is not over
        if(!gameOver) {
            //If it is the players turn, update the information for the selected tile (mouse movement)
            //Check for a tie since it will be the players turn if the match ends in a tie.
            if (playerTurn) {
                updateSelectedTileLocation();
                if(turnCount >= 21){
                    gameOver = true;
                }
            }
            //Otherwise it is the AIs turn
            else {
                //Get the best board result from minimax
                Board bestBoard = AIManager.minimax(currentBoard, 4, 0, TileType.RED);

                //I COULD JUST UPDATE THE CURRENT BOARD TO THE RETURNED ONE
                //It feels more natural though to have the AI actually play the move that was returned

                //Get the move that was made for the best play and parse into int X and Y
                Vector2D bestMove = bestBoard.getMoveMade();
                int xLocation = (int) bestMove.x;
                int yLocation = (int) bestMove.y;

                //Make the move to the board
                currentBoard.makeMove(xLocation, yLocation, TileType.YELLOW);

                //Check for gameOver
                if(currentBoard.isGameOver()) {
                    gameOver = true;
                    playerLoseCount++;
                    return;
                }

                //Add a counter to the turn total and switch to players turn
                turnCount++;
                playerTurn = true;
            }
        }
    }

    //Updates the selected tile based on the current mouse data
    public void updateSelectedTileLocation(){
        //Ignore if the mouse is inside the top UI
        if(mouseLocation.y < 50){
            return;
        }
        //Column will be set by the x location of the mouse while the y need to be the lowest available spot
        int xLocation = (int)mouseLocation.x/100;
        int yLocation = 6; //Set off screen if column is full (Used to ignore placement)

        //Starting at the bottom, cycle up until the first empty spot.
        for(int i = 5; i >= 0; i--){
            TileType[][] board = currentBoard.getBoard();
            //If an empty spot is found set the y location to that spot
            if(board[xLocation][i] == TileType.EMPTY){
                yLocation = i;
                break;
            }
        }

        //Set the position of the tile to the new values.
        selectedTile.setPosition(new Vector2D(xLocation*100, (yLocation*100)+50));
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //If it is the players and the game isnt over
        if(playerTurn && !gameOver) {
            //Create a bounding box for the game board
            Rectangle board = new Rectangle(0, 50, 700, 600);

            //If the click is inside that box
            if (board.contains(e.getPoint())) {
                //If selectedTile is set to out of bounds then column is full so do not accept click
                if(selectedTile.getYLocation() > 550){
                    return;
                }

                //Get the array positions
                int xLocation = (int) selectedTile.getXLocation() / 100;
                int yLocation = (int) (selectedTile.getYLocation()-50)/ 100;

                //Make the move
                currentBoard.makeMove(xLocation, yLocation, TileType.RED);

                //Check for a game over
                if(currentBoard.isGameOver()) {
                    gameOver = true;
                    playerWinCount++;
                    return;
                }

                //End the players turn
                playerTurn = false;
            }
            //Check if the forfeit button was pressed and end the round
            if(forfeitButton.getHitBox().contains(e.getPoint())){
                playerLoseCount++;
                resetBoard();
            }
        }
        //If the game is over then check for button presses on the next button
        if(gameOver){
            if(nextMatchButton.getHitBox().contains(e.getPoint())){
                resetBoard();
            }
        }
    }

    //Store the mouse position.
    @Override
    public void mouseMoved(MouseEvent e) {
        mouseLocation.x = e.getX();
        mouseLocation.y = e.getY();
    }

    //NOT USED
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void keyPressed(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
}

