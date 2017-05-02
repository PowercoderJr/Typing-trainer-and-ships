package typingtrainer.Game;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;

/**
 * Created by Meow on 23.04.2017.
 */
public abstract class PvpObject
{
	public enum Belonging {FRIENDLY, HOSTILE}
	protected Belonging belonging;
	protected Point2D position;
	protected boolean isHorFlipable;
	protected Image image;

	public PvpObject()
	{
		this.belonging = Belonging.FRIENDLY;
		this.position = new Point2D(0, 0);
		this.isHorFlipable = true;
	}

	public PvpObject(Belonging belonging, Point2D position)
	{
		this();
		this.belonging = belonging;
		this.position = position;
	}

	public PvpObject(Belonging belonging, Point2D position, boolean isHorFlipable)
	{
		this(belonging, position);
		this.isHorFlipable = isHorFlipable;
	}

	public Belonging getBelonging()
	{
		return belonging;
	}

	public void setBelonging(Belonging belonging)
	{
		this.belonging = belonging;
	}

	public Point2D getPosition()
	{
		return position;
	}

	public void setPosition(Point2D position)
	{
		this.position = position;
	}

	public Image getImage()
	{
		return image;
	}

	public boolean isHorFlipable()
	{
		return isHorFlipable;
	}

	public void setHorFlipable(boolean horFlipable)
	{
		isHorFlipable = horFlipable;
	}
}
