package Reika.DragonAPI.Libraries;

import java.util.HashMap;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;

public class ReikaDispenserHelper {

	public static void addDispenserAction(ItemStack is, IBehaviorDispenseItem action) {
		addDispenserAction(is, action, false);
	}

	public static void overrideDispenserAction(ItemStack is, IBehaviorDispenseItem action) {
		addDispenserAction(is, action, true);
	}

	private static void addDispenserAction(ItemStack is, IBehaviorDispenseItem action, boolean override) {
		Object o = BlockDispenser.dispenseBehaviorRegistry.getObject(is.getItem());
		boolean flag = o == null || o instanceof StackDataSensitiveEntry || o.getClass() == BehaviorDefaultDispenseItem.class;
		if (!flag && !override)
			return;
		StackDataSensitiveEntry e = flag && o.getClass() != BehaviorDefaultDispenseItem.class ? (StackDataSensitiveEntry)o : null;
		if (e == null) {
			e = new StackDataSensitiveEntry((IBehaviorDispenseItem)o);
			BlockDispenser.dispenseBehaviorRegistry.putObject(is.getItem(), e);
		}
		e.addAction(is, action);
	}

	public static void addDispenserAction(KeyedItemStack is, IBehaviorDispenseItem action) {
		addDispenserAction(is, action, false);
	}

	public static void overrideDispenserAction(KeyedItemStack is, IBehaviorDispenseItem action) {
		addDispenserAction(is, action, true);
	}

	private static void addDispenserAction(KeyedItemStack is, IBehaviorDispenseItem action, boolean override) {
		Object o = BlockDispenser.dispenseBehaviorRegistry.getObject(is.getItemStack().getItem());
		boolean flag = o == null || o instanceof StackDataSensitiveEntry || o.getClass() == BehaviorDefaultDispenseItem.class;
		if (!flag && !override)
			return;
		StackDataSensitiveEntry e = flag && o.getClass() != BehaviorDefaultDispenseItem.class ? (StackDataSensitiveEntry)o : null;
		if (e == null) {
			e = new StackDataSensitiveEntry((IBehaviorDispenseItem)o);
			BlockDispenser.dispenseBehaviorRegistry.putObject(is.getItemStack().getItem(), e);
		}
		e.addAction(is, action);
	}

	private static class StackDataSensitiveEntry implements IBehaviorDispenseItem {

		private final HashMap<KeyedItemStack, IBehaviorDispenseItem> actions = new HashMap();
		private final IBehaviorDispenseItem fallback;

		private StackDataSensitiveEntry(IBehaviorDispenseItem ibd) {
			fallback = ibd;
		}

		@Override
		public ItemStack dispense(IBlockSource ibs, ItemStack is) {
			KeyedItemStack ks = this.getKey(is);
			IBehaviorDispenseItem ibd = actions.get(ks);
			if (ibd == null)
				ibd = fallback;
			if (ibd != null) {
				return ibd.dispense(ibs, is);
			}
			return is;
		}

		private void addAction(ItemStack is, IBehaviorDispenseItem action) {
			KeyedItemStack ks = this.getKey(is);
			actions.put(ks, action);
		}

		private void addAction(KeyedItemStack ks, IBehaviorDispenseItem action) {
			actions.put(ks, action);
		}

		private KeyedItemStack getKey(ItemStack is) {
			return new KeyedItemStack(is).setSimpleHash(true).lock();
		}

	}

	public static IBehaviorDispenseItem bonemealEffect = new IBehaviorDispenseItem() {

		@Override
		public ItemStack dispense(IBlockSource ibs, ItemStack is) {
			EnumFacing facing = BlockDispenser.func_149937_b(ibs.getBlockMetadata());
			World world = ibs.getWorld();
			int x = ibs.getXInt()+facing.getFrontOffsetX();
			int y = ibs.getYInt()+facing.getFrontOffsetY();
			int z = ibs.getZInt()+facing.getFrontOffsetZ();

			if (ItemDye.func_150919_a(is, world, x, y, z)) {
				if (!world.isRemote) {
					world.playAuxSFX(2005, x, y, z, 0);
				}
			}

			return is;
		}

	};
	/*
	public static IBehaviorDispenseItem rightClickEffect = new IBehaviorDispenseItem() {

		@Override
		public ItemStack dispense(IBlockSource ibs, ItemStack is) {
			EnumFacing facing = BlockDispenser.func_149937_b(ibs.getBlockMetadata());
			World world = ibs.getWorld();
			int x = ibs.getXInt()+facing.getFrontOffsetX();
			int y = ibs.getYInt()+facing.getFrontOffsetY();
			int z = ibs.getZInt()+facing.getFrontOffsetZ();

			world.getBlock(x, y, z).onBlockActivated(world, x, y, z, null, ReikaDirectionHelper.getOpposite(facing).ordinal(), 0, 0, 0);

			return is;
		}

	};
	 */
}
