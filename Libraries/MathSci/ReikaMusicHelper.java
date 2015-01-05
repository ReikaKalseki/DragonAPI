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

	public static enum MusicKey {
		C3(131),
		Cs3(139),
		D3(147),
		Eb3(156),
		F3(175),
		Fs3(185),
		G3(196),
		Ab3(208),
		A3(220),
		Bb3(233),
		B(247),
		C4(262),
		Cs4(277),
		D4(294),
		Eb4(311),
		E4(330),
		F4(349),
		Fs4(370),
		G4(392),
		Ab4(415),
		A4(440),
		Bb4(466),
		B4(494),
		C5(523),
		Cs5(554),
		D5(587),
		Eb5(622),
		E5(659),
		F5(698),
		Fs5(740),
		G5(784),
		Ab5(830),
		A5(880),
		Bb5(932),
		B5(988),
		C6(1046),
		Cs6(1109),
		D6(1175),
		Eb6(1245),
		E6(1319),
		F6(1397),
		Fs6(1480),
		G6(1568),
		Ab6(1661),
		A6(1760),
		Bb6(1865),
		B6(1976),
		C7(2093),
		;

		public final int pitch;

		private static final MusicKey[] list = values();

		private MusicKey(int f) {
			pitch = f;
		}

		public MusicKey getMinorThird() {
			return this.getInterval(3);
		}

		public MusicKey getMajorThird() {
			return this.getInterval(4);
		}

		public MusicKey getFourth() {
			return this.getInterval(5);
		}

		public MusicKey getFifth() {
			return this.getInterval(7);
		}

		public MusicKey getOctave() {
			return this.getInterval(12);
		}

		public MusicKey getInterval(int n) {
			int o = this.ordinal()+n;
			return o >= 0 && o < list.length ? list[o] : this;
		}

		public double getRatio(MusicKey k) {
			return this.getRatio(k, this);
		}

		public static double getRatio(MusicKey k1, MusicKey k2) {
			return (double)k2.pitch/k1.pitch;
		}
	}
}
