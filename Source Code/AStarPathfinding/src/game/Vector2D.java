package game;
/*
 * WEREWOLF SURVIVORS GAME
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     March 27
 * COURSE:   COMP452 - AI for Game Developers (Athabasca University)
 *
 * Vector2D
 * Description:
 * Custom vector class used to store an X, Y coordinate. Can be used as a coordinate location or to store a directional
 * vector.
 *
 * Future Updates/Refactor:
 * As the need arises more vector math solutions could be added. X and Y are public for ease of use and cleaner code.
 * Protections should not be necessary here. I can be swayed though to make these private and use getters/setters. Code
 * readability and ease of use had a higher priority in this case. One thing I could consider doing is creating
 * static methods for all math. Instead of adjusting the current class it could always return a new vector. For example,
 * adding as a static would take two vectors in as arguments and return a new vector with the add completed (like with
 * blend and vectorBetweenPoints). However, since all objects contain a vector for their locations and since this is
 * updated every frame it is likely more worthwhile to change a Vector2D instead of always creating a new object in memory
 * and having garbage collection get the de-referenced Vector.
 *
 */

public class Vector2D {
    //Basic x and y coordinates
    public float x;
    public float y;

    public Vector2D(float x, float y) {
        this.x = x;
        this.y = y;
    }

    //Normalizes the vector. Used for directional vectors.
    public void normalize() {
        float magnitude = (float) Math.sqrt(x * x + y * y);
        if (magnitude != 0) {
            x /= magnitude;
            y /= magnitude;
        }
    }

    //Adds a Vector to the current one. GameObject movement could use this in future refactors.
    public void addVector(Vector2D v1){
        x += v1.x;
        y += v1.y;
    }

    //Multiplies vector by a scalar value
    public void scalarMultiply(float scalar){
        x *= scalar;
        y *= scalar;
    }

    //Takes in two vectors and a blend ratio and returns a vector using the blend. Blend ratio should be < 1 and is the
    //ratio used for the first vector. The second vector utilizes the inverse ratio providing the full 100% blend.
    public static Vector2D blendVectors(Vector2D v1, Vector2D v2, float blendRatio) {
        float blendedXDirection = blendRatio * v1.x + (1 - blendRatio) * v2.x;
        float blendedYDirection = blendRatio * v1.y + (1 - blendRatio) * v2.y;

        return new Vector2D(blendedXDirection, blendedYDirection);
    }

    //Takes in two points and returns a vector between them. This should eventually become basic vector subtraction.
    //However, the current build still uses a fix of coordinate storage.
    public static Vector2D getVectorBetweenPoints(float xStart, float yStart, float xEnd, float yEnd){
        return new Vector2D(xEnd - xStart, yEnd - yStart);
    }

    public boolean equals(Vector2D otherVector){
        if(otherVector.x == x && otherVector.y == y) {
            return true;
        }
        return false;
    }
}

