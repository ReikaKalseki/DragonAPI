package Reika.DragonAPI.ASM.Patchers;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraftforge.classloading.FMLForgePlugin;

import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class MobSpawnerPatch extends Patcher {

	public MobSpawnerPatch() {
		super("net.minecraft.world.SpawnerAnimals", "aho");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_77192_a", "findChunksForSpawning", "(Lnet/minecraft/world/WorldServer;ZZZ)I");
		MethodInsnNode min = ReikaASMHelper.getFirstMethodCallByName(cn, m, FMLForgePlugin.RUNTIME_DEOBF ? "func_77190_a" : "canCreatureTypeSpawnAtLocation");
		min.owner = "Reika/DragonAPI/ASM/ASMCalls";
		min.name = "canSpawnCreature";
	}

}
