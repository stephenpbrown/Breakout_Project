package bounce;

import java.util.Iterator;

import jig.Vector;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;


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
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		bounces = 0;
		livesRemaining = 3;
		container.setSoundOn(true);
		
		BounceGame bg = (BounceGame)game;
		
		int numBricks = 8;
		
		// Add bricks
		for (int i = 1; i < numBricks; i++)
		{
			bg.brick.add(new Bricks(i*bg.ScreenWidth / numBricks, bg.ScreenHeight / 4));
			bricksRemaining = i;
		}
	}
	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
		BounceGame bg = (BounceGame)game;
		
		bg.ball.render(g); // Draw the ball
		bg.paddle.render(g); // Draw the paddle
		
		// Draw the bricks
		for (Bricks b : bg.brick)
		{
			b.render(g);
		}
		
		g.drawString("Bounces: " + bounces, 10, 30);
		g.drawString("Lives Remaining: " + livesRemaining, 10, 50);
		
		for (Bang b : bg.explosions)
			b.render(g);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {

		Input input = container.getInput();
		BounceGame bg = (BounceGame)game;
	
		// Move the paddle left
		if (input.isKeyDown(Input.KEY_LEFT) && !(bg.paddle.getCoarseGrainedMinX() < 0)) 
		{
			bg.paddle.setVelocity(new Vector(-0.5f,0));
		}
		// Move the paddle right
		else if (input.isKeyDown(Input.KEY_RIGHT) && !(bg.paddle.getCoarseGrainedMaxX() > bg.ScreenWidth)) 
		{
			bg.paddle.setVelocity(new Vector(0.5f, 0));
		}
		else
		{
			bg.paddle.setVelocity(new Vector(0,0));
		}	
		bg.paddle.update(delta);
		
		boolean bounced = false;
		boolean redrawBall = false;
		// Bounce off the paddle
		// Ball hits side of the paddle
		if (bg.ball.collides(bg.paddle) != null && bg.ball.getCoarseGrainedMaxY() > bg.paddle.getCoarseGrainedMinY() + 5)
		{
			bg.ball.bounce(90);
			bg.ball.setVelocity(new Vector(1, 1));
			bounced = true;
			bounces++;
		}
		// Ball hits top of the paddle
		else if (bg.ball.collides(bg.paddle) != null)
		{
			bg.ball.bounce(0);
			bounced = true;
			bounces++;
		}
		
		Bricks refBrick = null;
		if(bg.brick.get(0) != null)
			refBrick = bg.brick.get(0);
		
		// Top right angle
		double topRightdY = (refBrick.getCoarseGrainedMinY() - bg.ball.getCoarseGrainedHeight()/2)  - refBrick.getY();
		double topRightdX  = (refBrick.getCoarseGrainedMaxX() + bg.ball.getCoarseGrainedWidth()/2) - refBrick.getX();
		double topRightAngle = Math.atan2(topRightdY, topRightdX) * 180 / Math.PI;
		topRightAngle = -topRightAngle;
		
		// Top left angle
		double topLeftdY = topRightdY;
		double topLeftdX = (refBrick.getCoarseGrainedMinX() - bg.ball.getCoarseGrainedWidth()/2) - refBrick.getX();
		double topLeftAngle = Math.atan2(topLeftdY, topLeftdX) * 180 / Math.PI;
		topLeftAngle = -topLeftAngle;
		
		// Bottom left angle
		double bottomLeftdY = (refBrick.getCoarseGrainedMaxY() + bg.ball.getCoarseGrainedHeight()/2)  - refBrick.getY();
		double bottomLeftdX = topLeftdX;
		double bottomLeftAngle = Math.atan2(bottomLeftdY, bottomLeftdX) * 180 / Math.PI;
		bottomLeftAngle = 360 - bottomLeftAngle;
		
		// Bottom right angle
		double bottomRightdY = bottomLeftdY;
		double bottomRightdX = topRightdX;
		double bottomRightAngle = Math.atan2(bottomRightdY, bottomRightdX) * 180 / Math.PI;
		bottomRightAngle = 360 - bottomRightAngle;
		
		// System.out.println("TR = " + topRightAngle + "\nTL = " + topLeftAngle + "\nBR = " + bottomRightAngle + "\nBL = " + bottomLeftAngle); // DEBUG
		
		// Ball hits the bricks
		for (Bricks b : bg.brick)
		{
			if(bg.ball.collides(b) != null)
			{		
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
				b.decrementHP(1);
				
				
				break;	
			}
		}
		
		// Remove bricks when their HP is 0
		for (Iterator<Bricks> b = bg.brick.iterator(); b.hasNext();)
		{
			if(b.next().getHP() == 0)
			{
				b.remove();
				bricksRemaining--;
			}
		}
		
		// All bricks are gone, game is won!
		if(bricksRemaining == 0)
		{
			((GameOverState)game.getState(BounceGame.GAMEOVERSTATE)).setUserScore(bounces);
			game.enterState(BounceGame.GAMEOVERSTATE);
			bg.paddle.setPosition(bg.ScreenWidth/2, bg.ScreenHeight-16);
		}
		
		// System.out.println(bricksRemaining); // DEBUG
			
		// bounce the ball
		if (bg.ball.getCoarseGrainedMaxX() > bg.ScreenWidth && bg.ball.getVelocity().getX() > 0) // Right horizontal check
		{
			bg.ball.bounce(90);
			bounced = true;
			bounces++;
		} 
		else if (bg.ball.getCoarseGrainedMinX() < 0 && bg.ball.getVelocity().getX() < 0) // Left horizontal check
		{
			bg.ball.bounce(90);
			bounced = true;
			bounces++;
		}
		else if (bg.ball.getCoarseGrainedMaxY() > bg.ScreenHeight && bg.ball.getVelocity().getY() > 0) // Bottom vertical check
		{
			bounced = true;
			livesRemaining--;
			redrawBall = true;
		}
		else if (bg.ball.getCoarseGrainedMinY() < 0 && bg.ball.getVelocity().getY() < 0) // Top vertical check
		{
			bg.ball.bounce(0);
			bounced = true;
			bounces++;
		}
		
		if (bounced) {
			bg.explosions.add(new Bang(bg.ball.getX(), bg.ball.getY()));
			//bounces++;
		}
		
		if (redrawBall)
		{
			bg.ball.setPosition(bg.ScreenWidth / 4, bg.ScreenHeight / 2);
			bg.ball.setVelocity(new Vector(.1f, .2f));
		}
		
		bg.ball.update(delta);

		// check if there are any finished explosions, if so remove them
		for (Iterator<Bang> i = bg.explosions.iterator(); i.hasNext();) {
			if (!i.next().isActive()) {
				i.remove();
			}
		}

		if (livesRemaining == 0) {
			((GameOverState)game.getState(BounceGame.GAMEOVERSTATE)).setUserScore(bounces);
			game.enterState(BounceGame.GAMEOVERSTATE);
		}
	}

	@Override
	public int getID() {
		return BounceGame.PLAYINGSTATE;
	}
	
}