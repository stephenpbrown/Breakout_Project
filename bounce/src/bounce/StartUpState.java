package bounce;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Scanner;

import jig.ResourceManager;
import jig.Vector;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * This state is active prior to the Game starting. In this state, sound is
 * turned off, and the bounce counter shows '?'. The user can only interact with
 * the game by pressing the SPACE key which transitions to the Playing State.
 * Otherwise, all game objects are rendered and updated normally.
 * 
 * Transitions From (Initialization), GameOverState
 * 
 * Transitions To PlayingState
 */
class StartUpState extends BasicGameState {

	int highScore;
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		container.setSoundOn(false);
		
		try {
			highScore = readHighScore();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int readHighScore() throws FileNotFoundException
	{
		Scanner scanner = new Scanner(new File("highScore.txt"));
		int score = scanner.nextInt();
		System.out.println(score);
		return score;
	}

	public int getUserHighScore()
	{
		return highScore;
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
		BounceGame bg = (BounceGame)game;
		
		bg.ball.render(g); // Draw ball
		
		g.drawImage(ResourceManager.getImage(BounceGame.STARTUP_BANNER_RSC), 185, 210);	
		
		//g.drawString("Score: 0", 10, 30);
		//g.drawString("Highscore: " + ((GameWonState)game.getState(BounceGame.GAMEWONSTATE)).getUserHighScore(), 620, 10);
		g.drawString("Highscore: " + highScore, 620, 10);
		//g.drawString("Lives Remaining: ?", 10, 50);
		
		for (Bang b : bg.explosions)
			b.render(g);	
		
		//bg.paddle.render(g); // Draw paddle
	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {

		Input input = container.getInput();
		BounceGame bg = (BounceGame)game;

		if (input.isKeyDown(Input.KEY_SPACE))
		{
			bg.ball.setPosition(bg.ScreenWidth / 4, bg.ScreenHeight / 2);
			bg.ball.setVelocity(new Vector(.1f, .2f));
			bg.ball.removeBrokenBall();
			bg.enterState(BounceGame.LEVEL1STATE);	
		}
			
		// bounce the ball...
		boolean bounced = false;
		if (bg.ball.getCoarseGrainedMaxX() > bg.ScreenWidth && bg.ball.getVelocity().getX() > 0) // Right horizontal check
		{
			bg.ball.bounce(90);
			bounced = true;
		} 
		else if (bg.ball.getCoarseGrainedMinX() < 0 && bg.ball.getVelocity().getX() < 0) // Left horizontal check
		{
			bg.ball.bounce(90);
			bounced = true;
		}
		else if (bg.ball.getCoarseGrainedMaxY() > bg.ScreenHeight && bg.ball.getVelocity().getY() > 0) // Top vertical check
		{
			bg.ball.bounce(0);
			bounced = true;
		}
		else if (bg.ball.getCoarseGrainedMinY() < 0 && bg.ball.getVelocity().getY() < 0) // Bottom vertical check
		{
			bg.ball.bounce(0);
			bounced = true;
		}
		
		// Bounce off the paddle
//		if (bg.ball.getCoarseGrainedMaxY() > bg.paddle.getCoarseGrainedMinY()
//			&& bg.ball.getCoarseGrainedMinX() < bg.paddle.getCoarseGrainedMaxX()
//			&& bg.ball.getCoarseGrainedMaxX() > bg.paddle.getCoarseGrainedMinX())
//		{
//			bg.ball.bounce(0);
//			bounced = true;
//		}
		
		if (bounced) {
			bg.explosions.add(new Bang(bg.ball.getX(), bg.ball.getY()));
		}
		bg.ball.update(delta);

		// check if there are any finished explosions, if so remove them
		// CAREFUL WITH GETTING CONCURRENT MODIFYING EXCEPTION HERE
		// HAPPENS WHEN YOU MODIFY THE LIST WHILE YOU'RE TRYING TO WALK IT
		for (Iterator<Bang> i = bg.explosions.iterator(); i.hasNext();) {
			if (!i.next().isActive()) {
				i.remove();
			}
		}

	}

	@Override
	public int getID() {
		return BounceGame.STARTUPSTATE;
	}
	
}