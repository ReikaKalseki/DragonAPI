package Reika.DragonAPI.Interfaces;

import net.minecraft.entity.Entity;


public interface MotionController {

	public void update(Entity e);
	public double getMotionX(Entity e);
	public double getMotionY(Entity e);
	public double getMotionZ(Entity e);

}
