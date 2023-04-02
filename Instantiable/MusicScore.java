/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.MusicKey;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class MusicScore {

	private final ScoreTrack[] music;
	private final HashSet<Integer> activeTracks = new HashSet();
	private final int channelCount;

	private int length;
	private int noteCount;

	private MusicKey lowest;
	private MusicKey highest;
	private int firstNoteTime = Integer.MAX_VALUE;

	public MusicScore(int channels) {
		channelCount = channels;
		music = new ScoreTrack[channels];
	}

	public void addNote(int time, int channel, MusicKey note, int voice, int vol, int len, boolean perc) {
		this.addNote(channel, time, new Note(note, voice, vol, len, perc));
	}

	private void addNote(int channel, int time, Note note) {
		if (music[channel] == null) {
			music[channel] = new ScoreTrack(channel);
		}
		NoteData c = music[channel].getNoteAt(time);
		if (c == null) {
			c = new NoteData(time);
			music[channel].put(time, c);
		}
		else {
			//ReikaJavaLibrary.pConsole("Adding "+note+" @ C"+channel+" : "+time+" to "+c);
		}
		activeTracks.add(channel);
		noteCount++;
		c.add(note);
		length = Math.max(length, time);
		firstNoteTime = Math.min(firstNoteTime, time);
		if (note.key == null)
			return;
		if (lowest == null || lowest.ordinal() > note.key.ordinal())
			lowest = note.key;
		if (highest == null || highest.ordinal() < note.key.ordinal())
			highest = note.key;
	}

	public Collection<Note> getNotes(int time) {
		Collection<Note> li = new ArrayList();
		for (int i = 0; i < channelCount; i++) {
			NoteData n = music[i] != null ? music[i].getNoteAt(time) : null;
			if (n != null) {
				li.addAll(n.notes.values());
			}
		}
		return li;
	}

	public Collection<Note> getNotes(int channel, int time) {
		NoteData c = music[channel] != null ? music[channel].getNoteAt(time) : null;
		return c != null ? Collections.unmodifiableCollection(c.notes.values()) : null;
	}

	public void backspace(int channel) {
		if (music[channel] != null && !music[channel].isEmpty()) {
			if (music[channel].remove(music[channel].lastNoteTime()) != null) {
				noteCount--;
				if (music[channel].isEmpty())
					activeTracks.remove(channel);
			}
		}
	}

	public ScoreTrack getTrack(int channel) {
		return music[channel] != null ? music[channel] : null;
	}

	public int getLatestPos(int channel) {
		return music[channel] != null && !music[channel].isEmpty() ? music[channel].lastNoteTime() : 0;
	}

	public int getLatestPos() {
		return length;
	}

	public int countTracks() {
		return music.length;
	}

	public MusicScore scaleSpeed(float factor, boolean alignToZero) {
		MusicScore mus = new MusicScore(channelCount);

		int shift = alignToZero ? (int)(-firstNoteTime/factor) : 0;

		for (int i = 0; i < channelCount; i++) {
			if (music[i] != null) {
				for (Entry<Integer, NoteData> e : music[i].entrySet()) {
					int time = e.getKey();
					NoteData c = e.getValue();
					if (c != null)
						for (Note n : c.notes.values())
							mus.addNote(i, (int)(time/factor)+shift, n.scaleSpeed(factor));
				}
			}
		}

		return mus;
	}

	public MusicScore alignToZero() {
		MusicScore mus = new MusicScore(channelCount);
		for (int i = 0; i < channelCount; i++) {
			if (music[i] != null) {
				for (Entry<Integer, NoteData> e : music[i].entrySet()) {
					int time = e.getKey();
					NoteData c = e.getValue();
					if (c != null)
						for (Note n : c.notes.values())
							mus.addNote(i, time-firstNoteTime, n);
				}
			}
		}
		return mus;
	}

	public void transpose(int semitones) {
		if (semitones == 0)
			return;
		for (int i = 0; i < channelCount; i++) {
			if (music[i] != null) {
				for (Entry<Integer, NoteData> e : music[i].entrySet()) {
					int time = e.getKey();
					NoteData c = e.getValue();
					if (c != null) {
						e.setValue(c.transpose(semitones));
					}
				}
			}
		}
	}

	public MusicKey getLowest() {
		return lowest;
	}

	public MusicKey getHighest() {
		return highest;
	}

	public void normalizeToRange(MusicKey min, MusicKey max) {
		if (noteCount == 0)
			return;
		int under = min.ordinal()-lowest.ordinal();
		int over = highest.ordinal()-max.ordinal();
		int ounder = ReikaMathLibrary.roundToNearestX(12, under);
		int oover = ReikaMathLibrary.roundToNearestX(12, over);
		if ((oover <= 0 && ounder <= 0) || (oover == ounder)) {
			return;
		}
		else if (oover > ounder) {
			this.transpose(-12*Math.round((oover-ounder)/24F));
		}
		else {
			this.transpose(12*Math.round((ounder-oover)/24F));
		}
	}

	public void writeToNBT(NBTTagCompound tag) {
		for (int i = 0; i < channelCount; i++) {
			NBTTagCompound nbt = new NBTTagCompound();
			if (music[i] != null) {
				for (Entry<Integer, NoteData> e : music[i].entrySet()) {
					NBTTagList li = e.getValue().writeToNBT();
					nbt.setTag(String.valueOf(e.getKey()), li);
				}
			}
			tag.setTag("Ch_"+i, nbt);
		}
		tag.setInteger("numchan", channelCount);
		tag.setInteger("len", length);
		tag.setInteger("first", firstNoteTime);
		tag.setInteger("count", noteCount);
		tag.setInteger("lowest", lowest != null ? lowest.ordinal() : -1);
		tag.setInteger("highest", highest != null ? highest.ordinal() : -1);
	}

	public static MusicScore readFromNBT(NBTTagCompound tag) {
		MusicScore mus = new MusicScore(tag.getInteger("numchan"));

		for (int i = 0; i < mus.channelCount; i++) {
			if (tag.hasKey("Ch_"+i)) {
				mus.music[i] = new ScoreTrack(i);
				NBTTagCompound nbt = tag.getCompoundTag("Ch_"+i);
				for (Object o : nbt.func_150296_c()) {
					String s = (String)o;
					int time = Integer.parseInt(s);
					NBTTagList li = nbt.getTagList(s, NBTTypes.COMPOUND.ID);
					NoteData c = NoteData.readFromNBT(time, li);
					mus.music[i].put(time, c);
				}
				if (mus.music[i].isEmpty())
					mus.music[i] = null;
				else
					mus.activeTracks.add(i);
			}
		}

		mus.length = tag.getInteger("len");
		mus.firstNoteTime = tag.getInteger("first");
		mus.noteCount = tag.getInteger("count");
		int low = tag.getInteger("lowest");
		mus.lowest = low == -1 ? null : MusicKey.getByIndex(low);
		int high = tag.getInteger("highest");
		mus.highest = high == -1 ? null : MusicKey.getByIndex(high);

		return mus;
	}

	@Override
	public String toString() {
		return Arrays.toString(music);
	}

	public int noteCount() {
		return noteCount;
	}

	public Set<Integer> getActiveTracks() {
		return Collections.unmodifiableSet(activeTracks);
	}

	public void clearChannel(int channel) {
		noteCount -= music[channel].noteCount();
		music[channel] = null;
		activeTracks.remove(channel);
	}

	@SideOnly(Side.CLIENT)
	public void renderPianoRoll() {

	}

	public MusicScore copy() {
		MusicScore mus = new MusicScore(channelCount);
		for (int i = 0; i < channelCount; i++) {
			if (music[i] != null) {
				mus.music[i] = music[i].copy();
			}
		}
		mus.length = length;
		return mus;
	}

	public static class ScoreTrack {

		private final TreeMap<Integer, NoteData> notes = new TreeMap();

		public final int channel;

		private MusicKey lowest;
		private MusicKey highest;
		private int firstNoteTime = Integer.MAX_VALUE;

		private ScoreTrack(int ch) {
			channel = ch;
		}

		public ScoreTrack copy() {
			ScoreTrack ret = new ScoreTrack(channel);
			ret.putAll(this);
			return ret;
		}

		public ScoreTrack alignToGrid(int step) {
			ScoreTrack ret = new ScoreTrack(channel);
			for (Entry<Integer, NoteData> e : notes.entrySet()) {
				int tick = ReikaMathLibrary.roundDownToX(step, e.getKey());
				ret.put(tick, e.getValue().setTick(tick));
			}
			return ret;
		}

		public NoteData getNoteAt(int time) {
			return notes.get(time);
		}

		public int firstNoteTime() {
			return notes.isEmpty() ? -1 : notes.firstKey();
		}

		public int lastNoteTime() {
			return notes.isEmpty() ? -1 : notes.lastKey();
		}

		public MusicKey getLowest() {
			return lowest;
		}

		public MusicKey getHighest() {
			return highest;
		}

		public int getLengthInTicks() {
			if (this.isEmpty())
				return 0;
			int last = this.lastNoteTime();
			NoteData note = this.getNoteAt(last);
			return last+note.length();
		}

		public boolean isEmpty() {
			return notes.isEmpty();
		}

		public int noteCount() {
			return notes.size();
		}

		private Collection<Entry<Integer, NoteData>> entrySet() {
			return notes.entrySet();
		}

		public Collection<Entry<Integer, NoteData>> entryView() {
			return Collections.unmodifiableMap(notes).entrySet();
		}

		private Collection<Integer> keySet() {
			return notes.keySet();
		}

		private void put(int time, NoteData data) {
			notes.put(time, data);

			for (Note n : data.notes())
				this.onAddNote(time, n);
		}

		private void putAll(ScoreTrack s) {
			notes.putAll(s.notes);

			firstNoteTime = Math.min(firstNoteTime, s.firstNoteTime);
			if (lowest == null || lowest.ordinal() > s.lowest.ordinal()) {
				lowest = s.lowest;
			}
			if (highest == null || highest.ordinal() < s.highest.ordinal()) {
				highest = s.highest;
			}
		}

		private void onAddNote(int time, Note n) {
			firstNoteTime = Math.min(firstNoteTime, time);
			if (lowest == null || lowest.ordinal() > n.key.ordinal())
				lowest = n.key;
			if (highest == null || highest.ordinal() < n.key.ordinal())
				highest = n.key;
		}

		private NoteData remove(int time) {
			return notes.remove(time);
		}

		public Collection<NoteData> getNotes() {
			return Collections.unmodifiableCollection(notes.values());
		}

		@Override
		public String toString() {
			return notes.toString();
		}

		public Collection<Note> getActiveNotesAt(int tick) {
			Collection<Note> ret = new ArrayList();
			Integer prev = notes.floorKey(tick);
			while (prev != null) {
				NoteData nd = notes.get(prev);
				for (Note n : nd.notes.values()) {
					int end = nd.tick+n.length;
					if (end > tick)
						ret.add(n);
				}

				prev = notes.lowerKey(prev);
			}
			return ret;
		}

	}

	public static class NoteData {

		private final HashMap<MusicKey, Note> notes = new HashMap();
		public final int tick;

		private int longestNote = 0;

		private NoteData(int t) {
			tick = t;
		}

		public NoteData setTick(int newtick) {
			NoteData ret = new NoteData(newtick);
			ret.notes.putAll(notes);
			ret.longestNote = longestNote;
			return ret;
		}

		public int length() {
			return longestNote;
		}

		public NoteData transpose(int semitones) {
			NoteData ret = new NoteData(tick);
			for (Note n : notes.values()) {
				ret.add(n.transpose(semitones));
			}
			return ret;
		}

		private static NoteData readFromNBT(int t, NBTTagList li) {
			NoteData dat = new NoteData(t);
			for (Object o2 : li.tagList) {
				NBTTagCompound val = (NBTTagCompound)o2;
				Note n = Note.readFromNBT(val);
				dat.add(n);
			}
			return dat;
		}

		private NBTTagList writeToNBT() {
			NBTTagList li = new NBTTagList();
			for (Note n : notes.values()) {
				NBTTagCompound val = new NBTTagCompound();
				n.writeToNBT(val);
				li.appendTag(val);
			}
			return li;
		}

		private void add(Note note) {
			notes.put(note.key, note);
			longestNote = Math.max(longestNote, note.length);
		}

		public Collection<Note> notes() {
			return Collections.unmodifiableCollection(notes.values());
		}

		public Set<MusicKey> keys() {
			return Collections.unmodifiableSet(notes.keySet());
		}

		@Override
		public String toString() {
			return notes.values().toString()+" @ "+tick;
		}

	}

	public static class Note {

		public final MusicKey key;

		/** With General MIDI Spec, is 0-127. See MIDI Spec for details */
		public final int voice;

		/** With MIDI Spec, is 0-127 */
		public final int volume;

		/** In MC ticks */
		public final int length;

		public final boolean percussion;

		private Note(MusicKey note, int instru, int vol, int len, boolean perc) {
			key = note;
			voice = instru;
			volume = vol;
			length = len;
			percussion = perc;
		}

		public Note scaleSpeed(float speed) {
			return new Note(key, voice, volume, (int)(length/speed), percussion);
		}

		public Note transpose(int semitones) {
			return new Note(key.getInterval(semitones), voice, volume, length, percussion);
		}

		public void writeToNBT(NBTTagCompound nbt) {
			nbt.setInteger("key", key.ordinal());
			nbt.setInteger("volume", volume);
			nbt.setInteger("voice", voice);
			nbt.setInteger("length", length);
			nbt.setBoolean("percussion", percussion);
		}

		public static Note readFromNBT(NBTTagCompound nbt) {
			return new Note(MusicKey.getByIndex(nbt.getInteger("key")), nbt.getInteger("voice"), nbt.getInteger("volume"), nbt.getInteger("length"), nbt.getBoolean("percussion"));
		}

		@Override
		public String toString() {
			return key.name()+" / instr="+voice+" / vol="+volume+" / len="+length;
		}

		@Override
		public int hashCode() {
			return key.ordinal() << 16 | voice << 8 | volume;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Note) {
				Note n = (Note)o;
				return n.key == key && n.voice == voice && n.volume == volume;
			}
			return false;
		}

	}

}
