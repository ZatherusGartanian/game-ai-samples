package game;

import java.util.Random;
/*
 * ANT COLONY SIMULATION
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     April 20, 2024
 * COURSE:   COMP452 - AI for Game Developers (Athabasca University)
 *
 * Direction (enum)
 * Description:
 * Contains directional information for tile based movement
 *
 * Future Updates/Refactor:
 * Nothing major to note here.
 */

public enum Direction {
        UP, RIGHT, DOWN, LEFT;

        public static Direction getRandomDirection(){
            Random random = new Random();
            int direction = random.nextInt(4);

            return values()[direction % values().length];
        }

        public static Vector2D getDirectionalVector(Direction direction){
            //First time trying the enhanced switch statement formatting
            Vector2D directionalVector = switch (direction) {
                case UP -> new Vector2D(0, -1);
                case RIGHT -> new Vector2D(1, 0);
                case DOWN -> new Vector2D(0, 1);
                case LEFT -> new Vector2D(-1, 0);
            };

            return directionalVector;
        }
}
