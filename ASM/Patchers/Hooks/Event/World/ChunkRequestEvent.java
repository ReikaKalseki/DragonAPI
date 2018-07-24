/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers.Hooks.Event.World;

import net.minecraftforge.classloading.FMLForgePlugin;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class ChunkRequestEvent extends Patcher {

	public ChunkRequestEvent() {
		super("net.minecraft.world.gen.ChunkProviderServer", "ms");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_73154_d", "provideChunk", "(II)Lnet/minecraft/world/chunk/Chunk;");
		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new FieldInsnNode(Opcodes.GETFIELD, cn.name, FMLForgePlugin.RUNTIME_DEOBF ? "field_73251_h" : "worldObj", "Lnet/minecraft/world/WorldServer;"));
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new VarInsnNode(Opcodes.ILOAD, 1));
		li.add(new VarInsnNode(Opcodes.ILOAD, 2));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/ChunkRequestEvent", "fire", "(Lnet/minecraft/world/WorldServer;Lnet/minecraft/world/gen/ChunkProviderServer;II)V", false));
		m.instructions.insert(li);
		//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));
	}

}
