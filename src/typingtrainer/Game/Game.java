package typingtrainer.Game;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;

import java.util.Queue;

/**
 * Created by Meow on 22.04.2017.
 */
public class Game
{
	public static final Image SPRITE_SHEET = new Image("typingtrainer/Game/spritesheet.png");
	public static final int SHIPS_COUNT = 2;

	Ship[] ships;
	Queue<Cannonball> cannonballs;

	public Game()
	{
		ships = new Ship[SHIPS_COUNT];
		ships[0] = new Ship(PvpObject.Belonging.FRIENDLY, new Point2D(0, 0));
		ships[1] = new Ship(PvpObject.Belonging.HOSTILE, new Point2D(0, 0));
	}

	public Ship getShip(int index)
	{
		return ships[index];
	}
}
