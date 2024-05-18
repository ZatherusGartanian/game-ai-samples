package actors;

import game.TileType;
import game.Vector2D;

import java.util.ArrayList;
/*
 * CONNECT FOUR
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     April 28, 2024
 * COURSE:   COMP452 - AI for Game Developers (Athabasca University)
 *
 * Board
 * Description:
 * Represents the state of the connect four board. Tracks additional values for ease of use
 *
 * Future Updates/Refactor:
 * Should probably move the scoring logic here, but it made sense to me that that AI ws the one scoring the board and
 * not the board that understood how to score itself. This is especially true because I chose to score the board based
 * on the AIs perspective, not the current players. That said, I still bubble up the score through this class as I did
 * not want to create a custom container class that just stored a vector for move and an int for best score. The code
 * to check for a win is incredibly repeatative and unwieldy. It solves for a win incredibly well but because of the
 * very custom directional updates the code repeats. I could pull out some sections into a helper method but I had
 * already finished writing it out this way and it felt like an unnecessary refactor.
 */

public class Board {
    private TileType[][] boardState;
    final static int COLUMNS = 7;
    final static int ROWS = 6;
    private Vector2D moveMade; //Tracks the move the was made to get the board in this position
    private boolean gameOver = false; //Tracks if the board is in a game over state
    private int score; //Holds the best possible score for the AI

    //Tracks the outside tokens of a winning line of four (if there is one)
    private Vector2D edgeOne;
    private Vector2D edgeTwo;

    public Board(TileType[][] board){
        this.boardState = board;
        //Initialize vectors to 0. Will update once a move is applied to the board
        moveMade = new Vector2D(0, 0);
        edgeOne = new Vector2D(0, 0);
        edgeTwo = new Vector2D(0, 0);
    }

    //Set the score that is best at this location (For AI)
    public void setScore(int updatedScore){
        score = updatedScore;
    }


    public ArrayList<Board> getPossibleBoards(TileType playerTurn){
        ArrayList<Board> boards = new ArrayList<>();
        //Top of each column

        for(int xLocation = 0; xLocation < COLUMNS; xLocation++){
            for(int yLocation = ROWS-1; yLocation >= 0; yLocation--){
                if(boardState[xLocation][yLocation] == TileType.EMPTY){
                    TileType[][] copyBoardState = new TileType[COLUMNS][ROWS];
                    for(int i = 0; i < COLUMNS; i++){
                        for(int j = 0; j < ROWS; j++){
                            TileType tileState = boardState[i][j];
                            copyBoardState[i][j] = tileState;
                        }
                    }
                    Board newBoard = new Board(copyBoardState);
                    newBoard.makeMove(xLocation, yLocation, playerTurn);

                    boards.add(newBoard);
                    break;
                }
            }
        }

        return boards;
    }

    //Update the board at the location to the colour of the person that played
    public void makeMove(int xLocation, int yLocation, TileType playerColor){
        boardState[xLocation][yLocation] = playerColor;
        moveMade.x = xLocation;
        moveMade.y = yLocation;

        //Check the updated board for a win
        checkForWin(playerColor);
    }

    //Getters
    public Vector2D getMoveMade(){return moveMade;}
    public TileType[][] getBoard(){return boardState;}
    public Vector2D getEdgeOne(){return edgeOne;}
    public Vector2D getEdgeTwo(){return edgeTwo;}
    public int getScore(){
        return score;
    }
    public boolean isGameOver(){
        return gameOver;
    }

    //Checks the board to see if there was a win.
    public void checkForWin(TileType playerColor){
        //Check each direction from the placement of the move that was made
        //If a win is found, sets the board gameOver state to true

        boolean winner;
        winner = checkWinDiagonal(playerColor);
        if(winner){
            gameOver = true;
            return;
        }
        winner = checkWinHorizontal(playerColor);
        if(winner){
            gameOver = true;
            return;
        }
        winner = checkWinVertical(playerColor);
        if(winner){
            gameOver = true;
            return;
        }
    }

    //Checks for diagonal wins
    public boolean checkWinDiagonal(TileType playerColor){
        int count = 0;
        //Check Angled Right
        //Move Up and Right
        int checkingY = (int)moveMade.y;
        for(int checkingX = (int)moveMade.x; checkingX < 7; checkingX++){
            //If the location is the correct colour, increase the counter and set new edge
            if (boardState[checkingX][checkingY] == playerColor){
                count++;
                edgeOne.x = checkingX;
                edgeOne.y = checkingY;
            }
            //Otherwise the location breaks the line
            else{
                break;
            }

            //Update Y to move up
            checkingY--;
            if(checkingY < 0){
                break;
            }
        }

        count--; //Will count starting token again in next for loop (Already counted in first loop)

        //Move Down and Left
        checkingY = (int)moveMade.y;
        for(int checkingX = (int)moveMade.x; checkingX >= 0; checkingX--){
            //If the location is the correct colour, increase the counter and set second edge
            if (boardState[checkingX][checkingY] == playerColor){
                count++;
                edgeTwo.x = checkingX;
                edgeTwo.y = checkingY;
            }
            //Otherwise the location breaks the line
            else{
                break;
            }

            //Update Y to move down
            checkingY++;
            if(checkingY >= 6){
                break;
            }
        }

        //If the count was at least 4 then a win was found
        if(count >= 4){
            return true;
        }
        //Otherwise, reset and check the other diagonal
        else
            count = 0;

        //Angled Left
        //Move Up and Left
        checkingY = (int)moveMade.y;
        for(int checkingX = (int)moveMade.x; checkingX >= 0; checkingX--){
            //If the location is the correct colour, increase the counter and set as edge
            if (boardState[checkingX][checkingY] == playerColor){
                count++;
                edgeOne.x = checkingX;
                edgeOne.y = checkingY;
            }
            //Otherwise the location breaks the line
            else{
                break;
            }

            //Update Y to move up
            checkingY--;
            if(checkingY < 0){
                break;
            }
        }

        count--; //Will count starting token again in next for loop (Already counted in first loop)

        //Move Down and Right
        checkingY = (int)moveMade.y;
        for(int checkingX = (int)moveMade.x; checkingX < 7; checkingX++){
            //If the location is the correct colour, increase the counter and set as edge
            if (boardState[checkingX][checkingY] == playerColor){
                count++;
                edgeTwo.x = checkingX;
                edgeTwo.y = checkingY;
            }
            //Otherwise the location breaks the line
            else{
                break;
            }
            checkingY++;
            if(checkingY >= 6){
                break;
            }
        }
        //If the count was at least 4 then a win was found
        if(count >= 4){
            return true;
        }

        //Otherwise no win was found
        return false;
    }


    //Check the horizontal line
    public boolean checkWinHorizontal(TileType playerColor){
        int count = 0;
        int checkingY = (int)moveMade.y;
        //Move Left
        for(int checkingX = (int)moveMade.x; checkingX >= 0; checkingX--){
            //If the location is the correct colour, increase the counter and set as edge
            if (boardState[checkingX][checkingY] == playerColor){
                count++;
                edgeOne.x = checkingX;
                edgeOne.y = checkingY;
            }
            //Otherwise the location breaks the line
            else{
                break;
            }
        }

        count--; //Will count starting token again in next for loop (Already counted in first loop)

        //Move Right
        for(int checkingX = (int)moveMade.x; checkingX < 7; checkingX++){
            //If the location is the correct colour, increase the counter and set as edge
            if (boardState[checkingX][checkingY] == playerColor){
                count++;
                edgeTwo.x = checkingX;
                edgeTwo.y = checkingY;
            }
            //Otherwise the location breaks the line
            else{
                break;
            }
        }

        //If the count was at least 4 then a win was found
        if(count >= 4) {
            return true;
        }
        //Otherwise no win was found
        return false;
    }

    //Check the vertical line
    public boolean checkWinVertical(TileType playerColor){
        int count = 0;
        int checkingX = (int)moveMade.x;
        //Move Up
        for(int checkingY = (int)moveMade.y; checkingY >= 0; checkingY--){
            //If the location is the correct colour, increase the counter and set as edge
            if (boardState[checkingX][checkingY] == playerColor){
                count++;
                edgeOne.x = checkingX;
                edgeOne.y = checkingY;
            }
            //Otherwise the location breaks the line
            else{
                break;
            }
        }

        count--; //Will count starting token again in next for loop (Already counted in first loop)

        //Move Down
        for(int checkingY = (int)moveMade.y; checkingY < 6; checkingY++){
            //If the location is the correct colour, increase the counter and set as edge
            if (boardState[checkingX][checkingY] == playerColor){
                count++;
                edgeTwo.x = checkingX;
                edgeTwo.y = checkingY;
            }
            //Otherwise the location breaks the line
            else{
                break;
            }
        }

        //If the count was at least 4 then a win was found
        if(count >= 4) {
            return true;
        }

        //Otherwise no win was found
        return false;
    }
}

