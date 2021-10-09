package Reika.DragonAPI.ASM.Patchers.Fixes;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class ClimateControlReikaBiomes extends Patcher {

	public ClimateControlReikaBiomes() {
		super("climateControl.biomeSettings.ReikasPackage");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "freshBiomeSetting", "()LclimateControl/api/BiomeSettings;");
		for (int i = 0; i < m.instructions.size(); i++) {
			AbstractInsnNode ain = m.instructions.get(i);
			if (ain.getOpcode() == Opcodes.NEW) {
				TypeInsnNode tin = (TypeInsnNode)ain;
				tin.desc = "Reika/DragonAPI/ModInteract/ReikaClimateControl";
				ReikaASMHelper.log("Successfully applied "+this+" ASM handler 1!");
			}
			else if (ain.getOpcode() == Opcodes.INVOKESPECIAL) {
				MethodInsnNode min = (MethodInsnNode)ain;
				min.owner = "Reika/DragonAPI/ModInteract/ReikaClimateControl";
				ReikaASMHelper.log("Successfully applied "+this+" ASM handler 2!");
			}
		}
	}

}
