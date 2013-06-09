/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI;

import cpw.mods.fml.common.FMLCommonHandler;


public class RegistrationException extends RuntimeException {

	public RegistrationException(Object mod, String msg) {
		super(FMLCommonHandler.instance().findContainerFor(mod).getName()+" has a registration error: "+msg);
		this.printStackTrace();
	}

}
