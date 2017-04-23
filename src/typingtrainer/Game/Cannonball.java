package typingtrainer.Game;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Created by Meow on 22.04.2017.
 */
public class Cannonball extends PvpObject
{
	private PvpWord word;
	private Point2D target;

	public Cannonball(Belonging belonging, Point2D position)
	{
		super(belonging, position);
	}
}
