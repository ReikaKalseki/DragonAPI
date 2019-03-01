/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2018
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers.Hooks.Event.World.Gen;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraftforge.classloading.FMLForgePlugin;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class BiomeDecorationHook extends Patcher {

	public BiomeDecorationHook() {
		super("net.minecraft.world.gen.ChunkProviderGenerate", "aqz");
	}

	@Override
	public void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_73153_a", "populate", "(Lnet/minecraft/world/chunk/IChunkProvider;II)V");
		String name = FMLForgePlugin.RUNTIME_DEOBF ? "func_76728_a" : "decorate";
		MethodInsnNode min = ReikaASMHelper.getFirstMethodCall(cn, m, "net/minecraft/world/biome/BiomeGenBase", name, "(Lnet/minecraft/world/World;Ljava/util/Random;II)V");
		min.setOpcode(Opcodes.INVOKESTATIC);
		min.owner = "Reika/DragonAPI/Auxiliary/WorldGenInterceptionRegistry";
		min.name = "runBiomeDecorator";
		min.desc = ReikaASMHelper.addLeadingArgument(min.desc, "Lnet/minecraft/world/biome/BiomeGenBase;");
	}
}
