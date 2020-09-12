package bounce;

import java.util.Iterator;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import jig.ResourceManager;
import jig.Vector;

public class Level2State extends BasicGameState
{
	private int timer;
	private int lastKnownScore; // the user's score, to be displayed, but not updated.
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		timer = 1000;
	}

	public void setUserScore(int score) {
		lastKnownScore = score;
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {

		BounceGame bg = (BounceGame)game;
		
//		g.drawString("Score: " + lastKnownScore, 10, 30);
//		g.drawString("Highscore: " + ((GameWonState)game.getState(BounceGame.GAMEWONSTATE)).getUserHighScore(), 620, 10);
		
		g.drawImage(ResourceManager.getImage(BounceGame.LEVEL_2_RSC), 185, 210);
	}
	
	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {
		
		BounceGame bg = (BounceGame)game;
		timer -= delta;
		if (timer <= 0)
		{
			bg.paddle.setPosition(bg.ScreenWidth/2, bg.ScreenHeight-16);
			bg.paddles.get(0).setPosition(16, bg.ScreenHeight-110);
			bg.paddles.get(1).setPosition(bg.ScreenWidth-16, bg.ScreenHeight-110);
			bg.ball.setPosition(bg.ScreenWidth / 4, bg.ScreenHeight / 2);
			bg.ball.setVelocity(new Vector(.09f, .19f));
			bg.ball.removeBrokenBall();
			game.enterState(BounceGame.GOSTATE, new FadeOutTransition(), new FadeInTransition());
		}
			
		// check if there are any finished explosions, if so remove them
		for (Iterator<Bang> i = ((BounceGame)game).explosions.iterator(); i.hasNext();) {
			if (!i.next().isActive()) {
				i.remove();
			}
		}

	}

	@Override
	public int getID() {
		return BounceGame.LEVEL2STATE;
	}
}
