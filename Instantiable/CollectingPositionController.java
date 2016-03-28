package Reika.DragonAPI.Instantiable;

import net.minecraft.entity.Entity;
import Reika.DragonAPI.Interfaces.PositionController;


public class CollectingPositionController implements PositionController {

	public final int duration;

	public final double targetX;
	public final double targetY;
	public final double targetZ;

	public final double startX;
	public final double startY;
	public final double startZ;

	private int tick = 0;

	public CollectingPositionController(double x, double y, double z, double tx, double ty, double tz, int t) {
		startX = x;
		startY = y;
		startZ = z;

		targetX = tx;
		targetY = ty;
		targetZ = tz;

		duration = t;
	}

	@Override
	public void update(Entity e) {
		tick++;
	}

	@Override
	public double getPositionX(Entity e) {
		return startX+(targetX-startX)*((double)tick/duration);
	}

	@Override
	public double getPositionY(Entity e) {
		return startY+(targetY-startY)*((double)tick/duration);
	}

	@Override
	public double getPositionZ(Entity e) {
		return startZ+(targetZ-startZ)*((double)tick/duration);
	}

}
