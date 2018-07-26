package Reika.DragonAPI.ASM.Patchers.Hooks;

import java.util.ArrayList;
import java.util.Collection;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class BiomeProfiling extends Patcher {

	public BiomeProfiling() {
		super("net.minecraft.world.biome.BiomeGenBase", "ahu");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_150560_b", "genBiomeTerrain", "(Lnet/minecraft/world/World;Ljava/util/Random;[Lnet/minecraft/block/Block;[BIID)V");
		Collection<AbstractInsnNode> c = new ArrayList();
		for (int i = 0; i < m.instructions.size(); i++) {
			AbstractInsnNode ain = m.instructions.get(i);
			if (ain.getOpcode() == Opcodes.RETURN) {
				c.add(ain);
			}
		}
		this.inject(m, m.instructions.getFirst(), true);
		for (AbstractInsnNode ain : c)
			this.inject(m, ain, false);
	}

	private void inject(MethodNode m, AbstractInsnNode ain, boolean isPre) {
		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 1));
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new VarInsnNode(Opcodes.ILOAD, 5)); //x
		li.add(new VarInsnNode(Opcodes.ILOAD, 6)); //z
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Auxiliary/Trackers/WorldgenProfiler", isPre ? "startBiomeTerrain" : "finishBiomeTerrain", "(Lnet/minecraft/world/World;Lnet/minecraft/world/biome/BiomeGenBase;II)V", false));

		m.instructions.insertBefore(ain, li);
	}

}
