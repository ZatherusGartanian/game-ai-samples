package input;
import game.GameManager;
import java.awt.event.*;
/*
 * WEREWOLF SURVIVORS GAME
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     March 27
 * COURSE:   COMP452 - AI for Game Developers (Athabasca University)
 *
 * InputController
 * Description:
 * Detects input and passes it to the GameManger to be handled.
 *
 * Future Updates/Refactor:
 * Ensure if I am happy with this implementation. Input controls feel a bit awkward. I want the scene to have more
 * control over how the input is handled in order to prevent the InputController from having to understand how other classes
 * function. In past projects the input controller knew about the Player class and the key press calls were done within
 * the input controller. That works fine but requires that the input controller is aware of what it is controlling.
 * Perhaps the scene could track its input controller and if it wanted to swap to another it could tell the game manager to update
 * the panel with the new active input controller. This could work better but goes back to the issue of the controller having to
 * understand the workings of the thing it controls.
 */

public class InputController implements KeyListener, MouseListener, MouseMotionListener {
    GameManager gameManager;

    public InputController(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        gameManager.getCurrentScene().keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        gameManager.getCurrentScene().keyReleased(e);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        gameManager.getCurrentScene().mouseClicked(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        gameManager.getCurrentScene().mousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        gameManager.getCurrentScene().mouseReleased(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        gameManager.getCurrentScene().mouseMoved(e);
    }

    //Not being used
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mouseDragged(MouseEvent e) {gameManager.getCurrentScene().mouseMoved(e);}
}

