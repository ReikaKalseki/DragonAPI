package Reika.DragonAPI.Instantiable;

import Reika.DragonAPI.Interfaces.BlockEnum;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public final class ItemBlockCustomLocalization extends ItemBlock {

	private BlockEnum object;

	public ItemBlockCustomLocalization(Block b) {
		super(b);
	}

	@Override
	public String getItemStackDisplayName(ItemStack is) {
		if (object == null) {
			ReikaJavaLibrary.pConsole(this+", block "+field_150939_a+" has a null block enum!");
			return is.getItem().getUnlocalizedName(is);
		}
		return object.hasMultiValuedName() ? object.getMultiValuedName(is.getItemDamage()) : object.getBasicName();
	}

	public void setEnumObject(BlockEnum b) {
		object = b;
	}

}
