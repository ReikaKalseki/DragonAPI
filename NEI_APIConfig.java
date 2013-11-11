package Reika.DragonAPI;

import Reika.DragonAPI.Auxiliary.ExpandedOreHandler;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;

public class NEI_APIConfig implements IConfigureNEI {

	private static final ExpandedOreHandler expanded = new ExpandedOreHandler();

	@Override
	public void loadConfig() {
		DragonAPIInit.instance.getModLogger().log("Loading NEI Compatibility!");

		API.registerRecipeHandler(expanded);
		API.registerUsageHandler(expanded);
	}

	@Override
	public String getName() {
		return "DragonAPI NEI Handlers";
	}

	@Override
	public String getVersion() {
		return "Gamma";
	}

}
