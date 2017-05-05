package typingtrainer.Game;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

/**
 * Created by Meow on 22.04.2017.
 */
public class Cannonball extends PvpObject
{
	public enum Type {OFFENCIVE, DEFENCIVE};
	private Type type;
	private Cannon parentCannon;
	private PvpWord word;
	private Point2D target;
	private double speed;

	public Cannonball(Cannon parentCannon, Belonging belonging, Point2D position)
	{
		super(belonging, position, false);
		this.parentCannon = parentCannon;
		image = new WritableImage(Game.SPRITE_SHEET.getPixelReader(),132, 55, 16, 16);
		speed = 300;
		word = new PvpWord("");
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
		this.target = target;
	}

	public double getSpeed()
	{
		return speed;
	}

	public void setSpeed(double speed)
	{
		this.speed = speed;
	}

	public PvpWord getWord()
	{
		return word;
	}

	public Type getType()
	{
		return type;
	}
}
