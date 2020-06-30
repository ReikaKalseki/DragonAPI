package Reika.DragonAPI.Instantiable.Math.Noise;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.util.MathHelper;

public abstract class NoiseGeneratorBase {

	protected double inputFactor = 1;

	protected final Collection<Octave> octaves = new ArrayList();
	protected double maxRange = 1;

	/** As opposed to scaling */
	public boolean clampEdge = false;

	public final long seed;

	protected NoiseGeneratorBase(long s) {
		seed = s;
	}

	public final double getValue(double x, double z) {
		return this.calculateValues(x*inputFactor, 0, z*inputFactor);
	}

	public final double getValue(double x, double y, double z) {
		return this.calculateValues(x*inputFactor, y*inputFactor, z*inputFactor);
	}

	private double calculateValues(double x, double y, double z) {
		double val = this.calcValue(x, y, z, 1, 1);

		if (!octaves.isEmpty()) {
			for (Octave o : octaves) {
				val += this.calcValue(x+o.phaseShift, y+o.phaseShift, z+o.phaseShift, o.frequency, o.amplitude);
			}
			if (clampEdge)
				val = MathHelper.clamp_double(val, -1, 1);
			else
				val /= maxRange;
		}

		return val;
	}

	protected abstract double calcValue(double x, double y, double z, double freq, double amp);

	public final NoiseGeneratorBase setFrequency(double f) {
		inputFactor = f;
		return this;
	}

	public final double getFrequencyScale() {
		return inputFactor;
	}

	public final NoiseGeneratorBase addOctave(double relativeFrequency, double relativeAmplitude) {
		return this.addOctave(relativeFrequency, relativeAmplitude, 0);
	}

	public final NoiseGeneratorBase addOctave(double relativeFrequency, double relativeAmplitude, double phaseShift) {
		octaves.add(new Octave(relativeFrequency, relativeAmplitude, phaseShift));
		maxRange += relativeAmplitude;
		return this;
	}
}
