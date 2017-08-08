/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Exception;

import Reika.DragonAPI.Base.DragonAPIMod;

public class TamperingException extends DragonAPIException {

	public TamperingException(DragonAPIMod mod, String culpritMod) {
		message.append(mod.getDisplayName()+" was tampered with by another mod:\n");
		message.append(culpritMod+"tried to edit "+mod.getDisplayName()+"'s code.\n");
		message.append("The author of "+culpritMod+" is NOT authorized to do this and "+mod.getModAuthorName()+" has \n");
		message.append("disallowed the game from continuing. Try contacting the author of "+culpritMod+", or\n");
		message.append("try consulting "+mod.getDocumentationSite().toString()+"for information.\n");
		message.append("This is not a "+mod.getDisplayName()+" bug, but please post it to the mod site at\n");
		message.append(mod.getDocumentationSite().toString());
		this.crash();
	}

}
