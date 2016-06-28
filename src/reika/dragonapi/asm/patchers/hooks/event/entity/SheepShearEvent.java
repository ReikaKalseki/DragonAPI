package reika.dragonapi.asm.patchers.hooks.event.entity;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import reika.dragonapi.asm.patchers.Patcher;
import reika.dragonapi.libraries.java.ReikaASMHelper;

public class SheepShearEvent extends Patcher {

	public SheepShearEvent() {
		super("net.minecraft.entity.passive.EntitySheep", "wp");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "onSheared", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/IBlockAccess;IIII)Ljava/util/ArrayList;");
		m.instructions.clear();
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 6));
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/SheepShearEvent", "fire", "(Lnet/minecraft/entity/passive/EntitySheep;Lnet/minecraft/item/ItemStack;I)Ljava/util/ArrayList;", false));
		m.instructions.add(new InsnNode(Opcodes.ARETURN));
	}
}
