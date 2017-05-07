package typingtrainer.Game;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

/**
 * Created by Meow on 22.04.2017.
 */
public class Cannonball extends PvpObject implements IHavingWord
{
	public enum Type {OFFENCIVE, DEFENCIVE};
	private Type type;
	private Cannon parentCannon;
	private PvpWord word;
	private Point2D target;
	private double speed;
	private boolean isCountershooted;

	public Cannonball(Cannon parentCannon, Belonging belonging, Point2D position)
	{
		super(belonging, position, false);
		this.parentCannon = parentCannon;
		image = new WritableImage(Game.SPRITE_SHEET.getPixelReader(),132, 55, 16, 16);
		speed = 30;
		word = new PvpWord("");
		isCountershooted = false;
		if (parentCannon.getClass() == OffenciveCannon.class)
			type = Type.OFFENCIVE;
		else if (parentCannon.getClass() == DefenciveCannon.class)
			type = Type.DEFENCIVE;
	}

	public Point2D getTarget()
	{
		return target;
	}

	public void setTarget(Point2D target)
	{
		this.target = target/*.subtract(image.getWidth() / 2, image.getHeight() / 2)*/;
	}

	public double getSpeed()
	{
		return speed;
	}

	public void setSpeed(double speed)
	{
		this.speed = speed;
	}

	@Override
	public PvpWord getPvpWord()
	{
		return word;
	}

	public Type getType()
	{
		return type;
	}

	public Point2D getDirection()
	{
		return target.subtract(position).normalize();
	}

	public Point2D getPositionAfterDistance(double distance)
	{
		return position.add(getDirection().getX() * distance, getDirection().getY() * distance);
	}

	public boolean isCountershooted()
	{
		return isCountershooted;
	}

	public void setCountershooted(boolean countershooted)
	{
		isCountershooted = countershooted;
	}
}
