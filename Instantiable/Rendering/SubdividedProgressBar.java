package Reika.DragonAPI.Instantiable.Rendering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import Reika.DragonAPI.Exception.MisuseException;

public class SubdividedProgressBar {

	private final TreeMap<Float, ProgressBarSection> sections = new TreeMap();
	private final HashMap<Integer, ProgressBarSection> lookups = new HashMap();

	private int tick;
	private int duration;

	public SubdividedProgressBar() {

	}

	public SubdividedProgressBar addSection(int len, float f) {
		return this.addSection(new ProgressBarSection(len, f));
	}

	public SubdividedProgressBar addSection(int len) {
		return this.addSection(new ProgressBarSection(len));
	}

	public SubdividedProgressBar addSection(ProgressBarSection p) {
		this.recalculateFractions(p);
		return this;
	}

	private void recalculateFractions(ProgressBarSection add) {
		ArrayList<ProgressBarSection> li = new ArrayList(sections.values()); //order is maintained
		if (add != null)
			li.add(add);
		sections.clear();
		lookups.clear();
		float total = 0;
		for (ProgressBarSection p : li) {
			total += p.ticksDuration;
		}
		float at = 0;
		for (ProgressBarSection p : li) {
			float start = at/total;
			at += p.ticksDuration;
			float end = at/total;
			p.startFraction = start;
			p.endFraction = end;
			sections.put(p.startFraction, p);
			p.index = lookups.size();
			lookups.put(p.index, p);
		}
	}

	public void setTick(int tick, int duration) {
		if (tick >= duration)
			throw new MisuseException("You cannot set the time greater than the maximum!");
		this.tick = tick;
		this.duration = duration;
		float f = tick/(float)duration;
		for (ProgressBarSection p : sections.values()) {
			//ReikaJavaLibrary.pConsole(p);
			if (p.startFraction >= f) {
				p.fraction = 0;
			}
			else if (p.endFraction <= f) {
				p.fraction = 1;
			}
			else {
				p.fraction = (f-p.startFraction)/(p.endFraction-p.startFraction);
			}
		}
	}

	public int getScaled(int section) {
		return lookups.get(section).getScaled();
	}

	public int barCount() {
		return sections.size();
	}

	public static class ProgressBarSection {

		private float startFraction;
		private float endFraction;

		private float fraction;

		private int index;

		public final int pixelLength;
		public final float speedFactor;

		private final float ticksDuration;

		public ProgressBarSection(int len) {
			this(len, 1);
		}

		public int getScaled() {
			return (int)(fraction*pixelLength);
		}

		public ProgressBarSection(int len, float s) {
			pixelLength = len;
			speedFactor = s;

			ticksDuration = pixelLength/speedFactor;
		}

		@Override
		public String toString() {
			return "#"+index+" ["+startFraction+" - "+endFraction+"]";
		}

	}

}
