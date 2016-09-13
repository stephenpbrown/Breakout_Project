package bounce;

import jig.Entity;
import jig.ResourceManager;
import jig.Vector;

public class Bricks extends Entity
{
	private Vector velocity;
	private int hp_left = 1;
	private int level = 1;
	
	public Bricks(final float x, final float y, final int l)
	{
		super(x, y);
		level = l;
		hp_left = l;
//		System.out.println("Level = " + level + ", " + "hp_left = " + hp_left); // DEBUG
		if(level == 1)
		{
			addImageWithBoundingBox(ResourceManager.getImage(BounceGame.BRICK_RSC));
		}
		else if(level == 2)
		{
			addImageWithBoundingBox(ResourceManager.getImage(BounceGame.BRICK_LEVEL_2_RSC));
		}
		else if(level == 3)
			addImageWithBoundingBox(ResourceManager.getImage(BounceGame.BRICK_LEVEL_3_RSC));
		
		velocity = new Vector(0,0);
	}
	
	public void decrementHP(final int hp)
	{
		//System.out.println("hp_left = " + hp_left + ", " + "hp = " + hp); // DEBUG
		if(hp_left > 0)
			hp_left -= hp;
		
		if(hp_left == 1)
		{
			removeImage(ResourceManager.getImage(BounceGame.BRICK_LEVEL_2_RSC));
			addImageWithBoundingBox(ResourceManager.getImage(BounceGame.BRICK_RSC));
		}
		else if(hp_left == 2)
		{
			removeImage(ResourceManager.getImage(BounceGame.BRICK_LEVEL_3_RSC));
			addImageWithBoundingBox(ResourceManager.getImage(BounceGame.BRICK_LEVEL_2_RSC));
		}
	}
	
	public int getHP()
	{
		//System.out.println(hp_left);
		return hp_left;
	}
	
	public void setLevel(final int lev)
	{
		level = lev;
	}
	
	public int getLevel()
	{
		return level;
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
