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

import net.minecraftforge.classloading.FMLForgePlugin;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class BossColorEvent extends Patcher {

	public BossColorEvent() {
		super("net.minecraft.client.renderer.EntityRenderer", "blt");
	}

	@Override
	protected void apply(ClassNode cn) {
		this.process(ReikaASMHelper.getMethodByName(cn, "func_78466_h", "updateFogColor", "(F)V"), false);
		this.process(ReikaASMHelper.getMethodByName(cn, "func_78472_g", "updateLightmap", "(F)V"), true);

		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_78464_a", "updateRenderer", "()V");
		String name = FMLForgePlugin.RUNTIME_DEOBF ? "field_82825_d" : "hasColorModifier";
		FieldInsnNode fin = ReikaASMHelper.getLastFieldRefBefore(m.instructions, m.instructions.size(), name);
		m.instructions.insert(fin, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/BossColorEvent", "reset", "()V", false));
	}

	private void process(MethodNode m, boolean lightMap) {
		String name = FMLForgePlugin.RUNTIME_DEOBF ? "field_82831_U" : "bossColorModifier";
		FieldInsnNode fin = ReikaASMHelper.getLastFieldRefBefore(m.instructions, m.instructions.size(), name);
		int idx = m.instructions.indexOf(fin);
		LdcInsnNode lin = (LdcInsnNode)ReikaASMHelper.getFirstOpcodeAfter(m.instructions, idx, Opcodes.LDC); //red 0.7F*
		ReikaASMHelper.replaceInstruction(m.instructions, lin, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/BossColorEvent", lightMap ? "fireAndReturnRed_Light" : "fireAndReturnRed", "()F", false));

		lin = (LdcInsnNode)ReikaASMHelper.getFirstOpcodeAfter(m.instructions, idx, Opcodes.LDC); //green 0.6F*
		ReikaASMHelper.replaceInstruction(m.instructions, lin, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/BossColorEvent", "returnGreen", "()F", false));

		lin = (LdcInsnNode)ReikaASMHelper.getFirstOpcodeAfter(m.instructions, idx, Opcodes.LDC); //blue 0.6F*
		ReikaASMHelper.replaceInstruction(m.instructions, lin, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/BossColorEvent", "returnBlue", "()F", false));
	}

}
