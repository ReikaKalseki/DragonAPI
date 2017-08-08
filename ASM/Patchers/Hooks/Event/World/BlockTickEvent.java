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
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.DragonAPIClassTransformer;
import Reika.DragonAPI.ASM.DragonAPIClassTransformer.BukkitBitflags;
import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class BlockTickEvent extends Patcher {

	public BlockTickEvent() {
		super("net.minecraft.world.WorldServer", "mt");
	}

	@Override
	protected void apply(ClassNode cn) {
		String sig = "(Lnet/minecraft/world/World;IIILjava/util/Random;)V";
		String func = FMLForgePlugin.RUNTIME_DEOBF ? "func_149674_a" : "updateTick";
		int shift = ((DragonAPIClassTransformer.getBukkitFlags() & (BukkitBitflags.CAULDRON.flag | BukkitBitflags.THERMOS.flag)) != 0) ? 3 : 0;

		InsnList fire = new InsnList();
		fire.add(new VarInsnNode(Opcodes.ALOAD, 0));
		fire.add(new VarInsnNode(Opcodes.ILOAD, 16+shift));
		fire.add(new VarInsnNode(Opcodes.ILOAD, 5+shift));
		fire.add(new InsnNode(Opcodes.IADD));
		fire.add(new VarInsnNode(Opcodes.ILOAD, 18+shift));
		fire.add(new VarInsnNode(Opcodes.ALOAD, 13+shift));
		String getY = FMLForgePlugin.RUNTIME_DEOBF ? "func_76662_d" : "getYLocation";
		fire.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/world/chunk/storage/ExtendedBlockStorage", getY, "()I", false));
		fire.add(new InsnNode(Opcodes.IADD));
		fire.add(new VarInsnNode(Opcodes.ILOAD, 17+shift));
		fire.add(new VarInsnNode(Opcodes.ILOAD, 6+shift));
		fire.add(new InsnNode(Opcodes.IADD));
		fire.add(new VarInsnNode(Opcodes.ALOAD, 19+shift));
		fire.add(new FieldInsnNode(Opcodes.GETSTATIC, "Reika/DragonAPI/Instantiable/Event/BlockTickEvent$UpdateFlags", "NATURAL", "LReika/DragonAPI/Instantiable/Event/BlockTickEvent$UpdateFlags;"));
		fire.add(new FieldInsnNode(Opcodes.GETFIELD, "Reika/DragonAPI/Instantiable/Event/BlockTickEvent$UpdateFlags", "flag", "I"));
		fire.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/BlockTickEvent", "fire", "(Lnet/minecraft/world/World;IIILnet/minecraft/block/Block;I)V", false));
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_147456_g", "func_147456_g", "()V");
		AbstractInsnNode ain = ReikaASMHelper.getFirstMethodCall(cn, m, "net/minecraft/block/Block", func, sig);
		m.instructions.insert(ain, fire);

		String xc = FMLForgePlugin.RUNTIME_DEOBF ? "field_77183_a" : "xCoord";
		String yc = FMLForgePlugin.RUNTIME_DEOBF ? "field_77181_b" : "yCoord";
		String zc = FMLForgePlugin.RUNTIME_DEOBF ? "field_77182_c" : "zCoord";
		fire.clear();
		fire.add(new VarInsnNode(Opcodes.ALOAD, 0));
		fire.add(new VarInsnNode(Opcodes.ALOAD, 7));
		fire.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/world/NextTickListEntry", xc, "I"));
		fire.add(new VarInsnNode(Opcodes.ALOAD, 7));
		fire.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/world/NextTickListEntry", yc, "I"));
		fire.add(new VarInsnNode(Opcodes.ALOAD, 7));
		fire.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/world/NextTickListEntry", zc, "I"));
		fire.add(new VarInsnNode(Opcodes.ALOAD, 9));
		fire.add(new FieldInsnNode(Opcodes.GETSTATIC, "Reika/DragonAPI/Instantiable/Event/BlockTickEvent$UpdateFlags", "SCHEDULED", "LReika/DragonAPI/Instantiable/Event/BlockTickEvent$UpdateFlags;"));
		fire.add(new FieldInsnNode(Opcodes.GETFIELD, "Reika/DragonAPI/Instantiable/Event/BlockTickEvent$UpdateFlags", "flag", "I"));
		fire.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/BlockTickEvent", "fire", "(Lnet/minecraft/world/World;IIILnet/minecraft/block/Block;I)V", false));
		m = ReikaASMHelper.getMethodByName(cn, "func_147454_a", "scheduleBlockUpdateWithPriority", "(IIILnet/minecraft/block/Block;II)V");
		ain = ReikaASMHelper.getFirstMethodCall(cn, m, "net/minecraft/block/Block", func, sig);
		m.instructions.insert(ain, fire);

		fire.clear();
		fire.add(new VarInsnNode(Opcodes.ALOAD, 0));
		fire.add(new VarInsnNode(Opcodes.ALOAD, 4));
		fire.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/world/NextTickListEntry", xc, "I"));
		fire.add(new VarInsnNode(Opcodes.ALOAD, 4));
		fire.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/world/NextTickListEntry", yc, "I"));
		fire.add(new VarInsnNode(Opcodes.ALOAD, 4));
		fire.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/world/NextTickListEntry", zc, "I"));
		fire.add(new VarInsnNode(Opcodes.ALOAD, 6));
		fire.add(new FieldInsnNode(Opcodes.GETSTATIC, "Reika/DragonAPI/Instantiable/Event/BlockTickEvent$UpdateFlags", "SCHEDULED", "LReika/DragonAPI/Instantiable/Event/BlockTickEvent$UpdateFlags;"));
		fire.add(new FieldInsnNode(Opcodes.GETFIELD, "Reika/DragonAPI/Instantiable/Event/BlockTickEvent$UpdateFlags", "flag", "I"));
		fire.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/BlockTickEvent", "fire", "(Lnet/minecraft/world/World;IIILnet/minecraft/block/Block;I)V", false));
		m = ReikaASMHelper.getMethodByName(cn, "func_72955_a", "tickUpdates", "(Z)Z");
		ain = ReikaASMHelper.getFirstMethodCall(cn, m, "net/minecraft/block/Block", func, sig);
		m.instructions.insert(ain, fire);
	}
}
