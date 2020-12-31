package Reika.DragonAPI.ASM.Patchers.Fixes;

import java.lang.reflect.Modifier;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class PotentialSpawnRefLeak extends Patcher {

	public PotentialSpawnRefLeak() {
		super("net.minecraftforge.event.world.WorldEvent$PotentialSpawns");
	}

	@Override
	protected void apply(ClassNode cn) {
		FieldNode f = ReikaASMHelper.getFieldByName(cn, "list");
		f.access &= ~Modifier.FINAL;

		MethodNode m = ReikaASMHelper.getMethodByName(cn, "<init>", "(Lnet/minecraft/world/World;Lnet/minecraft/entity/EnumCreatureType;IIILjava/util/List;)V");
		FieldInsnNode fin = ReikaASMHelper.getNthFieldCallByName(cn, m, "list", 1);
		VarInsnNode localList = (VarInsnNode)fin.getPrevious();
		m.instructions.remove(localList);
		InsnList li = new InsnList();
		li.add(new TypeInsnNode(Opcodes.NEW, "java/util/ArrayList"));
		li.add(new InsnNode(Opcodes.DUP));
		li.add(new VarInsnNode(Opcodes.ALOAD, localList.var));
		li.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/ArrayList", "<init>", "(Ljava/util/Collection;)V", false));
		m.instructions.insertBefore(fin, li);
	}

}
