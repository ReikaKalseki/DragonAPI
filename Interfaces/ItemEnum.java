package Reika.DragonAPI.Interfaces;

import net.minecraft.item.Item;

/** This is an interface for ENUMS! */
public interface ItemEnum extends RegistrationList {

	public Item getItemInstance();

	public boolean overwritingItem();

}
