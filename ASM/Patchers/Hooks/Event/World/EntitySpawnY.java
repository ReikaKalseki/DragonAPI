package Reika.DragonAPI.ASM.Patchers.Hooks.Event.World;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class EntitySpawnY extends Patcher {

	public EntitySpawnY() {
		super("net.minecraft.world.SpawnerAnimals", "aho");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_151350_a", "(Lnet/minecraft/world/World;II)Lnet/minecraft/world/ChunkPosition;");
		VarInsnNode vin = (VarInsnNode)ReikaASMHelper.getLastOpcode(m.instructions, Opcodes.ISTORE);
		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0)); //world
		li.add(new VarInsnNode(Opcodes.ILOAD, vin.var-2)); //x
		li.add(new VarInsnNode(Opcodes.ILOAD, vin.var));  // y
		li.add(new VarInsnNode(Opcodes.ILOAD, vin.var-1)); //z
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/GetYToSpawnMobEvent", "fire", "(Lnet/minecraft/world/World;III)I", false));
		li.add(new VarInsnNode(Opcodes.ISTORE, vin.var));
		m.instructions.insert(vin, li);
	}

}
