
import java.util.ArrayList;
import java.util.Random;

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

    // laser boolean flag
//    boolean shootingLaser = false;
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

    private int alienpos = 1; // initial position of the first alien
    private ArrayList<Alien> onScreen = new ArrayList<Alien>(); // arrayList of all aliens
    private int numRows = 0; // number of rows of all aliens - initialize at 0

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
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (personXCoordinate > 0) {
                personXCoordinate -= 10;
            }


        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            // player
            if (personXCoordinate < 600 - personSize) {
                personXCoordinate += 10;
            }

        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
        	while (framesSinceFire > 20) {
        		lasers.add(new PlayerLaser(personXCoordinate+(this.personSize/2)-(this.laserSize/2), this.canvasHeight-this.personSize, this.laserSize, new Color(255,0,0)));
        		framesSinceFire = 0;
        	}
        
        	
        }
    }

    /* Update the game objects
     */
    private void update() {
        this.person1 = new Person(personXCoordinate, personYCoordinate, personSize, new Color(0,255,0));
        framesSinceFire++;

    }

    /* Check if the player has lost the game
     *
     * @returns  true if the player has lost, false otherwise
     */
    private boolean hasLostGame() {
    	
        return false; // FIXME delete this when ready
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
        person1.draw(g);
        
        for(Alien a : onScreen) {
        	
        	Random rand = new Random();
        	int firePrb =  rand.nextInt(1000);
        	if (firePrb > 998) {
        		this.alienLasers.add(new AlienLaser((int) a.x, (int) a.y, alienLaserSize, new Color(255,0,0)));
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
        	}
        
        if (frame % 20 == 0) {
        	Random rand = new Random();
        	int index = rand.nextInt(onScreen.size());
        	onScreen.remove(index);
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
