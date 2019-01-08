package Reika.DragonAPI.ASM.Patchers.Fixes;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class FractionalVillageWeight extends Patcher {

	public FractionalVillageWeight() {
		super("net.minecraft.world.gen.structure.StructureVillagePieces", "avp");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_75084_a", "getStructureVillageWeightedPieceList", "(Ljava/util/Random;I)Ljava/util/List;");
		AbstractInsnNode ain = ReikaASMHelper.getLastOpcode(m.instructions, Opcodes.ARETURN);
		m.instructions.insertBefore(ain, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Worldgen/VillageBuilding", "applyFractionalWeights", "(Ljava/util/List;)Ljava/util/List;", false));
	}

}
