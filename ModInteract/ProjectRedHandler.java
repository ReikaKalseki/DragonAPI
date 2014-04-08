package Reika.DragonAPI.ModInteract;

import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;

public class ProjectRedHandler extends ModHandlerBase {

	@Override
	public boolean initializedProperly() {
		return false;
	}

	@Override
	public ModList getMod() {
		return ModList.PROJRED;
	}

}
