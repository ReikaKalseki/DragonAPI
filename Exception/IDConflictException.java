/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Exception;

import java.util.ArrayList;

import net.minecraft.item.Item;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.DragonAPIMod;

public class IDConflictException extends DragonAPIException {

	public IDConflictException(DragonAPIMod mod, String msg) {
		message.append(mod.getDisplayName()+" was not installed correctly:\n");
		message.append("CONFLICT: "+msg+"\n");
		message.append("Check your IDs and change them if possible.\n");
		//if (mod.getDocumentationSite() != null)
		//	message.append("This is NOT a mod bug. Do not post it to "+mod.getDocumentationSite().toString());
		//else
		message.append("This is NOT a mod bug. Do not post it to the mod website.");
		this.crash();
	}

	public IDConflictException(ModList mod, String msg) {
		message.append(mod.getDisplayName()+" was not installed correctly:\n");
		message.append("CONFLICT: "+msg+"\n");
		message.append("Check your IDs and change them if possible.\n");
		//if (mod.getDocumentationSite() != null)
		//	message.append("This is NOT a mod bug. Do not post it to "+mod.getDocumentationSite().toString());
		//else
		message.append("This is NOT a mod bug. Do not post it to the mod website.");
		this.crash();
	}

	public IDConflictException(String msg) {
		message.append("The mods were not installed correctly:\n");
		message.append("CONFLICT: "+msg+"\n");
		message.append("Check your IDs and change them if possible.\n");
		//if (mod.getDocumentationSite() != null)
		//	message.append("This is NOT a mod bug. Do not post it to "+mod.getDocumentationSite().toString());
		//else
		message.append("This is NOT a mod bug. Do not post it to the mod website.");
		this.crash();
	}

	public IDConflictException(ArrayList<ItemConflict> items) {
		message.append("The mods were not installed correctly:\n");
		message.append("The following ID conflicts were detected:\n");
		for (int i = 0; i < items.size(); i++) {
			ItemConflict it = items.get(i);
			message.append(it.toString()+"\n");
		}
		message.append("Check your IDs and change them if possible.\n");
		message.append("This is NOT a mod bug. Do not post it to the mod website.");
		this.crash();
	}

	public static class ItemConflict {

		private final int itemID;
		private final Item original;
		private final Item overwriter;

		public ItemConflict(int id, Item orig, Item over) {
			itemID = id;
			original = orig;
			overwriter = over;
		}

		@Override
		public String toString() {
			return "ID "+itemID+": "+original.getUnlocalizedName()+" is being overwritten by "+overwriter.getUnlocalizedName();
		}

	}
}
