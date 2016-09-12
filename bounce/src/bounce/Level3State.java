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

public class Level3State extends BasicGameState
{
	private int timer;
	private int lastKnownBounces; // the user's score, to be displayed, but not updated.
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		timer = 1000;
	}

	public void setUserScore(int bounces) {
		lastKnownBounces = bounces;
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {

		BounceGame bg = (BounceGame)game;
		
		g.drawImage(ResourceManager.getImage(BounceGame.LEVEL_3_RSC), 185, 210);
	}
	
	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {
		
		
		timer -= delta;
		if (timer <= 0)
		{
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
		return BounceGame.LEVEL3STATE;
	}
}
