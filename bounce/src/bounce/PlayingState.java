package bounce;

import java.io.IOException;
import java.util.Iterator;

import jig.Vector;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.EmptyTransition;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;


/**
 * This state is active when the Game is being played. In this state, sound is
 * turned on, the bounce counter begins at 0 and increases until 10 at which
 * point a transition to the Game Over state is initiated. The user can also
 * control the ball using the WAS & D keys.
 * 
 * Transitions From StartUpState
 * 
 * Transitions To GameOverState
 */
class PlayingState extends BasicGameState {
	int bounces;
	int livesRemaining;
	int bricksRemaining;
	int level = 1;
	boolean hitBrick;
	Bricks lastBrickHit = null;
	SidePaddles lastSidePaddleHit = null;
	boolean paddleAlreadyHit = false;
	boolean resetLives = true;
	Bricks refBrick = null;
	int keepScore = 0;
	int bricksHitInARow = 0;
	int highScore = 0;
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		bounces = 0;
		container.setSoundOn(true);
		
		BounceGame bg = (BounceGame)game;
		
		int numBricks = 8;
		
		// Remove remaining bricks
		for (Iterator<Bricks> b = bg.brick.iterator(); b.hasNext();)
		{
			if(b.next() != null)
				b.remove();
		}
		
		// Remove remaining side paddles
		for (Iterator<SidePaddles> p = bg.paddles.iterator(); p.hasNext();)
		{
			if(p.next() != null)
				p.remove();
		}
			
		if(resetLives == true)
			livesRemaining = 3;
		
		// Level 1 bricks
		if(level == 1)
		{
			// Add bricks
			for (int i = 1; i < numBricks; i++)
			{
				bg.brick.add(new Bricks(i*bg.ScreenWidth / numBricks, bg.ScreenHeight / 4, 1));
				bricksRemaining = i;
			}		
		}
		
		// Level 2 bricks
		if(level == 2)
		{
			// Add bricks
			for (int i = 2; i < numBricks; i+=2)
			{
				for(int j = 1; j < 4; j++)
					bg.brick.add(new Bricks(i*bg.ScreenWidth / numBricks, bg.ScreenHeight/2 - 50*j, 1));
				bricksRemaining = i;
			}
			for (int i = 1; i < numBricks; i+=2)
			{
				for(int j = 1; j < 4; j++)
					bg.brick.add(new Bricks(i*bg.ScreenWidth / numBricks, bg.ScreenHeight/2 - 50*j, 2));
			}
			bricksRemaining *= 4;
//			System.out.println(bricksRemaining); DEBUG
		}
		
		// Level 3 bricks
		if(level == 3)
		{
			// Add bricks
			for (int i = 2; i < numBricks; i+=2)
			{
				for(int j = 1; j < 6; j++)
					bg.brick.add(new Bricks(i*bg.ScreenWidth / numBricks, bg.ScreenHeight/2 - 40*j, 2));
				bricksRemaining = i;
			}
			for (int i = 1; i < numBricks; i+=2)
			{
				for(int j = 1; j < 6; j++)
					bg.brick.add(new Bricks(i*bg.ScreenWidth / numBricks, bg.ScreenHeight/2 - 40*j, 3));
				bricksRemaining = i;
			}
			bricksRemaining *= 8;
		}
		
		// Side paddles
		Bricks refBrickInit = new Bricks(bg.ScreenWidth, bg.ScreenHeight, 1);
		refBrick = refBrickInit;
		
		bg.paddles.add(new SidePaddles(16, bg.ScreenHeight-110));
		bg.paddles.add(new SidePaddles(bg.ScreenWidth-16, bg.ScreenHeight-110));
	}
	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
		BounceGame bg = (BounceGame)game;
		
		g.drawString("Score: " + keepScore, 10, 30);
		//g.drawString("Highscore: " + ((GameWonState)game.getState(BounceGame.GAMEWONSTATE)).getUserHighScore(), 620, 10);
		g.drawString("Highscore: " + ((StartUpState)game.getState(BounceGame.STARTUPSTATE)).getUserHighScore(), 620, 10);
		g.drawString("Lives Remaining: " + livesRemaining, 10, 50);
		
		bg.ball.render(g); // Draw the ball
		bg.paddle.render(g); // Draw the paddle
		
		// Render the side paddles
		for (SidePaddles p : bg.paddles)
		{
			p.render(g);
		}
		
		// Render the bricks
		for (Bricks b : bg.brick)
		{
			b.render(g);
		}
		
		// Render the explosions
		for (Bang b : bg.explosions)
			b.render(g);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {

		Input input = container.getInput();
		BounceGame bg = (BounceGame)game;
	
		resetLives = false;
		
		// Move the paddle left
		if (input.isKeyDown(Input.KEY_LEFT) && !(bg.paddle.getCoarseGrainedMinX() < 0-30)) 
		{
			bg.paddle.setVelocity(new Vector(-0.55f,0));
		}
		// Move the paddle right
		else if (input.isKeyDown(Input.KEY_RIGHT) && !(bg.paddle.getCoarseGrainedMaxX() > bg.ScreenWidth+30)) 
		{
			bg.paddle.setVelocity(new Vector(0.55f, 0));
		}
		else
		{
			bg.paddle.setVelocity(new Vector(0,0));
		}	
		bg.paddle.update(delta);
		
		if (input.isKeyDown(Input.KEY_UP) && !(bg.paddles.get(0).getCoarseGrainedMinY() < 0))
		{
			bg.paddles.get(0).setVelocity(new Vector(0, -0.55f));
			bg.paddles.get(1).setVelocity(new Vector(0, -0.55f));
		}
		else if (input.isKeyDown(Input.KEY_DOWN) && !(bg.paddles.get(0).getCoarseGrainedMaxY() > bg.paddle.getCoarseGrainedMinY()-5))
		{
			bg.paddles.get(0).setVelocity(new Vector(0, 0.55f));
			bg.paddles.get(1).setVelocity(new Vector(0, 0.55f));
		}
		else
		{
			bg.paddles.get(0).setVelocity(new Vector(0, 0));
			bg.paddles.get(1).setVelocity(new Vector(0, 0));
		}
		bg.paddles.get(0).update(delta);
		bg.paddles.get(1).update(delta);

		// Top right angle
		double paddleTopRightdY = (bg.paddle.getCoarseGrainedMinY() - bg.ball.getCoarseGrainedHeight()/2)  - bg.paddle.getY();
		double paddleTopRightdX  = (bg.paddle.getCoarseGrainedMaxX() + bg.ball.getCoarseGrainedWidth()/2) - bg.paddle.getX();
		double paddleTopRightAngle = Math.atan2(paddleTopRightdY, paddleTopRightdX) * 180 / Math.PI;
		paddleTopRightAngle = -paddleTopRightAngle;
		
		// Top left angle
		double paddleTopLeftdY = paddleTopRightdY;
		double paddleTopLeftdX = (bg.paddle.getCoarseGrainedMinX() - bg.ball.getCoarseGrainedWidth()/2) - bg.paddle.getX();
		double paddleTopLeftAngle = Math.atan2(paddleTopLeftdY, paddleTopLeftdX) * 180 / Math.PI;
		paddleTopLeftAngle = -paddleTopLeftAngle;
		
		boolean bounced = false;
		boolean redrawBall = false;
		// Bounce off the paddle
		// Ball hits side of the paddle
		if(bg.ball.collides(bg.paddle) != null)
		{
			bricksHitInARow = 0;
			// Reference: http://stackoverflow.com/questions/7586063/how-to-calculate-the-angle-between-a-line-and-the-horizontal-axis
			double deltaY = bg.ball.getY() - bg.paddle.getY();
			double deltaX = bg.ball.getX() - bg.paddle.getX();
			
			double ballAngle = Math.atan2(deltaY, deltaX) * 180 / Math.PI;
			
			if(ballAngle > 0)
				ballAngle = 360 - ballAngle;
			else if(ballAngle < 0)
				ballAngle = -ballAngle;
			
			if(!paddleAlreadyHit)
			{
				paddleAlreadyHit = true;
				
				if(ballAngle >= paddleTopRightAngle && ballAngle <= paddleTopLeftAngle) // Top of the paddle
				{
					// Get the points and scale, which should give the angle to put the ball at
					double offset = bg.paddle.getX() - bg.ball.getX();
					double maxOffset = bg.paddle.getCoarseGrainedWidth()/2 + bg.ball.getCoarseGrainedWidth();
					
					double scaleX = 0;
					if(level == 1)
						scaleX = 0.3;
					else if(level == 2)
						scaleX = 0.33;
					else if(level == 3)
						scaleX = 0.37;
					
					double xVelocity = Math.abs((offset/maxOffset)) * scaleX;
					
					if(offset > 0)
						xVelocity = -xVelocity;
						
					double yVelocity = Math.sqrt(scaleX*scaleX - xVelocity*xVelocity);
					
//					System.out.println("xVelocity = " + xVelocity + ", yVelcity = " + yVelocity);
//					System.out.println(bg.ball.getVelocity());
					//System.out.println("direction = " + direction); // + ", ballAngle = " + ballAngle);
					bg.ball.setVelocity(new Vector((float) xVelocity, (float) yVelocity*(-1)));
					//bg.ball.bounce(0); 
				}
				else if(ballAngle >= paddleTopLeftAngle) // Left of the paddle
				{
					bg.ball.bounce(0);
				}
				else  // Right of the brick
				{
					bg.ball.bounce(0);
				}
				
				bounced = true;
				bounces++;
			}
			
			lastSidePaddleHit = null;
			lastBrickHit = null;
		}
		
		SidePaddles refPaddle = bg.paddles.get(0);
		
		// Top right angle
		double topRightdY = (refPaddle.getCoarseGrainedMinY() - bg.ball.getCoarseGrainedHeight()/2)  - refPaddle.getY();
		double topRightdX  = (refPaddle.getCoarseGrainedMaxX() + bg.ball.getCoarseGrainedWidth()/2) - refPaddle.getX();
		double topRightAngle = Math.atan2(topRightdY, topRightdX) * 180 / Math.PI;
		topRightAngle = -topRightAngle;
		
		// Top left angle
		double topLeftdY = topRightdY;
		double topLeftdX = (refPaddle.getCoarseGrainedMinX() - bg.ball.getCoarseGrainedWidth()/2) - refPaddle.getX();
		double topLeftAngle = Math.atan2(topLeftdY, topLeftdX) * 180 / Math.PI;
		topLeftAngle = -topLeftAngle;
		
		// Bottom left angle
		double bottomLeftdY = (refPaddle.getCoarseGrainedMaxY() + bg.ball.getCoarseGrainedHeight()/2)  - refPaddle.getY();
		double bottomLeftdX = topLeftdX;
		double bottomLeftAngle = Math.atan2(bottomLeftdY, bottomLeftdX) * 180 / Math.PI;
		bottomLeftAngle = 360 - bottomLeftAngle;
		
		// Bottom right angle
		double bottomRightdY = bottomLeftdY;
		double bottomRightdX = topRightdX;
		double bottomRightAngle = Math.atan2(bottomRightdY, bottomRightdX) * 180 / Math.PI;
		bottomRightAngle = 360 - bottomRightAngle;
		
		// Ball hits side paddles
		for (SidePaddles p : bg.paddles)
		{
			if(p.collides(bg.ball) != null)
			{
				bricksHitInARow = 0;
				// Reference: http://stackoverflow.com/questions/7586063/how-to-calculate-the-angle-between-a-line-and-the-horizontal-axis
				double deltaY = bg.ball.getY() - p.getY();
				double deltaX = bg.ball.getX() - p.getX();
				
				double ballAngle = Math.atan2(deltaY, deltaX) * 180 / Math.PI;
				
				if(ballAngle > 0)
					ballAngle = 360 - ballAngle;
				else if(ballAngle < 0)
					ballAngle = -ballAngle;
				
				if(lastSidePaddleHit != p)
				{
					if(ballAngle >= topRightAngle && ballAngle <= topLeftAngle) // Top of the paddle
					{
						bg.ball.bounce(0); 
					}
					else if(ballAngle <= bottomRightAngle && ballAngle >= bottomLeftAngle) // Bottom of the paddle
					{
						bg.ball.bounce(0);
					}
					else if(ballAngle <= bottomLeftAngle && ballAngle >= topLeftAngle) // Left of the paddle
					{
						bg.ball.bounce(90);
					}
					else  // Right of the paddle
					{
						bg.ball.bounce(90);
					}
					
					bounced = true;
					bounces++;
				}
				lastSidePaddleHit = p;
				lastBrickHit = null;
				paddleAlreadyHit = false;
			}
		}
		
		// Top right angle
		topRightdY = (refBrick.getCoarseGrainedMinY() - bg.ball.getCoarseGrainedHeight()/2)  - refBrick.getY();
		topRightdX  = (refBrick.getCoarseGrainedMaxX() + bg.ball.getCoarseGrainedWidth()/2) - refBrick.getX();
		topRightAngle = Math.atan2(topRightdY, topRightdX) * 180 / Math.PI;
		topRightAngle = -topRightAngle;
		
		// Top left angle
		topLeftdY = topRightdY;
		topLeftdX = (refBrick.getCoarseGrainedMinX() - bg.ball.getCoarseGrainedWidth()/2) - refBrick.getX();
		topLeftAngle = Math.atan2(topLeftdY, topLeftdX) * 180 / Math.PI;
		topLeftAngle = -topLeftAngle;
		
		// Bottom left angle
		bottomLeftdY = (refBrick.getCoarseGrainedMaxY() + bg.ball.getCoarseGrainedHeight()/2)  - refBrick.getY();
		bottomLeftdX = topLeftdX;
		bottomLeftAngle = Math.atan2(bottomLeftdY, bottomLeftdX) * 180 / Math.PI;
		bottomLeftAngle = 360 - bottomLeftAngle;
		
		// Bottom right angle
		bottomRightdY = bottomLeftdY;
		bottomRightdX = topRightdX;
		bottomRightAngle = Math.atan2(bottomRightdY, bottomRightdX) * 180 / Math.PI;
		bottomRightAngle = 360 - bottomRightAngle;
		
		// System.out.println("TR = " + topRightAngle + "\nTL = " + topLeftAngle + "\nBR = " + bottomRightAngle + "\nBL = " + bottomLeftAngle); // DEBUG
		
		// Ball hits the bricks
		for (Bricks b : bg.brick)
		{
			if(bg.ball.collides(b) != null)
			{		
				// Keep track of extra points for hitting multiple bricks
				bricksHitInARow++;
				if(keepScore == 0)
					keepScore = 1;
				else
					keepScore += bricksHitInARow;
				
				if(bricksHitInARow >= 1 && bricksHitInARow <= 3)
					keepScore += 10*bricksHitInARow;
				else if(bricksHitInARow >= 4 && bricksHitInARow <= 5)
					keepScore += 13*bricksHitInARow;
				else
					keepScore += 15*bricksHitInARow;
					
				// Reference: http://stackoverflow.com/questions/7586063/how-to-calculate-the-angle-between-a-line-and-the-horizontal-axis
				double deltaY = bg.ball.getY() - b.getY();
				double deltaX = bg.ball.getX() - b.getX();
				
				double ballAngle = Math.atan2(deltaY, deltaX) * 180 / Math.PI;
				
				if(ballAngle > 0)
					ballAngle = 360 - ballAngle;
				else if(ballAngle < 0)
					ballAngle = -ballAngle;
				
				//System.out.println("angle = " + angleInDegrees); // DEBUG
				
				if(lastBrickHit != b)
				{
					if(ballAngle >= topRightAngle && ballAngle <= topLeftAngle) // Top of the brick
					{
						bg.ball.bounce(0); 
					}
					else if(ballAngle <= bottomRightAngle && ballAngle >= bottomLeftAngle) // Bottom of the brick
					{
						bg.ball.bounce(0);
					}
					else if(ballAngle <= bottomLeftAngle && ballAngle >= topLeftAngle) // Left of the brick
					{
						bg.ball.bounce(90);
					}
					else  // Right of the brick
					{
						bg.ball.bounce(90);
					}
				}
				bounced = true;
				bounces++;
				lastBrickHit = b;
				lastSidePaddleHit = null;
				paddleAlreadyHit = false;
				
				if(b.getHP() > 0)
					b.decrementHP(1);
				
				break;	
			}
		}
		
			
		// bounce the ball
		if (bg.ball.getCoarseGrainedMaxX() > bg.ScreenWidth && bg.ball.getVelocity().getX() > 0) // Right horizontal check
		{
			//bg.ball.bounce(90);
			bounced = true;
			livesRemaining--;
			redrawBall = true;
			//bounces++;
			lastSidePaddleHit = null;
			lastBrickHit = null;
			paddleAlreadyHit = false;
		} 
		else if (bg.ball.getCoarseGrainedMinX() < 0 && bg.ball.getVelocity().getX() < 0) // Left horizontal check
		{
			//bg.ball.bounce(90);
			bounced = true;
			livesRemaining--;
			redrawBall = true;
			//bounces++;
			lastSidePaddleHit = null;
			lastBrickHit = null;
			paddleAlreadyHit = false;
		}
		else if (bg.ball.getCoarseGrainedMaxY() > bg.ScreenHeight && bg.ball.getVelocity().getY() > 0) // Bottom vertical check
		{
			bounced = true;
			livesRemaining--;
			redrawBall = true;
			lastSidePaddleHit = null;
			lastBrickHit = null;
			paddleAlreadyHit = false;
		}
		else if (bg.ball.getCoarseGrainedMinY() < 0 && bg.ball.getVelocity().getY() < 0) // Top vertical check
		{
			bg.ball.bounce(0);
			bounced = true;
			bounces++;
			lastSidePaddleHit = null;
			lastBrickHit = null;
			paddleAlreadyHit = false;
		}
		
		if (bounced) {
			bg.explosions.add(new Bang(bg.ball.getX(), bg.ball.getY()));
			//bounces++;
		}
		
		if (redrawBall)
		{
			bricksHitInARow = 0;
			bg.ball.setPosition(bg.ScreenWidth / 4, bg.ScreenHeight / 2);
			bg.ball.setVelocity(new Vector(.09f, .19f));
		}
		
		// Remove bricks when their HP is 0
				for (Iterator<Bricks> b = bg.brick.iterator(); b.hasNext();)
				{
					// System.out.println(b.hasNext()); // DEBUG
					if(b.next().getHP() == 0)
					{
						if(!bg.brick.isEmpty()){
							b.remove();
							bricksRemaining--;
						}
					}
				}
				//System.out.println(bg.brick.size());
				// All bricks are gone, game is won!
				if(bg.brick.isEmpty())
				{
					if(level == 1)
					{
						game.enterState(BounceGame.LEVEL2STATE);
						level = 2;
						bg.paddle.setPosition(bg.ScreenWidth/2, bg.ScreenHeight-16);
						bg.paddles.get(0).setPosition(16, bg.ScreenHeight-110);
						bg.paddles.get(1).setPosition(bg.ScreenWidth-16, bg.ScreenHeight-110);
						bg.ball.setPosition(bg.ScreenWidth / 4, bg.ScreenHeight / 2);
						bg.ball.setVelocity(new Vector(.09f, .19f));
						paddleAlreadyHit = false;
						bg.ball.removeBrokenBall();
					}
					else if(level == 2)
					{
						game.enterState(BounceGame.LEVEL3STATE);
						level = 3;
						bg.paddle.setPosition(bg.ScreenWidth/2, bg.ScreenHeight-16);
						bg.paddles.get(0).setPosition(16, bg.ScreenHeight-110);
						bg.paddles.get(1).setPosition(bg.ScreenWidth-16, bg.ScreenHeight-110);
						bg.ball.setPosition(bg.ScreenWidth / 4, bg.ScreenHeight / 2);
						bg.ball.setVelocity(new Vector(.09f, .19f));
						paddleAlreadyHit = false;
						bg.ball.removeBrokenBall();
					}
					else if(level == 3)
					{
						if(livesRemaining == 1)
							keepScore += 100;
						else if(livesRemaining == 2)
							keepScore += 200;
						else if(livesRemaining >= 3)
							keepScore += 300;
						
						((GameWonState)game.getState(BounceGame.GAMEWONSTATE)).setUserScore(keepScore);
						if(keepScore > ((GameWonState)game.getState(BounceGame.GAMEWONSTATE)).getUserHighScore())
						{
							try {
								((GameWonState)game.getState(BounceGame.GAMEWONSTATE)).saveHighScore(keepScore);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							((GameWonState)game.getState(BounceGame.GAMEWONSTATE)).setUserHighScore(keepScore);
						}
						game.enterState(BounceGame.GAMEWONSTATE);
						keepScore = 0;
						bricksHitInARow = 0;
						level = 1;
						resetLives = true;
						bg.paddles.get(0).setPosition(16, bg.ScreenHeight-110);
						bg.paddles.get(1).setPosition(bg.ScreenWidth-16, bg.ScreenHeight-110);
						bg.paddle.setPosition(bg.ScreenWidth/2, bg.ScreenHeight-16);
						paddleAlreadyHit = false;
						bg.ball.removeBrokenBall();
					}
				}
				
				// Keyboard shortcuts
				if(input.isKeyDown(Input.KEY_LCONTROL) || input.isKeyDown(Input.KEY_RCONTROL))
				{
					if(input.isKeyDown(Input.KEY_1))
					{
						level = 1;
						bg.paddle.setPosition(bg.ScreenWidth/2, bg.ScreenHeight-16);
						bg.paddles.get(0).setPosition(16, bg.ScreenHeight-110);
						bg.paddles.get(1).setPosition(bg.ScreenWidth-16, bg.ScreenHeight-110);
						bg.ball.setPosition(bg.ScreenWidth / 4, bg.ScreenHeight / 2);
						bg.ball.setVelocity(new Vector(.09f, .19f));
						paddleAlreadyHit = false;
						bg.ball.removeBrokenBall();
						game.enterState(BounceGame.LEVEL1STATE);
					}
					else if(input.isKeyDown(Input.KEY_2))
					{
						level = 2;
						bg.paddle.setPosition(bg.ScreenWidth/2, bg.ScreenHeight-16);
						bg.paddles.get(0).setPosition(16, bg.ScreenHeight-110);
						bg.paddles.get(1).setPosition(bg.ScreenWidth-16, bg.ScreenHeight-110);
						bg.ball.setPosition(bg.ScreenWidth / 4, bg.ScreenHeight / 2);
						bg.ball.setVelocity(new Vector(.09f, .19f));
						paddleAlreadyHit = false;
						bg.ball.removeBrokenBall();
						game.enterState(BounceGame.LEVEL2STATE);
					}
					else if(input.isKeyDown(Input.KEY_3))
					{
						level = 3;
						bg.paddle.setPosition(bg.ScreenWidth/2, bg.ScreenHeight-16);
						bg.paddles.get(0).setPosition(16, bg.ScreenHeight-110);
						bg.paddles.get(1).setPosition(bg.ScreenWidth-16, bg.ScreenHeight-110);
						bg.ball.setPosition(bg.ScreenWidth / 4, bg.ScreenHeight / 2);
						bg.ball.setVelocity(new Vector(.09f, .19f));
						paddleAlreadyHit = false;
						bg.ball.removeBrokenBall();
						game.enterState(BounceGame.LEVEL3STATE);
					}
					else if(input.isKeyDown(Input.KEY_4))
					{
						// Add bonus to final score depending on number of lives remaining
						if(livesRemaining == 1)
							keepScore += 100;
						else if(livesRemaining == 2)
							keepScore += 200;
						else if(livesRemaining >= 3)
							keepScore += 300;
						
						((GameWonState)game.getState(BounceGame.GAMEWONSTATE)).setUserScore(keepScore);
						if(keepScore > ((GameWonState)game.getState(BounceGame.GAMEWONSTATE)).getUserHighScore())
						{
							try {
								((GameWonState)game.getState(BounceGame.GAMEWONSTATE)).saveHighScore(keepScore);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							((GameWonState)game.getState(BounceGame.GAMEWONSTATE)).setUserHighScore(keepScore);
						}
						game.enterState(BounceGame.GAMEWONSTATE);
						keepScore = 0;
						bricksHitInARow = 0;
						level = 1;
						resetLives = true;
						bg.paddles.get(0).setPosition(16, bg.ScreenHeight-110);
						bg.paddles.get(1).setPosition(bg.ScreenWidth-16, bg.ScreenHeight-110);
						bg.paddle.setPosition(bg.ScreenWidth/2, bg.ScreenHeight-16);
						paddleAlreadyHit = false;
						bg.ball.removeBrokenBall();
					}
					else if(input.isKeyDown(Input.KEY_L))
					{
						livesRemaining += 1;
					}
				}
				
		bg.ball.update(delta);

		// check if there are any finished explosions, if so remove them
		for (Iterator<Bang> i = bg.explosions.iterator(); i.hasNext();) {
			if (!i.next().isActive()) {
				i.remove();
			}
		}

		if (livesRemaining == 0) {
			((GameOverState)game.getState(BounceGame.GAMEOVERSTATE)).setUserScore(keepScore);
			game.enterState(BounceGame.GAMEOVERSTATE);
			resetLives = true;
			level = 1; 
			bricksHitInARow = 0;
			keepScore = 0;
		}
	}

	@Override
	public int getID() {
		return BounceGame.PLAYINGSTATE;
	}
	
}