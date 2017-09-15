/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers.Hooks.Event;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

//@Deprecated
public class AddCraftingEvent extends Patcher {

	public AddCraftingEvent() { //replace list with one that fires events
		super("net.minecraft.item.crafting.CraftingManager", "afe");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "<init>", "()V");
		TypeInsnNode type = (TypeInsnNode)ReikaASMHelper.getFirstOpcode(m.instructions, Opcodes.NEW);
		MethodInsnNode cons = (MethodInsnNode)type.getNext().getNext();

		String s = "Reika/DragonAPI/Instantiable/Data/Collections/EventRecipeList";

		type.desc = s;
		cons.owner = s;
	}
}
