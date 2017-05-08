package typingtrainer.Game;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Circle;
import typingtrainer.GameScene.GameSceneController;
import typingtrainer.Word;

import java.util.ArrayList;

/**
 * Created by Meow on 22.04.2017.
 */

public class Game
{
	public static final Image SPRITE_SHEET = new Image("typingtrainer/Game/spritesheet.png");
	public static final int SHIPS_COUNT = 2;
	public static final int MAX_WORD_LENGTH = 12;
	public static final int MIN_WORD_LENGTH_TO_SHOOT = 3;
	private static final double DEFENCIVE_ANTICIPATION_DISTANCE = 100.0;
	private static final Object CANNONBALLS_LOCK = new Object();

	private Word.Languages langParam;
	private int difficultyParam;
	private boolean isRegisterParam;

	private Ship[] ships;
	private ArrayList<Cannonball> cannonballs;
	private ArrayList<Animation> smokeClouds;

	public Game()
	{
		langParam = Word.Languages.RU;
		difficultyParam = 2;
		isRegisterParam = false;

		ships = new Ship[SHIPS_COUNT];
		ships[0] = new Ship(this, PvpObject.Belonging.FRIENDLY, new Point2D(0, 0));
		ships[1] = new Ship(this, PvpObject.Belonging.HOSTILE, new Point2D(0, 0));
		cannonballs = new ArrayList<>();
		smokeClouds = new ArrayList<>();
	}

	public void tick(int dt)
	{
		//Cannonballs
		synchronized (CANNONBALLS_LOCK)
		{
			for (int i = 0; i < cannonballs.size(); ++i)
			{
				Cannonball cannonball = cannonballs.get(i);
				boolean alreadyDestroyed = false;
				if (cannonball.getType() == Cannonball.Type.DEFENCIVE)
					for (int j = 0; j < cannonballs.size(); ++j)
					{
						Cannonball victim = cannonballs.get(j);
						if (victim.getType() == Cannonball.Type.OFFENCIVE &&
								victim.getBelonging() != cannonball.getBelonging() &&
								victim.getPvpWord().toString().equals(cannonball.getPvpWord().toString()) &&
								GameSceneController.mirrorRelativelyToDefaultWidth(victim.getPosition().add(victim.getPivot())).distance(cannonball.getPosition().add(cannonball.getPivot())) < cannonball.getImage().getWidth() * 2)
						{
							cannonballs.remove(i);
							cannonballs.remove(j);
							alreadyDestroyed = true;
							if (j > i)
								--i;
							break;
						}
					}

				if (!alreadyDestroyed)
				{
					if (cannonball.getPosition().getY() < 0 ||
							cannonball.getPosition().getX() > GameSceneController.DEFAULT_SCREEN_WIDTH ||
							cannonball.getPosition().getY() > GameSceneController.DEFAULT_SCREEN_HEIGHT)
					{
						cannonballs.remove(i--);
						alreadyDestroyed = true;
					}
					else
					{
						cannonball.setPosition(cannonball.getPositionAfterDistance(cannonball.getSpeed() / 1000 * dt));
						if (cannonball.getPositionAfterDistance(150).getX() > GameSceneController.DEFAULT_SCREEN_WIDTH - ships[0].getImage().getWidth())
							cannonball.setCanBeCountershooted(false);
					}
				}
			}
		}

		//Smoke clouds
		for (int i = 0; i < smokeClouds.size(); ++i)
		{
			Animation smokeCloud = smokeClouds.get(i);

			if (smokeCloud.isCompleted())
				smokeClouds.remove(i--);
			else
			{
				//smokeCloud.setPosition(new Point2D(smokeCloud.getPosition().getX(), smokeCloud.getPosition().getY() + GameSceneController.BACKGROUND_SPEED));
			}
		}
	}

	public void setAllCharsDoneToZero()
	{
		//Cannonballs
		synchronized (CANNONBALLS_LOCK)
		{
			for (int i = 0; i < cannonballs.size(); ++i)
				cannonballs.get(i).getPvpWord().setCharsDone(0);
		}

		//Cannons
		for (int i = 0; i < Ship.OFFENCIVE_CANNONS_COUNT; ++i)
			ships[0].getOffenciveCannon(i).getPvpWord().setCharsDone(0);
	}

	/**
	 *
	 * @return При успешном выстреле возвращает кодограмму с информацией о выстреле, при неудаче - пустую строку.
	 */
	public String shootOffenciveFriendly()
	{
		String longestSubstr = "";
		int cannonWithLongestSubstrID = -1;
		for (int i = 0; i < Ship.OFFENCIVE_CANNONS_COUNT; ++i)
		{
			OffenciveCannon cannon = ships[0].getOffenciveCannon(i);
			if (cannon.getPvpWord().getCharsDone() >= MIN_WORD_LENGTH_TO_SHOOT && cannon.getPvpWord().getCharsDone() > longestSubstr.length())
			{
				cannonWithLongestSubstrID = i;
				longestSubstr = ships[0].getOffenciveCannon(cannonWithLongestSubstrID).getPvpWord().getSubstrBefore();
			}
			//getShip(0).getOffenciveCannon(i).getPvpWord().setCharsDone(0);
		}
		if (cannonWithLongestSubstrID != -1)
		{
			Cannonball cannonball = ships[0].getOffenciveCannon(cannonWithLongestSubstrID).shoot(new Point2D(1280, Math.random() * 720));
			cannonball.getPvpWord().setWord(longestSubstr);
			ships[0].getOffenciveCannon(cannonWithLongestSubstrID).getPvpWord().setWord(Word.generateRndWord(Game.MAX_WORD_LENGTH, difficultyParam, langParam, isRegisterParam));
			return GameSceneController.OFFENCIVE_SHOT_CODEGRAM + ":" +
					cannonWithLongestSubstrID + GameSceneController.SEPARATOR_CODEGRAM +
					cannonball.getTarget().getX() + GameSceneController.SEPARATOR_CODEGRAM +
					cannonball.getTarget().getY() + GameSceneController.SEPARATOR_CODEGRAM +
					cannonball.getSpeed() + GameSceneController.SEPARATOR_CODEGRAM + longestSubstr;
		}
		else
			return "";
	}

	public String shootOffenciveHostile(int cannonID, Point2D target, double speed, String word)
	{
		Cannonball cannonball = ships[1].getOffenciveCannon(cannonID).shoot(target);
		cannonball.getPvpWord().setWord(word);
		cannonball.setSpeed(speed);
		return GameSceneController.OFFENCIVE_SHOT_CODEGRAM + ":" +
				cannonID + GameSceneController.SEPARATOR_CODEGRAM +
				cannonball.getTarget().getX() + GameSceneController.SEPARATOR_CODEGRAM +
				cannonball.getTarget().getY() + GameSceneController.SEPARATOR_CODEGRAM +
				cannonball.getSpeed() + GameSceneController.SEPARATOR_CODEGRAM +
				word;
	}

	public String shootDefenciveFriendly()
	{
		synchronized (CANNONBALLS_LOCK)
		{
			Cannonball target = null;
			for (int i = 0; i < cannonballs.size() && target == null; ++i)
			{
				Cannonball cannonball = cannonballs.get(i);
				if (cannonball.getBelonging() == PvpObject.Belonging.HOSTILE &&
						cannonball.getType() == Cannonball.Type.OFFENCIVE &&
						cannonball.getPvpWord().getCharsDone() == cannonball.getPvpWord().toString().length() &&
						cannonball.canBeCountershooted())
					target = cannonball;
			}
			if (target != null)
			{
				Point2D collisionPoint = GameSceneController.mirrorRelativelyToDefaultWidth(target.getPositionAfterDistance(DEFENCIVE_ANTICIPATION_DISTANCE));
				//Debug
				GameSceneController.colPoints.add(new GameSceneController.Line(collisionPoint.getX(), collisionPoint.getY(), 0, 0));
				//
				Cannonball cannonball = ships[0].getDefenciveCannon().shoot(collisionPoint.add(collisionPoint.normalize().getX(), collisionPoint.normalize().getY()));
				cannonball.getPvpWord().setWord(target.getPvpWord().toString());
				cannonball.setSpeed(cannonball.getPosition().distance(collisionPoint) * target.getSpeed() / DEFENCIVE_ANTICIPATION_DISTANCE);
				target.setCanBeCountershooted(false);
				return GameSceneController.DEFENCIVE_SHOT_CODEGRAM + ":" +
						cannonball.getTarget().getX() + GameSceneController.SEPARATOR_CODEGRAM +
						cannonball.getTarget().getY() + GameSceneController.SEPARATOR_CODEGRAM +
						cannonball.getSpeed() + GameSceneController.SEPARATOR_CODEGRAM +
						cannonball.getPvpWord().toString();
			}
			else
				return "";
		}
	}

	public String shootDefenciveHostile(Point2D target, double speed, String word)
	{
		Cannonball cannonball = ships[1].getDefenciveCannon().shoot(target);
		cannonball.getPvpWord().setWord(word);
		cannonball.setSpeed(speed);
		//setCountershooted ?
		return GameSceneController.DEFENCIVE_SHOT_CODEGRAM + ":" +
				cannonball.getTarget().getX() + GameSceneController.SEPARATOR_CODEGRAM +
				cannonball.getTarget().getY() + GameSceneController.SEPARATOR_CODEGRAM +
				cannonball.getSpeed() + GameSceneController.SEPARATOR_CODEGRAM +
				cannonball.getPvpWord().toString();
	}

	public void handleShootableChar(KeyEvent event)
	{
		char typedChar;
		if (event.isShiftDown())
		{
			String[] alphabet;
			switch (langParam)
			{
				case RU:
				default:
					alphabet = Word.ALPH_RU;
					break;
				case EN:
					alphabet = Word.ALPH_EN;
					break;
			}
			int symbolIndex = alphabet[0].indexOf(event.getText().charAt(0));
			typedChar = alphabet[1].charAt(symbolIndex);
		}
		else
		{
			typedChar = event.getText().charAt(0);
		}

		for (int i = 0; i < Ship.OFFENCIVE_CANNONS_COUNT; ++i)
		{
			PvpWord word = ships[0].getOffenciveCannon(i).getPvpWord();
			if (word.getCharsDone() < word.toString().length() && word.getCurrChar() == typedChar)
				word.incCharsDone();
			else
				word.setCharsDone(0);
		}

		for (int i = 0; i < cannonballs.size(); ++i)
		{
			if (cannonballs.get(i).canBeCountershooted())
			{
				PvpWord word = cannonballs.get(i).getPvpWord();
				if (word.getCharsDone() < word.toString().length() && word.getCurrChar() == typedChar)
					word.incCharsDone();
				else
					word.setCharsDone(0);
			}
		}
	}

	private void updateWords(ArrayList<IHavingWord> a, char typedChar)
	{
		for (int i = 0; i < a.size(); ++i)
		{
			PvpWord word = a.get(i).getPvpWord();
			if (word.getCharsDone() < word.toString().length() && word.getCurrChar() == typedChar)
				word.incCharsDone();
			else
				word.setCharsDone(0);
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

	public ArrayList<Animation> getSmokeClouds()
	{
		return smokeClouds;
	}

	public Word.Languages getLangParam()
	{
		return langParam;
	}

	public void setLangParam(Word.Languages langParam)
	{
		this.langParam = langParam;
	}

	public int getDifficultyParam()
	{
		return difficultyParam;
	}

	public void setDifficultyParam(int difficultyParam)
	{
		this.difficultyParam = difficultyParam;
	}

	public boolean isRegisterParam()
	{
		return isRegisterParam;
	}

	public void setRegisterParam(boolean registerParam)
	{
		isRegisterParam = registerParam;
	}
}
