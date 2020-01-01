/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers.Hooks;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraftforge.classloading.FMLForgePlugin;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class ThirdPersonDistanceDispatch extends Patcher {

	private static final boolean betweenlands = ReikaASMHelper.checkForClass("thebetweenlands.TheBetweenlands");

	public ThirdPersonDistanceDispatch() {
		super("net.minecraft.client.renderer.EntityRenderer", "blt");
	}

	@Override
	protected void apply(ClassNode cn) {
		if (betweenlands)
			return;
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_78467_g", "orientCamera", "(F)V");

		AbstractInsnNode ain = ReikaASMHelper.getFirstFieldCallByName(cn, m, FMLForgePlugin.RUNTIME_DEOBF ? "field_78490_B" : "thirdPersonDistance");
		ain = ReikaASMHelper.getFirstOpcodeAfter(m.instructions, m.instructions.indexOf(ain), Opcodes.DSTORE);
		int var = ((VarInsnNode)ain).var;

		String sin = FMLForgePlugin.RUNTIME_DEOBF ? "func_76126_a" : "sin";
		boolean primed = false;
		for (int i = m.instructions.indexOf(ain); i < m.instructions.size(); i++) {
			ain = m.instructions.get(i);
			if (ain.getOpcode() == Opcodes.INVOKESTATIC) {
				MethodInsnNode min = (MethodInsnNode)ain;
				if (primed) {
					if (min.name.equals("glTranslatef")) {
						break;
					}
				}
				else {
					if (min.name.equals(sin)) {
						primed = true;
					}
				}
			}
		}
		ain = ReikaASMHelper.getLastOpcodeBefore(m.instructions, m.instructions.indexOf(ain), Opcodes.INVOKESTATIC);

		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.DLOAD, var));
		li.add(new FieldInsnNode(Opcodes.PUTSTATIC, "Reika/DragonAPI/Libraries/IO/ReikaRenderHelper", "thirdPersonDistance", "D"));
		m.instructions.insert(ain, li);
	}

}
