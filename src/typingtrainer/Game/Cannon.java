package typingtrainer.Game;

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
	public Cannon(Ship parentShip, Belonging belonging, Point2D position)
	{
		super(belonging, position);
		this.parentShip = parentShip;
		image = new WritableImage(Game.SPRITE_SHEET.getPixelReader(), 132, 0, 150, 55);
	}

	public void shoot(Point2D target)
	{
		/* Через вектора, угол получается только положительный
		double vectorX = target.getX() - (position.getX() + pivot.getX()), vectorY = target.getY() - (position.getY() + pivot.getY());
		rotationAngle = Math.toDegrees(Math.acos(vectorX / Math.sqrt(vectorX * vectorX + vectorY * vectorY)));*/
		rotationAngle = Math.toDegrees(Math.atan((target.getY() - position.getY() - pivot.getY()) / (target.getX() - position.getX() - pivot.getX())));
		//Cannonball cannonball = new Cannonball(this, belonging, new Point2D(position.getX() + 145 - 8, position.getY() + 27 - 8));
		Cannonball cannonball = new Cannonball(this, belonging, new Point2D(position.getX() + pivot.getX() - 8, position.getY() + pivot.getY()  - 8));
		cannonball.setTarget(target);
		getParentShip().getParentGame().getCannonballs().add(cannonball);
	}

	public Ship getParentShip()
	{
		return parentShip;
	}
}
