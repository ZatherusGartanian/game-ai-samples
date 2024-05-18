package scenes;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
/*
 * WEREWOLF SURVIVORS GAME
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     March 27
 * COURSE:   COMP452 - AI for Game Developers (Athabasca University)
 *
 * Scene Abstract Class
 * Description:
 * Establishes the structure of a scene. Used to easily pass input and drawing control from the gameManager to the scene.
 *
 * Future Updates/Refactor:
 * See input control concerns. Would an interface be a better solution?
 */
public abstract class Scene {

    abstract public void draw(Graphics g);
    abstract public void update(double deltaTime);
    abstract public void mouseClicked(MouseEvent e);
    abstract public void mousePressed(MouseEvent e);
    abstract public void mouseReleased(MouseEvent e);
    abstract public void mouseMoved(MouseEvent e);
    abstract public void keyPressed(KeyEvent e);
    abstract public void keyReleased(KeyEvent e);
}
