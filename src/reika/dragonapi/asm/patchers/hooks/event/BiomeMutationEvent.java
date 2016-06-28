package reika.dragonapi.asm.patchers.hooks.event;

import net.minecraftforge.classloading.FMLForgePlugin;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import reika.dragonapi.asm.patchers.Patcher;
import reika.dragonapi.libraries.java.ReikaASMHelper;

public class BiomeMutationEvent extends Patcher {

	public BiomeMutationEvent() {
		super("net.minecraft.world.gen.layer.GenLayerHills", "axr");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_75904_a", "getInts", "(IIII)[I");
		//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));
		String get = FMLForgePlugin.RUNTIME_DEOBF ? "func_150568_d" : "getBiome";
		Object[] patt = {
				Opcodes.ILOAD,
				new IntInsnNode(Opcodes.SIPUSH, 128),
				new InsnNode(Opcodes.IADD),
				new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/world/biome/BiomeGenBase", get, "(I)Lnet/minecraft/world/biome/BiomeGenBase;", false),
				Opcodes.IFNULL
		};
		VarInsnNode id = (VarInsnNode)ReikaASMHelper.getPattern(m.instructions, patt);
		JumpInsnNode jump = (JumpInsnNode)m.instructions.get(m.instructions.indexOf(id)+patt.length-1);
		int var = id.var; //10

		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new VarInsnNode(Opcodes.ILOAD, 1));
		li.add(new VarInsnNode(Opcodes.ILOAD, 2));
		li.add(new VarInsnNode(Opcodes.ILOAD, 8));
		li.add(new VarInsnNode(Opcodes.ILOAD, 9));
		li.add(new VarInsnNode(Opcodes.ILOAD, var));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/BiomeMutationEvent", "fireTry", "(Lnet/minecraft/world/gen/layer/GenLayer;IIIII)Z", false));

		ReikaASMHelper.changeOpcode(jump, Opcodes.IFEQ);
		ReikaASMHelper.deleteFrom(m.instructions, id, jump.getPrevious());
		m.instructions.insertBefore(jump, li);


		AbstractInsnNode end = ReikaASMHelper.getFirstOpcode(m.instructions, Opcodes.IASTORE);
		AbstractInsnNode start = ReikaASMHelper.getLastInsnBefore(m.instructions, m.instructions.indexOf(end), Opcodes.ILOAD, var);
		AbstractInsnNode pre = start.getPrevious();

		li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new VarInsnNode(Opcodes.ILOAD, 1));
		li.add(new VarInsnNode(Opcodes.ILOAD, 2));
		li.add(new VarInsnNode(Opcodes.ILOAD, 8));
		li.add(new VarInsnNode(Opcodes.ILOAD, 9));
		li.add(new VarInsnNode(Opcodes.ILOAD, var));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/BiomeMutationEvent$GetMutatedBiomeEvent", "fireGet", "(Lnet/minecraft/world/gen/layer/GenLayer;IIIII)I", false));
		li.add(new InsnNode(Opcodes.IASTORE));
		ReikaASMHelper.deleteFrom(m.instructions, start, end);
		m.instructions.insert(pre, li);


		id = (VarInsnNode)ReikaASMHelper.getPattern(m.instructions, patt);
		jump = (JumpInsnNode)m.instructions.get(m.instructions.indexOf(id)+patt.length-1);
		var = id.var; //13

		li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new VarInsnNode(Opcodes.ILOAD, 1));
		li.add(new VarInsnNode(Opcodes.ILOAD, 2));
		li.add(new VarInsnNode(Opcodes.ILOAD, 8));
		li.add(new VarInsnNode(Opcodes.ILOAD, 9));
		li.add(new VarInsnNode(Opcodes.ILOAD, var));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/BiomeMutationEvent", "fireTry", "(Lnet/minecraft/world/gen/layer/GenLayer;IIIII)Z", false));

		ReikaASMHelper.changeOpcode(jump, Opcodes.IFEQ);
		ReikaASMHelper.deleteFrom(m.instructions, id, jump.getPrevious());
		m.instructions.insertBefore(jump, li);


		AbstractInsnNode ain = ReikaASMHelper.getFirstInsnAfter(m.instructions, 0, Opcodes.IINC, var, 128);
		if (ain == null) {
			ReikaASMHelper.log("Could not find normal IINC "+var+" 128 Insn. Checking for alternate.");
			ain = ReikaASMHelper.getLastInsn(m.instructions, Opcodes.SIPUSH, 128);
			start = ain.getPrevious();
			end = ReikaASMHelper.getFirstOpcodeAfter(m.instructions, m.instructions.indexOf(ain), Opcodes.ISTORE);
		}
		else {
			start = ain;
			end = start;
		}
		pre = start.getPrevious();

		li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new VarInsnNode(Opcodes.ILOAD, 1));
		li.add(new VarInsnNode(Opcodes.ILOAD, 2));
		li.add(new VarInsnNode(Opcodes.ILOAD, 8));
		li.add(new VarInsnNode(Opcodes.ILOAD, 9));
		li.add(new VarInsnNode(Opcodes.ILOAD, var));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/BiomeMutationEvent$GetMutatedBiomeEvent", "fireGet", "(Lnet/minecraft/world/gen/layer/GenLayer;IIIII)I", false));
		li.add(new VarInsnNode(Opcodes.ISTORE, var));
		ReikaASMHelper.deleteFrom(m.instructions, start, end);
		m.instructions.insert(pre, li);
		//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));
	}
}
