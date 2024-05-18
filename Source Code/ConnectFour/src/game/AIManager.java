package game;
import actors.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/*
 * CONNECT FOUR
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     April 28, 2024
 * COURSE:   COMP452 - AI for Game Developers (Athabasca University)
 *
 * AIManager
 * Description:
 * Contains methods used for solving for the AI turn. Contains logic for scoring the board.
 *
 * Future Updates/Refactor:
 * No major notes on this one. See supporting document for future updates with alpha and beta pruning or building a
 * tree structure to track previously built board.
 */

public class AIManager {

    //Minimax recursion to return the board with the best movement from the provided board.
    public static Board minimax(Board currentBoard, int maxDepth, int currentDepth, TileType recentPlayer){
        //If the board is at a leaf node or is a game over board score the board and end the recursion.
        if(currentBoard.isGameOver() || currentDepth == maxDepth){
            currentBoard.setScore(evaluateBoard(currentBoard, recentPlayer));
            return currentBoard;
        }

        //Trackers for recursion loop
        TileType nextPlayer;
        int bestScore;
        Board bestBoard = null;

        //Get flipped colour for updating board movements and set best score as high or low if min or maxing
        if(recentPlayer == TileType.YELLOW){
            nextPlayer = TileType.RED;
            bestScore = Integer.MAX_VALUE;
        }
        else{
            nextPlayer = TileType.YELLOW;
            bestScore = Integer.MIN_VALUE;
        }
        //Get boards using the nextPlayer to go (Returns all the boards with completed move)
        ArrayList<Board> boards = currentBoard.getPossibleBoards(nextPlayer);

        //For each possible board
        for(Board nextBoard: boards){
            //Recurse with the board. Returned value will be the best move possible from all children of the board
            Board bestChildBoard = minimax(nextBoard, maxDepth, currentDepth+1, nextPlayer);
            int currentScore;
            //If the player is the one that made the move, try to force the lowest score (MINIMIZE)
            if(nextPlayer == TileType.RED) {
                //Check the score the bubbled up against all other boards in this layer
                currentScore = bestChildBoard.getScore();
                if(currentScore <= bestScore){
                    //New best move found so update best board data
                    bestScore = currentScore;
                    bestBoard = nextBoard;
                    nextBoard.setScore(bestScore); //MUST SET THE BOARDS SCORE HERE SINCE BUBBLING UP SCORE THROUGH RETURNED BOARD
                };
            }
            //Otherwise the AI was the one making the move and it should maximize its score (MAXIMIZE)
            else {
                //Check the score the bubbled up against all other boards in this layer
                currentScore = bestChildBoard.getScore();
                if(currentScore >= bestScore){
                    //New best move found so update best board data
                    bestScore = currentScore;
                    bestBoard = nextBoard;
                    nextBoard.setScore(bestScore); //MUST SET THE BOARDS SCORE HERE SINCE BUBBLING UP SCORE THROUGH RETURNED BOARD
                };
            }
        }

        //Looped through all boards and found the best one to return
        return bestBoard;
    }

    //Get a score for the current board. Uses the recentPlayer to gauge who made the move. Used for quick game over tracking.
    public static int evaluateBoard(Board board, TileType recentPlayer) {
        //Quick check for game over maximize scoring
        if(board.isGameOver()){
            //If the player that made the move was yellow it was the AI and score should max
            if(recentPlayer == TileType.YELLOW){
                return Integer.MAX_VALUE;
            }
            //Otherwise the player made the move and it should be minimum
            else
                return Integer.MIN_VALUE;
        }

        //Track the score
        int score = 0;
        final int COLUMNS = 7;
        final int ROWS = 6;

        TileType[][] boardState = board.getBoard();

        //Cycle through all possible groupings of 4 in the board.
        //Check every vertical set of 4
        for (int x = 0; x < COLUMNS; x++) {
            for (int y = 0; y <= ROWS - 4; y++) {
                score += evaluateSetOfFour(boardState[x][y], boardState[x][y+1], boardState[x][y+2], boardState[x][y+3]);
            }
        }

        //Check every horizontal set of 4
        for (int x = 0; x <= COLUMNS - 4; x++) {
            for (int y = 0; y < ROWS; y++) {
                score += evaluateSetOfFour(boardState[x][y], boardState[x+1][y], boardState[x+2][y], boardState[x+3][y]);
            }
        }

        //Check every diagonal set of 4 (One angle)
        for (int x = 0; x <= COLUMNS - 4; x++) {
            for (int y = 0; y <= ROWS - 4; y++) {
                score += evaluateSetOfFour(boardState[x][y], boardState[x+1][y+1], boardState[x+2][y+2], boardState[x+3][y+3]);
            }
        }

        //Check every diagonal set of 4 (Opposite angle)
        for (int x = 3; x < COLUMNS; x++) {
            for (int y = 0; y <= ROWS - 4; y++) {
                score += evaluateSetOfFour(boardState[x][y], boardState[x-1][y+1], boardState[x-2][y+2], boardState[x-3][y+3]);
            }
        }

        //Entire board has been scored and can be returned
        return score;
    }

    //Score the 4 nodes in the set
    private static int evaluateSetOfFour(TileType position1, TileType position2, TileType position3, TileType position4) {
        //Counters for number of tokens in set for each player
        int AICount = 0;
        int playerCount = 0;

        //Check for AI counts
        if (position1 == TileType.YELLOW) AICount++;
        if (position2 == TileType.YELLOW) AICount++;
        if (position3 == TileType.YELLOW) AICount++;
        if (position4 == TileType.YELLOW) AICount++;

        //Check for player counts
        if (position1 == TileType.RED) playerCount++;
        if (position2 == TileType.RED) playerCount++;
        if (position3 == TileType.RED) playerCount++;
        if (position4 == TileType.RED) playerCount++;

        //Score based on the results. Any set where both players have at least one, will not score anything as there is
        //no future outcome where the set of 4 can be owned by one player

        //WINNING TRACKERS SHOULDN'T GET HIT ANYMORE SINCE GAME OVER BOARD WILL CATCH BEFORE HITTING HERE
        if (AICount == 4) return 50000; // AI wins
        if (playerCount == 4) return -50000; // Player wins


        //TWEAKING THESE VALUES WILL ADJUST HOW THE AI BEHAVES.
        //If count is 1, early potential and should be worth minimal score (1)
        //If count is 2, better potential and should be worth much more (10)
        //If count is 3, near a win and should score really high (100)
        //Currently set to increment by multiples of 10 to favor moves that will set up winning sets

        //Increase score if the AI is the only one in the set
        if (AICount == 3 && playerCount == 0) return 100;
        if (AICount == 2 && playerCount == 0) return 10;
        if (AICount == 1 && playerCount == 0) return 1;
        //Decrease score if the player is the only one in the set
        if (playerCount == 3 && AICount == 0) return -100;
        if (playerCount == 2 && AICount == 0) return -10;
        if (playerCount == 1 && AICount == 0) return -1;

        //No possibility of score in the set or set is empty
        return 0;
    }
}
