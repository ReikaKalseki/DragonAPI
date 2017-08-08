/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Exception;

import net.minecraft.item.ItemStack;
import Reika.DragonAPI.Auxiliary.Trackers.EnvironmentSanityChecker;



public class EnvironmentSanityException extends DragonAPIException {

	public EnvironmentSanityException(ErrorType type, Object... data) {
		message.append(type.getString(data));
		Exception e = type.getException(data);
		if (e != null)
			this.initCause(e);
		this.crash();
	}

	public static enum ErrorType {
		NULLREG(),
		NULLENTRY(),
		IDMISMATCH(),
		INVALIDVALUE(),
		UNPARSEABLE(),
		LOOT(),
		OREDICT();

		public String getString(Object... data) {
			switch(this) {
				case NULLREG:
					return data[0]+" ("+data[0].getClass()+") was assigned a null name in the GameRegistry!";
				case NULLENTRY:
					return "Null was registered to the GameRegistry as '"+data[0]+"'!";
				case IDMISMATCH:
					return data[0]+" ("+data[0].getClass()+") occupies an ID ("+data[1]+") that does not match its stored value ("+data[2]+")!";
				case INVALIDVALUE:
					return data[0]+" ("+data[0].getClass()+") returns an invalid ("+data[1]+") value for a critical field or function ('"+data[2]+"')!";
				case UNPARSEABLE:
					return data[0]+" ("+data[0].getClass()+") throws an exception ("+data[1]+") when trying to parse it for '"+data[2]+"'! This is almost certainly caused by an illegal internal state.";
				case OREDICT:
					return EnvironmentSanityChecker.getSafeItemString((ItemStack)data[0])+" ("+data[0].getClass()+") registered to the OreDict as '"+data[1]+"', but is an invalid item, throwing "+data[2]+" when parsing '"+data[3]+"'!";
				case LOOT:
					return EnvironmentSanityChecker.getSafeItemString((ItemStack)data[0])+" was registered to the loot table '"+data[1]+"', and is invalid: "+data[2];
			}
			return "";
		}

		private Exception getException(Object... data) {
			switch(this) {
				case UNPARSEABLE:
					return (Exception)data[1];
				case OREDICT:
					return (Exception)data[2];
				default:
					return null;
			}
		}
	}

}
