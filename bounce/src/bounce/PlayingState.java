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
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		bounces = 0;
		container.setSoundOn(true);
	}
	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
		BounceGame bg = (BounceGame)game;
		
		bg.ball.render(g); // Draw the ball
		bg.paddle.render(g); // Draw the paddle
		
		g.drawString("Bounces: " + bounces, 10, 30);
		for (Bang b : bg.explosions)
			b.render(g);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {

		Input input = container.getInput();
		BounceGame bg = (BounceGame)game;
		
//		if (input.isKeyDown(Input.KEY_W)) {
//			bg.paddle.setVelocity(bg.paddle.getVelocity().add(new Vector(0f, -.001f)));
//		}
//		if (input.isKeyDown(Input.KEY_S)) {
//			bg.paddle.setVelocity(bg.paddle.getVelocity().add(new Vector(0f, +.001f)));
//		}
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
		
		// bounce the ball
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
		if (bg.ball.getCoarseGrainedMaxY() > bg.paddle.getCoarseGrainedMinY()
			&& bg.ball.getCoarseGrainedMinX() < bg.paddle.getCoarseGrainedMaxX()
			&& bg.ball.getCoarseGrainedMaxX() > bg.paddle.getCoarseGrainedMinX())
		{
			bg.ball.bounce(0);
			bounced = true;
		}
		if (bounced) {
			bg.explosions.add(new Bang(bg.ball.getX(), bg.ball.getY()));
			bounces++;
		}
		
		bg.ball.update(delta);

		// check if there are any finished explosions, if so remove them
		for (Iterator<Bang> i = bg.explosions.iterator(); i.hasNext();) {
			if (!i.next().isActive()) {
				i.remove();
			}
		}

		if (bounces >= 10) {
			((GameOverState)game.getState(BounceGame.GAMEOVERSTATE)).setUserScore(bounces);
			game.enterState(BounceGame.GAMEOVERSTATE);
		}
	}

	@Override
	public int getID() {
		return BounceGame.PLAYINGSTATE;
	}
	
}