/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Exception;

import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Extras.ModVersion;

public class VersionMismatchException extends DragonAPIException {

	public VersionMismatchException(DragonAPIMod mod, DragonAPIMod mod2) {
		ModVersion v = mod.getModVersion();
		ModVersion dep = mod2.getModVersion();
		message.append(mod.getDisplayName()+" was not installed correctly:\n");
		message.append(mod.getDisplayName()+" "+v+" was installed with "+mod2.getDisplayName()+" "+dep+"\n");
		if (v.majorVersion != dep.majorVersion) {
			message.append("The major version numbers must match!\n");
		}
		else {
			message.append("Version "+v+" of "+mod+" cannot run with "+mod2.getDisplayName()+" "+dep+"\n");
		}
		message.append("This is not a "+mod.getDisplayName()+" bug. Do not post it to "+mod.getDocumentationSite().toString()+" unless you are really stuck.");
		this.crash();
	}

}
