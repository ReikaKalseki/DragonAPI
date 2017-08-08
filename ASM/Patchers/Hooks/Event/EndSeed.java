/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers.Hooks.Event;

import java.lang.reflect.Modifier;

import net.minecraftforge.classloading.FMLForgePlugin;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class EndSeed extends Patcher {

	public EndSeed() {
		super("net.minecraft.world.WorldProviderEnd", "aqr");
	}

	@Override
	protected void apply(ClassNode cn) {
		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/DimensionSeedEvent", "fire", "(Lnet/minecraft/world/WorldProvider;)J", false));
		li.add(new InsnNode(Opcodes.LRETURN));
		String name = FMLForgePlugin.RUNTIME_DEOBF ? "getSeed" : "getSeed"; //Forge
		ReikaASMHelper.addMethod(cn, li, name, "()J", Modifier.PUBLIC);
	}
}
