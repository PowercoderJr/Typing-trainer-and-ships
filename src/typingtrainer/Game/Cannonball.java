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
}
