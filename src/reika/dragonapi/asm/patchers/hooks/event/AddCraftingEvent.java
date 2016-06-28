package reika.dragonapi.asm.patchers.hooks.event;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import reika.dragonapi.asm.patchers.Patcher;
import reika.dragonapi.libraries.java.ReikaASMHelper;

public class AddCraftingEvent extends Patcher {

	public AddCraftingEvent() { //replace list with one that fires events
		super("net.minecraft.item.crafting.CraftingManager", "afe");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "<init>", "()V");
		TypeInsnNode type = (TypeInsnNode)ReikaASMHelper.getFirstOpcode(m.instructions, Opcodes.NEW);
		MethodInsnNode cons = (MethodInsnNode)type.getNext().getNext();

		String s = "Reika/DragonAPI/Instantiable/Data/Collections/EventRecipeList";

		type.desc = s;
		cons.owner = s;
	}
}
