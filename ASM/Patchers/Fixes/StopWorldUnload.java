/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers.Fixes;

import net.minecraftforge.classloading.FMLForgePlugin;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class StopWorldUnload extends Patcher {

	public StopWorldUnload() {
		super("net.minecraft.world.gen.ChunkProviderServer", "ms");
	}

	@Override
	public void apply(ClassNode cn) {
		MethodNode mn = ReikaASMHelper.getMethodByName(cn, "func_73156_b", "unloadQueuedChunks", "()Z");
		MethodInsnNode node = ReikaASMHelper.getFirstMethodCall(cn, mn, "net/minecraftforge/common/DimensionManager", "shouldLoadSpawn", "(I)Z");
		LabelNode jumpLabel = ((JumpInsnNode) node.getNext()).label;
		String playerFieldName = FMLForgePlugin.RUNTIME_DEOBF ? "field_73010_i" : "playerEntities";
		String worldFieldName = FMLForgePlugin.RUNTIME_DEOBF ? "field_73251_h" : "worldObj";

		InsnList list = new InsnList();
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/world/gen/ChunkProviderServer", worldFieldName, "Lnet/minecraft/world/WorldServer;"));
		list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/world/World", playerFieldName, "Ljava/util/List;"));
		list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/util/List", "isEmpty", "()Z", true));
		list.add(new JumpInsnNode(Opcodes.IFEQ, jumpLabel));
		mn.instructions.insert(node.getNext(), list);
	}

}

