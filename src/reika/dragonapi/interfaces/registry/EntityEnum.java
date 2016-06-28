/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.interfaces.registry;


public interface EntityEnum extends RegistryEntry {

	public int getTrackingDistance();

	public boolean sendsVelocityUpdates();

	public boolean hasSpawnEgg();

	public int eggColor1();
	public int eggColor2();

}
