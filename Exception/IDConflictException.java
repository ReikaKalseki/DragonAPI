/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Exception;

import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.DragonAPIMod;

public class IDConflictException extends DragonAPIException {

	public IDConflictException(DragonAPIMod mod, String msg) {
		message.append(mod.getDisplayName()+" was not installed correctly:\n");
		message.append("CONFLICT: "+msg+"\n");
		message.append("Check your IDs and change them if possible.\n");
		//if (mod.getDocumentationSite() != null)
		//	message.append("This is NOT a mod bug. Do not post it to "+mod.getDocumentationSite().toString());
		//else
		message.append("This is NOT a mod bug. Do not post it or ask for support or you will look lazy or extremely foolish..");
		this.crash();
	}

	public IDConflictException(ModList mod, String msg) {
		message.append(mod.getDisplayName()+" was not installed correctly:\n");
		message.append("CONFLICT: "+msg+"\n");
		message.append("Check your IDs and change them if possible.\n");
		//if (mod.getDocumentationSite() != null)
		//	message.append("This is NOT a mod bug. Do not post it to "+mod.getDocumentationSite().toString());
		//else
		message.append("This is NOT a mod bug. Do not post it or ask for support or you will look lazy or extremely foolish..");
		this.crash();
	}

	public IDConflictException(String msg) {
		message.append("The mods were not installed correctly:\n");
		message.append("CONFLICT: "+msg+"\n");
		message.append("Check your IDs and change them if possible.\n");
		//if (mod.getDocumentationSite() != null)
		//	message.append("This is NOT a mod bug. Do not post it to "+mod.getDocumentationSite().toString());
		//else
		message.append("This is NOT a mod bug. Do not post it or ask for support or you will look lazy or extremely foolish..");
		this.crash();
	}
}
