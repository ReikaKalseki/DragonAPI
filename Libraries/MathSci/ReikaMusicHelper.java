/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.MathSci;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Reika.DragonAPI.IO.ReikaMIDIReader;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;


public class ReikaMusicHelper {

	public static enum Note {
		C("C"),
		CSHARP("C#"),
		D("D"),
		EFLAT("Eb"),
		E("E"),
		F("F"),
		FSHARP("F#"),
		G("G"),
		GSHARP("G#"),
		A("A"),
		BFLAT("Bb"),
		B("B");

		public final String name;

		private static final ArrayList<Note> pureNotes = new ArrayList();

		private static final Note[] notes = values();

		private Note(String s) {
			name = s;
		}

		public boolean isPure() {
			return this.name().length() == 1;//pureNotes.contains(this); do not use this, causes empty list due to list using this to populate
		}

		public Note getFlat() {
			return this == C ? B : notes[this.ordinal()-1];
		}

		public Note getSharp() {
			return this == B ? C : notes[this.ordinal()+1];
		}

		@Override
		public String toString() {
			return name;
		}

		static {
			for (int i = 0; i < notes.length; i++) {
				Note n = notes[i];
				if (n.isPure()) {
					pureNotes.add(n);
				}
			}
		}
	}

	public static Note getNote(int note) {
		return Note.notes[(note+6)%Note.notes.length];
	}

	public static String getNoteName(int note) {
		return getNote(note).name;
	}

	public static boolean isNoteSharpOrFlat(int note) {
		return !getNote(note).isPure();
	}

	public static boolean isNoteSharp(int note) {
		return getNoteName(note).endsWith("#");
	}

	public static boolean isNoteFlat(int note) {
		return getNoteName(note).endsWith("b");
	}

	public static enum MusicKey {
		C2(65),
		Cs2(69),
		D2(73),
		Eb2(78),
		E2(82),
		F2(87),
		Fs2(92),
		G2(98),
		Ab2(104),
		A2(110),
		Bb2(117),
		B2(123),
		C3(131),
		Cs3(139),
		D3(147),
		Eb3(156),
		E3(165),
		F3(175),
		Fs3(185),
		G3(196),
		Ab3(208),
		A3(220),
		Bb3(233),
		B3(247),
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

		@Override
		public String toString() {
			return this.displayName()+" @ "+pitch+" Hz";
		}

		public Note getNote() {
			return Note.notes[this.ordinal()%Note.notes.length];
		}

		public static Collection<MusicKey> getAllOf(Note n) {
			ArrayList<MusicKey> li = new ArrayList();
			for (int i = n.ordinal(); i < list.length; i += Note.notes.length) {
				li.add(list[i]);
			}
			return li;
		}

		public static MusicKey getKeyFromMIDI(int key) {
			int index = key-ReikaMIDIReader.MIDI_C5+C5.ordinal();
			return index >= 0 && index < list.length ? list[index] : null;
		}

		public static MusicKey getByIndex(int key) {
			return key >= 0 && key < list.length ? list[key] : null;
		}

		public String displayName() {
			return this.name().replaceAll("s", "#");
		}
	}

	public static enum KeySignature {
		C(Note.C),
		F(Note.F, Note.B),
		BFLAT(Note.B, Note.B, Note.E),
		EFLAT(Note.E, Note.B, Note.E, Note.A),
		AFLAT(Note.A, Note.B, Note.E, Note.A, Note.D),
		DFLAT(Note.D, Note.B, Note.E, Note.A, Note.D, Note.G),
		GFLAT(Note.G, Note.B, Note.E, Note.A, Note.D, Note.G, Note.C),
		CFLAT(Note.C, Note.B, Note.E, Note.A, Note.D, Note.G, Note.C, Note.F),
		G(Note.G, Note.F),
		D(Note.D, Note.F, Note.C),
		A(Note.A, Note.F, Note.C, Note.G),
		E(Note.E, Note.F, Note.C, Note.G, Note.D),
		B(Note.B, Note.F, Note.C, Note.G, Note.D, Note.A),
		FSHARP(Note.F, Note.F, Note.C, Note.G, Note.D, Note.A, Note.E),
		CSHARP(Note.C, Note.F, Note.C, Note.G, Note.D, Note.A, Note.E, Note.B);

		private final HashSet<Note> sharps = new HashSet();
		private final HashSet<Note> flats = new HashSet();

		private final HashSet<Note> notes = new HashSet();

		private final ArrayList<Note> scale = new ArrayList();
		private final ArrayList<Note> minor = new ArrayList();

		public final Note tonic;

		private static final EnumMap<Note, KeySignature> keyMap = new EnumMap(Note.class);
		private static final EnumMap<Note, KeySignature> minorKeyMap = new EnumMap(Note.class);

		public static final KeySignature[] keys = values();

		private KeySignature(Note ton, Note... key) {
			for (int i = 0; i < key.length; i++) {
				Note n = key[i];
				if (this.isSharp()) {
					sharps.add(n);
				}
				else if (this.isFlat()) {
					flats.add(n);
				}
			}

			Note ctr = ton;

			if (sharps.contains(ton))
				ton = ton.getSharp();
			else if (flats.contains(ton))
				ton = ton.getFlat();
			tonic = ton;

			ArrayList<Note> li = new ArrayList(Note.pureNotes);
			ReikaJavaLibrary.cycleList(li, li.size()-li.indexOf(ctr));
			for (int i = 0; i < li.size(); i++) {
				Note n = li.get(i);
				if (sharps.contains(n)) {
					li.set(i, n.getSharp());
				}
				else if (flats.contains(n)) {
					li.set(i, n.getFlat());
				}
			}
			notes.addAll(li);
			scale.addAll(li);
			minor.addAll(li);
			ReikaJavaLibrary.cycleList(minor, 2);
		}

		public boolean isFlat() {
			return this.ordinal() != 0 && this.ordinal() < 8; //G
		}

		public boolean isSharp() {
			return this.ordinal() >= 8; //G
		}

		public Set<Note> getFlats() {
			return Collections.unmodifiableSet(flats);
		}

		public Set<Note> getSharps() {
			return Collections.unmodifiableSet(sharps);
		}

		public List<Note> getScale() {
			return Collections.unmodifiableList(scale);
		}

		public List<Note> getRelativeMinor() {
			return Collections.unmodifiableList(minor);
		}

		@Override
		public String toString() {
			return "Scale '"+this.name()+"' ("+tonic.name+"): M+"+scale+" & m-"+minor;
		}

		public static KeySignature getByTonic(MusicKey key) {
			return getByTonic(key.getNote());
		}

		public static KeySignature getByTonic(Note key) {
			return keyMap.get(key);
		}

		public static KeySignature getByMinorTonic(MusicKey key) {
			return getByMinorTonic(key.getNote());
		}

		public static KeySignature getByMinorTonic(Note key) {
			return minorKeyMap.get(key);
		}

		static {
			for (int i = 0; i < keys.length; i++) {
				KeySignature ks = KeySignature.keys[i];
				keyMap.put(ks.tonic, ks);
				minorKeyMap.put(ks.minor.get(0), ks);
			}
		}

		public boolean isNoteValid(Note n) {
			return notes.contains(n);
		}

		public static KeySignature findSignature(ArrayList<MusicKey> li) {
			HashSet<Note> notes = new HashSet();
			for (MusicKey key : li) {
				notes.add(key.getNote());
			}
			for (int i = 0; i < keys.length; i++) {
				KeySignature ks = keys[i];
				boolean flag = true;
				for (Note n : notes) {
					if (!ks.isNoteValid(n)) {
						flag = false;
						break;
					}
				}
				if (flag)
					return ks;
			}
			return null;
		}
	}

	public static enum ChordType {
		OCTAVE(0, 12),
		POWER(0, 7, 12),
		MAJOR(0, 4, 7, 12),
		MINOR(0, 3, 7, 12),
		AUGMENTED(0, 4, 8, 12),
		DIMINISHED(0, 3, 6, 12);

		private final ArrayList<Integer> notes;

		private ChordType(int... n) {
			notes = ReikaJavaLibrary.makeIntListFromArray(n);
		}

		public ArrayList<MusicKey> getChord(MusicKey tonic) {
			ArrayList<MusicKey> li = new ArrayList();
			for (int note : notes) {
				li.add(tonic.getInterval(note));
			}
			return li;
		}

		@Override
		public String toString() {
			return this.name()+" Chord: "+notes;
		}
	}
}
