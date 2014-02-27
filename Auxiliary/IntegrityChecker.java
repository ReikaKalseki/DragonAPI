/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Exception.ModIntegrityException;
import Reika.DragonAPI.Interfaces.RegistryEnum;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;

public class IntegrityChecker {

	public static final IntegrityChecker instance = new IntegrityChecker();

	private final HashMap<DragonAPIMod, RegistryEnum[]> modBlocks = new HashMap();
	private final HashMap<DragonAPIMod, RegistryEnum[]> modItems = new HashMap();

	private IntegrityChecker() {

	}

	public void addMod(DragonAPIMod mod, RegistryEnum[] blocks, RegistryEnum[] items) {
		modBlocks.put(mod, blocks);
		modItems.put(mod, items);
	}

	/** Returns null if mod integrity is preserved. */
	private Tamper testBlockIntegrity(DragonAPIMod mod) {
		RegistryEnum[] blocks = modBlocks.get(mod);
		for (int i = 0; i < blocks.length; i++) {
			RegistryEnum ir = blocks[i];
			if (Block.blocksList[ir.getID()] == null)
				return new Tamper(TamperType.DELETION, ir.getBasicName());
			Block b = Block.blocksList[ir.getID()];
			if (ir.getObjectClass() != b.getClass())
				return new Tamper(TamperType.OVERWRITE, ir.getBasicName());
		}
		return null;
	}

	/** Returns null if mod integrity is preserved. */
	private Tamper testItemIntegrity(DragonAPIMod mod) {
		RegistryEnum[] items = modItems.get(mod);
		for (int i = 0; i < items.length; i++) {
			RegistryEnum ir = items[i];
			if (Item.itemsList[ir.getID()+256] == null)
				return new Tamper(TamperType.DELETION, ir.getBasicName());
			Item b = Item.itemsList[ir.getID()+256];
			if (ir.getObjectClass() != b.getClass())
				return new Tamper(TamperType.OVERWRITE, ir.getBasicName());
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
