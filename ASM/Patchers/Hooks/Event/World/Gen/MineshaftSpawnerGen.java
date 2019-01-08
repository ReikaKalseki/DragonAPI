package Reika.DragonAPI.ASM.Patchers.Hooks.Event.World.Gen;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import Reika.DragonAPI.ASM.Patchers.SpawnerGen;
import Reika.DragonAPI.Instantiable.Event.SpawnerGenerationEvent.SpawnerSource;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class MineshaftSpawnerGen extends SpawnerGen {

	public MineshaftSpawnerGen() {
		super("net.minecraft.world.gen.structure.StructureMineshaftPieces$Corridor", "asy");
	}

	@Override
	protected MethodNode getMethod(ClassNode cn) {
		return ReikaASMHelper.getMethodByName(cn, "func_74875_a", "addComponentParts", "(Lnet/minecraft/world/World;Ljava/util/Random;Lnet/minecraft/world/gen/structure/StructureBoundingBox;)Z");
	}

	@Override
	protected SpawnerSource getEnumEntry() {
		return SpawnerSource.MINESHAFT;
	}

}
