package typingtrainer.Game;

import typingtrainer.Word;

/**
 * Created by Meow on 22.04.2017.
 */
public class PvpWord extends Word
{
	private int charsDone;

	public PvpWord()
	{
		super();
		charsDone = 0;
	}

	public PvpWord(int length, int difficulty, Languages language, boolean isShiftIncluding)
	{
		super(length, difficulty, language, isShiftIncluding);
		charsDone = 0;
	}

	public PvpWord(String word)
	{
		super(word);
		charsDone = 0;
	}

	public String getSubstrBefore()
	{
		return word.substring(0, charsDone);
	}

	public String getSubstrBeforeWithSpaces()
	{
		StringBuffer result;
		String substrBefore = getSubstrBefore();
		result = new StringBuffer(substrBefore);
		for (int i = 0; i < word.length() - substrBefore.length(); ++i)
			result.append(" ");
		return result.toString();
	}

	public String getSubstrAfter()
	{
		return word.substring(charsDone);
	}

	public String getSubstrAfterWithSpaces()
	{
		StringBuffer result;
		String substrAfter = getSubstrAfter();
		result = new StringBuffer(substrAfter);
		for (int i = 0; i < word.length() - substrAfter.length(); ++i)
			result.insert(0, " ");
		return result.toString();
	}

	public boolean incCharsDone()
	{
		return word.length() == ++charsDone;
	}

	public int getCharsDone()
	{
		return charsDone;
	}

	public void setCharsDone(int charsDone)
	{
		this.charsDone = charsDone;
	}

	public char getCurrChar()
	{
		return word.charAt(charsDone);
	}
}
