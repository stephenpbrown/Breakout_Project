package bounce;

import jig.Entity;
import jig.ResourceManager;
import jig.Vector;

public class SidePaddles extends Entity
{
	private Vector velocity;
	
	public SidePaddles(final float x, final float y)
	{
		super(x, y);
		addImageWithBoundingBox(ResourceManager
				.getImage(BounceGame.SIDE_PADDLE_RSC)); // LOAD IMAGE BEFORE YOU GET TO CONSTRUCTOR
		velocity = new Vector(0,0);
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
