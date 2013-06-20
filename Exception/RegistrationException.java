/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Exception;

import Reika.DragonAPI.Interfaces.DragonAPIMod;
import Reika.DragonAPI.Libraries.ReikaJavaLibrary;


public class RegistrationException extends RuntimeException {

	public RegistrationException(DragonAPIMod mod, String msg) {
		super(mod.getDisplayName()+" has a registration error: "+msg);
		ReikaJavaLibrary.pConsole("Contact "+mod.getModAuthorName()+" immediately!");
		ReikaJavaLibrary.pConsole("Include the following information:");
		this.printStackTrace();
	}

}
