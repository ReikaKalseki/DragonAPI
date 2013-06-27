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

}
