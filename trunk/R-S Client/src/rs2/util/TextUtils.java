package rs2.util;

import rs2.sign.signlink;

public final class TextUtils {

	public static long longForName(String string) {
		long name = 0L;
		for (int character = 0; character < string.length() && character < 12; character++) {
			char c = string.charAt(character);
			name *= 37L;
			if (c >= 'A' && c <= 'Z') {
				name += (1 + c) - 65;
			} else if (c >= 'a' && c <= 'z') {
				name += (1 + c) - 97;
			} else if (c >= '0' && c <= '9') {
				name += (27 + c) - 48;
			}
		}
		for (; name % 37L == 0L && name != 0L; name /= 37L);
		return name;
	}

	public static String nameForLong(long name) {
		try {
			if (name <= 0L || name >= 0x5b5b57f8a98a5dd1L) {
				return "invalid_name";
			}
			if (name % 37L == 0L) {
				return "invalid_name";
			}
			int index = 0;
			char characters[] = new char[12];
			while (name != 0L) {
				long l = name;
				name /= 37L;
				characters[11 - index++] = validChars[(int) (l - name * 37L)];
			}
			return new String(characters, 12 - index, index);
		} catch (RuntimeException runtimeexception) {
			signlink.reportError("81570, " + name + ", " + (byte) -99 + ", " + runtimeexception.toString());
		}
		throw new RuntimeException();
	}

	public static long method585(String s) {
		s = s.toUpperCase();
		long l = 0L;
		for (int i = 0; i < s.length(); i++) {
			l = (l * 61L + (long) s.charAt(i)) - 32L;
			l = l + (l >> 56) & 0xffffffffffffffL;
		}
		return l;
	}

	public static String method586(int i) {
		return (i >> 24 & 0xff) + "." + (i >> 16 & 0xff) + "." + (i >> 8 & 0xff) + "." + (i & 0xff);
	}

	public static String fixName(String name) {
		if (name.length() > 0) {
			char characters[] = name.toCharArray();
			for (int index = 0; index < characters.length; index++)
				if (characters[index] == '_') {
					characters[index] = ' ';
					if (index + 1 < characters.length && characters[index + 1] >= 'a' && characters[index + 1] <= 'z') {
						characters[index + 1] = (char) ((characters[index + 1] + 65) - 97);
					}
				}
			if (characters[0] >= 'a' && characters[0] <= 'z') {
				characters[0] = (char) ((characters[0] + 65) - 97);
			}
			return capitalize(new String(characters));
		} else {
			return capitalize(name);
		}
	}

	public static String capitalize(String string) {
		for (int index = 0; index < string.length(); index++) {
			if (index == 0) {
				string = String.format("%s%s", Character.toUpperCase(string.charAt(0)), string.substring(1));
			}
			if (!Character.isLetterOrDigit(string.charAt(index))) {
				if (index + 1 < string.length()) {
					string = String.format("%s%s%s", string.subSequence(0, index + 1), Character.toUpperCase(string.charAt(index + 1)), string.substring(index + 2));
				}
			}
		}
		return string;
	}

	public static String passwordAsterisks(String password) {
		StringBuffer stringbuffer = new StringBuffer();
		for (int index = 0; index < password.length(); index++) {
			stringbuffer.append("*");
		}
		return stringbuffer.toString();
	}

	private static final char[] validChars = {
		'_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 
		'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
		'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
	};

}
