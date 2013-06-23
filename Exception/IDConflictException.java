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

public class IDConflictException extends RuntimeException {

	public IDConflictException(DragonAPIMod mod, Throwable src) {
		super(mod.getDisplayName()+" was not installed correctly:\n"+"CONFLICT: "+src.getMessage());
		ReikaJavaLibrary.pConsole("Check your IDs and change them if possible.");
		this.printStackTrace();
	}

}
