/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract;

import java.util.ArrayList;

import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.block.Block;
import Reika.DragonAPI.Interfaces.BlockEnum;

public class LegacyWailaHelper {

	private static final ArrayList<BlockEnum> wailaData = new ArrayList();

	public static void registerObjects(IWailaRegistrar reg) {
		for (int i = 0; i < wailaData.size(); i++) {
			BlockEnum r = wailaData.get(i);
			IWailaDataProvider b = (IWailaDataProvider)r.getBlockInstance();
			Class<?extends Block> c = r.getObjectClass();
			//reg.registerHeadProvider(b, c);
			reg.registerBodyProvider(b, c);
			//reg.registerTailProvider(b, c);
			//reg.registerStackProvider(b, c);
		}
	}

	public static void registerLegacyWAILACompat(BlockEnum r) {
		wailaData.add(r);
	}

}
