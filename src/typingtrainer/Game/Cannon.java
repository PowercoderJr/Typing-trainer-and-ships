package typingtrainer.Game;

import com.sun.javafx.geom.Vec2d;
import com.sun.javafx.geom.Vec2f;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import typingtrainer.Main;

/**
 * Created by Meow on 22.04.2017.
 */
public abstract class Cannon extends PvpObject
{
	private Ship parentShip;
	private Point2D basePosition;
	private Thread pushingThread;
	public Cannon(Ship parentShip, Belonging belonging, Point2D position)
	{
		super(belonging, position);
		basePosition = new Point2D(position.getX(), position.getY());
		this.parentShip = parentShip;
		image = new WritableImage(Game.SPRITE_SHEET.getPixelReader(), 132, 0, 150, 55);
	}

	public void shoot(Point2D target)
	{
		rotationAngle = Math.toDegrees(Math.atan((target.getY() - position.getY() - pivot.getY()) / (target.getX() - position.getX() - pivot.getX())));

		double pivotToMuzzleLength = image.getWidth() - pivot.getX();
		Animation smokeCloud = new Animation(belonging, new Point2D(position.getX() + image.getWidth() - 8, position.getY() + pivot.getY() - 30), Game.SPRITE_SHEET, 7, 2, 132, 71, 120, 60);
		smokeCloud.setPivot(new Point2D(-pivotToMuzzleLength, smokeCloud.getHeight() / 2));
		smokeCloud.setRotationAngle(rotationAngle);
		getParentShip().getParentGame().getSmokeClouds().add(smokeCloud);

		smokeCloud.play(1000);
		Cannonball cannonball = new Cannonball(this, belonging, new Point2D(position.getX() + pivot.getX() - 8, position.getY() + pivot.getY()  - 8));
		cannonball.setTarget(target);
		getParentShip().getParentGame().getCannonballs().add(cannonball);

		if (pushingThread != null && pushingThread.isAlive())
			pushingThread.interrupt();
		pushingThread = new Thread(() ->
		{
			try
			{
				for (int i = 0; i < 3; ++i)
				{
					//Point2D backPos = new Point2D(position.getX())
					position = position.subtract(20, 0);
					Thread.sleep(50);

				}
				double step = position.distance(basePosition) / 12;
				Point2D dir = basePosition.subtract(position).normalize();
				for (int i = 0; i < 12; ++i)
				{
					//Point2D backPos = new Point2D(position.getX())
					position = position.add(dir.getX() * step, dir.getY() * step);
					Thread.sleep(50);
					
				}
			}
			catch (InterruptedException e)
			{
				System.out.println(e.getMessage());
			}
		});
		pushingThread.start();
	}

	public Point2D getBasePosition()
	{
		return basePosition;
	}

	public Ship getParentShip()
	{
		return parentShip;
	}
}
