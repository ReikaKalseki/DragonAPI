/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.interfaces.config;


/** This is an interface for ENUMS! */
public interface ConfigList {

	public Class getPropertyType();

	public String getLabel();

	//public boolean isDummiedOut();

	public boolean isEnforcingDefaults();

	public boolean shouldLoad();

	/** To avoid casting */
	public int ordinal();

}
