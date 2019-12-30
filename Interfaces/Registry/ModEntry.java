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

public interface ModEntry {

	public boolean isLoaded();

	public String getVersion();

	public String getModLabel();

	public String getDisplayName();

	public Class getBlockClass();
	public Class getItemClass();

}
