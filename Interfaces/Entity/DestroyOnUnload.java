/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2018
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces.Entity;


/** Ensure your entity writes isDead to NBT! */
public interface DestroyOnUnload {

	/** Usually calls setDead */
	public void destroy();

}
