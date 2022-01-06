/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.ItemHandlers;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class MagicBeesHandler extends ModHandlerBase {

	private boolean init = false;

	private static final MagicBeesHandler instance = new MagicBeesHandler();

	public enum ItemEntry {
		;

		private final String tag;

		private static final ItemEntry[] list = values();

		private ItemEntry(String id) {
			tag = id;
		}

		public ItemStack getItem() {
			return ReikaItemHelper.lookupItem(tag);
		}
	}

	private MagicBeesHandler() {
		super();
		Block extra = null;
		if (this.hasMod()) {
			init = true;
		}
		else {
			this.noMod();
		}
	}

	public static MagicBeesHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return init;
	}

	@Override
	public ModList getMod() {
		return ModList.FORESTRY;
	}

	public enum Combs {
		MUNDANE,
		MOLTEN,
		OCCULT,
		OTHERWORLDLY,
		TRANSMUTING,
		PAPERY,
		SOUL,
		FURTIVE,
		MEMORY,
		TEMPORAL,
		FORGOTTEN,
		WINDY,
		FIERY,
		WATERY,
		EARTHY,
		GLITTERWINDY, //TC
		GLITTERFIERY,
		GLITTERWATERY,
		GLITTEREARTHY,
		GLITTERORDER,
		GLITTERENTROPY,
		ESSENCE, //ARSMAGICA
		POTENT,
		ELECTRIC, //TE
		CARBON,
		LUX,
		ENDEARING,
		;

		private Combs() {

		}

		public ItemStack getItem() {
			return ReikaItemHelper.lookupItem("MagicBees:comb:"+this.ordinal());
		}
	}

}
