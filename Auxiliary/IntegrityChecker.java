package Reika.DragonAPI.Auxiliary;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Exception.ModIntegrityException;
import Reika.DragonAPI.Interfaces.RegistryEnum;

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

	/** Returns true if mod integrity is preserved. */
	private boolean testIntegrity(DragonAPIMod mod) {
		RegistryEnum[] blocks = modBlocks.get(mod);
		RegistryEnum[] items = modItems.get(mod);
		for (int i = 0; i < blocks.length; i++) {
			RegistryEnum ir = blocks[i];
			if (Block.blocksList[ir.getID()] == null)
				return false;
			Block b = Block.blocksList[ir.getID()];
			if (ir.getObjectClass() != b.getClass())
				return false;
		}
		for (int i = 0; i < items.length; i++) {
			RegistryEnum ir = items[i];
			if (Item.itemsList[ir.getID()+256] == null)
				return false;
			Item b = Item.itemsList[ir.getID()+256];
			if (ir.getObjectClass() != b.getClass())
				return false;
		}
		return true;
	}

	public void testIntegrity() {
		for (DragonAPIMod mod : modBlocks.keySet()) {
			if (!this.testIntegrity(mod))
				throw new ModIntegrityException(mod);
		}
	}

}
