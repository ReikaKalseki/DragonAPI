package Reika.DragonAPI.Interfaces;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

/** This is an interface for ENUMS! */
public interface BlockEnum extends RegistrationList {

	public Block getBlockInstance();

	public Class<? extends ItemBlock> getItemBlock();

	public boolean hasItemBlock();

	public Item getItem();

}
