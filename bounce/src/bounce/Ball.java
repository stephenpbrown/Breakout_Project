package bounce;

import jig.Entity;
import jig.ResourceManager;
import jig.Vector;

/**
 * The Ball class is an Entity that has a velocity (since it's moving). When
 * the Ball bounces off a surface, it temporarily displays a image with
 * cracks for a nice visual effect.
 * 
 */
 class Ball extends Entity {

	private Vector velocity;
	private int countdown;

	public Ball(final float x, final float y, final float vx, final float vy) {
		super(x, y);
		addImageWithBoundingBox(ResourceManager
				.getImage(BounceGame.BALL_EARTH_RSC)); // LOAD IMAGE BEFORE YOU GET TO CONSTRUCTOR
		velocity = new Vector(vx, vy);
		countdown = 0;
	}
	
	public void setVelocity(final Vector v) {
		velocity = v;
	}
	
	public Vector getVelocity() {
		return velocity;
	}

	/**
	 * Bounce the ball off a surface. This simple implementation, combined
	 * with the test used when calling this method can cause "issues" in
	 * some situations. Can you see where/when? If so, it should be easy to
	 * fix!
	 * 
	 * @param surfaceTangent
	 */
	public void bounce(float surfaceTangent) {
		//removeImage(ResourceManager.getImage(BounceGame.BALL_EARTH_RSC));
//		addImageWithBoundingBox(ResourceManager
//				.getImage(BounceGame.BALL_EARTH_DESTROYED_RSC)); 
		countdown = 500; // Milliseconds
		velocity = velocity.bounce(surfaceTangent);
	}

	public void removeBrokenBall()
	{
		removeImage(ResourceManager.getImage(BounceGame.BALL_EARTH_DESTROYED_RSC));
		addImageWithBoundingBox(ResourceManager
				.getImage(BounceGame.BALL_EARTH_RSC)); 
	}
	
	/**
	 * Update the Ball based on how much time has passed...
	 * 
	 * @param delta
	 *            the number of milliseconds since the last update
	 */
	public void update(final int delta) {
		translate(velocity.scale(delta)); // SCALING HERE IS VERY IMPORTANT
		if (countdown > 0) {
			countdown -= delta;
			if (countdown <= 0) {
				addImageWithBoundingBox(ResourceManager
						.getImage(BounceGame.BALL_EARTH_RSC));
				removeImage(ResourceManager
						.getImage(BounceGame.BALL_EARTH_DESTROYED_RSC));
			}
		}
	}
}
