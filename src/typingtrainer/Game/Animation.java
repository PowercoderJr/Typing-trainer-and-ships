package typingtrainer.Game;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

/**
 * Created by Meow on 22.04.2017.
 */
public class Animation extends PvpObject
{
	private int framesCount;
	private int columns;
	private int offsetX;
	private int offsetY;
	private int width;
	private int height;

	private Image spriteSheet;
	private int currFrame;
	private boolean isCompleted;

	public Animation(Belonging belonging, Point2D position, Image spriteSheet, int framesCount, int columns, int offsetX, int offsetY, int width, int height)
	{
		super(belonging, position);
		this.spriteSheet = spriteSheet;
		this.framesCount = framesCount;
		this.columns = columns;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.width = width;
		this.height = height;
		pivot = new Point2D(width / 2, height / 2);
		currFrame = 0;
		isCompleted = false;
		this.image = getCurrFrame();
	}

	public Image getCurrFrame()
	{
		int x = (currFrame % columns) * width + offsetX;
		int y = (currFrame / columns) * height + offsetY;
		return new WritableImage(spriteSheet.getPixelReader(), x, y, width, height);
	}

	public void play(long animationDuration)
	{
		final long frameDuration = animationDuration / framesCount;
		currFrame = 0;
		isCompleted = false;
		new Thread(() ->
		{
			try
			{
				while (currFrame < framesCount - 1)
				{
					Thread.sleep(frameDuration);
					++currFrame;
					image = getCurrFrame();
				}
				Thread.sleep(frameDuration);
				isCompleted = true;
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}).start();
	}

	public boolean isCompleted()
	{
		return isCompleted;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}
}
