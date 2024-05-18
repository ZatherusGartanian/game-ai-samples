package game;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
/*
 * WEREWOLF SURVIVORS GAME
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     March 27
 * COURSE:   COMP452 - AI for Game Developers (Athabasca University)
 *
 * Resource Loader
 * Description:
 * Used for easier loading of assets into the game. Currently only contains methods for loading images.
 *
 * Future Updates/Refactor:
 * In the future this class could include other methods for loading different asset types. Sound will need to be loaded.
 */

public class ResourceLoader {
    public static final String DESERT_TILE = "desert.png";
    public static final String GRASS_TILE = "grass.png";
    public static final String SWAMP_TILE = "swamp.png";
    public static final String BARRIER_TILE = "barrier.png";
    public static final String UI_BACKGROUND = "ui_background.png";
    public static final String GOAL = "goal.png";
    public static final String ANT = "ant_sheet.png";
    public static final String START = "start.png";
    public static final String RESET = "reset.png";
    public static final String NO_PATH = "no_path.png";

    //Takes in string name (see above constant list), and returns the image.
    //The file path name here potentially could cause issues depending on how the compiler uses path names. If images are not
    //being found you may need to customize a path to the assets folder and replace that portion of the string. If following
    //the compile instructions of using the IDE ItelliJ, then this should not be a concern at all.
    public static BufferedImage loadImage(String assetName){
        BufferedImage image = null;
        try {
            // Load the player bitmap image
            image = ImageIO.read(new File("assets/" + assetName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }
}
