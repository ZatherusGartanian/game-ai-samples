package game;
import input.InputController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
/*
 * WEREWOLF SURVIVORS GAME
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     March 27
 * COURSE:   COMP452 - AI for Game Developers (Athabasca University)
 *
 * GameWindow and Viewport
 * Description:
 * Is the main drawing window for the game. Sets up the frame and panel for the game manager to use.
 *
 * Future Updates/Refactor:
 * Currently the size is hardcoded. Ideally I allow for more flexible window sizing and scaling. It is also convenient
 * to allow for additional panels for drawing UI and the use of JButtons. Because of the current setup I had to design
 * my own buttons. It does add more flexability to the build but it adds more complexity to building UI. I do like
 * that the window logic is hidden instead of passing everything to the panel and having the panel draw all at once for
 * draw calls, everything manually draws itself. I find it to be a much cleaner solution but the loss of additional
 * panels and Java Swing components is annoying.
 *
 */

public class GameWindow {
    private JFrame jframe; // Main frame for the game
    private ViewPort viewPort; //Drawing surface (main game panel)

    public GameWindow(GameManager gameManager, InputController inputController) {
        jframe = new JFrame();
        viewPort = new ViewPort(gameManager, inputController);

        //Initialize the frame
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.add(viewPort);
        jframe.setResizable(false);
        jframe.pack(); //Resize frame to fit the panel (Just the ViewPort)
        jframe.setLocationRelativeTo(null); //Launch in the center of the users screen.
        jframe.setVisible(true);

        //Custom listener to stop the game thread when the window loses focus. If the user clicks to their desktop or
        //another window the game should "pause" which is accomplished by stopping the game thread.
        jframe.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowLostFocus(WindowEvent e) { gameManager.stopGameLoop();}

            @Override
            public void windowGainedFocus(WindowEvent e) {
                gameManager.startGameLoop();
                System.out.println("Gained Focus");
            }
        });
    }

    //ViewPort getter
    public JPanel getViewPort(){return viewPort;}
}

//Viewport class which is the main game drawing panel
class ViewPort extends JPanel {
    private static final int WIDTH = 900;
    private static final int HEIGHT = 800;
    private GameManager gameManager;


    ViewPort(GameManager gameManager, InputController inputController) {
        this.gameManager = gameManager;

        //Set the size of viewport
        Dimension size = new Dimension(WIDTH, HEIGHT);
        setPreferredSize(size);

        //Add input listeners to the game window
        addKeyListener(inputController);
        addMouseListener(inputController);
        addMouseMotionListener(inputController);

        //Sets the panel as focusable so it can get input
        setFocusable(true);
        //Request focus for the panel so that it starts with focus
        requestFocusInWindow();
    }

    //Drawing method for the panel. Passes the drawing surface to the game for drawing.
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        gameManager.draw(g);
    }
}
