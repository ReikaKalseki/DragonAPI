/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.MathSci;


public class ReikaMusicHelper {

	private static final String[] notes = {"C", "C#", "D", "Eb", "E", "F", "F#", "G", "G#", "A", "Bb", "B"};
	private static final String[] pureNotes = {"C", "D", "E", "F", "G", "A", "B"};

	public static String getNoteName(int note) {
		return notes[(note+6)%notes.length];
	}

	public static boolean isNoteSharpOrFlat(int note) {
		return getNoteName(note).length() == 1;
	}

	public static boolean isNoteSharp(int note) {
		return getNoteName(note).endsWith("#");
	}

	public static boolean isNoteFlat(int note) {
		return getNoteName(note).endsWith("b");
	}
}