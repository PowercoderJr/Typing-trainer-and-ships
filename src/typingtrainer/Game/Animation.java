package typingtrainer.Game;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;

/**
 * Created by Meow on 22.04.2017.
 */
public class Animation
{
	private ImageView canvas;
	private int framesCount;
	private int columns;
	private int offsetX;
	private int offsetY;
	private int width;
	private int height;

	public Animation(ImageView canvas, int framesCount, int columns, int offsetX, int offsetY, int width, int height)
	{
		this.canvas = canvas;
		this.framesCount = framesCount;
		this.columns = columns;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.width = width;
		this.height = height;
	}

	protected void interpolate(double frac)
	{
		int index = Math.min((int) Math.floor(frac * framesCount), framesCount - 1);
		int x = (index % columns) * width + offsetX;
		int y = (index / columns) * height + offsetY;
		canvas.setViewport(new Rectangle2D(x, y, width, height));
	}
}
