/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces;



/** This is an interface for ENUMS! */
public interface RegistrationList {

	public Class[] getConstructorParamTypes();

	public Object[] getConstructorParams();

	public String getUnlocalizedName();

	public Class getObjectClass();

	public String getBasicName();

	public String getMultiValuedName(int meta);

	public boolean hasMultiValuedName();

	/** Whether to create it or not */
	public boolean isDummiedOut();

	public int getNumberMetadatas();

}
