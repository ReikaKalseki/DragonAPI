package Reika.DragonAPI.Exception;

import Reika.DragonAPI.Base.DragonAPIMod;

public class MissingDependencyException extends DragonAPIException {

	public MissingDependencyException(DragonAPIMod mod, String mod2) {
		message.append(mod.getDisplayName()+" was not installed correctly:\n");
		message.append(mod.getDisplayName()+" was installed without its dependency "+mod2+"!\n");
		message.append("This is not a "+mod.getDisplayName()+" bug. Do not post it to "+mod.getDocumentationSite().toString()+".");
		this.crash();
	}

}
