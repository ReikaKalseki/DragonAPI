/*******************************************************************************
 * @author Reika
 * 
 * This code is the property of and owned and copyrighted by Reika.
 * This code is provided under a modified visible-source license that is as follows:
 * 
 * Any and all users are permitted to use the source for educational purposes, or to create other mods that call
 * parts of this code and use DragonAPI as a dependency.
 * 
 * Unless given explicit written permission - electronic writing is acceptable - no user may redistribute this
 * source code nor any derivative works. These pre-approved works must prominently contain this copyright notice.
 * 
 * Additionally, no attempt may be made to achieve monetary gain from this code by anyone except the original author.
 * In the case of pre-approved derivative works, any monetary gains made will be shared between the original author
 * and the other developer(s), proportional to the ratio of derived to original code.
 * 
 * Finally, any and all displays, duplicates or derivatives of this code must be prominently marked as such, and must
 * contain attribution to the original author, including a link to the original source. Any attempts to claim credit
 * for this code will be treated as intentional theft.
 * 
 * Due to the Mojang and Minecraft Mod Terms of Service and Licensing Restrictions, compiled versions of this code
 * must be provided for free. However, with the exception of pre-approved derivative works, only the original author
 * may distribute compiled binary versions of this code.
 * 
 * Failure to comply with these restrictions is a violation of copyright law and will be dealt with accordingly.
 ******************************************************************************/
package Reika.DragonAPI;

import javax.sound.midi.Sequence;

import Reika.DragonAPI.IO.ReikaMIDIReader;

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

	public int getLength() {
		return ReikaMIDIReader.getMidiLength(midi);
	}

	public int[][][] fill() {
		return ReikaMIDIReader.readMIDIFile(midi);
	}

}
