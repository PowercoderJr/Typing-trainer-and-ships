package typingtrainer.Game;

import javafx.geometry.Point2D;
import javafx.scene.control.Label;

/**
 * Created by Meow on 22.04.2017.
 */
public class OffenciveCannon extends Cannon
{
	private PvpWord word;

	public OffenciveCannon(Ship parentShip, Belonging belonging, Point2D position)
	{
		super(parentShip, belonging, position);
		Game game = getParentShip().getParentGame();
		word = new PvpWord(Game.MAX_WORD_LENGTH, game.getDifficultyParam(), game.getLangParam(), game.isRegisterParam());
	}

	public PvpWord getWord()
	{
		return word;
	}
}