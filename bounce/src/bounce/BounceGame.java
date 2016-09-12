package bounce;

import java.util.ArrayList;

import jig.Entity;
import jig.ResourceManager;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * A Simple Game of Bounce.
 * 
 * The game has three states: StartUp, Playing, and GameOver, the game
 * progresses through these states based on the user's input and the events that
 * occur. Each state is modestly different in terms of what is displayed and
 * what input is accepted.
 * 
 * In the playing state, our game displays a moving rectangular "ball" that
 * bounces off the sides of the game container. The ball can be controlled by
 * input from the user.
 * 
 * When the ball bounces, it appears broken for a short time afterwards and an
 * explosion animation is played at the impact site to add a bit of eye-candy
 * additionally, we play a short explosion sound effect when the game is
 * actively being played.
 * 
 * Our game also tracks the number of bounces and syncs the game update loop
 * with the monitor's refresh rate.
 * 
 * Graphics resources courtesy of qubodup:
 * http://opengameart.org/content/bomb-explosion-animation
 * 
 * Sound resources courtesy of DJ Chronos:
 * http://www.freesound.org/people/DJ%20Chronos/sounds/123236/
 * 
 * 
 * @author wallaces
 * 
 */
public class BounceGame extends StateBasedGame {
	
	public static final int STARTUPSTATE = 0;
	public static final int PLAYINGSTATE = 1;
	public static final int GAMEOVERSTATE = 2;
	public static final int SPLASHSCREENSTATE = 3;
	public static final int GAMEWONSTATE = 4;
	public static final int LEVEL1STATE = 5;
	public static final int LEVEL2STATE = 6;
	public static final int LEVEL3STATE = 7;
	public static final int GOSTATE = 8;
	
	public static final String BALL_BALLIMG_RSC = "bounce/resource/ball.png";
	public static final String BALL_BROKENIMG_RSC = "bounce/resource/brokenball.png";
	public static final String SPLASH_SCREEN_RSC = "bounce/resource/SplashScreen.png";
	public static final String GAMEOVER_BANNER_RSC = "bounce/resource/Gameover1.png";
	public static final String STARTUP_BANNER_RSC = "bounce/resource/PressSpace1.png";
	public static final String BANG_EXPLOSIONIMG_RSC = "bounce/resource/explosion.png";
	public static final String BANG_EXPLOSIONSND_RSC = "bounce/resource/explosion.wav";
	public static final String PADDLE_LEVEL_1_RSC = "bounce/resource/paddle_level1.png";
	public static final String SIDE_PADDLE_RSC = "bounce/resource/side_paddle.png";
	public static final String PADDLE_LEVEL_3_RSC = "bounce/resource/paddle_level3.png";
	public static final String BRICK_RSC = "bounce/resource/brick.png";
	public static final String BALL_EARTH_RSC = "bounce/resource/ball_earth.png";
	public static final String BALL_EARTH_DESTROYED_RSC = "bounce/resource/ball_earth_destroyed.png";
	public static final String LEVEL_1_RSC = "bounce/resource/Level_1.png";
	public static final String LEVEL_2_RSC = "bounce/resource/Level_2.png";
	public static final String LEVEL_3_RSC = "bounce/resource/Level_3.png";
	public static final String GO_RSC = "bounce/resource/Go.png";
	public static final String GAMEWON_RSC = "bounce/resource/GameWon.png";
	
	public final int ScreenWidth;
	public final int ScreenHeight;

	Ball ball;
	Paddle paddle;
	Bricks bricks;
	ArrayList<Bang> explosions; // MAKE SURE THIS IS CLEARED PERIODICALLY
	ArrayList<Bricks> brick;
	ArrayList<SidePaddles> paddles;

	/**
	 * Create the BounceGame frame, saving the width and height for later use.
	 * 
	 * @param title
	 *            the window's title
	 * @param width
	 *            the window's width
	 * @param height
	 *            the window's height
	 */
	public BounceGame(String title, int width, int height) {
		super(title);
		ScreenHeight = height;
		ScreenWidth = width;

		Entity.setCoarseGrainedCollisionBoundary(Entity.AABB);
		explosions = new ArrayList<Bang>(10);
		brick = new ArrayList<Bricks>(50);
		paddles = new ArrayList<SidePaddles>(3);
	}


	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		addState(new SplashScreenState());
		addState(new StartUpState());
		addState(new GameOverState());
		addState(new PlayingState());
		addState(new GameWonState());
		addState(new Level1State());
		addState(new Level2State());
		addState(new Level3State());
		addState(new GoState());
		
		// the sound resource takes a particularly long time to load,
		// we preload it here to (1) reduce latency when we first play it
		// and (2) because loading it will load the audio libraries and
		// unless that is done now, we can't *disable* sound as we
		// attempt to do in the startUp() method.
		ResourceManager.loadSound(BANG_EXPLOSIONSND_RSC);	

		// preload all the resources to avoid warnings & minimize latency...
		ResourceManager.loadImage(BALL_BALLIMG_RSC);
		ResourceManager.loadImage(BALL_BROKENIMG_RSC);
		ResourceManager.loadImage(SPLASH_SCREEN_RSC);
		ResourceManager.loadImage(GAMEOVER_BANNER_RSC);
		ResourceManager.loadImage(STARTUP_BANNER_RSC);
		ResourceManager.loadImage(BANG_EXPLOSIONIMG_RSC);
		ResourceManager.loadImage(PADDLE_LEVEL_1_RSC);
		ResourceManager.loadImage(PADDLE_LEVEL_3_RSC);
		ResourceManager.loadImage(SIDE_PADDLE_RSC);
		ResourceManager.loadImage(BRICK_RSC);
		ResourceManager.loadImage(BALL_EARTH_RSC);
		ResourceManager.loadImage(BALL_EARTH_DESTROYED_RSC);
		ResourceManager.loadImage(LEVEL_1_RSC);
		ResourceManager.loadImage(LEVEL_2_RSC);
		ResourceManager.loadImage(LEVEL_3_RSC);
		ResourceManager.loadImage(GO_RSC);
		ResourceManager.loadImage(GAMEWON_RSC);
		
		ball = new Ball(ScreenWidth / 4, ScreenHeight / 2, .1f, .2f);
		paddle = new Paddle(ScreenWidth / 2, ScreenHeight - 16);
	}
	
	public static void main(String[] args) {
		AppGameContainer app;
		try {
			app = new AppGameContainer(new BounceGame("Bounce!", 800, 600));
			app.setDisplayMode(800, 600, false);
			app.setVSync(true);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}

	}

	
}
