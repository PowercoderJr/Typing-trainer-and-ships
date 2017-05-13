package typingtrainer.Game;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;

/**
 * Created by Meow on 23.04.2017.
 */
public abstract class PvpObject
{
	public enum Belonging {FRIENDLY, HOSTILE}
	protected Belonging belonging;
	protected Shape shape;
	protected Point2D position;
	protected Point2D pivot;
	protected double rotationAngle;
	protected boolean isHorFlipable;
	protected Image image;
	protected double scale;

	public PvpObject()
	{
		this.belonging = Belonging.FRIENDLY;
		this.position = new Point2D(0, 0);
		this.pivot = new Point2D(0, 0);
		this.rotationAngle = 0;
		this.isHorFlipable = true;
		this.scale = 1.0;
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

	//http://stackoverflow.com/questions/18260421/how-to-draw-image-rotated-on-javafx-canvas
	public void render(GraphicsContext gc, double sceneWidth, double xCanvasScale, double yCanvasScale)
	{
		double finalX, finalY, finalWidth, finalHeight, finalAngle, finalPivotX, finalPivotY;
		if (belonging == PvpObject.Belonging.FRIENDLY)
		{
			finalX = position.getX() * xCanvasScale;
			finalWidth = image.getWidth() * xCanvasScale * scale;
			finalAngle = rotationAngle;
			finalPivotX = finalX + pivot.getX() * xCanvasScale;
		}
		else
		{
			if (isHorFlipable)
			{
				finalX = sceneWidth - position.getX() * xCanvasScale;
				finalWidth = -image.getWidth() * xCanvasScale * scale;
				finalAngle = -rotationAngle;
				finalPivotX = finalX - pivot.getX() * xCanvasScale;
			}
			else
			{
				finalX = sceneWidth - (position.getX() + image.getWidth() * scale) * xCanvasScale;
				finalWidth = image.getWidth() * xCanvasScale * scale;
				finalAngle = rotationAngle;
				finalPivotX = finalX + pivot.getX() * xCanvasScale;
			}
		}
		finalY = position.getY() * yCanvasScale;
		finalHeight = image.getHeight() * yCanvasScale * scale;
		finalPivotY = finalY + pivot.getY() * yCanvasScale;

		gc.save(); // saves the current state on stack, including the current transform
		rotateGraphicsContext(gc, finalAngle, finalPivotX, finalPivotY);
		gc.drawImage(image, finalX, finalY, finalWidth, finalHeight);
		gc.restore(); // back to original state (before rotation)
	}

	//http://stackoverflow.com/questions/18260421/how-to-draw-image-rotated-on-javafx-canvas
	private void rotateGraphicsContext(GraphicsContext gc, double angle, double x, double y)
	{
		Rotate r = new Rotate(angle, x, y);
		gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
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

	public Point2D getPivot()
	{
		return pivot;
	}

	public void setPivot(Point2D pivot)
	{
		this.pivot = pivot;
	}

	public double getRotationAngle()
	{
		return rotationAngle;
	}

	public void setRotationAngle(double rotationAngle)
	{
		this.rotationAngle = rotationAngle;
	}

	public Shape getShape()
	{
		return shape;
	}

	public void setShape(Shape shape)
	{
		this.shape = shape;
	}

	public double getScale()
	{
		return scale;
	}

	public void setScale(double scale)
	{
		double oldScale = this.scale;
		this.scale = scale;
		pivot = new Point2D(pivot.getX() / oldScale * scale, pivot.getY() / oldScale * scale);
	}
}
