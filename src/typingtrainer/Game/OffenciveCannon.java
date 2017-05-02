package typingtrainer.Game;

import com.sun.javafx.geom.Vec2d;
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
}
