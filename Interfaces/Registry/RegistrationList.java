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

import net.minecraft.item.ItemStack;

/** This is an interface for ENUMS! */
public interface RegistrationList extends RegistryEntry {

	public Class[] getConstructorParamTypes();

	public Object[] getConstructorParams();

	public String getMultiValuedName(int meta);

	public boolean hasMultiValuedName();

	public int getNumberMetadatas();

	//public ItemStack getStackOf();

	public ItemStack getStackOfMetadata(int meta);

}
