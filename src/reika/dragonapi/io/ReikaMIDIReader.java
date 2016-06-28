/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.io;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import reika.dragonapi.DragonAPICore;
import reika.dragonapi.instantiable.MusicScore;
import reika.dragonapi.libraries.mathsci.ReikaMusicHelper.MusicKey;

public final class ReikaMIDIReader {

	private ReikaMIDIReader() {throw new RuntimeException("The class "+this.getClass()+" cannot be instantiated!");}

	public static final int NOTE_ON = 0x90;
	public static final int NOTE_OFF = 0x80;
	public static final int INSTRU_CHANGE = 0xC0;
	public static final int TEMPO = 0x51;
	public static final String[] NOTE_NAMES = {"F#", "G", "G#", "A", "Bb", "B", "C", "C#", "D", "Eb", "E", "F"};
	public static final int MIDI_C5 = 60;

	public static Sequence getMIDIFromFile(Class root, String path, String back) {
		DragonAPICore.log("Reading MIDI at "+path+" with backup at "+back);
		InputStream input = root.getResourceAsStream(path);
		InputStream backup = root.getResourceAsStream(back);

		if (input == null && backup == null) {
			DragonAPICore.logError("Neither main file at "+path+" nor backup at "+back+" found. Aborting.");
			return null;
		}

		Sequencer seq = null;

		try {
			seq = MidiSystem.getSequencer();
		}
		catch (MidiUnavailableException e1) {
			//e1.printStackTrace();
			DragonAPICore.logError("MIDI system unavailable.");
			return null;
		}

		try {
			seq.setSequence(input);
		}
		catch (IOException e1) {
			//e1.printStackTrace();
			if (backup != null) {
				DragonAPICore.logError("MIDI File at "+path+" unreadable. Switching to backup.");
				try {
					seq.setSequence(backup);
				}
				catch (IOException e) {
					//e.printStackTrace();
					DragonAPICore.logError("Backup MIDI File at "+back+" unreadable.");
				}
				catch (InvalidMidiDataException e) {
					//e.printStackTrace();
					DragonAPICore.logError("Backup MIDI File at "+back+" invalid.");
				}
			}
			else {
				DragonAPICore.logError("MIDI File at "+path+" unreadable, and no backup was available.");
			}
		}
		catch (InvalidMidiDataException e1) {
			//e1.printStackTrace();
			if (backup != null) {
				DragonAPICore.log("MIDI File at "+path+" invalid. Switching to backup.");
				try {
					seq.setSequence(backup);
				}
				catch (IOException e) {
					//e.printStackTrace();
					DragonAPICore.logError("Backup MIDI File at "+back+" unreadable.");
				}
				catch (InvalidMidiDataException e) {
					//e.printStackTrace();
					DragonAPICore.logError("Backup MIDI File at "+back+" invalid.");
				}
			}
			else {
				DragonAPICore.logError("MIDI File at "+path+" unreadable, and no backup was available.");
			}
		}
		return seq.getSequence();
	}

	/** Reads a parameter from a MIDI Sequence. Args: Sequence, channel (1-16), time, task (0 = note, 1 = voice, 2 = volume) *//*
	public static int readMIDI(Sequence seq, int channel, int time, int task) {
		int voice = -1;
		int note = -1;
		int vol = -1;
		/*1 4/4 bar = 1920 ticks
		 1 MC tick = 1 32nd; 2 MC tick = 1 16th; 4 MC tick = 1 8th; 8 MC tick = 1 qtr; 16 MC = 1 half; 32 MC tick = 1 whole = 1920
		32 = 1920
		1 MC tick = 60 MIDI ticks
	 *//*
		time *= 60;
		time += 120;
		Track[] tr = seq.getTracks();
		if (channel >= tr.length)
			return 0;
        //for (int k = 0; k < tr.length; k++) {
        	int t = ReikaMathLibrary.extrema(time, tr[channel].size(), "min");
        	for (int i = 0; i < t; i++) {
        		MidiEvent event = tr[channel].get(i);
        		MidiMessage msg = event.getMessage();
        		byte[] data = msg.getMessage();
        		//DragonAPICore.log(data[0]+256);
        		if (data[0]/16 == INSTRU_CHANGE) {
        			voice = data[1]%16;
        		}
        	}
        	int step = -1;
        	for (int i = 0; i < tr[channel].size(); i++) {
        		if (tr[channel].get(i).getTick() == time)
        			step = i;
        	}
        	if (step != -1) {
	        	MidiEvent event = tr[channel].get(step);
	            MidiMessage msg = event.getMessage();
	            if (msg instanceof ShortMessage) {
	                ShortMessage sm = (ShortMessage) msg;
	                if (sm.getCommand() == NOTE_ON) {
	                	//DragonAPICore.log(channel+" @  "+tr[channel].get(step).getTick()+"  for  "+time);
	                    int key = sm.getData1();
	                    int octave = (key / 12)-1;
	                    while (octave > 5)
	                    	octave -= 5;
	                    note = (key%12);
	                    if (octave == 5 && note != 0)
	                    	octave = 0;
	                    note += 12*octave;
	                    vol = sm.getData2();
	                }
	            }
        	}
        if (!(voice == -1 && note == -1))
        ;//DragonAPICore.log(note);
        //DragonAPICore.log("Channel "+channel+" has "+voice+" playing "+note+" @ "+tr[channel].get(step).getTick()/60);
        switch (task) {
        case 0:
        	return note;
        case 1:
        	return voice;
        case 2:
        	return vol;
        default:
        	return 0;
        }
    }

	public static int getTrackLength(Sequence seq, int channel) {
		if (seq == null)
			return 0;
		Track[] tr = seq.getTracks();
		if (channel >= tr.length)
			return 0;
		return 1+(int)(tr[channel].get(tr[channel].size()-1).getTick()/60);
    }

	public static boolean hasNoteOn(Sequence seq, int channel, int time) {
		Track[] tr = seq.getTracks();
		if (channel >= tr.length)
			return false;
		time *= 60;
		time += 120;
    	int step = -1;
    	for (int i = 0; i < tr[channel].size(); i++) {
    		if (tr[channel].get(i).getTick() == time)
    			step = i;
    	}
    	if (step != -1) {
        	MidiEvent event = tr[channel].get(step);
            MidiMessage msg = event.getMessage();
            if (msg instanceof ShortMessage) {
                ShortMessage sm = (ShortMessage) msg;
                if (sm.getCommand() == NOTE_ON)
                	return true;
            }
    	}
    	return false;
	}*/

	public static void debugMIDI(Sequence sequence) {
		if (sequence == null) {
			DragonAPICore.logError("Debugged MIDI is null!");
			return;
		}
		int trackNumber = 0;
		for (Track track :  sequence.getTracks()) {
			trackNumber++;
			System.out.println("Track " + trackNumber + ": size = " + track.size());
			System.out.println();
			for (int i=0; i < track.size(); i++) {
				MidiEvent event = track.get(i);
				System.out.print("@" + event.getTick() + " ");
				MidiMessage message = event.getMessage();
				if (message instanceof ShortMessage) {
					ShortMessage sm = (ShortMessage) message;
					System.out.print("Channel: " + sm.getChannel() + " ");
					if (sm.getCommand() == NOTE_ON) {
						int key = sm.getData1();
						int octave = (key / 12)-1;
						int note = key % 12;
						String noteName = NOTE_NAMES[note];
						int velocity = sm.getData2();
						System.out.println("Note on, " + noteName + octave + " key=" + key + " velocity: " + velocity);
					}
					else if (sm.getCommand() == NOTE_OFF) {
						int key = sm.getData1();
						int octave = (key / 12)-1;
						int note = key % 12;
						String noteName = NOTE_NAMES[note];
						int velocity = sm.getData2();
						System.out.println("Note off, " + noteName + octave + " key=" + key + " velocity: " + velocity);
					}
					else if (sm.getCommand() == INSTRU_CHANGE) {
						System.out.println("Instrument change " + sm.getCommand());
					}
					else
						System.out.println("Command:" + sm.getCommand());
				}
				else
					System.out.println("Other message: " + message.getClass());
			}
			System.out.println();
		}
	}

	public static int getMidiLength(Sequence seq) {
		return getSequenceLength(seq);
	}

	private static int getSequenceLength(Sequence seq) {
		if (seq == null)
			return 0;
		Track[] tr = seq.getTracks();
		int length = 0;
		for (int i = 0; i < tr.length; i++) {
			if (tr[i].size() > length)
				length = tr[i].size();
		}
		return length;
	}

	public static int MIDITickToMCTick(Sequence seq, long ti) {
		return (int)(ti*getMillisPerTick(seq)/5F);
	}

	public static long MCTickToMIDITick(Sequence seq, int ti) {
		return (long)(ti/getMillisPerTick(seq)*5F);
	}

	private static float getMillisPerTick(Sequence seq) {
		float bpm = 90;
		return 60000F/(seq.getResolution()*bpm);//1/1.2333F;
	}

	private static long getMIDITickLength(Sequence seq) {
		if (seq == null)
			return 0;
		Track[] tr = seq.getTracks();
		long length = 0;
		for (int i = 0; i < tr.length; i++) {
			if (tr[i].size() > length)
				length = tr[i].ticks();
		}
		return length;
	}

	private static int getMCTickLength(Sequence seq) {
		return MIDITickToMCTick(seq, getMIDITickLength(seq));
	}

	public static int[][][] readMIDIFileToArray(Sequence seq) {
		//debugMIDI(seq);
		int[][][] data = new int[getMCTickLength(seq)][64][3];
		if (seq == null) {
			DragonAPICore.logError("Sequence is empty!");
			return data;
		}
		int[][] dataline = new int[16][3];
		int time;
		int vol = 0;
		int voice = 0;
		int note = 0;
		int instru = 0;
		int channel;
		Track[] tr = seq.getTracks();
		for (int i = 0; i < tr.length; i++) {
			for (int j = 0; j < tr[i].size(); j++) {
				MidiEvent event = tr[i].get(j);
				time = MIDITickToMCTick(seq, event.getTick());
				//DragonAPICore.log(event.getTick()+" midi to "+time+" MC, out of length "+getSequenceLength(seq)+" ("+getSequenceLength(seq)/20F+") s");
				MidiMessage message = event.getMessage();
				if (message instanceof ShortMessage) {
					ShortMessage sm = (ShortMessage) message;
					channel = i-1;
					if (channel == -1) {
						throw new RuntimeException("Invalid MIDI has notes in the tempo track (track 0)!");
					}
					switch(sm.getCommand()) {
						case NOTE_ON:
							int key = sm.getData1();
							int octave = (key / 12)-1;
							int relnote = key % 12;
							note = key-24;
							vol = sm.getData2();
							voice = instru;
							int a = 0;
							dataline[channel][0] = getNoteblockFromGM(voice);
							if (dataline[channel][0] == 2)
								a = 12;
							if (dataline[channel][0] == 3)
								a = -12;
							dataline[channel][1] = note+a;
							dataline[channel][2] = vol;
							break;
						case NOTE_OFF:
							break;
						case INSTRU_CHANGE:
							instru = sm.getData1();
							break;
					}
					if (dataline[channel][0] != 0 && sm.getCommand() == NOTE_ON) {
						//DragonAPICore.log("Event "+sm.getCommand()+" at time: "+time+"; Channel "+channel+": "+dataline[channel][0]+"  "+dataline[channel][1]+"  "+dataline[channel][2]);
						for (int k = 0; k < 16; k++) {
							for (int l = 0; l < 3; l++) {
								data[time][k][l] = dataline[k][l];
							}
						}
					}
				}
			}
		}
		return data;
	}

	public static MusicScore readMIDIFileToScore(Sequence seq) {
		//debugMIDI(seq);
		MusicScore data = new MusicScore(16);
		if (seq == null) {
			DragonAPICore.logError("Sequence is empty!");
			return data;
		}
		int key = 0;
		int instru = 0;
		int channel;
		Track[] tr = seq.getTracks();
		for (int i = 0; i < tr.length; i++) {
			MIDINote[] lastOn = new MIDINote[256];
			for (int j = 0; j < tr[i].size(); j++) {
				MidiEvent event = tr[i].get(j);
				int time = MIDITickToMCTick(seq, event.getTick());
				//DragonAPICore.log(event.getTick()+" midi to "+time+" MC, out of length "+getSequenceLength(seq)+" ("+getSequenceLength(seq)/20F+") s");
				MidiMessage message = event.getMessage();
				if (message instanceof ShortMessage) {
					ShortMessage sm = (ShortMessage) message;
					channel = i;
					if (channel == 0) {
						throw new RuntimeException("Invalid MIDI has notes in the tempo track (track 0)!");
					}
					switch(sm.getCommand()) {
						case NOTE_ON:
							key = sm.getData1();
							int vol = sm.getData2();
							lastOn[key] = new MIDINote(event.getTick(), time, key, instru, vol);
							break;
						case NOTE_OFF:
							break;
						case INSTRU_CHANGE:
							instru = sm.getData1();
							break;
					}
					if (lastOn[key] != null && sm.getCommand() == NOTE_OFF) {
						MIDINote m = lastOn[key];
						int len = time-m.timeOn;
						MusicKey note = MusicKey.getKeyFromMIDI(key);
						data.addNote(m.timeOn, channel-1, note, m.voice, m.velocity, len);
						//ReikaJavaLibrary.pConsole("Note "+note+" on channel "+channel+" @ "+time+" (event time="+event.getTick()+")");
						//ReikaJavaLibrary.pConsole("Note "+note+" on channel "+channel+" ("+m+"), tick off="+time+"/tick_"+event.getTick()+", length = "+len);
						lastOn[key] = null;
					}
				}
			}
		}
		return data;
	}

	private static class MIDINote {

		private final long tick;
		private final int timeOn;
		private final int pitch;
		private final int voice;
		private final int velocity;

		private MIDINote(long tk, int t, int key, int inst, int vol) {
			tick = tk;
			timeOn = t;
			pitch = key;
			velocity = vol;
			voice = inst;
		}

		@Override
		public String toString() {
			return String.format("Tick: %d, P:%d, Vel:%d, Voice:%d, Time:%d", tick, pitch, velocity, voice, timeOn);
		}

	}

	public static int getNoteblockFromGM(int v) {
		switch (v) {
			case 0:
				return 1;
			case 32:
				return 2;
			case 18:
				return 3;
		}
		return 0;
	}
}
