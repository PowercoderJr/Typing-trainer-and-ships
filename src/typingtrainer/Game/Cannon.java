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
	public static final double CANNON_KNOCKBACK_STEP_DISTANCE = 20.0;
	public static final int CANNON_KNOCKBACK_STEP_DURATION = 50;

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
		Animation smokeCloud = new Animation(belonging, position.add(image.getWidth() - 8, pivot.getY() - 30), Game.SPRITE_SHEET, 7, 2, 132, 71, 120, 60);
		smokeCloud.setPivot(new Point2D(-pivotToMuzzleLength, smokeCloud.getHeight() / 2));
		smokeCloud.setRotationAngle(rotationAngle);
		getParentShip().getParentGame().getSmokeClouds().add(smokeCloud);
		smokeCloud.play(500);

		//Cannonball cannonball = new Cannonball(this, belonging, new Point2D(position.getX() + pivot.getX() - 8, position.getY() + pivot.getY() - 8));
		Cannonball cannonball = new Cannonball(this, belonging, position.add(pivot).subtract(8, 8));
		cannonball.setTarget(target);
		getParentShip().getParentGame().getCannonballs().add(cannonball);

		if (pushingThread != null && pushingThread.isAlive())
			pushingThread.interrupt();
		pushingThread = new Thread(() ->
		{
			try
			{
				//Определяется направление отдачи
				double cathetA = pivot.getX();
				double cathetB = cathetA * Math.tan(Math.toRadians(rotationAngle));
				Point2D pointA = new Point2D(position.getX(), position.getY() + pivot.getY() - cathetB);
				Point2D dir = pivot.add(position).subtract(pointA).normalize();
				for (int i = 0; i < 3; ++i)
				{
					position = position.subtract(dir.getX() * CANNON_KNOCKBACK_STEP_DISTANCE, dir.getY() * CANNON_KNOCKBACK_STEP_DISTANCE);
					Thread.sleep(CANNON_KNOCKBACK_STEP_DURATION);
				}

				double step = position.distance(basePosition) / 12;
				dir = basePosition.subtract(position).normalize();
				for (int i = 0; i < 12; ++i)
				{
					position = position.add(dir.getX() * step, dir.getY() * step);
					Thread.sleep(CANNON_KNOCKBACK_STEP_DURATION);
				}
			}
			catch (InterruptedException e)
			{
				//System.out.println(e.getMessage());
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
