package typingtrainer.Game;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

/**
 * Created by Meow on 22.04.2017.
 */
public class Game
{
	public static final Image SPRITE_SHEET = new Image("typingtrainer/Game/spritesheet.png");
	public static final int SHIPS_COUNT = 2;

	Ship[] ships;
	ArrayList<Cannonball> cannonballs;

	public Game()
	{
		ships = new Ship[SHIPS_COUNT];
		ships[0] = new Ship(this, PvpObject.Belonging.FRIENDLY, new Point2D(0, 0));
		ships[1] = new Ship(this, PvpObject.Belonging.HOSTILE, new Point2D(0, 0));
		cannonballs = new ArrayList<>();
	}

	public void tick(int dt)
	{
		double x, y, vectorLength, distX, distY;
		for (int i = 0; i < cannonballs.size(); ++i)
		{
			Cannonball cannonball = cannonballs.get(i);

			distX = cannonball.getTarget().getX() - cannonball.getPosition().getX();
			distY = cannonball.getTarget().getY() - cannonball.getPosition().getY();
			vectorLength = Math.sqrt(distX * distX + distY * distY);
			x = cannonball.getPosition().getX() + cannonball.getSpeed() * (distX / vectorLength) / 1000 * dt;
			y = cannonball.getPosition().getY() + cannonball.getSpeed() * (distY / vectorLength) / 1000 * dt;
			cannonball.setPosition(new Point2D(x, y));

			if (x > 1200)
				cannonballs.remove(i);
		}
	}

	public Ship getShip(int index)
	{
		return ships[index];
	}

	public ArrayList<Cannonball> getCannonballs()
	{
		return cannonballs;
	}
}
