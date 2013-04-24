package Reika.DragonAPI;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

public class ReikaMIDIReader {
	
    public static final int NOTE_ON = 0x90;
    public static final int INSTRU_CHANGE = 0xC0;
    public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

	public static Sequence getMIDIFromFile(Class root, String path, String back) {
		ReikaJavaLibrary.pConsole("Reading MIDI at "+path+" with backup at "+back);
		InputStream input = root.getResourceAsStream(path);
		InputStream backup = root.getResourceAsStream(back);
		
		if (input == null && backup == null) {
			ReikaJavaLibrary.pConsole("Neither main file at "+path+" nor backup at "+back+" found. Aborting.");
			return null;
		}
		
		Sequencer seq = null;
		
		try {
			seq = MidiSystem.getSequencer();
		}
		catch (MidiUnavailableException e1) {
			//e1.printStackTrace();
			ReikaJavaLibrary.pConsole("MIDI system unavailable.");
			return null;
		}
		
		try {
			seq.setSequence(input);
		}
		catch (IOException e1) {
			//e1.printStackTrace();
			ReikaJavaLibrary.pConsole("MIDI File at "+path+" unreadable. Switching to backup.");
			try {
				seq.setSequence(backup);
			}
			catch (IOException e) {
				//e.printStackTrace();
				ReikaJavaLibrary.pConsole("Backup MIDI File at "+back+" unreadable.");
			}
			catch (InvalidMidiDataException e) {
				//e.printStackTrace();
				ReikaJavaLibrary.pConsole("Backup MIDI File at "+back+" invalid.");
			}
		}
		catch (InvalidMidiDataException e1) {
			//e1.printStackTrace();
			ReikaJavaLibrary.pConsole("MIDI File at "+path+" invalid. Switching to backup.");
			try {
				seq.setSequence(backup);
			}
			catch (IOException e) {
				//e.printStackTrace();
				ReikaJavaLibrary.pConsole("Backup MIDI File at "+back+" unreadable.");
			}
			catch (InvalidMidiDataException e) {
				//e.printStackTrace();
				ReikaJavaLibrary.pConsole("Backup MIDI File at "+back+" invalid.");
			}
		}
		return seq.getSequence();
	}
	
	/** Reads a parameter from a MIDI Sequence. Args: Sequence, channel (1-16), time, task (0 = note, 1 = voice, 2 = volume) */
	public static int readMIDI(Sequence seq, int channel, int time, int task) {
		int voice = -1;
		int note = -1;
		int vol = -1;
		/*1 4/4 bar = 1920 ticks
		 1 MC tick = 1 32nd; 2 MC tick = 1 16th; 4 MC tick = 1 8th; 8 MC tick = 1 qtr; 16 MC = 1 half; 32 MC tick = 1 whole = 1920
		32 = 1920
		1 MC tick = 60 MIDI ticks		
		*/
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
        		//ReikaJavaLibrary.pConsole(data[0]+256);
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
	                	//ReikaJavaLibrary.pConsole(channel+" @  "+tr[channel].get(step).getTick()+"  for  "+time);
	                    int key = sm.getData1();
	                    int octave = (key / 12)-1;
	                    while (octave > 2)
	                    	octave -= 2;
	                    note = (key%12);
	                    if (octave == 2 && note != 0)
	                    	octave = 0;
	                    note += 12*octave;
	                    vol = sm.getData2();
	                }
	            }
        	}
        if (!(voice == -1 && note == -1))
        ;//ReikaJavaLibrary.pConsole(note);
        //ReikaJavaLibrary.pConsole("Channel "+channel+" has "+voice+" playing "+note+" @ "+tr[channel].get(step).getTick()/60);
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
	}
}
