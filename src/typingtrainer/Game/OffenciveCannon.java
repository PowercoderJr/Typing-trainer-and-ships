package typingtrainer.Game;

import javafx.geometry.Point2D;
import javafx.scene.control.Label;

/**
 * Created by Meow on 22.04.2017.
 */
public class OffenciveCannon extends Cannon
{
	private PvpWord word;
	private Label labelBefore;
	private Label labelAfter;

	public OffenciveCannon(Ship parentShip, Belonging belonging, Point2D position)
	{
		super(parentShip, belonging, position);
	}

	public void shoot(Point2D target)
	{
		Cannonball cannonball = new Cannonball(this, belonging, new Point2D(position.getX() + 145 - 8, position.getY() + 27 - 8));
		cannonball.setTarget(new Point2D(1280, position.getY()));
		getParentShip().getParentGame().getCannonballs().add(cannonball);
	}
}
