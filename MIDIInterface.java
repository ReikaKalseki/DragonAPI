package Reika.DragonAPI;

import javax.sound.midi.Sequence;

public final class MIDIInterface {

	private final Sequence midi;

	public MIDIInterface(Class root, String path, String back) {
		midi = ReikaMIDIReader.getMIDIFromFile(root, path, back);
	}

	/** Returns the note at the given track and time. Args: Track, Time */
	public int getNoteAtTrackAndTime(int track, int time) {/*
		int a = ReikaMIDIReader.readMIDI(midi, track, time, 0);
		if (a != 0 && a != -1)
		;//ReikaJavaLibrary.pConsole(time+" @ "+a);
		return a;*/
		//return ReikaMIDIReader.getMidiNoteAtChannelAndTime(midi, time, track);
		return 0;
	}

	public void debug() {
		ReikaMIDIReader.debugMIDI(midi);
	}

	/** Returns the voice at the given track and time. Args: Track, Time. Return:
	 * 0 = silence/invalid, 1 = piano, 2 = bass, 3 = pling, 4 = bassdrum, 5 = snare, 6 = click */
	public int getVoiceAtTrackAndTime(int track, int time) {
		int voice;
		voice = ReikaMIDIReader.readMIDI(midi, track, time, 1);
		if (voice == -1)
			return 0;
		switch(voice) {
		case 0:
			return 1;
		case 32: //Acoustic Bass
			return 2;
		case 18: //Rock Organ
			return 3;/*
		case 0:
			return 4;
		case 0:
			return 5;
		case 0:
			return 6;*/
		default:
			return 0;
		}
	}

	public int getVolumeAtTrackAndTime(int track, int time) {
		return ReikaMIDIReader.readMIDI(midi, track, time, 2);
	}

	public int getLength() {
		int maxl = -1;
		for (int i = 0; i < 16; i++) {
			int l = ReikaMIDIReader.getTrackLength(midi, i);
			if (l > maxl)
				maxl = l;
		}
		return maxl;
	}

	public boolean isPlayingAt(int track, int time) {
		return ReikaMIDIReader.hasNoteOn(midi, track, time);
	}

	public int[][][] fill() {
		return ReikaMIDIReader.readMIDIFile(midi);
	}

}
