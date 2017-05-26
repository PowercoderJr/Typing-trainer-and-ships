package typingtrainer.Game;

import javafx.geometry.Point2D;
import javafx.scene.image.WritableImage;
import typingtrainer.Word;

/**
 * Created by Meow on 22.04.2017.
 */
public class Cannonball extends PvpObject
{
	public static final double BASE_SPEED = 300.0;
	public static final double SPEED_WEIGHT_PENALTY = 20.0;
	public static final double WEIGHT_DAMAGE = 8.0;
	public enum Type {OFFENCIVE, DEFENCIVE};
	private Type type;
	private Cannon parentCannon;
	private PvpWord word;
	private Point2D target;
	private double speed;
	private boolean canBeCountershooted;
	private boolean hasDamaged;
	private String id;

	public Cannonball(Cannon parentCannon, Belonging belonging, Point2D position)
	{
		super(belonging, position, false);
		this.parentCannon = parentCannon;
		image = new WritableImage(Game.SPRITE_SHEET.getPixelReader(),132, 55, 16, 16);
		pivot = new Point2D(image.getWidth() / 2, image.getHeight() / 2);
		word = new PvpWord("");
		speed = 300;
		if (parentCannon.getClass() == OffenciveCannon.class)
			type = Type.OFFENCIVE;
		else if (parentCannon.getClass() == DefenciveCannon.class)
			type = Type.DEFENCIVE;
		canBeCountershooted = type == Type.OFFENCIVE;
		hasDamaged = false;
		id = Word.generateRndWord(10, 33, Word.Languages.EN, true);
	}

	public Point2D getTarget()
	{
		return target;
	}

	public void setTarget(Point2D target)
	{
		this.target = target/*.subtract(image.getWidth() / 2, image.getHeight() / 2)*/;
	}

	public double getSpeed()
	{
		return speed;
	}

	public void setSpeed(double speed)
	{
		this.speed = speed;
	}

	public void setSpeedAuto()
	{
		speed = BASE_SPEED - (word.toString().length() - Game.MIN_WORD_LENGTH_TO_SHOOT) * SPEED_WEIGHT_PENALTY;
	}

	public PvpWord getPvpWord()
	{
		return word;
	}

	public Type getType()
	{
		return type;
	}

	public Point2D getDirection()
	{
		return target.subtract(position).normalize();
	}

	public Point2D getPositionAfterDistance(double distance)
	{
		return position.add(getDirection().getX() * distance, getDirection().getY() * distance);
	}

	public boolean canBeCountershooted()
	{
		return canBeCountershooted;
	}

	public void setCanBeCountershooted(boolean canBeCountershooted)
	{
		this.canBeCountershooted = canBeCountershooted;
	}

	public boolean HasDamaged()
	{
		return hasDamaged;
	}

	public void setHasDamaged(boolean hasDamaged)
	{
		this.hasDamaged = hasDamaged;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}
}
