package typingtrainer.Game;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
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
	private static final int MIN_WORD_LENGTH_TO_SHOOT = 3;

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
		double x, y, vectorLength, distX, distY;
		for (int i = 0; i < cannonballs.size(); ++i)
		{
			Cannonball cannonball = cannonballs.get(i);
			if (cannonball.getPosition().getX() > 1200)
				cannonballs.remove(i--);
			else
			{
				distX = cannonball.getTarget().getX() - cannonball.getPosition().getX();
				distY = cannonball.getTarget().getY() - cannonball.getPosition().getY();
				vectorLength = Math.sqrt(distX * distX + distY * distY);
				x = cannonball.getPosition().getX() + cannonball.getSpeed() * (distX / vectorLength) / 1000 * dt;
				y = cannonball.getPosition().getY() + cannonball.getSpeed() * (distY / vectorLength) / 1000 * dt;
				cannonball.setPosition(new Point2D(x, y));
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
			if (cannon.getWord().getCharsDone() >= MIN_WORD_LENGTH_TO_SHOOT && cannon.getWord().getCharsDone() > longestSubstr.length())
			{
				cannonWithLongestSubstrID = i;
				longestSubstr = ships[0].getOffenciveCannon(cannonWithLongestSubstrID).getWord().getSubstrBefore();
			}
			getShip(0).getOffenciveCannon(i).getWord().setCharsDone(0);
		}
		if (cannonWithLongestSubstrID != -1)
		{
			Cannonball cannonball = ships[0].getOffenciveCannon(cannonWithLongestSubstrID).shoot(new Point2D(1280, Math.random() * 720));
			cannonball.getWord().setWord(longestSubstr);
			ships[0].getOffenciveCannon(cannonWithLongestSubstrID).getWord().setWord(Word.generateRndWord(Game.MAX_WORD_LENGTH, difficultyParam, langParam, isRegisterParam));
			return GameSceneController.OFFENCIVE_SHOT_CODEGRAM + ":" +
					cannonWithLongestSubstrID + GameSceneController.SEPARATOR_CODEGRAM +
					cannonball.getTarget().getX() + GameSceneController.SEPARATOR_CODEGRAM +
					cannonball.getTarget().getY() + GameSceneController.SEPARATOR_CODEGRAM +
					cannonball.getSpeed() + GameSceneController.SEPARATOR_CODEGRAM + longestSubstr;
		}
		else
			return null;
	}

	public String shootOffenciveHostile(int cannonID, Point2D target, double speed, String word)
	{
		Cannonball cannonball = ships[1].getOffenciveCannon(cannonID).shoot(target);
		cannonball.getWord().setWord(word);
		cannonball.setSpeed(speed);
		return GameSceneController.OFFENCIVE_SHOT_CODEGRAM + ":" +
				cannonID + GameSceneController.SEPARATOR_CODEGRAM +
				cannonball.getTarget().getX() + GameSceneController.SEPARATOR_CODEGRAM +
				cannonball.getTarget().getY() + GameSceneController.SEPARATOR_CODEGRAM +
				cannonball.getSpeed() + GameSceneController.SEPARATOR_CODEGRAM + word;
	}

	public boolean shootDefencive()
	{
		ships[0].getDefenciveCannon().shoot(new Point2D(1280, Math.random() * 720));
		for (int i = 0; i < Ship.OFFENCIVE_CANNONS_COUNT; ++i)
			ships[0].getOffenciveCannon(i).getWord().setCharsDone(0);
		return true;
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
			PvpWord word = ships[0].getOffenciveCannon(i).getWord();
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
