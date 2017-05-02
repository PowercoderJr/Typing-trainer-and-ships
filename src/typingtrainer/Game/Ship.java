package typingtrainer.Game;

import javafx.geometry.Point2D;
import javafx.scene.image.WritableImage;

/**
 * Created by Meow on 22.04.2017.
 */
public class Ship extends PvpObject
{
	public static final int OFFENCIVE_CANNONS_COUNT = 2;
	public static final Point2D[] CANNON_BASE_POSITIONS =
			{
					new Point2D(-40, 75),
					new Point2D(10, 360),
					new Point2D(0, 590)
			};

	private Game parentGame;

	private int hp;
	private DefenciveCannon defenciveCannon;
	private OffenciveCannon[] offenciveCannons;

	public Ship(Game parentGame, Belonging belonging, Point2D position)
	{
		super(belonging, position);
		this.parentGame = parentGame;
		image = new WritableImage(Game.SPRITE_SHEET.getPixelReader(), 0, 0, 132, 720);
		hp = 1000;
		defenciveCannon = new DefenciveCannon(this, belonging, CANNON_BASE_POSITIONS[0]);
		offenciveCannons = new OffenciveCannon[OFFENCIVE_CANNONS_COUNT];
		for (int i = 0; i < OFFENCIVE_CANNONS_COUNT; ++i)
		{
			offenciveCannons[i] = new OffenciveCannon(this, belonging, CANNON_BASE_POSITIONS[i + 1]);
		}
	}

	public int getHp()
	{
		return hp;
	}

	public void setHp(int hp)
	{
		this.hp = hp;
	}

	public OffenciveCannon getOffenciveCannon(int index)
	{
		return offenciveCannons[index];
	}

	public DefenciveCannon getDefenciveCannon()
	{
		return defenciveCannon;
	}

	public Game getParentGame()
	{
		return parentGame;
	}
}
