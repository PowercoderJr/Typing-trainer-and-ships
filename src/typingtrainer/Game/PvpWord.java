package typingtrainer.Game;

import typingtrainer.Word;

/**
 * Created by Meow on 22.04.2017.
 */
public class PvpWord extends Word
{
	int charsDone;

	public String getSubstrBefore()
	{
		return word.substring(0, charsDone);
	}

	public String getSubstrAfter()
	{
		return word.substring(charsDone);
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
}
