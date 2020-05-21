/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.Java;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.lang3.text.WordUtils;

import Reika.DragonAPI.DragonAPICore;

public class ReikaStringParser extends DragonAPICore {

	private static final String NUMBER_VARIABLE_CODE = "PARSE_NUMBER_VARIABLE";
	private static final String NUMBER_METHOD_CODE = "PARSE_NUMBER_METHOD";
	private static final String STRING_FORMAT_CODE = "PARSE_STRING_FORMAT";
	private static final String ENUM_FUNCTION_CODE = "PARSE_ENUM";

	private static final TreeMap<Integer, String> romanNumerals = new TreeMap();

	private static final HashSet<Character> wordChars = new HashSet();

	static {
		romanNumerals.put(1000, "M");
		romanNumerals.put(900, "CM");
		romanNumerals.put(500, "D");
		romanNumerals.put(400, "CD");
		romanNumerals.put(100, "C");
		romanNumerals.put(90, "XC");
		romanNumerals.put(50, "L");
		romanNumerals.put(40, "XL");
		romanNumerals.put(10, "X");
		romanNumerals.put(9, "IX");
		romanNumerals.put(5, "V");
		romanNumerals.put(4, "IV");
		romanNumerals.put(1, "I");

		for (char c = 'a'; c <= 'z'; c++) {
			wordChars.add(c);
		}
		for (char c = 'A'; c <= 'Z'; c++) {
			wordChars.add(c);
		}
		for (char c = '0'; c <= '9'; c++) {
			wordChars.add(c);
		}
		wordChars.add('\'');
	}

	public static String getStringWithEmbeddedReferences(String sg) {
		String[] parts = sg.split("\\+");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < parts.length; i++) {
			if (isReference(parts[i])) {
				sb.append(parseReference(parts[i]));
			}
			else
				sb.append(parts[i]);
		}
		return sb.toString().replaceAll("\\\\n", "\n").replaceAll("\\\\t", "\t");
	}

	private static String parseReference(String sg) {
		if (sg.startsWith(NUMBER_VARIABLE_CODE))
			return parseStaticNumberVariable(sg);
		if (sg.startsWith(NUMBER_METHOD_CODE))
			return parseNumberMethod(sg);
		if (sg.startsWith(STRING_FORMAT_CODE))
			return parseStringFormat(sg);
		if (sg.startsWith(ENUM_FUNCTION_CODE))
			return parseEnum(sg);
		return null;
	}

	private static String parseEnum(String sg) {
		String[] parts = sg.split("\\$");
		if (parts.length != 3)
			throw new RuntimeException("This method does not support multi-layer class calls! "+sg);
		String enumClassName = subtractFrom(parts[0], ENUM_FUNCTION_CODE+"(");
		parts[2] = parts[2].substring(0, parts[2].length()-1);
		Class enumClass;
		try {
			enumClass = Class.forName(enumClassName);
			Enum e = Enum.valueOf(enumClass, parts[1]);
			Method m = enumClass.getDeclaredMethod(parts[2], (Class[])null);
			Object o = m.invoke(e, (Object[])null);
			return o.toString();
		}
		catch (ClassNotFoundException e1) {
			e1.printStackTrace();
			throw new RuntimeException("Enum Class \""+enumClassName+"\" does not exist!");
		}
		catch (NoSuchMethodException e1) {
			e1.printStackTrace();
			throw new RuntimeException("Enum Class "+enumClassName+" does not contain method \""+parts[2]+"\"!");
		}
		catch (SecurityException e1) {
			e1.printStackTrace();
			throw new RuntimeException("Enum Class "+enumClassName+"'s method "+parts[2]+" threw security exception!");
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException("Class "+enumClassName+"'s method "+parts[2]+" threw illegal access exception!");
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new RuntimeException("Class "+enumClassName+"'s method "+parts[2]+" is not parameterless!");
		}
		catch (InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException("Class "+enumClassName+"'s method "+parts[2]+" threw invocation target exception!");
		}
	}

	public static String parseStringFormat(String sg) {
		String[] parts = sg.split(",");
		String str;
		str = parts[0].replaceAll("\\s","");
		Object[] args = new Object[parts.length-1];
		for (int i = 1; i < parts.length; i++) {
			parts[i].replaceAll("\\s","");
			args[i-1] = parseReference(parts[i]);
		}
		return String.format(str, args);
	}

	private static String parseNumberMethod(String sg) {
		String[] parts = sg.split("\\$");
		if (parts.length > 2)
			throw new RuntimeException("This method does not support multi-layer class calls! "+sg);
		parts[1] = parts[1].substring(0, parts[1].length()-1);
		String cl = subtractFrom(parts[0], NUMBER_METHOD_CODE+"(");
		String obj;
		try {
			Class c = Class.forName(cl);
			Method m = c.getMethod(parts[1], (Class[])null);
			Object g = new Object();
			Object o = m.invoke(g, (Object[])null);
			obj = o.toString();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Class \""+cl+"\" does not exist!");
		}
		catch (NoSuchMethodException e) {
			e.printStackTrace();
			throw new RuntimeException("Class "+cl+" does not contain method \""+parts[1]+"\"!");
		}
		catch (SecurityException e) {
			e.printStackTrace();
			throw new RuntimeException("Class "+cl+"'s method "+parts[1]+" threw security exception!");
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException("Class "+cl+"'s method "+parts[1]+" threw illegal access exception!");
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new RuntimeException("Class "+cl+"'s method "+parts[1]+" is not parameterless!");
		}
		catch (InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException("Class "+cl+"'s method "+parts[1]+" threw invocation target exception!");
		}
		return obj;
	}

	private static String parseStaticNumberVariable(String sg) {
		String[] parts = sg.split("\\$");
		if (parts.length > 2)
			throw new RuntimeException("This method does not support multi-layer class calls! "+sg);
		parts[1] = parts[1].substring(0, parts[1].length()-1);
		String cl = subtractFrom(parts[0], NUMBER_VARIABLE_CODE+"(");
		String obj = "NOT FOUND";
		try {
			Class c = Class.forName(cl);
			Field f = c.getField(parts[1]);
			Class type = f.getType();
			if (type == int.class) {
				int val = f.getInt(c);
				obj = String.valueOf(val);
			}
			if (type == float.class) {
				float val = f.getFloat(c);
				obj = String.valueOf(val);
			}
			if (type == double.class) {
				double val = f.getDouble(c);
				obj = String.valueOf(val);
			}
			if (type == long.class) {
				long val = f.getLong(c);
				obj = String.valueOf(val);
			}
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Class \""+cl+"\" does not exist!");
		}
		catch (NoSuchFieldException e) {
			e.printStackTrace();
			throw new RuntimeException("Class "+cl+" does not contain field \""+parts[1]+"\"!");
		}
		catch (SecurityException e) {
			e.printStackTrace();
			throw new RuntimeException("Class "+cl+"'s field "+parts[1]+" threw security exception!");
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new RuntimeException("Class "+cl+"'s field "+parts[1]+" threw illegal argument exception!");
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException("Class "+cl+"'s field "+parts[1]+" threw illegal access exception!");
		}
		return obj;
	}

	private static boolean isReference(String sg) {
		if (sg.startsWith(NUMBER_VARIABLE_CODE))
			return true;
		if (sg.startsWith(NUMBER_METHOD_CODE))
			return true;
		if (sg.startsWith(STRING_FORMAT_CODE))
			return true;
		if (sg.startsWith(ENUM_FUNCTION_CODE))
			return true;
		return false;
	}

	public static String splitCamelCase(String s) {
		s = s.replaceAll(" ", "");
		String cap = String.format("%s|%s|%s","(?<=[A-Z])(?=[A-Z][a-z])","(?<=[^A-Z])(?=[A-Z])","(?<=[A-Za-z])(?=[^A-Za-z])"	);
		String spl = s.replaceAll(cap, " ");
		return spl;
	}

	public static String stripSpaces(String s) {
		return s.replaceAll("\\s","");
	}

	public static String capFirstChar(String s) {
		return s.toUpperCase().substring(0, 1)+s.toLowerCase().substring(1);
	}

	public static String subtractFrom(String src, String p) {
		int len = p.length();
		return src.substring(len);
	}

	public static List<String> splitStringByLength(String str, int len) {
		return ReikaStringWrapper.listFormattedStringToWidth(str, len);
		/*
		List<String> li = new ArrayList();
		if (str == null || str.isEmpty())
			return li;
		if (str.length() <= len) {
			li.add(str);
			return li;
		}
		//char[] data = str.toCharArray();
		StringTokenizer tok = new StringTokenizer(str, "\\. | , | | -", true);

		String[] parts = new String[tok.countTokens()];

		for (int i = 0; i < parts.length; i++) {
			parts[i] = tok.nextToken();
		}

		//String[] parts = str.split("\\. | , | | -");
		//ReikaJavaLibrary.pConsole(Arrays.toString(parts));

		int space = len;
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < parts.length; i++) {
			String s = parts[i];
			String ms = s.replaceAll("\u00a7", "");//.replaceAll(" ", "");
			int reduct = ms.length()-(s.length()-ms.length());
			//ReikaJavaLibrary.pConsole(s+" : "+(len-space)+"+"+ms.length()+"/"+len);
			if (s.length() <= space) {
				space -= reduct;
				sb.append(s);
			}
			else {
				String line = sb.toString();
				if (line.length() > len) {
					String s1 = line.substring(0, len);
					li.add(s1);
					String s2 = line.substring(len);
					li.add(s2);
				}
				else
					li.add(line);
				sb.delete(0, sb.length());
				if (!" ".equals(s)) {
					sb.append(s);
					space = len-reduct;
				}
				else
					space = len;
			}
		}
		li.add(sb.toString());

		/*
		//ReikaJavaLibrary.pConsole(Arrays.toString(data));
		StringBuilder line = new StringBuilder();
		StringBuilder word = new StringBuilder();
		String formatter = "";
		for (int i = 0; i < data.length; i++) {
			char c = data[i];
			if (c == 0) {

			}
			if ("\u00a7".charAt(0) == c) {
				formatter = String.valueOf(c)+String.valueOf(data[i+1]);
				if ("\u00a7".charAt(0) == data[i+1])
					data[i+1] = 0;
			}
			if (c == ' ') {
				if (data[i+1] == ' ')
					data[i+1 ] = 0;
			}
			if (isSplittingChar(c)) {
				word.append(c);
				int a = Character.isWhitespace(c) ? 1 : 0;
				if ((line.length() + word.length()-a-formatter.length()) > len) {
					li.add(line.toString());
					line.delete(0, line.length());
				}
				line.append(formatter);
				line.append(word);
				word.delete(0, word.length());
			}
			else {
				// add it to the word and move on
				word.append(c);
			}
		}

		if (word.length() > 0) {
			if ((line.length() + word.length()) > len) {
				li.add(line.toString());
				line.delete(0, line.length());
			}

			line.append(word);
		}

		// handle extra line
		if (line.length() > 0) {
			li.add(line.toString());
		}*//*

		return li;*/
	}

	public static boolean isWordSplittingChar(char c) {
		return !wordChars.contains(c);
	}

	public static String getFirstWord(String s) {
		return s.split(" ")[0];
	}

	public static boolean containsWord(String s, String word) {
		if (s == null || s.length() < word.length())
			return false;
		//return s.matches(".*\\b" + Pattern.quote(word) + "\\b.*"); //does not treat '
		int len = word.length();
		if (s.regionMatches(0, word, 0, len)) {
			if (s.length() == word.length() || isWordSplittingChar(s.charAt(len)))
				return true;
		}
		for (int i = 0; i < s.length()-len; i++) {
			if (isWordSplittingChar(s.charAt(i))) {
				if (s.regionMatches(i+1, word, 0, len)) {
					if (word.length()+i+1 == s.length() || isWordSplittingChar(s.charAt(i+1+len)))
						return true;
				}
			}
		}
		return false;
	}

	public static String getLongestString(String[] sgs) {
		int idx = 0;
		for (int i = 0; i < sgs.length; i++) {
			String sg = sgs[i];
			if (sg.length() > sgs[idx].length())
				idx = i;
		}
		return sgs[idx];
	}

	public static String getDelimited(String delimiter, ArrayList<Object> args) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < args.size(); i++) {
			sb.append(args.get(i).toString());
			if (i < args.size()-1)
				sb.append(delimiter);
		}
		return sb.toString();
	}

	public static String getDelimited(String delimiter, Object... args) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < args.length; i++) {
			sb.append(args[i].toString());
			if (i < args.length-1)
				sb.append(delimiter);
		}
		return sb.toString();
	}

	public static String getNOf(String s, int n) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < n; i++) {
			sb.append(s);
		}
		return sb.toString();
	}

	public static String getAutoDecimal(double n) {
		String s = Double.toString(n);
		/*
		String[] parts = s.split("\\.");
		while (parts[1].charAt(0) == '0') {
			parts[1] = parts[1].substring(1);
		}
		while (parts[1].charAt(parts[1].length()-1) == '0') {
			parts[1] = parts[1].substring(0, parts[1].length()-1);
		}
		return parts[0]+"."+parts[1];
		 */
		while (s.indexOf('.') != -1 && (s.charAt(s.length()-1) == '0' || s.charAt(s.length()-1) == '.'))
			s = s.substring(0, s.length()-1);
		return s.isEmpty() ? "0" : s;
	}

	public static ArrayList<String> splitStringByNewlines(String s) {
		ArrayList<String> li = new ArrayList();
		String[] parts = s.split("\\n");
		for (int i = 0; i < parts.length; i++) {
			li.add(parts[i]);
		}
		return li;
	}

	public static char intToAlphaChar(int n) {
		return (char)('a'+n);
	}

	public static String clipStringBefore(String s, String clip) {
		int idx = s.indexOf(clip);
		if (idx == -1)
			idx = s.length();
		return s.substring(0, idx);
	}

	public static boolean isWholeWord(String s, String lv) {
		int idx1 = lv.indexOf(s);
		int idx2 = lv.indexOf(s)+s.length();
		return (idx1 == 0 || isWordSeparator(lv.charAt(idx1-1))) && (idx2 == lv.length() || isWordSeparator(lv.charAt(idx2)));
	}

	public static boolean isWordSeparator(char c) {
		return c == ' ' || c == '.' || c == '!' || c == '?' || c == ',' || c == ';';
	}

	public static String padToLength(String s, int len, String pad) {
		while (s.length() < len)
			s = s+pad;
		return s;
	}

	public static String capitalizeWords(String s) {
		s = capFirstChar(s);
		return WordUtils.capitalize(s);
	}

	public static String parseRomanRumeral(int val) {
		int digit = romanNumerals.floorKey(val);
		if (val == digit)
			return romanNumerals.get(val);
		return romanNumerals.get(digit) + parseRomanRumeral(val-digit);
	}

	public static String stripToAlphabet(String s, char repl) {
		char[] arr = s.toCharArray();
		for (int i = 0; i < arr.length; i++) {
			char c = arr[i];
			if (!Character.isLetter(c)) {
				arr[i] = repl;
			}
		}
		return new String(arr);
	}

	public static String stripToAlphaNumeric(String s, char repl) {
		char[] arr = s.toCharArray();
		for (int i = 0; i < arr.length; i++) {
			char c = arr[i];
			if (!Character.isLetterOrDigit(c)) {
				arr[i] = repl;
			}
		}
		return new String(arr);
	}

	public static String clearDoubledSpaces(String s) {
		while (s.contains("  ")) {
			s = s.replace("  ", " ");
		}
		return s;
	}

	public static String shuffle(String s) {
		char[] arr = s.toCharArray();
		ReikaArrayHelper.shuffleArray(arr);
		return new String(arr);
	}

	public static boolean isValidVariableName(String s) {
		if (!Character.isLetter(s.charAt(0)))
			return false;
		char[] arr = s.toCharArray();
		for (int i = 0; i < arr.length; i++) {
			char c = arr[i];
			if (c != '_' && !Character.isLetterOrDigit(c)) {
				return false;
			}
		}
		return true;
	}
}
