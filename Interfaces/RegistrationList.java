/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces;

import net.minecraft.item.ItemBlock;

/** This is an interface for ENUMS! */
public abstract interface RegistrationList {

	public Class[] getConstructorParamTypes();

	public Object[] getConstructorParams();

	public String getUnlocalizedName();

	public Class getObjectClass();

	public String getBasicName();

	public String getMultiValuedName(int meta);

	public boolean hasMultiValuedName();

	/** Only used in item registration */
	public int getNumberMetadatas();

	/** Only used in block registration */
	public Class<? extends ItemBlock> getItemBlock();

	/** Only used in block registration */
	public boolean hasItemBlock();

	public int getID();

	/** Only used in item registration */
	public boolean overwritingItem();

	/** Whether to create it or not */
	public boolean isDummiedOut();

}
