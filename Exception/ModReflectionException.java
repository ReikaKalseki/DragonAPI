package Reika.DragonAPI.Exception;

import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.DragonAPIMod;

public class ModReflectionException extends DragonAPIException {

	public ModReflectionException(DragonAPIMod mod, ModList target, String msg) {
		message.append(mod.getDisplayName()+" had an error reading "+target.getDisplayName()+":\n");
		message.append(msg+"\n");
		message.append("Please notify "+mod.getModAuthorName()+" as soon as possible, and include your version of "+target.getDisplayName());
		this.crash();
	}

}
