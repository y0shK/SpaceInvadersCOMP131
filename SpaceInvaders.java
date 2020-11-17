import java.util.ArrayList; // arrayList to contain objects
import java.util.Random; // randomly fired enemy projectiles

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
    private Person person1; // player instance
    private int personXCoordinate; // 0
    private int personYCoordinate; // this.canvasHeight - 20;
    private int personSize = 20; // size of player sprite

    // player laser variables
    private ArrayList<PlayerLaser> lasers= new ArrayList<PlayerLaser>(); // store all player lasers on screen
    private ArrayList<AlienLaser> alienLasers= new ArrayList<AlienLaser>(); // store all enemy lasers on screen

    int laserSize = 6; // player laser size
    int laserSpeed = 5; // player laser speed

    // can't move until after a certain amount of frames elapse after firing a laser
    private int framesSinceFire = 0; // will be initialized after th laser is fired

    // store deleted lasers to safely remove them from the screen
    private ArrayList<PlayerLaser> deletedLasers = new ArrayList<PlayerLaser>();
    private ArrayList<AlienLaser> deletedAlienLasers = new ArrayList<AlienLaser>();


    // alien variables
    private int alienHt = 50; // alien height
    private int alienWidth = 15; // alien width
    private int speed = 5; // alien speed
    private int alienLaserSize = 10;

    // variables that put aliens on screen
    private int alienpos = 1; // initial position of the first alien
    private ArrayList<Alien> onScreen = new ArrayList<Alien>(); // arrayList of all aliens
    private int numRows = 0; // number of rows of all aliens - initialize at 0

    // same concept as alien lasers
    private ArrayList<Alien> deletedAliens = new ArrayList<Alien>();

    // variables that deal with game ending
    private boolean isGameLost = false; // game lost
    // game won when enemy list size is 0 - no need to keep track with a flag

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
            while(alienpos + 15 <= canvasWidth - 100) {
                onScreen.add(new Alien(alienpos, alienHt, alienWidth, new Color(0,0,255)));
                alienpos += 30;
            }
            // move the aliens up while a new row is created below - reinitialize the position
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

        // move left
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (personXCoordinate > 0) { // does the player have space to move left?
                personXCoordinate -= 10; // subtract the x coordinate to move left
            }


        }
        // move right
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (personXCoordinate < 600 - personSize) { // does the player have space to move right?
                personXCoordinate += 10; // add to the x coordinate to move right
            }

        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            // make sure that the player can't "spam" projectiles - once one is fired,
                // there's a brief period that restricts movement and refiring
            while (framesSinceFire > 10) { // arbitrarily chosen amount of frames
                lasers.add(new PlayerLaser(personXCoordinate+(this.personSize/2)-(this.laserSize/2),
                        this.canvasHeight-this.personSize,
                        this.laserSize,
                        new Color(255,0,0)));
                framesSinceFire = 0; // reset the restriction period once it's over
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
        // this flag can trigger in 3 ways; player hit with alien projectile, hit with alien, or aliens get past player
        if (isGameLost) {
            return true;
        }
        return false; // else
    }

    /* Check if the player has won the game
     *
     * @returns  true if the player has won, false otherwise
     */
    private boolean hasWonGame() {
        if (this.onScreen.size() == 0) { // if alien arrayList size is 0 - i.e. there are no more aliens
            return true;
        }
        return false; // else
    }

    /* Paint the screen during normal gameplay
     *
     * @param g The Graphics for the JPanel
     */
    private void paintGameScreen(Graphics g) {
        person1.draw(g);

        for (Alien b : onScreen) {
            for (PlayerLaser l : lasers) {
                if (Math.abs(b.x - l.x) < 15 && Math.abs(b.y - l.y) < 3) { // if any player laser touches any alien
                    // then keep track of which aliens to remove and which lasers need to exit the screen
                    this.deletedAliens.add(b);
                    this.deletedLasers.add(l);
                }
            }

            // if the alien touches the player, the game is lost
            // contact is defined by abs(alien.x or y - person.x or y) is less than an arbitrary threshold
            if (Math.abs(b.x - personXCoordinate) < 15) {
                if (Math.abs(b.y - personYCoordinate) < 3) {
                    isGameLost = true;
                }
            }

            // if the alien exits the screen below the player, the game lost
            if (b.y >= canvasHeight) {
                isGameLost = true;
            }

        }

        for(Alien a : onScreen) {

            Random rand = new Random();
            int firePrb =  rand.nextInt(1000);

            // use a spontaneous probability (generated randomly) to add a new laser
            if (firePrb > 998) { // fire probability per frame
                this.alienLasers.add(new AlienLaser((int) a.x, (int) a.y, alienLaserSize, new Color(255,0,0)));
            }

            if (a.x >= (600-this.alienWidth)) { // if the aliens are going to exit the screen to the right
                speed = -5; // reverse the velocity
                for(Alien b : onScreen) { // keep the aliens going down the screen and switching direction
                    b.y +=5;
                    b.x-=5;
                }
            }
            // same process, in reverse
            else if (a.x <= 0){
                speed = 5;
                for(Alien b : onScreen) {
                    b.y +=5;
                    b.x+=5;
                }
            }

            // increase horizontal movement every 20 frames
            // makes it harder for the player after a certain amount of time
            if (frame % 20 == 0) {
                a.x += speed;
            }

            a.draw(g);
        }


        for (PlayerLaser l : lasers) {

            l.y -= laserSpeed; // every frame, move the laser up by the laser speed
            l.draw(g); // redraw the laser
            if (l.y < 0) { // if the laser exits the screen, remove it
                deletedLasers.add(l);
            }
        }

        for (AlienLaser l : alienLasers) {

            l.y+= laserSpeed; // every frame, move the laser down by the laser speed
            l.draw(g);
            if(l.y > this.canvasHeight) { // if the laser exits the screen, remove it
                deletedAlienLasers.add(l);

            }

            // if an alien laser hits the player, the game is lost
            if (Math.abs((l.x - personXCoordinate)) < 15) { // give some leeway for the laser to hit the player
                if (Math.abs(l.y - personYCoordinate) < 3) { // however, y coordinates should be relatively close
                    isGameLost = true;
                }

            }

        }

        // remove all aliens to be deleted
        for(Alien index : deletedAliens) {
            onScreen.remove(index);
        }

        for(PlayerLaser index : deletedLasers) {
            this.lasers.remove(index);
        }
        for(AlienLaser index : deletedAlienLasers) {
            this.alienLasers.remove(index);
        }

        // clear previous values before adding them again - makes sure gameplay works as expected
        deletedAlienLasers.clear();
        deletedAliens.clear();
        deletedLasers.clear();
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

