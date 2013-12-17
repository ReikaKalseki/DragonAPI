/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract;

import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.event.FMLInterModComms;


public class ReikaMystcraftHelper {

	public static void disableFluidPage(String name) {
		NBTTagCompound NBTMsg = new NBTTagCompound();
		NBTMsg.setCompoundTag("fluidsymbol", new NBTTagCompound());
		NBTMsg.getCompoundTag("fluidsymbol").setFloat("rarity", 0.0F);
		NBTMsg.getCompoundTag("fluidsymbol").setFloat("grammarweight", 0.0F);
		NBTMsg.getCompoundTag("fluidsymbol").setFloat("instabilityPerBlock", Float.MAX_VALUE);
		NBTMsg.getCompoundTag("fluidsymbol").setString("fluidname", name);
		FMLInterModComms.sendMessage("Mystcraft", "fluidsymbol", NBTMsg);
	}

}
