package Reika.DragonAPI.ASM.Patchers.Hooks.Event.World.Gen;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import Reika.DragonAPI.ASM.StructureLootHooks;
import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;
import net.minecraftforge.classloading.FMLForgePlugin;

public class DungeonLootGen extends Patcher {

	public DungeonLootGen() {
		super("net.minecraft.world.gen.feature.WorldGenDungeons", "asd");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_76484_a", "generate", "(Lnet/minecraft/world/World;Ljava/util/Random;III)Z");
		MethodInsnNode look = ReikaASMHelper.getFirstMethodCallByName(cn, m, FMLForgePlugin.RUNTIME_DEOBF ? "func_76293_a" : "generateChestContents");
		StructureLootHooks.inject(cn, m, look);
	}

}