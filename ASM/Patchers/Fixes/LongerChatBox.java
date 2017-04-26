/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers.Fixes;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

@Deprecated
public class LongerChatBox extends Patcher {

	public LongerChatBox() {
		super("*net.minecraft.client.gui.GuiChat", "*bct");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_73866_w_", "initGui", "()V");
		IntInsnNode iin = (IntInsnNode)ReikaASMHelper.getFirstOpcode(m.instructions, Opcodes.BIPUSH);
		iin.operand = 400;
		iin.setOpcode(Opcodes.SIPUSH);
	}

}
