package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Entity.Player;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class PostItemUseEvent extends Patcher {

	public PostItemUseEvent() {
		super("net.minecraft.item.ItemStack", "add");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_77943_a", "tryPlaceItemIntoWorld", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;IIIIFFF)Z");
		AbstractInsnNode ain = ReikaASMHelper.getLastOpcode(m.instructions, Opcodes.INVOKEVIRTUAL);
		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new VarInsnNode(Opcodes.ALOAD, 1));
		li.add(new VarInsnNode(Opcodes.ALOAD, 2));
		li.add(new VarInsnNode(Opcodes.ILOAD, 3));
		li.add(new VarInsnNode(Opcodes.ILOAD, 4));
		li.add(new VarInsnNode(Opcodes.ILOAD, 5));
		li.add(new VarInsnNode(Opcodes.ILOAD, 6));
		li.add(new VarInsnNode(Opcodes.FLOAD, 7));
		li.add(new VarInsnNode(Opcodes.FLOAD, 8));
		li.add(new VarInsnNode(Opcodes.FLOAD, 9));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/PostItemUseEvent", "fire", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;IIIIFFF)V", false));
		m.instructions.insert(ain, li);
		//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));
	}
}
