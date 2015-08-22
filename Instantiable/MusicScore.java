package Reika.DragonAPI.Instantiable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import net.minecraft.nbt.NBTTagCompound;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.MusicKey;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class MusicScore {

	private final TreeMap<Integer, Note>[] music;
	private final int channelCount;
	private int length;

	public MusicScore(int channels) {
		channelCount = channels;
		music = new TreeMap[channels];
	}

	public void addNote(int time, int channel, MusicKey note, int voice, int vol, int len) {
		this.addNote(channel, time, new Note(note, voice, vol, len));
	}

	private void addNote(int channel, int time, Note note) {
		if (music[channel] == null) {
			music[channel] = new TreeMap();
		}
		music[channel].put(time, note);
		length = Math.max(length, time);
	}

	public ArrayList<Note> getNotes(int time) {
		ArrayList<Note> li = new ArrayList();
		for (int i = 0; i < channelCount; i++) {
			Note n = music[i] != null ? music[i].get(time) : null;
			if (n != null) {
				li.add(n);
			}
		}
		return li;
	}

	public Note getNote(int channel, int time) {
		return music[channel] != null ? music[channel].get(time) : null;
	}

	public Map<Integer, Note> getTrack(int channel) {
		return music[channel] != null ? Collections.unmodifiableMap(music[channel]) : new HashMap();
	}

	public int getLatestPos(int channel) {
		return music[channel] != null && !music[channel].isEmpty() ? music[channel].lastKey() : 0;
	}

	public int getLatestPos() {
		return length;
	}

	public MusicScore scaleSpeed(float factor) {
		MusicScore mus = new MusicScore(channelCount);

		for (int i = 0; i < channelCount; i++) {
			if (music[i] != null) {
				for (int time : music[i].keySet()) {
					Note n = music[i].get(time);
					mus.addNote(i, (int)(time/factor), n);
				}
			}
		}

		return mus;
	}

	public static class Note {

		public final MusicKey key;

		/** With General MIDI Spec, is 0-127. See MIDI Spec for details */
		public final int voice;

		/** With MIDI Spec, is 0-127 */
		public final int volume;

		/** In MC ticks */
		public final int length;

		private Note(MusicKey note, int instru, int vol, int len) {
			key = note;
			voice = instru;
			volume = vol;
			length = len;
		}

		public void writeToNBT(NBTTagCompound nbt) {
			nbt.setInteger("key", key.ordinal());
			nbt.setInteger("volume", volume);
			nbt.setInteger("voice", voice);
			nbt.setInteger("length", length);
		}

		public static Note readFromNBT(NBTTagCompound nbt) {
			return new Note(MusicKey.getByIndex(nbt.getInteger("key")), nbt.getInteger("voice"), nbt.getInteger("volume"), nbt.getInteger("length"));
		}

		@Override
		public String toString() {
			return key.name()+"/"+voice+"/"+volume;
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

	public void writeToNBT(NBTTagCompound tag) {
		for (int i = 0; i < channelCount; i++) {
			NBTTagCompound nbt = new NBTTagCompound();
			if (music[i] != null) {
				for (int time : music[i].keySet()) {
					NBTTagCompound val = new NBTTagCompound();
					music[i].get(time).writeToNBT(val);
					nbt.setTag(String.valueOf(time), val);
				}
			}
			tag.setTag("Ch_"+i, nbt);
		}
		tag.setInteger("numchan", channelCount);
	}

	public static MusicScore readFromNBT(NBTTagCompound tag) {
		MusicScore mus = new MusicScore(tag.getInteger("numchan"));

		for (int i = 0; i < mus.channelCount; i++) {
			if (tag.hasKey("Ch_"+i)) {
				mus.music[i] = new TreeMap();
				NBTTagCompound nbt = tag.getCompoundTag("Ch_"+i);
				for (Object o : nbt.func_150296_c()) {
					String s = (String)o;
					NBTTagCompound val = nbt.getCompoundTag(s);
					mus.music[i].put(Integer.parseInt(s), Note.readFromNBT(val));
				}
			}
		}

		return mus;
	}

	@Override
	public String toString() {
		return Arrays.toString(music);
	}

	public void clearChannel(int channel) {
		music[channel] = null;
	}

	@SideOnly(Side.CLIENT)
	public void renderPianoRoll() {

	}

	public MusicScore copy() {
		MusicScore mus = new MusicScore(channelCount);
		for (int i = 0; i < channelCount; i++) {
			if (music[i] != null) {
				mus.music[i] = new TreeMap();
				mus.music[i].putAll(music[i]);
			}
		}
		mus.length = length;
		return mus;
	}

}
