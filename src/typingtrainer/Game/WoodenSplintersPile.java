package typingtrainer.Game;

import javafx.geometry.Point2D;
import javafx.scene.image.WritableImage;

/**
 * Created by Meow on 12.05.2017.
 */
public class WoodenSplintersPile
{
	public static final int SPLINTERS_COUNT = 4;
	private static final int[][] SPLINTER_SPRITES_LOCATIONS = //x, y, width, height
			{
					{132, 401, 18, 36},
					{150, 401, 24, 37},
					{174, 401, 29, 46},
					{203, 401, 15, 35}
			};

	private WoodenSplinter[] splinters;

	public WoodenSplintersPile(PvpObject.Belonging belonging, Point2D position)
	{
		splinters = new WoodenSplinter[SPLINTERS_COUNT];
		for (int i = 0; i < SPLINTERS_COUNT; ++i)
		{
			splinters[i] = new WoodenSplinter(belonging,
					position.add(SPLINTER_SPRITES_LOCATIONS[i][2] / 2, SPLINTER_SPRITES_LOCATIONS[i][3] / 2),
					new WritableImage(Game.SPRITE_SHEET.getPixelReader(),
					SPLINTER_SPRITES_LOCATIONS[i][0], SPLINTER_SPRITES_LOCATIONS[i][1],
					SPLINTER_SPRITES_LOCATIONS[i][2], SPLINTER_SPRITES_LOCATIONS[i][3]));
		}
	}

	public WoodenSplinter getSplinter(int index)
	{
		return splinters[index];
	}
}
