package bounce;

import jig.Entity;
import jig.ResourceManager;
import jig.Vector;

public class Bricks extends Entity
{
	private Vector velocity;
	private int hp_left = 1;
	
	public Bricks(final float x, final float y)
	{
		super(x, y);
		addImageWithBoundingBox(ResourceManager
				.getImage(BounceGame.PADDLE_LEVEL_3_RSC)); // LOAD IMAGE BEFORE YOU GET TO CONSTRUCTOR
		velocity = new Vector(0,0);
	}
	
	public void decrementHP(final int hp)
	{
		hp_left -= hp;
	}
	
	public int getHP()
	{
		return hp_left;
	}
	
	public void setVelocity(final Vector v) {
		velocity = v;
	}

	public Vector getVelocity() {
		return velocity;
	}
	
	public void update(final int delta) {
		translate(velocity.scale(delta)); // SCALING HERE IS VERY IMPORTANT
	}
}
