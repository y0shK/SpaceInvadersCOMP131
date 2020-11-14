import java.util.ArrayList;
import java.util.Random;

// import math for hurtbox detection
import java.lang.Math;

// graphics
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;

// events
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

// swing
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class SpaceInvaders extends JPanel implements ActionListener, KeyListener, Runnable {

    // JPanel variables - do not change
    private final int canvasWidth;
    private final int canvasHeight;
    private final Color backgroundColor;

    private final int framesPerSecond = 25;
    private final int msPerFrame = 1000 / framesPerSecond;
    private Timer timer;
    private int frame = 0;

    // player variables
    private Person person1; // player
    private int personXCoordinate; // 0
    private int personYCoordinate; // this.canvasHeight - 20;

    private int personSize = 20;

    // laser variables
    private ArrayList<PlayerLaser> lasers= new ArrayList<PlayerLaser>();
    private ArrayList<AlienLaser> alienLasers= new ArrayList<AlienLaser>();
    int laserSize = 6;
    int laserSpeed = 2;
    private int framesSinceFire = 0;

    // alien variables
    private int alienHt = 50;
    private int alienWidth = 15;
    private int speed = 5; // alien speed
    private int alienLaserSize = 10;

    // variables that put aliens on screen
    private int alienpos = 1; // initial position of the first alien
    private ArrayList<Alien> onScreen = new ArrayList<Alien>(); // arrayList of all aliens
    private int numRows = 0; // number of rows of all aliens - initialize at 0

    private int alienXCoordinate = alienpos; // potential FIXME?
    private int alienYCoordinate = alienHt; // again, for var name, potential FIXME?

    // variables that deal with game ending
    private boolean isGameLost = false;
    private boolean isGameWon = false;

    // FIXME list your game objects here

    /* Constructor for a Space Invaders game
     */
    public SpaceInvaders() {
        // fix the window size and background color
        this.canvasWidth = 600;
        this.canvasHeight = 400;
        this.backgroundColor = Color.BLACK;
        setPreferredSize(new Dimension(this.canvasWidth, this.canvasHeight));

        // set the drawing timer
        this.timer = new Timer(msPerFrame, this);

        // define person coordinates
        this.personXCoordinate = 0;
        this.personYCoordinate = this.canvasHeight - 20;

        // instantiate the player
        this.person1 = new Person(personXCoordinate, personYCoordinate, personSize, new Color(0,255,0));

        // while there aren't enough aliens drawn on the screen, add the remaining aliens
        // if there are finally enough aliens (5 rows), then break from the while loop and continue
        while (numRows < 5) {
            while(alienpos + 15 <= canvasWidth-100) {
                onScreen.add(new Alien(alienpos, alienHt, alienWidth, new Color(0,0,255)));
                alienpos+=30;
            }
            alienHt+=25;
            numRows++;
            alienpos = 0;
        }



    }

    /* Start the game
     */
    @Override
    public void run() {
        // show this window
        display();

        // set a timer to redraw the screen regularly
        this.timer.start();
    }

    /* Create the window and display it
     */
    private void display() {
        JFrame jframe = new JFrame();
        jframe.addKeyListener(this);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setContentPane(this);
        jframe.pack();
        jframe.setVisible(true);
    }

    /* Run all timer-based events
     *
     * @param e  An object describing the timer
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // update the game objects
        update();
        // draw every object (this calls paintComponent)
        repaint(0, 0, this.canvasWidth, this.canvasHeight);
        // increment the frame counter
        this.frame++;
    }

    /* Paint/Draw the canvas.
     *
     * This function overrides the paint function in JPanel. This function is
     * automatically called when the panel is made visible.
     *
     * @param g The Graphics for the JPanel
     */
    @Override
    protected void paintComponent(Graphics g) {
        // clear the canvas before painting
        clearCanvas(g);
        if (hasWonGame()) {
            paintWinScreen(g);
        } else if (hasLostGame()) {
            paintLoseScreen(g);
        } else {
            paintGameScreen(g);
        }
    }
    /* Clear the canvas
     *
     * @param g The Graphics representing the canvas
     */
    private void clearCanvas(Graphics g) {
        Color oldColor = g.getColor();
        g.setColor(this.backgroundColor);
        g.fillRect(0, 0, this.canvasWidth, this.canvasHeight);
        g.setColor(oldColor);
    }

    /* Respond to key release events
     *
     * A key release is when you let go of a key
     *
     * @param e  An object describing what key was released
     */
    public void keyReleased(KeyEvent e) {
        // you can leave this function empty
    }

    /* Respond to key type events
     *
     * A key type is when you press then let go of a key
     *
     * @param e  An object describing what key was typed
     */
    public void keyTyped(KeyEvent e) {
        // you can leave this function empty
    }

    /* Respond to key press events
     *
     * A key type is when you press then let go of a key
     *
     * @param e  An object describing what key was typed
     */
    public void keyPressed(KeyEvent e) {
        // personXCoordinate is meant to be instantiated as person.X - this notation makes it more clear

        // move left
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (personXCoordinate > 0) { // does the player have space to move left?
                personXCoordinate -= 10;
            }


        }
        // move right
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (personXCoordinate < 600 - personSize) { // does the player have space to move right?
                personXCoordinate += 10;
            }

        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            // make sure that the player can't "spam" projectiles
            while (framesSinceFire > 10) { // arbitrarily chosen amount of frames
                lasers.add(new PlayerLaser(personXCoordinate+(this.personSize/2)-(this.laserSize/2), this.canvasHeight-this.personSize, this.laserSize, new Color(255,0,0)));
                framesSinceFire = 0;
            }


        }
    }

    /* Update the game objects
     */
    private void update() {
        this.person1 = new Person(personXCoordinate, personYCoordinate, personSize, new Color(0,255,0));
        framesSinceFire++; // increase the frame counter

    }

    /* Check if the player has lost the game
     *
     * @returns  true if the player has lost, false otherwise
     */
    private boolean hasLostGame() {
        // FIXME add other lose conditions - I have only added if the enemy laser hits the player
        if (isGameLost) {
            return true;
        }
        return false;
    }

    /* Check if the player has won the game
     *
     * @returns  true if the player has won, false otherwise
     */
    private boolean hasWonGame() {
        if (this.onScreen.size() == 0) {
            return true;
        }
        return false; // FIXME delete this when ready
    }

    /* Paint the screen during normal gameplay
     *
     * @param g The Graphics for the JPanel
     */
    private void paintGameScreen(Graphics g) {

        // create margin of error for lasers hitting targets
        int hurtboxMarginOfError = 5;

        person1.draw(g);

        for(Alien a : onScreen) {

            Random rand = new Random();
            int firePrb =  rand.nextInt(1000);

            // use a spontaneous probability (generated randomly) to add a new laser (extra challenge)
            if (firePrb > 998) {
                this.alienLasers.add(new AlienLaser((int) a.x, (int) a.y, alienLaserSize, new Color(255,0,0)));
            }

            // if the player's laser touches the alien, remove it from the screen
            // Exception in thread "AWT-EventQueue-0" java.util.ConcurrentModificationException occurs, but no actual error
            for (Alien b : onScreen) {
                for (PlayerLaser l : lasers) {
                    if (Math.abs(b.x - l.x) < 15 && Math.abs(b.y - l.y) < 3) {
                        onScreen.remove(b);
                    }
                }

            }

            if (a.x >= (600-this.alienWidth)) {
                speed = -5;
                for(Alien b : onScreen) {
                    b.y +=5;
                    b.x-=5;
                }
            }
            else if (a.x <= 0){
                speed = 5;
                for(Alien b : onScreen) {
                    b.y +=5;
                    b.x+=5;
                }
            }

            if (frame % 20 == 0) {
                a.x += speed;
            }

            a.draw(g);
        }


        for (PlayerLaser l : lasers) {

            l.y -= laserSpeed;
            l.draw(g);
        }

        for (AlienLaser l : alienLasers) {

            l.y+= laserSpeed;
            l.draw(g);

            // if an alien laser hits the player, the game is lost
            if (Math.abs((l.x - personXCoordinate)) < 15) { // give some leeway for the laser to hit the player
                if (Math.abs(l.y - personYCoordinate) < 3) { // however, y coordinates should be relatively close
                    isGameLost = true;
                }

            }

        }

        if (frame % 20 == 0) {
            Random rand = new Random();

            // optional - uncomment if the aliens should randomly disappear
            //int index = rand.nextInt(onScreen.size());
            //onScreen.remove(index);
        }
    }

    /* Paint the screen when the player has won
     *
     * @param g The Graphics for the JPanel
     */
    private void paintWinScreen(Graphics g) {
        char[] win = {'Y', 'o','u',' ', 'w','o', 'n', '!', '!'};
        g.setColor(new Color(0,255,0));
        g.setFont(new Font("ComicSans", Font.PLAIN, 50));
        g.drawChars(win, 0, win.length, 200, 200);
    }

    /* Paint the screen when the player has lost
     *
     * @param g The Graphics for the JPanel
     */
    private void paintLoseScreen(Graphics g) {
        char[] lose = {'Y', 'o','u',' ', 'l','o', 's', 't', ' ', ':', '(', ' ', ':', '(', ' ', ':', '('};
        g.setColor(new Color(0,255,0));
        g.setFont(new Font("ComicSans", Font.PLAIN, 50));
        g.drawChars(lose, 0, lose.length, 150, 200);
    }

    public static void main(String[] args) {
        SpaceInvaders invaders = new SpaceInvaders();
        EventQueue.invokeLater(invaders);
    }
}
