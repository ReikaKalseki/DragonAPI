/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Render;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class SkyColorEvent extends Patcher {

	public SkyColorEvent() {
		super("net.minecraftforge.client.ForgeHooksClient");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "getSkyBlendColour", "(Lnet/minecraft/world/World;III)I");
		//VarInsnNode vin = (VarInsnNode)ReikaASMHelper.getLastOpcode(m.instructions, Opcodes.ISTORE);
		//int var = vin.var;
		m.instructions.insertBefore(ReikaASMHelper.getFirstOpcode(m.instructions, Opcodes.IRETURN), new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/SkyColorEvent", "fire", "(I)I", false));
		m.instructions.insertBefore(ReikaASMHelper.getLastOpcode(m.instructions, Opcodes.ISTORE), new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/SkyColorEvent", "fire", "(I)I", false));
	}

}
