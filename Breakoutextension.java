
import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakoutextension extends GraphicsProgram {

	/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

	/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

	/** Dimensions of the paddle */
	private int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

	/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

	/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

	/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

	/** Separation between bricks */
	private static final int BRICK_SEP = 4;

	/** Width of a brick */
	private static final int BRICK_WIDTH = (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

	/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

	/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

	/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

	/** Number of turns */
	private static final int NTURNS = 3;
	
	/*They are used for drawing some figures and saving some information
	 * It's clear from their names.
	*/
	int brokenBricks = 0;
	private GRect rect;
	private GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
	private GOval ball = new GOval(2 * BALL_RADIUS, 2 * BALL_RADIUS);
	private GLabel label = new GLabel("score: " + brokenBricks * 10);
	private GLabel livesLabel = new GLabel("Lives: " + NTURNS);
	private GRect powerLong = new GRect (PADDLE_WIDTH,  PADDLE_HEIGHT);
	

	//Using the variables to see if player won or lost the game.
	int bricks = NBRICKS_PER_ROW * NBRICK_ROWS;
	
	
	//Ball speed, vx is given by random generator, between certain numbers.
	private double vx;
	private double vy = 3;
	
	//Using to find sizes of labels.
	double labelHeight = label.getAscent();
	double labelWidth = label.getWidth();
	
	/* Method: run() */
	/** Runs the Breakout program. */
	
	//Draws, then lets player to play and then decides if he lost or won.
	public void run() {

		initialize();
		play();
		winOrLose();

	}
	

	/* Everything that's in action is in this method. it adds mouse listeners,
	 * gets random vx for ball dropping angle and moves ball, also it refletcs 
	 * from walls and padle and check when player loses, like when the ball is 
	 * below certain area.
	 */
	private void play() {
		for (int i = 0; i < NTURNS; i++) {
			remove(livesLabel);
			livesLabel.setLabel(String.valueOf("Lives: " + (NTURNS - i)));
	        add(livesLabel, getWidth() - labelWidth, labelHeight);
			addMouseListeners();
			randomV();
			ballMover();
		}
	}
	
	// This is pwoer up, it makes paddle biggerm you need to pick it up.
	private void powerBricks(){
		powerLong.setFilled(true);
		powerLong.setColor(Color.BLUE);
		add(powerLong, getWidth()/2 + 20, 400);
	}
	
	

	//Chooses random number for vx(Dropping angle and speed).
	private void randomV() {
		
		RandomGenerator rgen = RandomGenerator.getInstance();
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5))
			vx = -vx;

	}

	// Initialize method draws bricks, padel and power up.
	private void initialize() {
		brickRowDrawer();
		paddleDrawer();
		powerBricks();
	}

	
	// Draws rows of brick, so it draws all the bricks.
	private void brickRowDrawer() {

		for (int j = 0; j < NBRICK_ROWS; j++) {

			int Y = BRICK_Y_OFFSET + j * (BRICK_HEIGHT + BRICK_SEP);
			double X = (getWidth() - BRICK_WIDTH * NBRICKS_PER_ROW - 
					( NBRICKS_PER_ROW - 1 ) * BRICK_SEP) / 2;
			for (int i = 0; i < NBRICKS_PER_ROW; i++) {
	
				rect = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
				rect.setFilled(true);
				colorGiver(j);
				add(rect, X, Y);
				X = X + BRICK_WIDTH + BRICK_SEP;
			}

		}

	}

	// Gives color, depends on the row number
	private void colorGiver(int j) {
		if (j % 10 == 0 || j % 10 == 1) {
			rect.setColor(Color.RED);
		}
		if (j % 10 == 2 || j % 10 == 3) {
			rect.setColor(Color.ORANGE);
		}
		if (j % 10 == 4 || j % 10 == 5) {
			rect.setColor(Color.YELLOW);
		}
		if (j % 10 == 6 || j % 10 == 7) {
			rect.setColor(Color.GREEN);
		}
		if (j % 10 == 8 || j % 10 == 9) {
			rect.setColor(Color.CYAN);
		}
	}

	//Draws padle.
	private void paddleDrawer() {

		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle, (WIDTH - PADDLE_WIDTH) / 2, 
				HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT);

	}

	//This moves paddle and also controls it to not go out from the bounds.
	public void mouseMoved(MouseEvent event) {
		
		if (event.getX() - PADDLE_WIDTH / 2 >= 0 && 
				event.getX() + PADDLE_WIDTH / 2 <= WIDTH) {
			
			paddle.setLocation(event.getX() - PADDLE_WIDTH / 2, 
					HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		}
	}

	//This draws ball.
	private void ballDrawer() {

		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		double X = WIDTH / 2 - BALL_RADIUS;
		double Y = HEIGHT / 2 - BALL_RADIUS;
		add(ball, X, Y);

	}

	/* This method moves ball, reflects it from walls, paddle and controls
	 * if the ball is going below the paddle. If that happens it takes one life.
	 */
	private void ballMover() {
		ballDrawer();
		while (true) {

			ball.move(vx, vy);
			pause(10);

			if (ball.getX() + 2 * BALL_RADIUS >= WIDTH || ball.getX() <= 0) {
				vx = -vx;
			}
			if (ball.getY() <= 0) {
				vy = -vy;
				;
			}
			if (ball.getY() + 2 * BALL_RADIUS >= HEIGHT || bricks + 1 == brokenBricks) {
	
				break;
			}

			jumpingFromPaddle();
			brickBreaker();
		}
	}

	//Reflects ball from paddle.
	private void jumpingFromPaddle() {
		if (getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS) == paddle) {
			vy = -Math.abs(vy);
		}
		if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS) == paddle) {
			vy = -Math.abs(vy);
		}

	}

	/* Using for "breaking bricks". It returns what element the ball is touching.
	 * If touching nothing, returns "null".
	 */
	private GObject getCollidingObject(int x, int y) {

		return getElementAt(ball.getX() + x, ball.getY() + y);
	}

	
	/* Checks if the ball touched a brick, if it did, removes that birck and
	 * adds one to broken bricks.
	 * Also it doubles the size of the paddle if the power up is picked.
	 */
	
	private void brickBreaker() {
		GObject collidery = getCollidingObject(0, 0);
		GObject collidery1 = getCollidingObject(0, 2 * BALL_RADIUS);
		GObject colliderx = getCollidingObject(2 * BALL_RADIUS, 0);

		add(label, 0, labelHeight);
		
		if (collidery != null && collidery != paddle  ) {
			
			remove(collidery);
			vy = -vy;
			brokenBricks = brokenBricks + 1;
			speed();
			//label.setLabel("score: " + brokenBricks * 10);
			remove(label);
			label.setLabel(String.valueOf("score: " + brokenBricks*10));
	        add(label, 0, labelHeight);
			
		} else if (collidery1 != null && collidery1 != paddle && collidery1 != powerLong) {
			remove(collidery1);
			vy = -vy;
			
			brokenBricks = brokenBricks + 1;
			speed();
			//label.setLabel("score: " + brokenBricks * 10);
			remove(label);
			label.setLabel(String.valueOf("score: " + brokenBricks*10));
	        add(label, 0, labelHeight);
		}

		else if (colliderx != null && colliderx != paddle && colliderx != powerLong) {
			remove(colliderx);
			vx = -vx;
			brokenBricks = brokenBricks + 1;
			speed();
			//label.setLabel("score: " + brokenBricks * 10);
			remove(label);
			label.setLabel(String.valueOf("score: " + brokenBricks*10));
	        add(label, 0, labelHeight);
		}
		
		if(getElementAt(getWidth()/2 + 20, 400) == null ){
			if(PADDLE_WIDTH < 61){
			remove(paddle);
			PADDLE_WIDTH = 2 * PADDLE_WIDTH;
		    paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		    paddleDrawer();
			}
		}
		
	}
	
	//It increases the vy speed.
	private void speed(){
			
		if(brokenBricks < 15){
		vy = vy + brokenBricks / 5;
		}
	}

	/* Ends game, removes everything and chooses if the player lost or won.
	 * Also gives appropiate message.
	 */
	
	private void winOrLose() {
		removeAll();
		if (bricks > brokenBricks) {

			GLabel label = new GLabel("Game over. ");
			double labelWidth = label.getWidth();
			double labelHeight = label.getAscent();
			add(label, (getWidth() - labelWidth) / 2, (getHeight() - labelHeight) / 2);
		} else {
			GLabel label = new GLabel("You win! ");
			double labelWidth = label.getWidth();
			double labelHeight = label.getAscent();
			add(label, (getWidth() - labelWidth) / 2, (getHeight() - labelHeight) / 2);
		}

	}

}