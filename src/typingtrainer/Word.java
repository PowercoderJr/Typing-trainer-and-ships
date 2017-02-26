package typingtrainer;

/**
 * Created by Meow on 22.02.2017.
 */
public class Word
{
	//Длина каждого алфавита = 33
	public static final char[][] ALPH_RU =	{	{'а', 'о', 'в', 'л', 'ы', 'д', 'ф', 'ж', 'п', 'р', 'к', 'г', 'е', 'н', 'м', 'ь', 'и', 'т', 'у', 'ш', 'с', 'б', 'ц', 'щ', 'ч', 'ю', 'й', 'з', 'я', '.', 'х', 'ъ', 'э'},
												{'А', 'О', 'В', 'Л', 'Ы', 'Д', 'Ф', 'Ж', 'П', 'Р', 'К', 'Г', 'Е', 'Н', 'М', 'Ь', 'И', 'Т', 'У', 'Ш', 'С', 'Б', 'Ц', 'Щ', 'Ч', 'Ю', 'Й', 'З', 'Я', ',', 'Х', 'Ъ', 'Э'}};
	public static final char[][] ALPH_EN = 	{	{'f', 'j', 'd', 'k', 's', 'l', 'a', ';', 'g', 'h', 'r', 'u', 't', 'y', 'v', 'm', 'b', 'n', 'e', 'i', 'c', ',', 'w', 'o', 'x', '.', 'q', 'p', 'z', '/', '[', ']', '\''},
												{'F', 'J', 'D', 'K', 'S', 'L', 'A', ':', 'G', 'H', 'R', 'U', 'T', 'Y', 'V', 'M', 'B', 'N', 'E', 'I', 'C', '<', 'W', 'O', 'X', '>', 'Q', 'P', 'Z', '?', '{', '}', '\"'}};
	public static final int LANG_COUNT = 2;
	public enum Languages {RU, EN};
	private String word;

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
		char[][] alphabet;
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
			word[c] = alphabet[i][j];
		}
		return new String(word);
	}

	public Word()
	/** Генерирует слово из рандомных букв. Длина 2-11, сложность 33, язык случайный, режим с Shift включен.
	 * @return Слово из рандомных букв.
	 */
	{
		Languages lng;
		switch ((int)(Math.random() * LANG_COUNT))
		{
			case 0:
			default:
				lng = Languages.RU;
				break;
			case 1:
				lng = Languages.EN;
				break;
		}
		word = Word.generateRndWord((int)(2 + Math.random() * 10), 33, lng, true);
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
}
