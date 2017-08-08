/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces.Registry;


public interface EntityEnum extends RegistryEntry {

	public int getTrackingDistance();

	public boolean sendsVelocityUpdates();

	public boolean hasSpawnEgg();

	public int eggColor1();
	public int eggColor2();

}
