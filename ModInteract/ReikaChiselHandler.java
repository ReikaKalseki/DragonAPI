package Reika.DragonAPI.ModInteract;

import java.util.ArrayList;
import java.util.Collection;

import com.cricketcraft.chisel.api.carving.CarvingUtils;
import com.cricketcraft.chisel.api.carving.ICarvingGroup;
import com.cricketcraft.chisel.api.carving.ICarvingVariation;

import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class ReikaChiselHandler {

	public static Collection<ItemStack> getChiselableVariants(ItemStack is) {
		return getChiselableVariants(Block.getBlockFromItem(is.getItem()), is.getItemDamage());
	}

	public static Collection<ItemStack> getChiselableVariants(Block b, int meta) {
		Collection<ItemStack> c = new ArrayList();
		ICarvingGroup grp = CarvingUtils.chisel.getGroup(b, meta);
		if (grp == null)
			return c;
		for (ICarvingVariation var : grp.getVariations()) {
			c.add(new ItemStack(var.getBlock(), var.getItemMeta()));
		}
		return c;
	}

	public static int getChiselableSource(IInventory ii, Block b, int meta) {
		ItemHashMap<Integer> map = ItemHashMap.locateFromInventory(ii);
		Collection<ItemStack> look = getChiselableVariants(b, meta);
		for (ItemStack is : look) {
			Integer get = map.get(is);
			if (get != null) {
				return get.intValue();
			}
		}
		return -1;
	}

}
