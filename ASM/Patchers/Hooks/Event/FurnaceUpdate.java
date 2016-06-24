package Reika.DragonAPI.ASM.Patchers.Hooks.Event;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class FurnaceUpdate extends Patcher {

	public FurnaceUpdate() {
		super("net.minecraft.tileentity.TileEntityFurnace", "apg");
	}

	@Override
	protected void apply(ClassNode cn) {
		InsnList pre = new InsnList();
		LabelNode L1 = new LabelNode();
		LabelNode L2 = new LabelNode();
		pre.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
		pre.add(new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/FurnaceUpdateEvent$Pre"));
		pre.add(new InsnNode(Opcodes.DUP));
		pre.add(new VarInsnNode(Opcodes.ALOAD, 0));
		pre.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/FurnaceUpdateEvent$Pre", "<init>", "(Lnet/minecraft/tileentity/TileEntityFurnace;)V", false));
		pre.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
		pre.add(new JumpInsnNode(Opcodes.IFEQ, L1));
		pre.add(L2);
		pre.add(new InsnNode(Opcodes.RETURN));
		pre.add(L1);

		InsnList post = new InsnList();
		post.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
		post.add(new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/FurnaceUpdateEvent$Post"));
		post.add(new InsnNode(Opcodes.DUP));
		post.add(new VarInsnNode(Opcodes.ALOAD, 0));
		post.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/FurnaceUpdateEvent$Post", "<init>", "(Lnet/minecraft/tileentity/TileEntityFurnace;)V", false));
		post.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
		post.add(new InsnNode(Opcodes.POP));

		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_145845_h", "updateEntity", "()V");
		AbstractInsnNode ret = ReikaASMHelper.getLastInsn(m.instructions, Opcodes.RETURN);
		m.instructions.insert(pre);
		m.instructions.insertBefore(ret, post);
	}
}
