package reika.dragonapi.asm.patchers.hooks.event.world;

import net.minecraftforge.classloading.FMLForgePlugin;

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
import reika.dragonapi.asm.patchers.Patcher;
import reika.dragonapi.libraries.java.ReikaASMHelper;

public class BurnBlock extends Patcher {

	public BurnBlock() {
		super("net.minecraft.block.BlockFire", "alb");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_149674_a", "updateTick", "(Lnet/minecraft/world/World;IIILjava/util/Random;)V");
		String func = FMLForgePlugin.RUNTIME_DEOBF ? "func_147465_d" : "setBlock";
		String func2 = FMLForgePlugin.RUNTIME_DEOBF ? "func_147468_f" : "setBlockToAir";
		String world = "net/minecraft/world/World";
		MethodInsnNode min = ReikaASMHelper.getFirstMethodCall(cn, m, world, func, "(IIILnet/minecraft/block/Block;II)Z");
		InsnList add = new InsnList();
		LabelNode L34 = new LabelNode();
		add.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
		add.add(new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/BlockConsumedByFireEvent"));
		add.add(new InsnNode(Opcodes.DUP));
		add.add(new VarInsnNode(Opcodes.ALOAD, 1));
		add.add(new VarInsnNode(Opcodes.ILOAD, 10));
		add.add(new VarInsnNode(Opcodes.ILOAD, 12));
		add.add(new VarInsnNode(Opcodes.ILOAD, 11));
		add.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/BlockConsumedByFireEvent", "<init>", "(Lnet/minecraft/world/World;III)V", false));
		add.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
		add.add(new JumpInsnNode(Opcodes.IFNE, L34));
		AbstractInsnNode loc1 = ReikaASMHelper.getLastInsnBefore(m.instructions, m.instructions.indexOf(min), Opcodes.ALOAD, 1);
		AbstractInsnNode loc2 = ReikaASMHelper.getFirstInsnAfter(m.instructions, m.instructions.indexOf(min), Opcodes.POP);
		m.instructions.insertBefore(loc1, add);
		m.instructions.insert(loc2, L34);

		m = ReikaASMHelper.getMethodByName(cn, /*"func_149841_a", */"tryCatchFire", "(Lnet/minecraft/world/World;IIIILjava/util/Random;ILnet/minecraftforge/common/util/ForgeDirection;)V"); // Forge one
		min = ReikaASMHelper.getFirstMethodCall(cn, m, world, func, "(IIILnet/minecraft/block/Block;II)Z");
		add = new InsnList();
		LabelNode L12 = new LabelNode();
		add.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
		add.add(new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/BlockConsumedByFireEvent"));
		add.add(new InsnNode(Opcodes.DUP));
		add.add(new VarInsnNode(Opcodes.ALOAD, 1));
		add.add(new VarInsnNode(Opcodes.ILOAD, 2));
		add.add(new VarInsnNode(Opcodes.ILOAD, 3));
		add.add(new VarInsnNode(Opcodes.ILOAD, 4));
		add.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/BlockConsumedByFireEvent", "<init>", "(Lnet/minecraft/world/World;III)V", false));
		add.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
		add.add(new JumpInsnNode(Opcodes.IFNE, L12));
		loc1 = ReikaASMHelper.getLastInsnBefore(m.instructions, m.instructions.indexOf(min), Opcodes.ALOAD, 1);
		loc2 = ReikaASMHelper.getFirstInsnAfter(m.instructions, m.instructions.indexOf(min), Opcodes.POP);
		m.instructions.insertBefore(loc1, add);
		m.instructions.insert(loc2, L12);

		min = ReikaASMHelper.getFirstMethodCall(cn, m, world, func2, "(III)Z");
		//add = ReikaASMHelper.copyInsnList(add, L12, L12);
		add = new InsnList();
		add.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
		add.add(new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/BlockConsumedByFireEvent"));
		add.add(new InsnNode(Opcodes.DUP));
		add.add(new VarInsnNode(Opcodes.ALOAD, 1));
		add.add(new VarInsnNode(Opcodes.ILOAD, 2));
		add.add(new VarInsnNode(Opcodes.ILOAD, 3));
		add.add(new VarInsnNode(Opcodes.ILOAD, 4));
		add.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/BlockConsumedByFireEvent", "<init>", "(Lnet/minecraft/world/World;III)V", false));
		add.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
		add.add(new JumpInsnNode(Opcodes.IFNE, L12));
		loc1 = ReikaASMHelper.getLastInsnBefore(m.instructions, m.instructions.indexOf(min), Opcodes.ALOAD, 1);
		loc2 = ReikaASMHelper.getFirstInsnAfter(m.instructions, m.instructions.indexOf(min), Opcodes.POP);
		m.instructions.insertBefore(loc1, add);
		//m.instructions.insert(loc2, L12);
	}
}
