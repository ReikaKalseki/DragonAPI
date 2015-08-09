/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.Java;

import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;


public class ReikaEncryptionHelper {

	public static <O> boolean isEncryptedMatch(O s, Encrypter<O> e, O... parts) {
		return s.equals(e.encryptInto(parts));
	}

	public static interface Encrypter<O> {

		public O encryptInto(O... parts);

	}

	public static final class AdditiveEncrypter implements Encrypter<String> {

		@Override
		public String encryptInto(String... parts) {
			char[] chars = new char[ReikaStringParser.getLongestString(parts).length()];
			for (int i = 0; i < parts.length; i++) {
				String s = parts[i];
				for (int k = 0; k < s.length(); k++) {
					int c = s.charAt(k);
					chars[k] = (char)ReikaMathLibrary.addAndRollover(chars[k], c, 0x0020, 0x007E);
				}
			}
			return new String(chars);
		}

	}

	public static final class SpliceEncrypter implements Encrypter<String> {

		@Override
		public String encryptInto(String... parts) {
			StringBuilder sb = new StringBuilder();
			boolean action;
			int idx = 0;
			do {
				action = false;

				for (int i = 0; i < parts.length; i++) {
					if (idx < parts.length) {
						sb.append(parts[i].charAt(idx));
						action = true;
					}
				}

				idx++;

			} while(action);

			return sb.toString();
		}

	}

	public static final class MorphEncrypter implements Encrypter<String> {

		@Override
		public String encryptInto(String... parts) {
			char[] chars = new char[ReikaStringParser.getLongestString(parts).length()];

			System.arraycopy(parts[0].toCharArray(), 0, chars, 0, parts[0].length());
			for (int i = 1; i < parts.length; i++) {
				String s = parts[i];
				for (int k = 0; k < s.length(); k++) {
					int c = s.charAt(k);
					int d = c-chars[k];
					if (d != 0) {
						chars[k] = (char)ReikaMathLibrary.addAndRollover(chars[k], d/2, 0x0020, 0x007E);
					}
				}
			}

			return new String(chars);
		}

	}

	public static final class ShiftEncrypter implements Encrypter<String> {

		@Override
		public String encryptInto(String... parts) {
			char[] chars = new char[ReikaStringParser.getLongestString(parts).length()];
			for (int i = 0; i < parts.length; i++) {
				String s = parts[i];
				for (int k = 0; k < s.length(); k++) {
					int c = s.charAt(k);
					chars[k] = (char)ReikaMathLibrary.addAndRollover(chars[k], c, 0x0020, 0x007E);
					chars[k] = (char)ReikaMathLibrary.addAndRollover(chars[k], s.length(), 0x0020, 0x007E);
				}
			}
			return new String(chars);
		}

	}

}
