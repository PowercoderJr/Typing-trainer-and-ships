package typingtrainer.Game;

import javafx.geometry.Point2D;
import javafx.scene.image.WritableImage;
import javafx.scene.shape.Polygon;
import typingtrainer.GameScene.GameSceneController;
import typingtrainer.Main;

/**
 * Created by Meow on 22.04.2017.
 */
public class Ship extends PvpObject
{
	public static final int OFFENCIVE_CANNONS_COUNT = 2;
	public static final Point2D[] CANNON_BASE_POSITIONS =
			{
					new Point2D(-50.0, 85.0),
					new Point2D(10.0, 360.0),
					new Point2D(0.0, 590.0)
			};
	public static final Point2D[] CANNON_PIVOTS =
			{
					new Point2D(90.0, 25.0),
					new Point2D(115.0, 25.0),
					new Point2D(123.0, 25.0)
			};
	public static final int BASE_HP = 1000;

	private Game parentGame;

	private double hp;
	private String playerName;
	private DefenciveCannon defenciveCannon;
	private OffenciveCannon[] offenciveCannons;

	private double stoppingSpeed;

	public Ship(Game parentGame, Belonging belonging, Point2D position)
	{
		super(belonging, position);
		this.parentGame = parentGame;
		image = new WritableImage(Game.SPRITE_SHEET.getPixelReader(), 0, 0, 132, 720);
		hp = BASE_HP;
		playerName = "Player";
		defenciveCannon = new DefenciveCannon(this, belonging, CANNON_BASE_POSITIONS[0]);
		defenciveCannon.setPivot(CANNON_PIVOTS[0]);
		offenciveCannons = new OffenciveCannon[OFFENCIVE_CANNONS_COUNT];
		for (int i = 0; i < OFFENCIVE_CANNONS_COUNT; ++i)
		{
			offenciveCannons[i] = new OffenciveCannon(this, belonging, CANNON_BASE_POSITIONS[i + 1]);
			offenciveCannons[i].setPivot(CANNON_PIVOTS[i + 1]);
		}
		stoppingSpeed = 0;
	}

	public void stoppingTick(int dt)
	{
			if (stoppingSpeed < GameSceneController.BACKGROUND_SPEED)
				stoppingSpeed += GameSceneController.BACKGROUND_SPEED * 0.003;
			double step = stoppingSpeed / 1000 * dt;
			position = position.add(0, step);
			Cannon[] cannons = new Cannon[OFFENCIVE_CANNONS_COUNT + 1];
			cannons[0] = defenciveCannon;
			for (int i = 0; i < OFFENCIVE_CANNONS_COUNT; ++i)
				cannons[i + 1] = offenciveCannons[i];
			for (int i = 0; i < OFFENCIVE_CANNONS_COUNT + 1; ++i)
				cannons[i].setPosition(cannons[i].getPosition().add(0, step));
	}

	public double getHp()
	{
		return hp;
	}

	public void setHp(double hp)
	{
		this.hp = hp;
	}

	public void damage(double damage)
	{
		hp = damage < hp ? hp - damage : 0;
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

	public String getPlayerName()
	{
		return playerName;
	}

	public void setPlayerName(String playerName)
	{
		this.playerName = playerName;
	}
}
