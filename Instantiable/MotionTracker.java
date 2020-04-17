package Reika.DragonAPI.Instantiable;

import net.minecraft.entity.Entity;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;

public class MotionTracker {

	private double posX;
	private double posY;
	private double posZ;
	private int lastMoved = 0;

	private double totalDistance3DSq;

	private final double[] totalDistanceHistory;
	private final int sampleRate;

	private int tick = 0;

	public MotionTracker(int length) {
		this(length, 1);
	}

	public MotionTracker(int length, int rate) {
		totalDistanceHistory = new double[length];
		sampleRate = rate;
	}

	public void update(Entity e) {
		this.update(e.posX, e.posY, e.posZ);
	}

	public void update(double x, double y, double z) {
		tick++;
		double dx = x-posX;
		double dy = y-posY;
		double dz = z-posZ;
		if (dx != 0 || dy != 0 || dz != 0)
			lastMoved = 0;
		double dd = dx*dx+dy*dy+dz*dz;
		totalDistance3DSq += dd;
		if (tick%sampleRate == 0)
			ReikaArrayHelper.cycleArray(totalDistanceHistory, dd);
		posX = x;
		posY = y;
		posZ = z;
	}

	public int getLastMoved() {
		return lastMoved;
	}

	public double getTotalTravelDistanceSq() {
		return totalDistance3DSq;
	}

	public double getTotalTravelDistanceSince(int steps) {
		if (steps > totalDistanceHistory.length) {
			DragonAPICore.logError("You cannot get the travel history for more steps than the history is long!");
			return 0;
		}
		double ret = 0;
		for (int i = 0; i < steps; i++) {
			ret += totalDistanceHistory[i];
		}
		return ret;
	}

}
