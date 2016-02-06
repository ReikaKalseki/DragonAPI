package Reika.DragonAPI.Instantiable;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import Reika.DragonAPI.Interfaces.MotionController;


public class AttractiveMotionController implements MotionController {

	public final double targetX;
	public final double targetY;
	public final double targetZ;

	private final double damping;

	private final double acceleration;
	private double velocityXZ = 0;

	private double accelerationY = 0;
	private double maxVelocityY;
	private double velocityY;

	public AttractiveMotionController(TileEntity te, double axz, double vy, double damping) {
		this(te.xCoord+0.5, te.yCoord+0.5, te.zCoord+0.5, axz, vy, damping);
	}

	public AttractiveMotionController(double x, double y, double z, double axz, double vy, double damping) {
		targetX = x;
		targetY = y;
		targetZ = z;
		this.damping = damping;
		acceleration = axz;
		maxVelocityY = vy;
		velocityY = maxVelocityY;
	}

	public void update(Entity e) {
		accelerationY = -1*0.125*(e.posY-targetY-0.5);
		velocityY += accelerationY;
		velocityY = MathHelper.clamp_double(velocityY, -maxVelocityY, maxVelocityY);
		maxVelocityY *= damping;
		velocityXZ += acceleration;
	}

	@Override
	public double getMotionX(Entity e) {
		return -(e.posX-targetX)*velocityXZ/e.getDistance(targetX, targetY, targetZ);
	}

	@Override
	public double getMotionY(Entity e) {
		return velocityY;
	}

	@Override
	public double getMotionZ(Entity e) {
		return -(e.posZ-targetZ)*velocityXZ/e.getDistance(targetX, targetY, targetZ);
	}

}
