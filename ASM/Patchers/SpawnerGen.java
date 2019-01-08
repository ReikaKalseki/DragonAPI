package Reika.DragonAPI.ASM.Patchers;

import net.minecraftforge.classloading.FMLForgePlugin;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.Instantiable.Event.SpawnerGenerationEvent.SpawnerSource;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public abstract class SpawnerGen extends Patcher {

	public SpawnerGen(String deobf, String obf) {
		super(deobf, obf);
	}

	@Override
	protected final void apply(ClassNode cn) {
		MethodNode m = this.getMethod(cn);

		AbstractInsnNode ain = ReikaASMHelper.getFirstMethodCall(cn, m, "net/minecraft/tileentity/MobSpawnerBaseLogic", FMLForgePlugin.RUNTIME_DEOBF ? "func_98272_a" : "setEntityName", "(Ljava/lang/String;)V");
		VarInsnNode spawner = (VarInsnNode)ReikaASMHelper.getLastOpcodeBefore(m.instructions, m.instructions.indexOf(ain), Opcodes.ALOAD);
		VarInsnNode z = (VarInsnNode)ReikaASMHelper.getLastOpcodeBefore(m.instructions, m.instructions.indexOf(spawner), Opcodes.ILOAD);
		VarInsnNode y = (VarInsnNode)z.getPrevious();
		VarInsnNode x = (VarInsnNode)y.getPrevious();
		VarInsnNode world = (VarInsnNode)x.getPrevious();

		InsnList li = new InsnList();
		li.add(new FieldInsnNode(Opcodes.GETSTATIC, "Reika/DragonAPI/Instantiable/Event/SpawnerGenerationEvent$SpawnerSource", this.getEnumEntry().name(), "LReika/DragonAPI/Instantiable/Event/SpawnerGenerationEvent$SpawnerSource;"));
		li.add(new VarInsnNode(Opcodes.ALOAD, world.var));
		li.add(new VarInsnNode(Opcodes.ILOAD, x.var));
		li.add(new VarInsnNode(Opcodes.ILOAD, y.var));
		li.add(new VarInsnNode(Opcodes.ILOAD, z.var));
		li.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "Reika/DragonAPI/Instantiable/Event/SpawnerGenerationEvent$SpawnerSource", "fire", "(Lnet/minecraft/world/World;III)V", false));

		m.instructions.insert(ain, li);
	}

	protected abstract MethodNode getMethod(ClassNode cn);
	protected abstract SpawnerSource getEnumEntry();

}
