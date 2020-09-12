package bounce;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Scanner;

import javax.naming.Context;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.EmptyTransition;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.state.transition.HorizontalSplitTransition;

import jig.ResourceManager;

public class GameWonState extends BasicGameState
{
	private int timer;
	private int lastKnownScore;
	private int lastKnownHighScore;
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		timer = 4000;
	}

	public void setUserScore(int score) {
		lastKnownScore = score;
	}
	
	public int getUserScore()
	{
		return lastKnownScore;
	}
	
	public void setUserHighScore(int highScore)
	{
		lastKnownHighScore = highScore;
	}
	
	public int getUserHighScore()
	{
		return lastKnownHighScore;
	}
	
	public void saveHighScore(int highScore) throws IOException
	{
		//System.out.println("Saving highScore: " + highScore);
		Writer wr = new FileWriter("highScore.txt");
		wr.write(String.valueOf(highScore));
		wr.close();
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {

		BounceGame bg = (BounceGame)game;
		
		g.drawString("Score: " + lastKnownScore, 10, 30);
		g.drawString("Highscore: " + ((StartUpState)game.getState(BounceGame.STARTUPSTATE)).getUserHighScore(), 620, 10);
		//g.drawString("Lives Remaining: 0", 10, 50);
		
		for (Bang b : bg.explosions)
			b.render(g);
		g.drawImage(ResourceManager.getImage(BounceGame.GAMEWON_RSC), 185,
				210);

	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {
		
		BounceGame bg= (BounceGame)game;
		
		timer -= delta;
		if (timer <= 0)
		{
			game.enterState(BounceGame.STARTUPSTATE, new FadeOutTransition(), new FadeInTransition() );
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
		return BounceGame.GAMEWONSTATE;
	}
}
