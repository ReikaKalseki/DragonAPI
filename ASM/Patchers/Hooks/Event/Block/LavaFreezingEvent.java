package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Block;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraftforge.classloading.FMLForgePlugin;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class LavaFreezingEvent extends Patcher {

	public LavaFreezingEvent() {
		super("net.minecraft.block.BlockLiquid", "alw");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_149805_n", "func_149805_n", "(Lnet/minecraft/world/World;III)V");
		String func = FMLForgePlugin.RUNTIME_DEOBF ? "func_147465_d" : "setBlock";
		for (int i = 0; i < m.instructions.size(); i++) {
			AbstractInsnNode ain = m.instructions.get(i);
			if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL) {
				MethodInsnNode min = (MethodInsnNode)ain;
				if (func.equals(min.name)) {
					min.setOpcode(Opcodes.INVOKESTATIC);
					min.owner = "Reika/DragonAPI/Instantiable/Event/LavaFreezeEvent";
					min.name = "fire";
					min.desc = ReikaASMHelper.addLeadingArgument(min.desc, "Lnet/minecraft/world/World;");
				}
			}
		}
	}

}
