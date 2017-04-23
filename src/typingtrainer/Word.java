package typingtrainer;

/**
 * Created by Meow on 22.02.2017.
 */
public abstract class Word
{
	public static final String[] ALPH_RU =	{	"аовлыдфжпркгенмьитушсбцщчюйзя.хъэ",
												"АОВЛЫДФЖПРКГЕНМЬИТУШСБЦЩЧЮЙЗЯ,ХЪЭ"};
	public static final String[] ALPH_EN = 	{	"fjdksla;ghrutyvmbneic,wox.qpz/[]\'",
												"FJDKSLA:GHRUTYVMBNEIC<WOX>QPZ?{}\""};
	public static final int LANG_COUNT = 2;
	public static final int MAX_LEVEL = 33;
	public enum Languages {RU, EN};
	protected String word;

	public static String generateRndWord(int length, int difficulty, Languages language, boolean isShiftIncluding)
	/** Генерирует слово из рандомных букв.
	 * @param length - Длина слова.
	 * @param difficulty - Уровень сложности - количество различных букв алфавита, среди которых будет происходить рандом.
	 *                     Чётное число от 2 до 30, либо 33.
	 * @param language - Язык.
	 * @param isShiftIncluding - Будут ли рандомиться также символы, для набора которых надо держать Shift.
	 * @return Слово из рандомных букв (кэп).
	 */
	{
		String[] alphabet;
		switch (language)
		{
			case RU:
			default:
				alphabet = ALPH_RU;
				break;
			case EN:
				alphabet = ALPH_EN;
				break;
		}

		char[] word = new char[length];
		int i, j;
		for (int c = 0; c < length; c++)
		{
			if (isShiftIncluding)
				i = (int)(Math.random() * 2);
			else
				i = 0;
			j = (int)(Math.random() * difficulty);
			word[c] = alphabet[i].charAt(j);
		}
		return new String(word);
	}

	public Word()
	/** Генерирует слово из рандомных букв. Длина 2-11, сложность 33, язык случайный, режим с Shift включен.
	 * @return Слово из рандомных букв.
	 */
	{
		Languages lang;
		switch ((int)(Math.random() * LANG_COUNT))
		{
			case 0:
			default:
				lang = Languages.RU;
				break;
			case 1:
				lang = Languages.EN;
				break;
		}
		word = Word.generateRndWord((int)(2 + Math.random() * 10), MAX_LEVEL, lang, true);
	}

	public Word(int length, int difficulty, Languages language, boolean isShiftIncluding)
	/** Генерирует слово из рандомных букв.
	 * @param length - Длина слова.
	 * @param difficulty - Уровень сложности - количество различных букв алфавита, среди которых будет происходить рандом.
	 *                   Чётное число от 2 до 30, либо 33.
	 * @param language - Язык.
	 * @param isShiftIncluding - Будут ли рандомиться также символы, для набора которых надо держать Shift.
	 * @return Слово из рандомных букв.
	 */
	{
		word = Word.generateRndWord(length, difficulty, language, isShiftIncluding);
	}

	public String toString(){
		return this.word;
	}
}
