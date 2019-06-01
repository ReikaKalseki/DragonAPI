package Reika.DragonAPI.Instantiable.ParticleController;

import net.minecraft.entity.Entity;

import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Interfaces.PositionController;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;


public class SpringMotionController implements PositionController {

	private final double xComponent;
	private final double yComponent;
	private final double zComponent;

	private final double maxAmplitude;
	private final double angularSpeed;
	private final double damping;

	private final DecimalPosition origin;

	private double linearPosition;
	private int tick;

	public SpringMotionController(double theta, double phi, double a, double v, double d, DecimalPosition p) {
		this(ReikaPhysicsHelper.polarToCartesian(1, theta, phi), a, v, d, p);
	}

	public SpringMotionController(double[] xyz, double a, double v, double d, DecimalPosition p) {
		xComponent = xyz[0];
		yComponent = xyz[1];
		zComponent = xyz[2];

		maxAmplitude = a;
		angularSpeed = v;
		damping = d;

		origin = p;
	}

	@Override
	public void update(Entity e) {
		tick++;
		linearPosition = maxAmplitude*Math.pow(Math.E, -damping*tick)*Math.cos(angularSpeed*tick);
	}

	@Override
	public double getPositionX(Entity e) {
		return origin.xCoord+linearPosition*xComponent;
	}

	@Override
	public double getPositionY(Entity e) {
		return origin.yCoord+linearPosition*yComponent;
	}

	@Override
	public double getPositionZ(Entity e) {
		return origin.zCoord+linearPosition*zComponent;
	}

}
