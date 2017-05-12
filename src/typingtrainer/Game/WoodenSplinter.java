package typingtrainer.Game;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;

/**
 * Created by Meow on 12.05.2017.
 */
public class WoodenSplinter extends PvpObject
{
	public static final double MAX_ROTATION_SPEED = 180.0;
	public static final double MAX_FLY_SPEED = Cannonball.BASE_SPEED / 3 * 2;
	public static final double MIN_FLY_TIME = 100;
	public static final double MAX_FLY_TIME = 2000;
	public static final double FINAL_SCALE = 0.8;

	private double rotationSpeed;
	private double flySpeed;
	private double flyTime;
	private Point2D flyDir;

	public WoodenSplinter(Belonging belonging, Point2D position, Image image)
	{
		super(belonging, position);
		rotationSpeed = -MAX_ROTATION_SPEED + Math.random() * 2 * MAX_ROTATION_SPEED;
		flySpeed = Math.random() * MAX_FLY_SPEED;
		flyTime = MIN_FLY_TIME + Math.random() * (MAX_FLY_TIME - MIN_FLY_TIME);
		flyDir = new Point2D(0.2 + Math.random(), -0.5 + Math.random()).normalize();
		this.image = image;
		pivot = new Point2D(Math.random() * image.getWidth(), Math.random() * image.getHeight());
	}

	public double getRotationSpeed()
	{
		return rotationSpeed;
	}

	public void setRotationSpeed(double rotationSpeed)
	{
		this.rotationSpeed = rotationSpeed;
	}

	public double getFlySpeed()
	{
		return flySpeed;
	}

	public void setFlySpeed(double flySpeed)
	{
		this.flySpeed = flySpeed;
	}

	public double getFlyTime()
	{
		return flyTime;
	}

	public void setFlyTime(double flyTime)
	{
		this.flyTime = flyTime;
	}

	public Point2D getFlyDir()
	{
		return flyDir;
	}

	public void setFlyDir(Point2D flyDir)
	{
		this.flyDir = flyDir;
	}
}
