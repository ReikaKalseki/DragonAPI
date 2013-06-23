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

public class InstallationException extends RuntimeException {

	public InstallationException(DragonAPIMod mod, String msg) {
		super(mod.getDisplayName()+" was not installed correctly:\n"+msg+"\n"+"Try consulting "+mod.getDocumentationSite().toString());
		this.printStackTrace();
	}

}
