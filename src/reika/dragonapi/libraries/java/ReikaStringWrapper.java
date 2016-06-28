/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.libraries.java;

import java.util.Arrays;
import java.util.List;

public class ReikaStringWrapper {

	public static int FONT_HEIGHT = 9;

	/**
	 * Returns the width of this string. Equivalent of FontMetrics.stringWidth(String s).
	 */
	public static int getStringWidth(String par1Str)
	{
		if (par1Str == null)
		{
			return 0;
		}
		else
		{
			int i = 0;
			boolean flag = false;

			for (int j = 0; j < par1Str.length(); ++j)
			{
				char c0 = par1Str.charAt(j);
				int k = getCharWidth(c0);

				if (k < 0 && j < par1Str.length() - 1)
				{
					++j;
					c0 = par1Str.charAt(j);

					if (c0 != 108 && c0 != 76)
					{
						if (c0 == 114 || c0 == 82)
						{
							flag = false;
						}
					}
					else
					{
						flag = true;
					}

					k = 0;
				}

				i += k;

				if (flag)
				{
					++i;
				}
			}

			return i;
		}
	}

	/**
	 * Returns the width of this character as rendered.
	 */
	public static int getCharWidth(char par1)
	{
		/*
		if (par1 == 167)
		{
			return -1;
		}
		else if (par1 == 32)
		{
			return 4;
		}
		else
		{
			int i = ChatAllowedCharacters.allowedCharacters.indexOf(par1);

			if (i >= 0)
			{
				return 1;
			}
			else if (1 != 0)
			{
				int j = 1 >>> 4;
			int k = 1 & 15;

			if (k > 7)
			{
				k = 15;
				j = 0;
			}

			++k;
			return (k - j) / 2 + 1;
			}
			else
			{
				return 0;
			}
		}*/
		return 1;
	}

	/**
	 * Trims a string to fit a specified Width.
	 */
	public static String trimStringToWidth(String par1Str, int par2)
	{
		return trimStringToWidth(par1Str, par2, false);
	}

	/**
	 * Trims a string to a specified width, and will reverse it if par3 is set.
	 */
	public static String trimStringToWidth(String par1Str, int par2, boolean par3)
	{
		StringBuilder stringbuilder = new StringBuilder();
		int j = 0;
		int k = par3 ? par1Str.length() - 1 : 0;
		int l = par3 ? -1 : 1;
		boolean flag1 = false;
		boolean flag2 = false;

		for (int i1 = k; i1 >= 0 && i1 < par1Str.length() && j < par2; i1 += l)
		{
			char c0 = par1Str.charAt(i1);
			int j1 = getCharWidth(c0);

			if (flag1)
			{
				flag1 = false;

				if (c0 != 108 && c0 != 76)
				{
					if (c0 == 114 || c0 == 82)
					{
						flag2 = false;
					}
				}
				else
				{
					flag2 = true;
				}
			}
			else if (j1 < 0)
			{
				flag1 = true;
			}
			else
			{
				j += j1;

				if (flag2)
				{
					++j;
				}
			}

			if (j > par2)
			{
				break;
			}

			if (par3)
			{
				stringbuilder.insert(0, c0);
			}
			else
			{
				stringbuilder.append(c0);
			}
		}

		return stringbuilder.toString();
	}

	/**
	 * Remove all newline characters from the end of the string
	 */
	private static String trimStringNewline(String par1Str)
	{
		while (par1Str != null && par1Str.endsWith("\n"))
		{
			par1Str = par1Str.substring(0, par1Str.length() - 1);
		}

		return par1Str;
	}

	/**
	 * Returns the width of the wordwrapped String (maximum length is parameter k)
	 */
	public static int splitStringWidth(String par1Str, int par2)
	{
		return FONT_HEIGHT * listFormattedStringToWidth(par1Str, par2).size();
	}

	/**
	 * Breaks a string into a list of pieces that will fit a specified width.
	 */
	public static List listFormattedStringToWidth(String par1Str, int par2)
	{
		return Arrays.asList(wrapFormattedStringToWidth(par1Str, par2).split("\n"));
	}

	/**
	 * Inserts newline and formatting into a string to wrap it within the specified width.
	 */
	static String wrapFormattedStringToWidth(String par1Str, int par2)
	{

		int j = sizeStringToWidth(par1Str, par2);

		if (par1Str.length() <= j)
		{
			return par1Str;
		}
		else
		{
			String s1 = par1Str.substring(0, j);
			char c0 = par1Str.charAt(j);
			boolean flag = c0 == 32 || c0 == 10;
			String s2 = getFormatFromString(s1) + par1Str.substring(j + (flag ? 1 : 0));
			return s1 + "\n" + wrapFormattedStringToWidth(s2, par2);
		}
	}

	/**
	 * Determines how many characters from the string will fit into the specified width.
	 */
	private static int sizeStringToWidth(String par1Str, int par2)
	{
		int j = par1Str.length();
		int k = 0;
		int l = 0;
		int i1 = -1;

		for (boolean flag = false; l < j; ++l)
		{
			char c0 = par1Str.charAt(l);

			switch (c0)
			{
			case 10:
				--l;
				break;
			case 167:
				if (l < j - 1)
				{
					++l;
					char c1 = par1Str.charAt(l);

					if (c1 != 108 && c1 != 76)
					{
						if (c1 == 114 || c1 == 82 || isFormatColor(c1))
						{
							flag = false;
						}
					}
					else
					{
						flag = true;
					}
				}

				break;
			case 32:
				i1 = l;
			default:
				k += getCharWidth(c0);

				if (flag)
				{
					++k;
				}
			}

			if (c0 == 10)
			{
				++l;
				i1 = l;
				break;
			}

			if (k > par2)
			{
				break;
			}
		}

		return l != j && i1 != -1 && i1 < l ? i1 : l;
	}

	/**
	 * Checks if the char code is a hexadecimal character, used to set colour.
	 */
	private static boolean isFormatColor(char par0)
	{
		return par0 >= 48 && par0 <= 57 || par0 >= 97 && par0 <= 102 || par0 >= 65 && par0 <= 70;
	}

	/**
	 * Checks if the char code is O-K...lLrRk-o... used to set special formatting.
	 */
	private static boolean isFormatSpecial(char par0)
	{
		return par0 >= 107 && par0 <= 111 || par0 >= 75 && par0 <= 79 || par0 == 114 || par0 == 82;
	}

	/**
	 * Digests a string for nonprinting formatting characters then returns a string containing only that formatting.
	 */
	private static String getFormatFromString(String par0Str)
	{
		String s1 = "";
		int i = -1;
		int j = par0Str.length();

		while ((i = par0Str.indexOf(167, i + 1)) != -1)
		{
			if (i < j - 1)
			{
				char c0 = par0Str.charAt(i + 1);

				if (isFormatColor(c0))
				{
					s1 = "\u00a7" + c0;
				}
				else if (isFormatSpecial(c0))
				{
					s1 = s1 + "\u00a7" + c0;
				}
			}
		}

		return s1;
	}
}
