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

import Reika.DragonAPI.Base.DragonAPIMod;

import java.lang.reflect.Method;
import java.util.Arrays;

import net.minecraft.block.Block;

public class VanillaIntegrityException extends DragonAPIException {

	public VanillaIntegrityException(DragonAPIMod mod, String missing) {
		message.append(missing+" was deleted by another mod!\n");
		message.append(mod.getDisplayName()+" relies on this "+missing+", and thus you must fix that other mod or remove it.\n");
		this.crash();
	}

	public VanillaIntegrityException(Class<? extends Block> cl, int id, boolean isBlock) {
		String sg = isBlock ? "block" : "item";
		message.append("A "+sg+" was deleted from vanilla minecraft!\n");
		message.append("ID "+id+" (normally "+cl.getSimpleName()+") was deleted by another mod!\n");
		message.append("One or more of the DragonAPI mods rely on this "+sg+", and thus you must fix that other mod or remove it.\n");
		this.crash();
	}

	public VanillaIntegrityException(Method m) {
		message.append("The function "+m+" was deleted from forge/vanilla!\n");
		message.append("If you are using MCPC, try to replicate the crash in SSP!\n");
		message.append("If the crash does not occur there, you must fix your copy of MCPC.\n");
		message.append("One or more of the DragonAPI mods rely on this function.\n");
		this.crash();
	}

	public VanillaIntegrityException(String methodName, Class src, Class... argTypes) {
		message.append("A function was deleted from forge/vanilla!\n");
		message.append("Could not find "+methodName+"("+Arrays.toString(argTypes)+" in class "+src.getCanonicalName()+"\n");
		message.append("If you are using MCPC, try to replicate the crash in SSP!\n");
		message.append("If the crash does not occur there, you must fix your copy of MCPC.\n");
		message.append("One or more of the DragonAPI mods rely on this function.\n");
		this.crash();
	}

}
