/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary.Trackers;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Exception.ModIntegrityException;
import Reika.DragonAPI.Interfaces.Registry.BlockEnum;
import Reika.DragonAPI.Interfaces.Registry.ItemEnum;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;

public final class IntegrityChecker {

	public static final IntegrityChecker instance = new IntegrityChecker();

	private final HashMap<DragonAPIMod, BlockEnum[]> modBlocks = new HashMap();
	private final HashMap<DragonAPIMod, ItemEnum[]> modItems = new HashMap();

	private IntegrityChecker() {

	}

	public void addMod(DragonAPIMod mod, BlockEnum[] blocks, ItemEnum[] items) {
		modBlocks.put(mod, blocks);
		modItems.put(mod, items);
	}

	/** Returns null if mod integrity is preserved. */
	private Tamper testBlockIntegrity(DragonAPIMod mod) {
		BlockEnum[] blocks = modBlocks.get(mod);
		for (int i = 0; i < blocks.length; i++) {
			BlockEnum ir = blocks[i];
			if (!ir.isDummiedOut()) {
				Block b = ir.getBlockInstance();
				if (b == null)
					return new Tamper(TamperType.DELETION, ir.getBasicName());
				if (ir.getObjectClass() != b.getClass())
					return new Tamper(TamperType.OVERWRITE, ir.getBasicName());
			}
		}
		return null;
	}

	/** Returns null if mod integrity is preserved. */
	private Tamper testItemIntegrity(DragonAPIMod mod) {
		ItemEnum[] items = modItems.get(mod);
		for (int i = 0; i < items.length; i++) {
			ItemEnum ir = items[i];
			if (!ir.isDummiedOut()) {
				Item b = ir.getItemInstance();
				if (b == null)
					return new Tamper(TamperType.DELETION, ir.getBasicName());
				if (ir.getObjectClass() != b.getClass())
					return new Tamper(TamperType.OVERWRITE, ir.getBasicName());
			}
		}
		return null;
	}

	public void testIntegrity() {
		for (DragonAPIMod mod : modBlocks.keySet()) {
			Tamper t = this.testBlockIntegrity(mod);
			if (t != null) {
				throw new ModIntegrityException(mod, t.toString());
			}
		}
		for (DragonAPIMod mod : modItems.keySet()) {
			Tamper t = this.testItemIntegrity(mod);
			if (t != null) {
				throw new ModIntegrityException(mod, t.toString());
			}
		}
	}

	private class Tamper {

		public final TamperType editType;
		public final String tamperedElement;

		public Tamper(TamperType type, String element) {
			tamperedElement = element;
			editType = type;
		}

		@Override
		public String toString() {
			return ReikaStringParser.capFirstChar(editType.name())+" of "+tamperedElement;
		}
	}

	private enum TamperType {
		DELETION(),
		OVERWRITE();
	}

}
